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
 * @Desc: 案件工单确认
 * @Date:Create in 16:15 2018/8/7
 */
@Service("confirmImportWorkOrderService")
public class ConfirmImportWorkOrderService {
    Logger logger= LoggerFactory.getLogger(ConfirmImportWorkOrderService.class);

    @Autowired
    CaseWorkOrderImportTempRepository caseWorkOrderImportTempRepository;

    @Autowired
    ConfirmExcelImpWorkOrderSubTask confirmExcelImpWorkOrderSubTask;

    @Autowired
    ImportOthersDataExcelRecordRepository importOthersDataExcelRecordRepository;

    @Autowired
    DataimpBaseService dataimpBaseService;

    @Async
    public void confirmExcelImpOthersCase(ImportOthersDataExcelRecord record, OperatorModel operator) {
        logger.info("开始案件工单数据确认。。。。。。");
        Long total=0L;
        try {
            StopWatch watch = new StopWatch();
            watch.start();
            // 创建任务集合
            List<CompletableFuture<Long>> taskList = new ArrayList<>();
            List<CaseWorkOrderInfo> resultData=new ArrayList<>();
            int pageSize4= 5000;
            int pageCount4=0;
            BoolQueryBuilder boolQueryBuilder= QueryBuilders.boolQuery();
            boolQueryBuilder.must(matchPhraseQuery("operBatchNumber.keyword", record.getOperBatchNumber()));
            Iterable<CaseWorkImportOrderInfoTemp> caseWorkImportOrderInfoTemps=  caseWorkOrderImportTempRepository.search(boolQueryBuilder);
            if(caseWorkImportOrderInfoTemps.iterator().hasNext()){
                List<CaseWorkImportOrderInfoTemp>  caseWorkImportOrderInfoTempList= Lists.newArrayList(caseWorkImportOrderInfoTemps);
                if((caseWorkImportOrderInfoTempList.size()%pageSize4)>0){
                    pageCount4=caseWorkImportOrderInfoTempList.size()/pageSize4+1;
                }else {
                    pageCount4=caseWorkImportOrderInfoTempList.size()/pageSize4;
                }
                for(int pageNo=1;pageNo<=pageCount4;pageNo++) {
                    int startIndex = pageSize4 * (pageNo - 1);
                    int endIndex = pageSize4 * pageNo;
                    if (endIndex > caseWorkImportOrderInfoTempList.size()) {
                        endIndex = caseWorkImportOrderInfoTempList.size();
                    }
                    List<CaseWorkImportOrderInfoTemp> perCaseWorkOrderList = new ArrayList<>();
                    perCaseWorkOrderList.addAll(caseWorkImportOrderInfoTempList.subList(startIndex, endIndex));
                    CompletableFuture<Long> subTaskList=confirmExcelImpWorkOrderSubTask.doSubTask(perCaseWorkOrderList, pageNo);
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
        dataimpBaseService.sendMessage("工单信息确认，案件数量为".concat(String.valueOf(total)),
                operator,record.getImportDataExcelStatus().equals(ImportDataExcelStatus.UN_CONFIRMED) ?
                        record.getOperBatchNumber().concat("工单信息确认失败"):  record.getOperBatchNumber().concat("工单信息确认成功"),
                MessageType.IMPORT_WORKER_CONFIRMED, MessageMode.POPUP);
        logger.info("完成案件工单数据确认。。。。。。");
    }
}
