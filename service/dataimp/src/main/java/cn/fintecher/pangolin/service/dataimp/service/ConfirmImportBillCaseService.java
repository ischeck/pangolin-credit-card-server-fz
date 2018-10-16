package cn.fintecher.pangolin.service.dataimp.service;

import cn.fintecher.pangolin.common.enums.ImportDataExcelStatus;
import cn.fintecher.pangolin.common.enums.MessageMode;
import cn.fintecher.pangolin.common.enums.MessageType;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.entity.domain.*;
import cn.fintecher.pangolin.service.dataimp.repository.*;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.list.PredicatedList;
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

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

/**
 * @Author:peishouwen
 * @Desc: 案件对账确认
 * @Date:Create in 16:15 2018/8/7
 */
@Service("confirmImportBillCaseService")
public class ConfirmImportBillCaseService {
    Logger logger= LoggerFactory.getLogger(ConfirmImportBillCaseService.class);

    @Autowired
    CaseBillImportTempRepository caseBillImportTempRepository;

    @Autowired
    ConfirmExcelImpOthersBillSubTask confirmExcelImpOthersBillSubTask;

    @Autowired
    ImportBaseCaseRepository importBaseCaseRepository;

    @Autowired
    ImportOthersDataExcelRecordRepository importOthersDataExcelRecordRepository;
    @Autowired
    DataimpBaseService dataimpBaseService;


    @Async
    public void confirmExcelImpOthersCase(ImportOthersDataExcelRecord record, OperatorModel operator) {
        logger.info("开始案件对账数据确认。。。。。。");
        Long total=0L;
        try {
            StopWatch watch = new StopWatch();
            watch.start();
            // 创建任务集合
            List<CompletableFuture<Long>> taskList = new ArrayList<>();
            //分页确认
            int pageSize= 1000;
            int pageCount=0;
            BoolQueryBuilder boolQueryBuilder= QueryBuilders.boolQuery();
            boolQueryBuilder.must(matchPhraseQuery("operBatchNumber.keyword", record.getOperBatchNumber()));
            Iterable<CaseBillImportTemp> caseBillImportTemps=  caseBillImportTempRepository.search(boolQueryBuilder);
            if(caseBillImportTemps.iterator().hasNext()){
                List<CaseBillImportTemp>  caseBillImportTempList= Lists.newArrayList(caseBillImportTemps);
                if((caseBillImportTempList.size()%pageSize)>0){
                    pageCount=caseBillImportTempList.size()/pageSize+1;
                }else {
                    pageCount=caseBillImportTempList.size()/pageSize;
                }
                for(int pageNo=1;pageNo<=pageCount;pageNo++) {
                    int startIndex = pageSize * (pageNo - 1);
                    int endIndex = pageSize * pageNo;
                    if (endIndex > caseBillImportTempList.size()) {
                        endIndex = caseBillImportTempList.size();
                    }
                    List<CaseBillImportTemp> percaseBillImportTempList = new ArrayList<>();
                    percaseBillImportTempList.addAll(caseBillImportTempList.subList(startIndex, endIndex));
                    CompletableFuture<Long> subTaskList=confirmExcelImpOthersBillSubTask.doSubTask(percaseBillImportTempList, pageNo,operator);
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
        dataimpBaseService.sendMessage("案件对账确认，跟新案件数量为".concat(String.valueOf(total)),
                operator,record.getImportDataExcelStatus().equals(ImportDataExcelStatus.UN_CONFIRMED) ?
                        record.getOperBatchNumber().concat("案件对账确认失败"):  record.getOperBatchNumber().concat("案件对账确认成功"),
                MessageType.IMPORT_BILL_CONFIRMED, MessageMode.POPUP);
        logger.info("完成案件对账数据确认。。。。。。");
    }
}
