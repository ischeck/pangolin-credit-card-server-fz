package cn.fintecher.pangolin.service.dataimp.service;

import cn.fintecher.pangolin.common.enums.CaseDataStatus;
import cn.fintecher.pangolin.common.enums.CaseLeaveFlag;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.domain.BaseCase;
import cn.fintecher.pangolin.entity.domain.CaseEndImportTemp;
import cn.fintecher.pangolin.entity.domain.CaseLeafImportTemp;
import cn.fintecher.pangolin.service.dataimp.repository.ImportBaseCaseRepository;
import com.google.common.collect.Lists;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 17:02 2018/8/7
 */
@Service("confirmImportCaseEndSubTask")
public class ConfirmImportCaseEndSubTask {
    Logger logger= LoggerFactory.getLogger(ConfirmImportCaseEndSubTask.class);

    @Autowired
    ImportBaseCaseRepository importBaseCaseRepository;

    @Autowired
    DataimpBaseService dataimpBaseService;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Async
    public CompletableFuture<Long> doSubTask(List<CaseEndImportTemp> caseEndImportTempList, int pageNo){
        logger.info("开始进行案件停催第{}页数据,确认数据量:{}",pageNo,caseEndImportTempList.size());
        List<BaseCase> result=new ArrayList<>();
        Set<String> primaryKeySets=new HashSet<>();
        Set<String> deleteIds=new HashSet<>();
        List<String> baseIds=new ArrayList<>();
        Long total=0L;
        Map<String,CaseEndImportTemp> caseEndImportTempMap=new HashMap<>();
        try {
            for(CaseEndImportTemp obj:caseEndImportTempList){
                primaryKeySets.add(obj.getPrimaryKey());
                caseEndImportTempMap.put(obj.getPrimaryKey(),obj);
            }
            BoolQueryBuilder qb= QueryBuilders.boolQuery();
            qb.must(termsQuery("primaryKey.keyword",primaryKeySets));
            Iterable<BaseCase> baseCaseIterable=importBaseCaseRepository.search(qb);
            List<BaseCase> baseCaseList= Lists.newArrayList(baseCaseIterable);
            if(!baseCaseList.isEmpty()){
                for(BaseCase baseCase:baseCaseList){
                    if(caseEndImportTempMap.containsKey(baseCase.getPrimaryKey())){
                        baseCase.setCaseDataStatus(CaseDataStatus.PAUSE);
                        baseCase.setStopTime(ZWDateUtil.getNowDate());
                        result.add(baseCase);
                        deleteIds.add(caseEndImportTempMap.get(baseCase.getPrimaryKey()).getId());
                        baseIds.add(baseCase.getId());
                        total=total+1;
                    }
                }
            }
            if(!result.isEmpty()){
                importBaseCaseRepository.saveAll(result);
            }
            if(!baseIds.isEmpty()){
                dataimpBaseService.endApplyCase(baseIds);
            }
            //删除已经更新的数据
            if(!deleteIds.isEmpty()){
                BoolQueryBuilder queryBuilder= QueryBuilders.boolQuery();
                queryBuilder.must().add(termsQuery("id.keyword",deleteIds));
                DeleteByQueryRequestBuilder deleteByQueryRequestBuilder= DeleteByQueryAction.INSTANCE.
                        newRequestBuilder(elasticsearchTemplate.getClient());
                deleteByQueryRequestBuilder.source("case_end_import_temp").filter(queryBuilder).refresh(true).execute();
            }

        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }finally {
            result.clear();
            primaryKeySets.clear();
            deleteIds.clear();
            baseIds.clear();
        }
        logger.info("完成进行案件停催第{}页数据,确认数据量:{}",pageNo,caseEndImportTempList.size());
        return   CompletableFuture.completedFuture(total);
    }
}
