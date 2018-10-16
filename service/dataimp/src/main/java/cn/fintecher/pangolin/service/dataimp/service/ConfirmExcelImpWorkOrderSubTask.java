package cn.fintecher.pangolin.service.dataimp.service;

import cn.fintecher.pangolin.common.utils.BeanUtils;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.domain.*;
import cn.fintecher.pangolin.service.dataimp.repository.ImportBaseCaseRepository;
import cn.fintecher.pangolin.service.dataimp.repository.ImportCaseWorkOrderInfoRepository;
import com.google.common.collect.Lists;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

/**
 * @Author:peishouwen
 * @Desc: 工单导入
 * @Date:Create in 22:37 2018/8/6
 */
@Service("confirmExcelImpWorkOrderSubTask")
public class ConfirmExcelImpWorkOrderSubTask {
    Logger logger= LoggerFactory.getLogger(ConfirmExcelImpWorkOrderSubTask.class);

    @Autowired
    ImportBaseCaseRepository importBaseCaseRepository;

    @Autowired
    ImportCaseWorkOrderInfoRepository importCaseWorkOrderInfoRepository;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Async
    public CompletableFuture<Long> doSubTask(List<CaseWorkImportOrderInfoTemp> caseWorkImportOrderInfoTempList, int pageNo){
        logger.info("开始进行案件工单第{}页数据,确认数据量:{}",pageNo,caseWorkImportOrderInfoTempList.size());
        List<CaseWorkOrderInfo> caseWorkOrderInfoList=new ArrayList<>();
        Set<String> primaryKeySets=new HashSet<>();
        Set<String> deleteIds=new HashSet<>();
        Long total=0L;
        try {
            //根据primaryKey一次性查询所有需要更新的数据
            for(CaseWorkImportOrderInfoTemp obj:caseWorkImportOrderInfoTempList){
                primaryKeySets.add(obj.getPrimaryKey());
            }
            BoolQueryBuilder qb= QueryBuilders.boolQuery();
            qb.must(termsQuery("primaryKey.keyword",primaryKeySets));
            Iterable<BaseCase> baseCaseIterable=importBaseCaseRepository.search(qb);
            List<BaseCase> baseCaseList= Lists.newArrayList(baseCaseIterable);
            if(!baseCaseList.isEmpty()) {
                Map<String, BaseCase> baseCaseMap = baseCaseList.stream().collect(Collectors.toMap(BaseCase::getPrimaryKey, baseCase -> baseCase));
                for(CaseWorkImportOrderInfoTemp obj:caseWorkImportOrderInfoTempList){
                    if(Objects.nonNull(baseCaseMap.get(obj.getPrimaryKey()))){
                        CaseWorkOrderInfo caseWorkOrderInfo=new CaseWorkOrderInfo();
                        BeanUtils.copyProperties(obj,caseWorkOrderInfo);
                        caseWorkOrderInfo.setCaseId(baseCaseMap.get(obj.getPrimaryKey()).getId());
                        caseWorkOrderInfoList.add(caseWorkOrderInfo);
                        deleteIds.add(obj.getId());
                        total=total+1;
                    }
                }
                if(!caseWorkOrderInfoList.isEmpty()){
                    importCaseWorkOrderInfoRepository.saveAll(caseWorkOrderInfoList);
                }
                if(!deleteIds.isEmpty()){
                    BoolQueryBuilder queryBuilder= QueryBuilders.boolQuery();
                    queryBuilder.must().add(termsQuery("id.keyword",deleteIds));
                    DeleteByQueryRequestBuilder deleteByQueryRequestBuilder= DeleteByQueryAction.INSTANCE.
                            newRequestBuilder(elasticsearchTemplate.getClient());
                    deleteByQueryRequestBuilder.source("case_work_import_order_info_temp").filter(queryBuilder).refresh(true).execute();
                }
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }finally {
            caseWorkOrderInfoList.clear();
            deleteIds.clear();
        }
        logger.info("完成进行案件工单第{}页数据,确认数据量:{}",pageNo,caseWorkImportOrderInfoTempList.size());
        return   CompletableFuture.completedFuture(total);
    }
}
