package cn.fintecher.pangolin.service.dataimp.service;

import cn.fintecher.pangolin.common.enums.ImportDataExcelStatus;
import cn.fintecher.pangolin.common.enums.MessageMode;
import cn.fintecher.pangolin.common.enums.MessageType;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.entity.domain.BaseCase;
import cn.fintecher.pangolin.entity.domain.CaseEndImportTemp;
import cn.fintecher.pangolin.entity.domain.CaseLeafImportTemp;
import cn.fintecher.pangolin.entity.domain.ImportOthersDataExcelRecord;
import cn.fintecher.pangolin.service.dataimp.repository.CaseEndImportTempRepository;
import cn.fintecher.pangolin.service.dataimp.repository.CaseLeafImportTempRepository;
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
import java.util.concurrent.CompletableFuture;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

/**
 * @Author:peishouwen
 * @Desc: 案件留停催确认
 * @Date:Create in 16:15 2018/8/7
 */
@Service("confirmImportCaseEndService")
public class ConfirmImportCaseEndService {
    Logger logger= LoggerFactory.getLogger(ConfirmImportCaseEndService.class);

    @Autowired
    CaseEndImportTempRepository caseEndImportTempRepository;

    @Autowired
    ConfirmImportCaseEndSubTask confirmImportCaseEndSubTask;

    @Autowired
    ImportBaseCaseRepository importBaseCaseRepository;

    @Autowired
    ImportOthersDataExcelRecordRepository importOthersDataExcelRecordRepository;

    @Autowired
    DataimpBaseService dataimpBaseService;

    @Async
    public void confirmExcelImpOthersCase(ImportOthersDataExcelRecord record, OperatorModel operator) {
        logger.info("开始案件停催数据确认。。。。。。");
        Long total=0L;
        try{
            StopWatch watch = new StopWatch();
            watch.start();
            // 创建任务集合
            List<CompletableFuture<Long>> taskList = new ArrayList<>();
            int pageSize= 5000;
            int pageCount=0;
            BoolQueryBuilder boolQueryBuilder= QueryBuilders.boolQuery();
            boolQueryBuilder.must(matchPhraseQuery("operBatchNumber.keyword", record.getOperBatchNumber()));
            Iterable<CaseEndImportTemp> caseEndImportTemps=  caseEndImportTempRepository.search(boolQueryBuilder);
            if(caseEndImportTemps.iterator().hasNext()){
                List<CaseEndImportTemp>    caseLeafImportTempArrayList= Lists.newArrayList(caseEndImportTemps);
                if((caseLeafImportTempArrayList.size()%pageSize)>0){
                    pageCount=caseLeafImportTempArrayList.size()/pageSize+1;
                }else {
                    pageCount=caseLeafImportTempArrayList.size()/pageSize;
                }
                for(int pageNo=1;pageNo<=pageCount;pageNo++) {
                    int startIndex = pageSize * (pageNo - 1);
                    int endIndex = pageSize * pageNo;
                    if (endIndex > caseLeafImportTempArrayList.size()) {
                        endIndex = caseLeafImportTempArrayList.size();
                    }
                    List<CaseEndImportTemp> preCaseLeafImportTempArrayList = new ArrayList<>();
                    preCaseLeafImportTempArrayList.addAll(caseLeafImportTempArrayList.subList(startIndex, endIndex));
                    CompletableFuture<Long> subTaskList=confirmImportCaseEndSubTask.doSubTask(preCaseLeafImportTempArrayList, pageNo);
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
        dataimpBaseService.sendMessage("停催数据确认，跟新案件数量为".concat(String.valueOf(total)),
                operator,record.getImportDataExcelStatus().equals(ImportDataExcelStatus.UN_CONFIRMED) ?
                        record.getOperBatchNumber().concat("停催数据确认失败"):  record.getOperBatchNumber().concat("停催数据确认成功"),
                MessageType.IMPORT_END_CONFIRMED, MessageMode.POPUP);
        logger.info("完成案件停催数据确认。。。。。。");
    }
}
