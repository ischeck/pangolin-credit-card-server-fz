package cn.fintecher.pangolin.service.dataimp.service;

import cn.fintecher.pangolin.common.enums.ImportDataExcelStatus;
import cn.fintecher.pangolin.common.enums.MessageMode;
import cn.fintecher.pangolin.common.enums.MessageType;
import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.utils.InnerServiceUrl;
import cn.fintecher.pangolin.common.utils.SaxParseExcelUtil;
import cn.fintecher.pangolin.entity.domain.*;
import cn.fintecher.pangolin.common.annotation.ExcelAnno;
import cn.fintecher.pangolin.entity.managentment.ImportExcelConfig;
import cn.fintecher.pangolin.entity.managentment.ImportExcelConfigItem;
import cn.fintecher.pangolin.service.dataimp.model.request.BaseCaseImportExcelRequest;
import cn.fintecher.pangolin.common.model.CaseInfoPropertyResponse;
import cn.fintecher.pangolin.service.dataimp.repository.*;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 9:48 2018/7/26
 */
@Service("baseCaseImportExcelTempService")
public class BaseCaseImportExcelTempService {
    Logger logger=LoggerFactory.getLogger(BaseCaseImportExcelTempService.class);

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ParseCellToObjTask parseCellToObjTask;

    @Autowired
    ImportDataExcelRecordRepository importDataExcelRecordRepository;

    @Autowired
    BaseCaseImportExcelTempRepository baseCaseImportExcelTempRepository;

    @Autowired
    DataimpBaseService dataimpBaseService;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;



    /**
     * 获取指定类的属性
     * @param objClassList
     * @return
     */
    public List<CaseInfoPropertyResponse> getObjejctPro( List<Class<?>> objClassList){
        List<CaseInfoPropertyResponse> caseInfoPropertyResponseList=new ArrayList<>();
        for(Class<?> objClass : objClassList){
            //获取类中所有的字段
            Field[] fields = objClass.getDeclaredFields();
            for (Field field : fields) {
                //获取标记了ExcelAnno的注解字段
                if (field.isAnnotationPresent(ExcelAnno.class)) {
                    ExcelAnno f = field.getAnnotation(ExcelAnno.class);
                    CaseInfoPropertyResponse caseInfoPropertyResponse=new CaseInfoPropertyResponse();
                    caseInfoPropertyResponse.setAttribute(field.getName());
                    caseInfoPropertyResponse.setName(f.cellName());
                    caseInfoPropertyResponse.setPropertyType(f.fieldType().name());
                    caseInfoPropertyResponseList.add(caseInfoPropertyResponse);
                }
            }
        }

        return caseInfoPropertyResponseList;
    }

