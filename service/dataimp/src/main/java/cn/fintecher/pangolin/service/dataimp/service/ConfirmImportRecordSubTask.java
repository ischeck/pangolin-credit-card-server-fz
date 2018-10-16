package cn.fintecher.pangolin.service.dataimp.service;

import cn.fintecher.pangolin.common.enums.CaseDataStatus;
import cn.fintecher.pangolin.common.enums.CaseLeaveFlag;
import cn.fintecher.pangolin.entity.domain.*;
import cn.fintecher.pangolin.service.dataimp.repository.ImpPreCaseFollowupRecordRepository;
import cn.fintecher.pangolin.service.dataimp.repository.ImportBaseCaseRepository;
import com.google.common.collect.Lists;
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

import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 17:02 2018/8/7
 */
@Service("confirmImportRecordSubTask")
public class ConfirmImportRecordSubTask {
    Logger logger= LoggerFactory.getLogger(ConfirmImportRecordSubTask.class);

    @Autowired
    ImportBaseCaseRepository importBaseCaseRepository;

    @Autowired
    ImpPreCaseFollowupRecordRepository impPreCaseFollowupRecordRepository;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Async
    public CompletableFuture<Long> doSubTask(List<PreCaseFollowupRecordTemp> preCaseFollowupRecordTempList, int pageNo){
        logger.info("开始进行案件委前催计第{}页数据,确认数据量:{}",pageNo,preCaseFollowupRecordTempList.size());
        List<PreCaseFollowupRecord> preCaseFollowupRecords=new ArrayList<>();
        Map<String,PreCaseFollowupRecordTemp> followupRecordMap=new HashMap<>();
        Set<String> primaryKeySets=new HashSet<>();
        Set<String> deleteIds=new HashSet<>();
        Long total=0L;

        try {
            //根据primaryKey一次性查询所有需要更新的数据
            for(PreCaseFollowupRecordTemp obj:preCaseFollowupRecordTempList){
                primaryKeySets.add(obj.getPrimaryKey());
                followupRecordMap.put(obj.getPrimaryKey(),obj);
            }
            BoolQueryBuilder qb= QueryBuilders.boolQuery();
            qb.must(termsQuery("primaryKey.keyword",primaryKeySets));
            Iterable<BaseCase> baseCaseIterable=importBaseCaseRepository.search(qb);
            List<BaseCase> baseCaseList= Lists.newArrayList(baseCaseIterable);
            if(!baseCaseList.isEmpty()){
                for(BaseCase baseCase :baseCaseList){
                    if(followupRecordMap.containsKey(baseCase.getPrimaryKey())){
                        PreCaseFollowupRecord preCaseFollowupRecord=new PreCaseFollowupRecord();
                        BeanUtils.copyProperties(followupRecordMap.get(baseCase.getPrimaryKey()),preCaseFollowupRecord);
                        preCaseFollowupRecord.setCaseId(baseCase.getId());
                        preCaseFollowupRecords.add(preCaseFollowupRecord);
                        deleteIds.add(followupRecordMap.get(baseCase.getPrimaryKey()).getId());
                        total=total+1;
                    }
                }
            }
            if(!preCaseFollowupRecords.isEmpty()){
                impPreCaseFollowupRecordRepository.saveAll(preCaseFollowupRecords);
            }
            if(!deleteIds.isEmpty()){
                BoolQueryBuilder queryBuilder= QueryBuilders.boolQuery();
                queryBuilder.must().add(termsQuery("id.keyword",deleteIds));
                DeleteByQueryRequestBuilder deleteByQueryRequestBuilder= DeleteByQueryAction.INSTANCE.
                        newRequestBuilder(elasticsearchTemplate.getClient());
                deleteByQueryRequestBuilder.source("pre_case_followup_record_temp").filter(queryBuilder).refresh(true).execute();
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }finally {
            preCaseFollowupRecords.clear();
            followupRecordMap.clear();
            primaryKeySets.clear();
            deleteIds.clear();
        }
        logger.info("完成进行案件委前催计第{}页数据,确认数据量:{}",pageNo,preCaseFollowupRecordTempList.size());
        return   CompletableFuture.completedFuture(total);
    }
}
