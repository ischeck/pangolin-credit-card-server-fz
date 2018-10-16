package cn.fintecher.pangolin.service.dataimp.service;

import cn.fintecher.pangolin.entity.domain.*;
import cn.fintecher.pangolin.service.dataimp.repository.ImpCaseWarningInfoRepository;
import cn.fintecher.pangolin.service.dataimp.repository.ImportBaseCaseRepository;
import cn.fintecher.pangolin.service.dataimp.repository.ImportCaseWorkOrderInfoRepository;
import com.google.common.collect.Lists;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.search.aggregations.support.ValuesSourceAggregationBuilder;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

/**
 * @Author:peishouwen
 * @Desc: 警告信息确认
 * @Date:Create in 22:37 2018/8/6
 */
@Service("confirmExcelImpWarnInfoSubTask")
public class ConfirmExcelImpWarnInfoSubTask {
    Logger logger= LoggerFactory.getLogger(ConfirmExcelImpWarnInfoSubTask.class);

    @Autowired
    ImportBaseCaseRepository importBaseCaseRepository;

    @Autowired
    ImpCaseWarningInfoRepository impCaseWarningInfoRepository;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Async
    public CompletableFuture<Long> doSubTask(List<CaseWarnImportTemp> caseWarnImportTempList, int pageNo){
        logger.info("开始进行警告信息确认第{}页数据,确认数据量:{}",pageNo,caseWarnImportTempList.size());
        List<CaseWarningInfo> caseWarningInfoList=new ArrayList<>();
        Set<String> deleteIds=new HashSet<>();
        Set<String> primaryKeySets=new HashSet<>();
        Long total=0L;
        try {
            //根据primaryKey一次性查询所有需要更新的数据
            for(CaseWarnImportTemp obj:caseWarnImportTempList){
                primaryKeySets.add(obj.getPrimaryKey());
            }
            BoolQueryBuilder qb= QueryBuilders.boolQuery();
            qb.must(termsQuery("primaryKey.keyword",primaryKeySets));
            Iterable<BaseCase> baseCaseIterable=importBaseCaseRepository.search(qb);
            List<BaseCase> baseCaseList= Lists.newArrayList(baseCaseIterable);
            if(!baseCaseList.isEmpty()){
                Map<String,BaseCase> baseCaseMap=baseCaseList.stream().collect(Collectors.toMap(BaseCase ::getPrimaryKey, baseCase -> baseCase));
                for(CaseWarnImportTemp obj:caseWarnImportTempList){
                    if(Objects.nonNull(baseCaseMap.get(obj.getPrimaryKey()))){
                        CaseWarningInfo caseWarningInfo=new CaseWarningInfo();
                        BeanUtils.copyProperties(obj,caseWarningInfo);
                        caseWarningInfo.setCaseId(baseCaseMap.get(obj.getPrimaryKey()).getId());
                        caseWarningInfoList.add(caseWarningInfo);
                        deleteIds.add(obj.getId());
                        total=total+1;
                    }
                }
                if(!caseWarningInfoList.isEmpty()){
                    impCaseWarningInfoRepository.saveAll(caseWarningInfoList);
                }
                if(!deleteIds.isEmpty()){
                    BoolQueryBuilder queryBuilder= QueryBuilders.boolQuery();
                    queryBuilder.must().add(termsQuery("id.keyword",deleteIds));
                    DeleteByQueryRequestBuilder deleteByQueryRequestBuilder= DeleteByQueryAction.INSTANCE.
                            newRequestBuilder(elasticsearchTemplate.getClient());
                    deleteByQueryRequestBuilder.source("case_warn_import_temp").filter(queryBuilder).refresh(true).execute();
                }
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }finally {
            caseWarningInfoList.clear();
            deleteIds.clear();
        }
        logger.info("完成进行警告信息确认第{}页数据,确认数据量:{}",pageNo,caseWarnImportTempList.size());
        return   CompletableFuture.completedFuture(total);
    }
}
