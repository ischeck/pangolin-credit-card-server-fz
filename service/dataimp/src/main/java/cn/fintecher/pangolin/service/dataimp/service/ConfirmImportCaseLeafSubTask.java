package cn.fintecher.pangolin.service.dataimp.service;

import cn.fintecher.pangolin.common.enums.CaseDataStatus;
import cn.fintecher.pangolin.common.enums.CaseLeaveFlag;
import cn.fintecher.pangolin.entity.domain.*;
import cn.fintecher.pangolin.service.dataimp.repository.ImportBaseCaseRepository;
import cn.fintecher.pangolin.service.dataimp.repository.ImportHisCaseRepository;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.ListUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
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
 * @Desc:
 * @Date:Create in 17:02 2018/8/7
 */
@Service("confirmImportCaseLeafSubTask")
public class ConfirmImportCaseLeafSubTask {
    Logger logger= LoggerFactory.getLogger(ConfirmImportCaseLeafSubTask.class);

    @Autowired
    ImportBaseCaseRepository importBaseCaseRepository;

    @Autowired
    ImportHisCaseRepository importHisCaseRepository;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Async
    public CompletableFuture<Long> doSubTask(List<CaseLeafImportTemp> caseWarnImportTempList, int pageNo){
        logger.info("开始进行案件留案第{}页数据,确认数据量:{}",pageNo,caseWarnImportTempList.size());
        Set<String> primaryKeySets=new HashSet<>();
        Set<String> deleteIds=new HashSet<>();
        Long total=0L;
        try {
            //根据primaryKey一次性查询所有需要更新的数据
            for(CaseLeafImportTemp obj:caseWarnImportTempList){
                primaryKeySets.add(obj.getPrimaryKey());
            }
            Map<String,CaseLeafImportTemp> caseLeafImportTempMap=caseWarnImportTempList.stream().collect(
                    Collectors.toMap(CaseLeafImportTemp :: getPrimaryKey,caseLeafImportTemp -> caseLeafImportTemp));
            //查询在案表
            BoolQueryBuilder qb= QueryBuilders.boolQuery();
            qb.must(termsQuery("primaryKey.keyword",primaryKeySets));
            Iterable<BaseCase> baseCaseIterable=importBaseCaseRepository.search(qb);
            List<BaseCase> baseCaseList= Lists.newArrayList(baseCaseIterable);
            if(!baseCaseList.isEmpty()){
                for(BaseCase baseCase:baseCaseList){
                    if(caseLeafImportTempMap.containsKey(baseCase.getPrimaryKey())){
                        baseCase.setLeaveFlag(CaseLeaveFlag.HAS_LEAVE);
                        baseCase.setEndCaseDate(caseLeafImportTempMap.get(baseCase.getPrimaryKey()).getEndCaseDate());
                        baseCase.setCaseDataStatus(CaseDataStatus.IN_POOL);
                        baseCaseList.add(baseCase);
                        deleteIds.add(caseLeafImportTempMap.get(baseCase.getPrimaryKey()).getId());
                        total=total+1;
                    }
                }
            }
            //查询案件历史表
            Iterable<HisCase> hisCaseIterable= importHisCaseRepository.search(qb);
            List<HisCase> hisCaseList= Lists.newArrayList(hisCaseIterable);
            if(!hisCaseList.isEmpty()){
                for(HisCase hisCase:hisCaseList){
                    if(caseLeafImportTempMap.containsKey(hisCase.getPrimaryKey())){
                        BaseCase baseCase=new BaseCase();
                        BeanUtils.copyProperties(hisCase,baseCase);
                        baseCase.setLeaveFlag(CaseLeaveFlag.HAS_LEAVE);
                        baseCase.setEndCaseDate(caseLeafImportTempMap.get(baseCase.getPrimaryKey()).getEndCaseDate());
                        baseCase.setCaseDataStatus(CaseDataStatus.IN_POOL);
                        baseCaseList.add(baseCase);
                        deleteIds.add(caseLeafImportTempMap.get(hisCase.getPrimaryKey()).getId());
                        total=total+1;
                    }
                }
            }
            if(!baseCaseList.isEmpty()){
                importBaseCaseRepository.saveAll(baseCaseList);
            }
            if(!deleteIds.isEmpty()){
                BoolQueryBuilder queryBuilder= QueryBuilders.boolQuery();
                queryBuilder.must().add(termsQuery("id.keyword",deleteIds));
                DeleteByQueryRequestBuilder deleteByQueryRequestBuilder= DeleteByQueryAction.INSTANCE.
                        newRequestBuilder(elasticsearchTemplate.getClient());
                deleteByQueryRequestBuilder.source("case_leaf_import_temp").filter(queryBuilder).refresh(true).execute();
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }finally {
            primaryKeySets.clear();
            deleteIds.clear();
        }
        logger.info("完成进行案件留案第{}页数据,确认数据量:{}",pageNo,caseWarnImportTempList.size());
        return   CompletableFuture.completedFuture(total);
    }
}
