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
import org.elasticsearch.search.aggregations.support.ValuesSourceAggregationBuilder;
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
 * @Desc: 案件更新确认
 * @Date:Create in 16:15 2018/8/7
 */
@Service("confirmImportWarnInfoService")
public class ConfirmImportWarnInfoService {
    Logger logger= LoggerFactory.getLogger(ConfirmImportWarnInfoService.class);

    @Autowired
    CaseWarnImportTempRepository caseWarnImportTempRepository;

    @Autowired
    ConfirmExcelImpWarnInfoSubTask confirmExcelImpWarnInfoSubTask;



    @Autowired
    ImportOthersDataExcelRecordRepository importOthersDataExcelRecordRepository;

    @Autowired
    DataimpBaseService dataimpBaseService;

    @Async
    public void confirmExcelImpOthersCase(ImportOthersDataExcelRecord record, OperatorModel operator) {
        logger.info("开始案件警告数据确认。。。。。。");
        Long total=0L;
        try {
            StopWatch watch = new StopWatch();
            watch.start();
            // 创建任务集合
            List<CompletableFuture<Long>> taskList = new ArrayList<>();
            //分页确认
            int pageSize3 = 5000;
            int pageCount3=0;
            BoolQueryBuilder boolQueryBuilder= QueryBuilders.boolQuery();
            boolQueryBuilder.must(matchPhraseQuery("operBatchNumber.keyword", record.getOperBatchNumber()));
            Iterable<CaseWarnImportTemp> caseWarnImportTemps=  caseWarnImportTempRepository.search(boolQueryBuilder);
            if(caseWarnImportTemps.iterator().hasNext()){
                List<CaseWarnImportTemp>  caseWarnImportTempList= Lists.newArrayList(caseWarnImportTemps);
                if((caseWarnImportTempList.size()%pageSize3)>0){
                    pageCount3=caseWarnImportTempList.size()/pageSize3+1;
                }else {
                    pageCount3=caseWarnImportTempList.size()/pageSize3;
                }
                for(int pageNo=1;pageNo<=pageCount3;pageNo++) {
                    int startIndex = pageSize3 * (pageNo - 1);
                    int endIndex = pageSize3 * pageNo;
                    if (endIndex > caseWarnImportTempList.size()) {
                        endIndex = caseWarnImportTempList.size();
                    }
                    List<CaseWarnImportTemp> percaseCaseWarningInfoList = new ArrayList<>();
                    percaseCaseWarningInfoList.addAll(caseWarnImportTempList.subList(startIndex, endIndex));
                    CompletableFuture<Long> subTaskList=confirmExcelImpWarnInfoSubTask.doSubTask(percaseCaseWarningInfoList, pageNo);
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
        dataimpBaseService.sendMessage("警告信息确认，确认数量为".concat(String.valueOf(total)),
                operator,record.getImportDataExcelStatus().equals(ImportDataExcelStatus.UN_CONFIRMED) ?
                        record.getOperBatchNumber().concat("警告信息确认失败"):  record.getOperBatchNumber().concat("警告信息确认成功"),
                MessageType.IMPORT_WARNING_CONFIRMED, MessageMode.POPUP);
        logger.info("完成案件警告数据确认。。。。。。");
    }
}
