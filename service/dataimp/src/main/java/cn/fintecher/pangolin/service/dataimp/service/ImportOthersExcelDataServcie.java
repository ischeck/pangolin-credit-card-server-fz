package cn.fintecher.pangolin.service.dataimp.service;

import cn.fintecher.pangolin.common.enums.ImportDataExcelStatus;
import cn.fintecher.pangolin.common.enums.MessageMode;
import cn.fintecher.pangolin.common.enums.MessageType;
import cn.fintecher.pangolin.common.enums.TemplateType;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.model.UploadFile;
import cn.fintecher.pangolin.common.utils.InnerServiceUrl;
import cn.fintecher.pangolin.common.utils.SaxParseExcelUtil;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.domain.ImportOthersDataExcelRecord;
import cn.fintecher.pangolin.entity.managentment.ImportExcelConfig;
import cn.fintecher.pangolin.entity.managentment.ImportExcelConfigItem;
import cn.fintecher.pangolin.service.dataimp.repository.*;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * @Author:peishouwen
 * @Desc: 导入案件其他相关信息服务
 * @Date:Create in 13:36 2018/8/2
 */
@Service("importOthersExcelDataServcie")
public class ImportOthersExcelDataServcie {
    Logger logger= LoggerFactory.getLogger(ImportOthersExcelDataServcie.class);

    @Autowired
    DataimpBaseService dataimpBaseService;

    @Autowired
    ParseCellToCaseUpdateTask parseCellToCaseUpdateTask;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ParseCellToCaseBillTask parseCellToCaseBillTask;

    @Autowired
    ParseCellToCaseWarnTask parseCellToCaseWarnTask;

    @Autowired
    ParseCellToCaseLeafTask parseCellToCaseLeafTask;

    @Autowired
    ParseCellToCaseChangeCityTask parseCellToCaseChangeCityTask;

    @Autowired
    ParseCellToCaseEndTask parseCellToCaseEndTask;

    @Autowired
    ImportOthersDataExcelRecordRepository importOthersDataExcelRecordRepository;


    @Autowired
    ParseCellToCaseWorkOrderTask parseCellToCaseWorkOrderTask;

    @Autowired
    ParseCellToPreRecordTask parseCellToPreRecordTask;


