package cn.fintecher.pangolin.service.dataimp.service;

import cn.fintecher.pangolin.common.enums.ImportDataExcelStatus;
import cn.fintecher.pangolin.common.enums.MessageMode;
import cn.fintecher.pangolin.common.enums.MessageType;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.entity.domain.BaseCase;
import cn.fintecher.pangolin.entity.domain.CaseUpdateImportTemp;
import cn.fintecher.pangolin.entity.domain.ImportOthersDataExcelRecord;
import cn.fintecher.pangolin.entity.domain.LeftAmtLog;
import cn.fintecher.pangolin.service.dataimp.repository.CaseUpdateImportTempRepostory;
import cn.fintecher.pangolin.service.dataimp.repository.ImpLeftAmtLogRepPository;
import cn.fintecher.pangolin.service.dataimp.repository.ImportBaseCaseRepository;
import cn.fintecher.pangolin.service.dataimp.repository.ImportOthersDataExcelRecordRepository;
import com.google.common.collect.Lists;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

/**
 * @Author:peishouwen
 * @Desc: 案件更新确认
 * @Date:Create in 16:15 2018/8/7
 */
@Service("confirmImportUpdateCaseService")
public class ConfirmImportUpdateCaseService {
    Logger logger= LoggerFactory.getLogger(ConfirmImportUpdateCaseService.class);

    @Autowired
    CaseUpdateImportTempRepostory caseUpdateImportTempRepostory;

    @Autowired
    ConfirmExcelImpOthersUpdateSubTask confirmExcelImpOthersUpdateSubTask;

    @Autowired
    ImportBaseCaseRepository importBaseCaseRepository;

    @Autowired
    ImportOthersDataExcelRecordRepository importOthersDataExcelRecordRepository;

    @Autowired
    DataimpBaseService dataimpBaseService;

    @Async
    public void confirmExcelImpOthersCase(ImportOthersDataExcelRecord record, OperatorModel operator) {
        logger.info("开始案件跟新数据确认。。。。。。");
        Long total=0L;
        try {
            StopWatch watch = new StopWatch();
            watch.start();
            // 创建任务集合
            List<CompletableFuture<Long>> taskList = new ArrayList<>();
            //分页确认
            int pageSize = 1000;
            int pageCount=0;
            BoolQueryBuilder boolQueryBuilder= QueryBuilders.boolQuery();
            boolQueryBuilder.must(matchPhraseQuery("operBatchNumber.keyword", record.getOperBatchNumber()));
            Iterable<CaseUpdateImportTemp> caseUpdateImportTemps=  caseUpdateImportTempRepostory.search(boolQueryBuilder);
            if(caseUpdateImportTemps.iterator().hasNext()){
                List<CaseUpdateImportTemp>  caseUpdateImportTempList= Lists.newArrayList(caseUpdateImportTemps);
                if((caseUpdateImportTempList.size()%pageSize)>0){
                    pageCount=caseUpdateImportTempList.size()/pageSize+1;
                }else {
                    pageCount=caseUpdateImportTempList.size()/pageSize;
                }
                for(int pageNo=1;pageNo<=pageCount;pageNo++) {
                    int startIndex = pageSize * (pageNo - 1);
                    int endIndex = pageSize * pageNo;
                    if (endIndex > caseUpdateImportTempList.size()) {
                        endIndex = caseUpdateImportTempList.size();
                    }
                    List<CaseUpdateImportTemp> percaseUpdateImportTempList = new ArrayList<>();
                    percaseUpdateImportTempList.addAll(caseUpdateImportTempList.subList(startIndex, endIndex));
                    CompletableFuture<Long> subTaskList=confirmExcelImpOthersUpdateSubTask.doSubTask(percaseUpdateImportTempList, pageNo,operator);
                    taskList.add(subTaskList);
                }

                logger.info("等待子线程完成。。。。。。");
                //收集子线程返回结果
                for(CompletableFuture<Long> obj:taskList){
                        total=total+ obj.get();
                }
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            record.setImportDataExcelStatus(ImportDataExcelStatus.UN_CONFIRMED);
            importOthersDataExcelRecordRepository.save(record);
        }
        record.setImportDataExcelStatus(ImportDataExcelStatus.CONFIRMED);
        importOthersDataExcelRecordRepository.save(record);
        //发送消息
        dataimpBaseService.sendMessage("案件跟新确认，跟新案件数量为".concat(String.valueOf(total)),
                operator,record.getImportDataExcelStatus().equals(ImportDataExcelStatus.UN_CONFIRMED) ?
                        record.getOperBatchNumber().concat("案件跟新确认失败"):  record.getOperBatchNumber().concat("案件跟新确认成功"),
                        MessageType.IMPORT_UPDATE_CONFIRMED, MessageMode.POPUP);
        logger.info("完成案件跟新数据确认。。。。。。");
    }
}
