package cn.fintecher.pangolin.service.dataimp.service;

import cn.fintecher.pangolin.common.enums.ImportDataExcelStatus;
import cn.fintecher.pangolin.common.enums.MessageDataType;
import cn.fintecher.pangolin.common.enums.MessageMode;
import cn.fintecher.pangolin.common.enums.MessageType;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.entity.domain.*;
import cn.fintecher.pangolin.entity.managentment.Principal;
import cn.fintecher.pangolin.service.dataimp.repository.*;
import com.google.common.collect.Lists;
import org.apache.lucene.queryparser.flexible.core.builders.QueryBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;


/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 22:22 2018/8/1
 */
@Service("confirmExcelImpCaseService")
public class ConfirmExcelImpCaseService {

    Logger logger= LoggerFactory.getLogger(ConfirmExcelImpCaseService.class);

    @Autowired
    ConfirmExcelImpCaseSubTask confirmExcelImpCaseSubTask;

    @Autowired
    ImportBaseCaseRepository importBaseCaseRepository;

    @Autowired
    BaseCaseImportExcelTempRepository baseCaseImportExcelTempRepository;


    @Autowired
    ImportDataExcelRecordRepository importDataExcelRecordRepository;

    @Autowired
    DataimpBaseService dataimpBaseService;
    /**
     * 案件确认
     * @param importDataExcelRecord
     * @param operator
     */
    @Async
    public void confirmExcelImpCase(ImportDataExcelRecord importDataExcelRecord,OperatorModel operator){
        logger.info("开始案件确认。。。。。。");
        StopWatch watch = new StopWatch();
        watch.start();
        BoolQueryBuilder queryBuilder= QueryBuilders.boolQuery();
        queryBuilder.must().add(matchPhraseQuery("batchNumber",importDataExcelRecord.getBatchNumber()));
        Iterable<BaseCaseAllImportExcelTemp> baseCaseIterables=baseCaseImportExcelTempRepository.search(queryBuilder);
        Integer totals=0;
        Integer confirmTotals=0;
        if(baseCaseIterables.iterator().hasNext()){
            List<BaseCaseAllImportExcelTemp> baseCaseList= Lists.newArrayList(baseCaseIterables);
            totals=baseCaseList.size();
            // 创建任务集合
            List<CompletableFuture<Integer>> taskList = new ArrayList<>();
            try {
                //分页确认
                int pageSize = 1000;
                int pageCount=0;
                if((baseCaseList.size()%pageSize)>0){
                    pageCount=baseCaseList.size()/pageSize+1;
                }else {
                    pageCount=baseCaseList.size()/pageSize;
                }
                for(int pageNo=1;pageNo<=pageCount;pageNo++) {
                    int startIndex = pageSize * (pageNo - 1);
                    int endIndex = pageSize * pageNo;
                    if (endIndex > baseCaseList.size()) {
                        endIndex = baseCaseList.size();
                    }
                    List<BaseCaseAllImportExcelTemp> perBaseCaseList = new ArrayList<>();
                    perBaseCaseList.addAll(baseCaseList.subList(startIndex, endIndex));
                    Principal principal=new Principal();
                    principal.setId(importDataExcelRecord.getPrincipalId());
                    principal.setPrincipalName(importDataExcelRecord.getPrincipalName());
                    CompletableFuture<Integer> subTaskList=confirmExcelImpCaseSubTask.doSubTask(perBaseCaseList,operator,principal,pageNo);
                    taskList.add(subTaskList);
                }
                logger.info("等待子线程完成。。。。。。");
                //收集子线程返回结果
                for(CompletableFuture<Integer> resultData:taskList){
                    confirmTotals=confirmTotals+resultData.get();
                }
                if(totals.intValue()!=confirmTotals.intValue()){
                    importDataExcelRecord.setImportDataExcelStatus(ImportDataExcelStatus.UN_CONFIRMED);
                }else {
                    importDataExcelRecord.setImportDataExcelStatus(ImportDataExcelStatus.CONFIRMED);
                }
            }catch (Exception e){
                logger.error(e.getMessage(),e);
                importDataExcelRecord.setImportDataExcelStatus(ImportDataExcelStatus.UN_CONFIRMED);
            }finally {
                importDataExcelRecordRepository.save(importDataExcelRecord);
            }
        }
        watch.stop();
        //发送消息
        dataimpBaseService.sendMessage("案件导入确认,确认数量:".concat(String.valueOf(confirmTotals)),
                operator,importDataExcelRecord.getImportDataExcelStatus().equals(ImportDataExcelStatus.UN_CONFIRMED) ?
                        importDataExcelRecord.getBatchNumber().concat("确认失败"): importDataExcelRecord.getBatchNumber().concat("确认成功"), MessageType.IMPORT_CONFIRMED_MSG, MessageMode.POPUP);
        logger.info("案件确认完成,耗时:{}",watch.getTotalTimeMillis());
    }
}
