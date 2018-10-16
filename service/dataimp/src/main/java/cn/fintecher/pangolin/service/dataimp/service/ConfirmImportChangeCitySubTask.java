package cn.fintecher.pangolin.service.dataimp.service;

import cn.fintecher.pangolin.common.enums.CaseDataStatus;
import cn.fintecher.pangolin.common.enums.CaseLeaveFlag;
import cn.fintecher.pangolin.entity.domain.BaseCase;
import cn.fintecher.pangolin.entity.domain.CaseChangeCityImportTemp;
import cn.fintecher.pangolin.entity.domain.CaseLeafImportTemp;
import cn.fintecher.pangolin.entity.domain.CaseUpdateImportTemp;
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
 * @Desc: 城市调整
 * @Date:Create in 17:02 2018/8/7
 */
@Service("confirmImportChangeCitySubTask")
public class ConfirmImportChangeCitySubTask {
    Logger logger= LoggerFactory.getLogger(ConfirmImportChangeCitySubTask.class);

    @Autowired
    ImportBaseCaseRepository importBaseCaseRepository;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Async
    public CompletableFuture<Long> doSubTask(List<CaseChangeCityImportTemp> caseChangeCityImportTempList, int pageNo){
        logger.info("开始进行案件城市调整第{}页数据:{}",pageNo,caseChangeCityImportTempList.size());
        Map<String,CaseChangeCityImportTemp> caseChangeCityImportTempMap=new HashMap<>();
        Set<String> primaryKeySets=new HashSet<>();
        Set<String> deleteIds=new HashSet<>();
        List<BaseCase> resultList=new ArrayList<>();
        Long total=0L;
        try {
            //根据primaryKey一次性查询所有需要更新的数据
            for(CaseChangeCityImportTemp obj:caseChangeCityImportTempList){
                primaryKeySets.add(obj.getPrimaryKey());
                caseChangeCityImportTempMap.put(obj.getPrimaryKey(),obj);
            }
            BoolQueryBuilder qb= QueryBuilders.boolQuery();
            qb.must(termsQuery("primaryKey.keyword",primaryKeySets));
            Iterable<BaseCase> baseCaseIterable=importBaseCaseRepository.search(qb);
            List<BaseCase> baseCaseList= Lists.newArrayList(baseCaseIterable);
            if(!baseCaseList.isEmpty()){
                for(BaseCase baseCase:baseCaseList){
                    if(caseChangeCityImportTempMap.containsKey(baseCase.getPrimaryKey())){
                        baseCase.setCity(caseChangeCityImportTempMap.get(baseCase.getPrimaryKey()).getCity());
                        resultList.add(baseCase);
                        deleteIds.add(caseChangeCityImportTempMap.get(baseCase.getPrimaryKey()).getId());
                        total=total+1;
                    }
                }
            }
            if(!resultList.isEmpty()){
                importBaseCaseRepository.saveAll(resultList);
            }
            //删除已经更新的数据
            if(!deleteIds.isEmpty()){
                BoolQueryBuilder queryBuilder= QueryBuilders.boolQuery();
                queryBuilder.must().add(termsQuery("id.keyword",deleteIds));
                DeleteByQueryRequestBuilder deleteByQueryRequestBuilder= DeleteByQueryAction.INSTANCE.
                        newRequestBuilder(elasticsearchTemplate.getClient());
                deleteByQueryRequestBuilder.source("case_change_city_import_temp").filter(queryBuilder).refresh(true).execute();
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }finally {
            caseChangeCityImportTempMap.clear();
            primaryKeySets.clear();
            deleteIds.clear();
            resultList.clear();
        }
        logger.info("完成进行城市调整第{}页数据",pageNo);
        return   CompletableFuture.completedFuture(total);
    }
}