    /**
     * 检测批次号是否存在
     * @param baseCaseImportExcelRequest
     * @return
     */
    public boolean checkBatchNumberExist(BaseCaseImportExcelRequest baseCaseImportExcelRequest){
        BoolQueryBuilder queryBuilder= QueryBuilders.boolQuery();
        queryBuilder.must(matchPhraseQuery("batchNumber",baseCaseImportExcelRequest.getBatchNumber()));
        queryBuilder.mustNot(matchPhraseQuery("importDataExcelStatus",ImportDataExcelStatus.IMPORT_CANCEL.name()));
        queryBuilder.mustNot(matchPhraseQuery("importDataExcelStatus",ImportDataExcelStatus.IMPORT_FAILED.name()));
        Iterable<ImportDataExcelRecord> importDataExcelRecords=importDataExcelRecordRepository.search(queryBuilder);
        if(importDataExcelRecords.iterator().hasNext()){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 案件导入
     * @param baseCaseImportExcelRequest
     * @param
     * @throws Exception
     */
    @Async
    public void importExcelData(BaseCaseImportExcelRequest baseCaseImportExcelRequest, OperatorModel operator,
                                ImportExcelConfig importExcelConfig,List<ImportExcelConfigItem> items,
                                InputStream inputStream,ImportDataExcelRecord importDataExcelRecord) throws Exception {
        logger.info("案件导入开始.......");
        StopWatch watch = new StopWatch();
        watch.start();
        // 创建任务集合
        List<CompletableFuture<List<String>>> taskList = new ArrayList<>();
        List<String> resultDataList=new ArrayList<>();
        //数据信息
        Map<Integer,List<Map<String,String>>> dataMap=null;
        long caseTotal=0;
        try {
            StopWatch watch2 = new StopWatch();
            watch2.start();
            logger.info("解析数据文件开始........");
            //数据信息
            dataMap=SaxParseExcelUtil.parseExcel(inputStream,importExcelConfig.getDataStartRow(),importExcelConfig.getDataStartCol(),-1,importExcelConfig.getSheetTotals());
            watch2.stop();
            logger.info("解析数据文件结束，耗时：{}",watch2.getTotalTimeMillis());

            //最多两个sheet页数据第一个sheet为案件基本信息，第二个为客户联系信息
            for(Map.Entry<Integer,List<Map<String,String>>> sheetData:dataMap.entrySet()){
                Integer sheetIndex=sheetData.getKey();
                List<Map<String,String>> sheetDataList=sheetData.getValue();
                //将模板配置转化为MAP
                Map<String ,ImportExcelConfigItem> itemMap=dataimpBaseService.excelConfigItemToMap(items,sheetIndex);
                //分页解析Excel中的数据对象
                int pageSize = 1000;
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
                    CompletableFuture<List<String>>  subTask=parseCellToObjTask.cellDataToObject(perDataListMap,BaseCaseAllImportExcelTemp.class, BasePersonalImportExcelTemp.class,itemMap,
                                baseCaseImportExcelRequest,baseCaseImportExcelRequest.getBatchNumber(),importExcelConfig.getSheetTotals(),pageNo,pageSize,operator,sheetIndex);
                    taskList.add(subTask);
                }
            }
            logger.info("等待子线程执行完成........");
            //收集子线程返回结果
            for(CompletableFuture<List<String>> resultData:taskList){
                    resultDataList.addAll(resultData.get());
            }
            if(resultDataList.isEmpty()){
                //获取导入案件的总数量
                caseTotal=baseCaseImportExcelTempRepository.countByBatchNumberAndPrincipalId(importDataExcelRecord.getBatchNumber(),
                        importDataExcelRecord.getPrincipalId());
                importDataExcelRecord.setImportDataExcelStatus(ImportDataExcelStatus.IMPORT_SUCCESSFULLY);
            }else {
                importDataExcelRecord.setImportDataExcelStatus(ImportDataExcelStatus.IMPORT_FAILED);
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            importDataExcelRecord.setImportDataExcelStatus(ImportDataExcelStatus.IMPORT_FAILED);
            BoolQueryBuilder queryBuilder= QueryBuilders.boolQuery();
            queryBuilder.must().add(matchPhraseQuery("batchNumber.keyword",importDataExcelRecord.getBatchNumber()));
            DeleteByQueryRequestBuilder deleteByQueryRequestBuilder= DeleteByQueryAction.INSTANCE.
                    newRequestBuilder(elasticsearchTemplate.getClient());
            deleteByQueryRequestBuilder.source("base_case_all_import_excel_temp").filter(queryBuilder).refresh(true).execute();
            deleteByQueryRequestBuilder.source("base_personal_import_excel_temp").filter(queryBuilder).refresh(true).execute();
        }finally {
            if(Objects.nonNull(inputStream)){
                inputStream.close();
            }
            if(Objects.nonNull(dataMap)){
                dataMap.clear();
            }
            if(!resultDataList.isEmpty()){
                importDataExcelRecord.setResultUrl(dataimpBaseService.createImpErrorFile(resultDataList));
            }
            importDataExcelRecord.setCaseTotal(caseTotal);
            importDataExcelRecordRepository.save(importDataExcelRecord);
        }
        watch.stop();
        logger.info("案件导入结束，耗时{}",watch.getTotalTimeMillis());
        dataimpBaseService.sendMessage("案件导入",
                operator,importDataExcelRecord.getImportDataExcelStatus().equals(ImportDataExcelStatus.IMPORT_FAILED) ?
                        importDataExcelRecord.getBatchNumber().concat("导入失败"): importDataExcelRecord.getBatchNumber().concat("导入成功"),MessageType.IMPORT_EXCEL_MSG, MessageMode.POPUP);
    }

    public InputStream readFile(String fileId){
        //获取文件
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<byte[]> response = restTemplate.exchange(InnerServiceUrl.COMMON_SERVICE_GETFILEBYID.concat("/").concat(fileId),
                HttpMethod.GET, new HttpEntity<byte[]>(headers), byte[].class);
        List<String> content=response.getHeaders().get("Content-Disposition");
        if(Objects.isNull(content) || content.isEmpty()){
            throw new BadRequestException(null, "importExcelData","templateFile.is.illegal");
        }
        if(!content.get(0).endsWith(SaxParseExcelUtil.EXCEL_TYPE_XLSX)){
            logger.error("fileName: {} ",content.get(0));
            throw   new BadRequestException(null, "importExcelData","file.format.error");
        }
        byte[] result = response.getBody();
        InputStream inputStream=new ByteArrayInputStream(result);
        return inputStream;
    }
}