    @Async
    public void importOthersExcelData(ImportOthersDataExcelRecord record, OperatorModel operator,ImportExcelConfig importExcelConfig, InputStream inputStream){
        logger.info("案件其他数据导入开始.......");
        TemplateType templateType=record.getTemplateType();
        StopWatch watch = new StopWatch();
        watch.start();
        // 创建任务集合
        List<CompletableFuture<List<String>>> taskList = new ArrayList<>();
        List<String> resultDataList=new ArrayList<>();
        //数据信息
        Map<Integer,List<Map<String,String>>> dataMap=null;
        long caseTotal=0;
        try {
            logger.info("解析数据文件开始........");
            //数据信息
            dataMap= SaxParseExcelUtil.parseExcel(inputStream,importExcelConfig.getDataStartRow(),importExcelConfig.getDataStartCol(),-1,importExcelConfig.getSheetTotals());
            //都是单个sheet页数据
            for(Map.Entry<Integer,List<Map<String,String>>> sheetData:dataMap.entrySet()) {
                Integer sheetIndex = sheetData.getKey();
                List<Map<String, String>> sheetDataList = sheetData.getValue();
                caseTotal = sheetDataList.size() + caseTotal;
                //将模板配置转化为MAP
                Map<String, ImportExcelConfigItem> itemMap = dataimpBaseService.excelConfigItemToMap(importExcelConfig.getItems(), sheetIndex);
                //分页解析Excel中的数据对象
                int pageSize = 5000;
                int pageCount=0;
                if((sheetDataList.size()%pageSize)>0){
                    pageCount=sheetDataList.size()/pageSize+1;
                }else {
                    pageCount=sheetDataList.size()/pageSize;
                }
                for(int pageNo=1;pageNo<=pageCount;pageNo++) {
                    int startIndex = pageSize * (pageNo - 1);
                    int endIndex = pageSize * pageNo;
                    if (endIndex > sheetDataList.size()) {
                        endIndex = sheetDataList.size();
                    }
                    List<Map<String,String>> perDataListMap=new ArrayList<>();
                    perDataListMap.addAll( sheetDataList.subList(startIndex,endIndex));
                    switch (templateType){
                        case IMPORT_UPDATE_CASE:
                            CompletableFuture<List<String>>  subTask1=parseCellToCaseUpdateTask.cellDataToObject(perDataListMap,record,itemMap,pageNo,pageSize,operator,sheetIndex);
                            taskList.add(subTask1);
                            break;
                        case IMPORT_WARNING_INFO:
                            CompletableFuture<List<String>>  subTask2=parseCellToCaseWarnTask.cellDataToObject(perDataListMap,record,itemMap,pageNo,pageSize,operator,sheetIndex);
                            taskList.add(subTask2);
                            break;
                        case IMPORT_WORKER_ORDER:
                            CompletableFuture<List<String>>  subTask3= parseCellToCaseWorkOrderTask.cellDataToObject(perDataListMap,record,itemMap,pageNo,pageSize,operator,sheetIndex);
                            taskList.add(subTask3);
                            break;
                        case  IMPORT_BILL:
                            CompletableFuture<List<String>>  subTask4= parseCellToCaseBillTask.cellDataToObject(perDataListMap,record,itemMap,pageNo,pageSize,operator,sheetIndex);
                            taskList.add(subTask4);
                            break;
                        case  IMPORT_LEFT_CASE:
                            CompletableFuture<List<String>>  subTask5=parseCellToCaseLeafTask.cellDataToObject(perDataListMap,record,itemMap,pageNo,pageSize,operator,sheetIndex);
                            taskList.add(subTask5);
                            break;
                        case IMPORT_CHANGE_CITY:
                            CompletableFuture<List<String>>  subTask6=parseCellToCaseChangeCityTask.cellDataToObject(perDataListMap,record,itemMap,pageNo,pageSize,operator,sheetIndex);
                            taskList.add(subTask6);
                            break;
                        case IMPORT_FOLLOW_RECORD:
                            CompletableFuture<List<String>>  subTask7=parseCellToPreRecordTask.cellDataToObject(perDataListMap,record,itemMap,pageNo,pageSize,operator,sheetIndex);
                            taskList.add(subTask7);
                            break;
                        case IMPORT_END_CASE:
                            CompletableFuture<List<String>>  subTask8=parseCellToCaseEndTask.cellDataToObject(perDataListMap,record,itemMap,pageNo,pageSize,operator,sheetIndex);
                            taskList.add(subTask8);
                            break;
                        default:
                            logger.error("模板类型不合法");
                    }
                }
            }
            logger.info("等待子线程执行完成........");
            //收集子线程返回结果
            for(CompletableFuture<List<String>> resultData:taskList){
                resultDataList.addAll(resultData.get());
            }
            if(resultDataList.isEmpty()){
                record.setImportDataExcelStatus(ImportDataExcelStatus.IMPORT_SUCCESSFULLY);
            }else {
                record.setImportDataExcelStatus(ImportDataExcelStatus.IMPORT_FAILED);
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            record.setImportDataExcelStatus(ImportDataExcelStatus.IMPORT_FAILED);
        }finally {
            if(!Objects.nonNull(inputStream)){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            createErrorLogs(record, resultDataList);
            record.setCaseTotal(caseTotal);
            importOthersDataExcelRecordRepository.save(record);
            watch.stop();
            logger.info("案件其他数据导入结束，耗时 {}",watch.getTotalTimeMillis());
            sendMsg(record,operator);
        }
    }

    private void  sendMsg(ImportOthersDataExcelRecord record, OperatorModel operator){
        TemplateType templateType=record.getTemplateType();
        switch (templateType){
            case IMPORT_UPDATE_CASE:
                dataimpBaseService.sendMessage("案件跟新导入",
                        operator,record.getImportDataExcelStatus().equals(ImportDataExcelStatus.IMPORT_FAILED) ?
                                record.getOperBatchNumber().concat("案件跟新导入失败"): record.getOperBatchNumber().concat("案件跟新导入成功"),
                        MessageType.IMPORT_UPDATE_CASE, MessageMode.POPUP);
                break;
            case IMPORT_WARNING_INFO:
                dataimpBaseService.sendMessage("警告信息导入",
                        operator,record.getImportDataExcelStatus().equals(ImportDataExcelStatus.IMPORT_FAILED) ?
                                record.getOperBatchNumber().concat("警告信息导入失败"): record.getOperBatchNumber().concat("警告信息导入成功"),
                        MessageType.IMPORT_WARNING_INFO, MessageMode.POPUP);
                break;
            case IMPORT_WORKER_ORDER:
                dataimpBaseService.sendMessage("工单信息导入",
                        operator,record.getImportDataExcelStatus().equals(ImportDataExcelStatus.IMPORT_FAILED) ?
                                record.getOperBatchNumber().concat("工单信息导入失败"): record.getOperBatchNumber().concat("工单信息导入成功"),
                        MessageType.IMPORT_WORKER_ORDER, MessageMode.POPUP);
                break;
            case  IMPORT_BILL:
                dataimpBaseService.sendMessage("对账单导入",
                        operator,record.getImportDataExcelStatus().equals(ImportDataExcelStatus.IMPORT_FAILED) ?
                                record.getOperBatchNumber().concat("对账单导入失败"): record.getOperBatchNumber().concat("对账单导入成功"),
                        MessageType.IMPORT_BILL, MessageMode.POPUP);
                break;
            case  IMPORT_LEFT_CASE:
                dataimpBaseService.sendMessage("留案导入",
                        operator,record.getImportDataExcelStatus().equals(ImportDataExcelStatus.IMPORT_FAILED) ?
                                record.getOperBatchNumber().concat("留案导入失败"): record.getOperBatchNumber().concat("留案导入成功"),
                        MessageType.IMPORT_LEFT_CASE, MessageMode.POPUP);
                break;
            case IMPORT_CHANGE_CITY:
                dataimpBaseService.sendMessage("城市调整导入",
                        operator,record.getImportDataExcelStatus().equals(ImportDataExcelStatus.IMPORT_FAILED) ?
                                record.getOperBatchNumber().concat("城市调整导入失败"): record.getOperBatchNumber().concat("城市调整导入成功"),
                        MessageType.IMPORT_CHANGE_CITY, MessageMode.POPUP);
                break;
            case IMPORT_FOLLOW_RECORD:
                dataimpBaseService.sendMessage("委前催计导入",
                        operator,record.getImportDataExcelStatus().equals(ImportDataExcelStatus.IMPORT_FAILED) ?
                                record.getOperBatchNumber().concat("委前催计导入失败"): record.getOperBatchNumber().concat("委前催计导入成功"),
                        MessageType.IMPORT_FOLLOW_RECORD, MessageMode.POPUP);
                break;
            case IMPORT_END_CASE:
                dataimpBaseService.sendMessage("停催导入",
                        operator,record.getImportDataExcelStatus().equals(ImportDataExcelStatus.IMPORT_FAILED) ?
                                record.getOperBatchNumber().concat("停催导入失败"): record.getOperBatchNumber().concat("停催导入成功"),
                        MessageType.IMPORT_END_CASE, MessageMode.POPUP);
                break;
            default:
                break;
        }
    }

    /**
     * 生成错误日志
     * @param record
     * @param resultDataList
     */
    private void createErrorLogs(ImportOthersDataExcelRecord record, List<String> resultDataList) {
        if(!resultDataList.isEmpty()){
            //生成错误信息txt
            String filePath = FileUtils.getTempDirectoryPath().concat(File.separator).concat(
                    ZWDateUtil.fomratterDate(ZWDateUtil.getNowDateTime(),"yyyyMMddHHmmss")).concat("数据导入错误信息.txt");
            File file=new File(filePath);
            if(file.exists()){
                file.delete();
            }else {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            BufferedWriter out = null;
            try {
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            for(String str:resultDataList){
                try {
                    out.write(str);
                    out.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //开始文件上传
            //文件上传
            FileSystemResource resource = new FileSystemResource(filePath);
            MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
            param.add("file", resource);
            ResponseEntity<UploadFile> response= restTemplate.postForEntity(InnerServiceUrl.COMMON_SERVICE_UPLOADFILE, param, UploadFile.class);
            record.setResultUrl(response.getBody().getUrl());
        }
    }
}
