package cn.fintecher.pangolin.service.dataimp.service;

import cn.fintecher.pangolin.common.enums.ImportDataExcelStatus;
import cn.fintecher.pangolin.common.enums.MessageMode;
import cn.fintecher.pangolin.common.enums.MessageType;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.entity.domain.*;
import cn.fintecher.pangolin.service.dataimp.repository.*;
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
import java.util.concurrent.CompletableFuture;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

/**
 * @Author:peishouwen
 * @Desc: 案件委前催计
 * @Date:Create in 16:15 2018/8/7
 */
@Service("confirmImportRecordService")
public class ConfirmImportRecordService {
    Logger logger= LoggerFactory.getLogger(ConfirmImportRecordService.class);

    @Autowired
    PreCaseFollowupRecordTempRepository preCaseFollowupRecordTempRepository;

    @Autowired
    ConfirmImportRecordSubTask confirmImportRecordSubTask;

    @Autowired
    ImportOthersDataExcelRecordRepository importOthersDataExcelRecordRepository;

    @Autowired
    DataimpBaseService dataimpBaseService;

    @Async
    public void confirmExcelImpOthersCase(ImportOthersDataExcelRecord record, OperatorModel operator) {
        logger.info("开始案件委前催计数据确认。。。。。。");
        Long total=0L;
        try {
            StopWatch watch = new StopWatch();
            watch.start();
            // 创建任务集合
            List<CompletableFuture<Long>> taskList = new ArrayList<>();
            List<PreCaseFollowupRecord> resultData=new ArrayList<>();
            int pageSize= 5000;
            int pageCount=0;
            BoolQueryBuilder boolQueryBuilder= QueryBuilders.boolQuery();
            boolQueryBuilder.must(matchPhraseQuery("operBatchNumber.keyword", record.getOperBatchNumber()));
            Iterable<PreCaseFollowupRecordTemp> preCaseFollowupRecordTemps=  preCaseFollowupRecordTempRepository.search(boolQueryBuilder);
            if(preCaseFollowupRecordTemps.iterator().hasNext()){
                List<PreCaseFollowupRecordTemp>    preCaseFollowupRecordTempList= Lists.newArrayList(preCaseFollowupRecordTemps);
                if((preCaseFollowupRecordTempList.size()%pageSize)>0){
                    pageCount=preCaseFollowupRecordTempList.size()/pageSize+1;
                }else {
                    pageCount=preCaseFollowupRecordTempList.size()/pageSize;
                }
                for(int pageNo=1;pageNo<=pageCount;pageNo++) {
                    int startIndex = pageSize * (pageNo - 1);
                    int endIndex = pageSize * pageNo;
                    if (endIndex > preCaseFollowupRecordTempList.size()) {
                        endIndex = preCaseFollowupRecordTempList.size();
                    }
                    List<PreCaseFollowupRecordTemp> preList = new ArrayList<>();
                    preList.addAll(preCaseFollowupRecordTempList.subList(startIndex, endIndex));
                    CompletableFuture<Long> subTaskList=confirmImportRecordSubTask.doSubTask(preList, pageNo);
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
        dataimpBaseService.sendMessage("委前催计确认，跟新案件数量为".concat(String.valueOf(total)),
                operator,record.getImportDataExcelStatus().equals(ImportDataExcelStatus.UN_CONFIRMED) ?
                        record.getOperBatchNumber().concat("委前催计确认失败"):  record.getOperBatchNumber().concat("委前催计确认成功"),
                MessageType.IMPORT_FOLLOW_CONFIRMED, MessageMode.POPUP);
        importOthersDataExcelRecordRepository.save(record);
        logger.info("完成案件委前催计数据确认。。。。。。");
    }
}
