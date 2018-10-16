package cn.fintecher.pangolin.service.dataimp.web;

import cn.fintecher.pangolin.common.enums.ImportDataExcelStatus;
import cn.fintecher.pangolin.common.enums.TemplateType;
import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.utils.InnerServiceUrl;
import cn.fintecher.pangolin.common.utils.SaxParseExcelUtil;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.domain.*;
import cn.fintecher.pangolin.entity.managentment.ImportExcelConfig;
import cn.fintecher.pangolin.service.dataimp.model.request.*;
import cn.fintecher.pangolin.service.dataimp.repository.*;
import cn.fintecher.pangolin.service.dataimp.service.*;
import io.swagger.annotations.*;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 23:47 2018/8/1
 */
@RestController
@RequestMapping("/api/importOthersDataExcelRecordController")
@Api(value = "其他数据导入记录", description = "其他数据导入记录")
public class ImportOthersDataExcelRecordController {

    Logger logger= LoggerFactory.getLogger(ImportOthersDataExcelRecordController.class);

    @Autowired
    DataimpBaseService dataimpBaseService;

    @Autowired
    ImportOthersDataExcelRecordRepository importOthersDataExcelRecordRepository;

    @Autowired
    CaseUpdateImportTempRepostory caseUpdateImportTempRepostory;

    @Autowired
    CaseBillImportTempRepository caseBillImportTempRepository;

    @Autowired
    CaseChangeCityImportTempRepository caseChangeCityImportTempRepository;

    @Autowired
    CaseEndImportTempRepository caseEndImportTempRepository;

    @Autowired
    CaseLeafImportTempRepository caseLeafImportTempRepository;

    @Autowired
    CaseWarnImportTempRepository caseWarnImportTempRepository;

    @Autowired
    BatchNumberSeqService batchNumberSeqService;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ImportOthersExcelDataServcie importOthersExcelDataServcie;

    @Autowired
    CaseWorkOrderImportTempRepository caseWorkOrderImportTempRepository;

    @Autowired
    PreCaseFollowupRecordTempRepository preCaseFollowupRecordTempRepository;

    @Autowired
    ConfirmImportUpdateCaseService confirmImportUpdateCaseService;

    @Autowired
    ConfirmImportBillCaseService confirmImportBillCaseService;

    @Autowired
    ConfirmImportWarnInfoService confirmImportWarnInfoService;

    @Autowired
    ConfirmImportWorkOrderService confirmImportWorkOrderService;

    @Autowired
    ConfirmImportCaseLeafService confirmImportCaseLeafService;

    @Autowired
    ConfirmImportChangeCityService confirmImportChangeCityService;

    @Autowired
    ConfirmImportRecordService confirmImportRecordService;

    @Autowired
    ConfirmImportCaseEndService confirmImportCaseEndService;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    @GetMapping("/getImportOthersDataExcelList")
    @ApiOperation(value = "查询导入结果", notes = "查询导入结果")
    public ResponseEntity<Page<ImportOthersDataExcelRecord>> getImportOthersDataExcelList(Pageable pageable, ImportOthersDataExcelRecordSearchRequest importOthersDataExcelRecordSearchRequest,
                                                                                          @RequestHeader(value = "X-UserToken") String token){
        try {
            OperatorModel operatorModel= dataimpBaseService.getUserByToken(token);
            BoolQueryBuilder queryBuilder=(BoolQueryBuilder) importOthersDataExcelRecordSearchRequest.generateQueryBuilder();
            queryBuilder.must(matchPhraseQuery("operatorUserName",operatorModel.getUsername()));
            SearchQuery searchQuery = new NativeSearchQueryBuilder()
                    .withPageable(pageable)
                    .withQuery(queryBuilder)
                    .build();
            logger.debug("search Activity : {} query :{}", importOthersDataExcelRecordSearchRequest.toString(), searchQuery.getQuery().toString());

            Page<ImportOthersDataExcelRecord> page= importOthersDataExcelRecordRepository.search(queryBuilder,pageable);
            return ResponseEntity.ok().body(page);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            throw  new BadRequestException(null,"getImportOthersDataExcelList", Objects.nonNull(e.getMessage()) ? e.getMessage() : "getImportOthersDataExcelList.is.fail");
        }
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    @GetMapping("/getImportUpdateDataList")
    @ApiOperation(value = "案件更新导入详情", notes = "案件更新导入详情")
    public ResponseEntity<Page<CaseUpdateImportTemp>> getImportUpdateDataList(Pageable pageable, CaseUpdateImportTempRequest request,
                                                                              @RequestHeader(value = "X-UserToken") String token){
        try {
            OperatorModel operatorModel= dataimpBaseService.getUserByToken(token);
            BoolQueryBuilder queryBuilder= (BoolQueryBuilder) request.generateQueryBuilder();
            queryBuilder.must(matchPhraseQuery("operator",operatorModel.getUsername()));
            SearchQuery searchQuery = new NativeSearchQueryBuilder()
                    .withPageable(pageable)
                    .withQuery(queryBuilder).build();
            logger.debug("search Activity : {} query :{}", request.toString(), searchQuery.getQuery().toString());
            Page<CaseUpdateImportTemp> page= caseUpdateImportTempRepostory.search(searchQuery);
            return ResponseEntity.ok().body(page);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            throw  new BadRequestException(null,"getImportUpdateDataList", Objects.nonNull(e.getMessage()) ? e.getMessage() : "getImportUpdateDataList.is.fail");
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    @GetMapping("/getImportBillDataList")
    @ApiOperation(value = "案件对账导入详情", notes = "案件对账导入详情")
    public ResponseEntity<Page<CaseBillImportTemp>> getImportBillDataList(Pageable pageable, CaseBillImportTempRequest request,
                                                                              @RequestHeader(value = "X-UserToken") String token){
        try {
            OperatorModel operatorModel= dataimpBaseService.getUserByToken(token);
            BoolQueryBuilder queryBuilder= (BoolQueryBuilder) request.generateQueryBuilder();
            queryBuilder.must(matchPhraseQuery("operator",operatorModel.getUsername()));
            SearchQuery searchQuery = new NativeSearchQueryBuilder()
                    .withPageable(pageable)
                    .withQuery(queryBuilder).build();
            logger.debug("search Activity : {} query :{}", request.toString(), searchQuery.getQuery().toString());
            Page<CaseBillImportTemp> page= caseBillImportTempRepository.search(searchQuery);
            return ResponseEntity.ok().body(page);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            throw  new BadRequestException(null,"getImportBillDataList", Objects.nonNull(e.getMessage()) ? e.getMessage() : "getImportBillDataList.is.fail");
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    @GetMapping("/getImportChangeCityList")
    @ApiOperation(value = "案件对账导入详情", notes = "案件对账导入详情")
    public ResponseEntity<Page<CaseChangeCityImportTemp>> getImportChangeCityList(Pageable pageable, CaseChangeCityImportTempRequest request,
                                                                          @RequestHeader(value = "X-UserToken") String token){
        try {
            OperatorModel operatorModel= dataimpBaseService.getUserByToken(token);
            BoolQueryBuilder queryBuilder= (BoolQueryBuilder) request.generateQueryBuilder();
            queryBuilder.must(matchPhraseQuery("operator",operatorModel.getUsername()));
            SearchQuery searchQuery = new NativeSearchQueryBuilder()
                    .withPageable(pageable)
                    .withQuery(queryBuilder).build();
            logger.debug("search Activity : {} query :{}", request.toString(), searchQuery.getQuery().toString());
            Page<CaseChangeCityImportTemp> page= caseChangeCityImportTempRepository.search(searchQuery);
            return ResponseEntity.ok().body(page);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            throw  new BadRequestException(null,"getImportChangeCityList", Objects.nonNull(e.getMessage()) ? e.getMessage() : "getImportChangeCityList.is.fail");
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    @GetMapping("/getImportEndList")
    @ApiOperation(value = "案件停催导入详情", notes = "案件停催导入详情")
    public ResponseEntity<Page<CaseEndImportTemp>> getImportEndList(Pageable pageable, CaseEndImportTempRequest request,
                                                                                  @RequestHeader(value = "X-UserToken") String token){
        try {
            OperatorModel operatorModel= dataimpBaseService.getUserByToken(token);
            BoolQueryBuilder queryBuilder= (BoolQueryBuilder) request.generateQueryBuilder();
            queryBuilder.must(matchPhraseQuery("operator",operatorModel.getUsername()));
            SearchQuery searchQuery = new NativeSearchQueryBuilder()
                    .withPageable(pageable)
                    .withQuery(queryBuilder).build();
            logger.debug("search Activity : {} query :{}", request.toString(), searchQuery.getQuery().toString());
            Page<CaseEndImportTemp> page= caseEndImportTempRepository.search(searchQuery);
            return ResponseEntity.ok().body(page);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            throw  new BadRequestException(null,"getImportEndList", Objects.nonNull(e.getMessage()) ? e.getMessage() : "getImportEndList.is.fail");
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    @GetMapping("/getImportLeafList")
    @ApiOperation(value = "案件留案导入详情", notes = "案件留案导入详情")
    public ResponseEntity<Page<CaseLeafImportTemp>> getImportLeafList(Pageable pageable, CaseLeafImportTempRequest request,
                                                                           @RequestHeader(value = "X-UserToken") String token){
        try {
            OperatorModel operatorModel= dataimpBaseService.getUserByToken(token);
            BoolQueryBuilder queryBuilder= (BoolQueryBuilder) request.generateQueryBuilder();
            queryBuilder.must(matchPhraseQuery("operator",operatorModel.getUsername()));
            SearchQuery searchQuery = new NativeSearchQueryBuilder()
                    .withPageable(pageable)
                    .withQuery(queryBuilder).build();
            logger.debug("search Activity : {} query :{}", request.toString(), searchQuery.getQuery().toString());
            Page<CaseLeafImportTemp> page= caseLeafImportTempRepository.search(searchQuery);
            return ResponseEntity.ok().body(page);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            throw  new BadRequestException(null,"getImportLeafList", Objects.nonNull(e.getMessage()) ? e.getMessage() : "getImportLeafList.is.fail");
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    @GetMapping("/getImportPreRecordList")
    @ApiOperation(value = "委前催计", notes = "委前催计")
    public ResponseEntity<Page<PreCaseFollowupRecordTemp>> getImportPreRecordList(Pageable pageable, PreCaseFollowupRecordTempRequest request,
                                                                      @RequestHeader(value = "X-UserToken") String token){
        try {
            OperatorModel operatorModel= dataimpBaseService.getUserByToken(token);
            BoolQueryBuilder queryBuilder= (BoolQueryBuilder) request.generateQueryBuilder();
            queryBuilder.must(matchPhraseQuery("operator",operatorModel.getUsername()));
            SearchQuery searchQuery = new NativeSearchQueryBuilder()
                    .withPageable(pageable)
                    .withQuery(queryBuilder).build();
            logger.debug("search Activity : {} query :{}", request.toString(), searchQuery.getQuery().toString());
            Page<PreCaseFollowupRecordTemp> page= preCaseFollowupRecordTempRepository.search(searchQuery);
            return ResponseEntity.ok().body(page);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            throw  new BadRequestException(null,"getImportPreRecordList", Objects.nonNull(e.getMessage()) ? e.getMessage() : "getImportPreRecordList.is.fail");
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    @GetMapping("/getImportWarnList")
    @ApiOperation(value = "案件警告信息导入详情", notes = "案件警告信息导入详情")
    public ResponseEntity<Page<CaseWarnImportTemp>> getImportWarnList(Pageable pageable, CaseWarnImportTempRequest request,
                                                                      @RequestHeader(value = "X-UserToken") String token){
        try {
            OperatorModel operatorModel= dataimpBaseService.getUserByToken(token);
            BoolQueryBuilder queryBuilder= (BoolQueryBuilder) request.generateQueryBuilder();
            queryBuilder.must(matchPhraseQuery("operator",operatorModel.getUsername()));
            SearchQuery searchQuery = new NativeSearchQueryBuilder()
                    .withPageable(pageable)
                    .withQuery(queryBuilder).build();
            logger.debug("search Activity : {} query :{}", request.toString(), searchQuery.getQuery().toString());
            Page<CaseWarnImportTemp> page= caseWarnImportTempRepository.search(searchQuery);
            return ResponseEntity.ok().body(page);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            throw  new BadRequestException(null,"getImportWarnList", Objects.nonNull(e.getMessage()) ? e.getMessage() : "getImportWarnList.is.fail");
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    @GetMapping("/getImportWorkOrderList")
    @ApiOperation(value = "案件工单信息导入详情", notes = "案件工单信息导入详情")
    public ResponseEntity<Page<CaseWorkImportOrderInfoTemp>> getImportWorkOrderList(Pageable pageable, CaseWorkImportOrderInfoTempRequest request,
                                                                      @RequestHeader(value = "X-UserToken") String token){
        try {
            OperatorModel operatorModel= dataimpBaseService.getUserByToken(token);
            BoolQueryBuilder queryBuilder= (BoolQueryBuilder) request.generateQueryBuilder();
            queryBuilder.must(matchPhraseQuery("operator",operatorModel.getUsername()));
            SearchQuery searchQuery = new NativeSearchQueryBuilder()
                    .withPageable(pageable)
                    .withQuery(queryBuilder).build();
            logger.debug("search Activity : {} query :{}", request.toString(), searchQuery.getQuery().toString());
            Page<CaseWorkImportOrderInfoTemp> page= caseWorkOrderImportTempRepository.search(searchQuery);
            return ResponseEntity.ok().body(page);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            throw  new BadRequestException(null,"getImportWorkOrderList", Objects.nonNull(e.getMessage()) ? e.getMessage() : "getImportWorkOrderList.is.fail");
        }
    }



    @ApiOperation(value = "取消导入", notes = "取消导入")
    @DeleteMapping("/cancelOthersExcelImpCase/{id}")
    public ResponseEntity cancelOthersExcelImpCase(@PathVariable String id,@RequestHeader(value = "X-UserToken") String token){
        try {
            OperatorModel operatorModel= dataimpBaseService.getUserByToken(token);
            ImportOthersDataExcelRecord record=importOthersDataExcelRecordRepository.findByIdAndOperatorUserName(id,operatorModel.getUsername());
            record.setImportDataExcelStatus(ImportDataExcelStatus.IMPORT_CANCEL);
            TemplateType templateType=record.getTemplateType();
            BoolQueryBuilder boolQueryBuilder= QueryBuilders.boolQuery();
            boolQueryBuilder.must(matchPhraseQuery("operBatchNumber.keyword", record.getOperBatchNumber()));
            DeleteByQueryRequestBuilder deleteByQueryRequestBuilder= DeleteByQueryAction.INSTANCE.
                    newRequestBuilder(elasticsearchTemplate.getClient());
            //根据导入类型删除相关数据
            switch (templateType){
                case IMPORT_UPDATE_CASE:
                    deleteByQueryRequestBuilder.source("case_update_import_temp").filter(boolQueryBuilder).refresh(true).execute();
                    importOthersDataExcelRecordRepository.save(record);
                    break;
                case IMPORT_WARNING_INFO:
                    deleteByQueryRequestBuilder.source("case_warn_import_temp").filter(boolQueryBuilder).refresh(true).execute();
                    importOthersDataExcelRecordRepository.save(record);
                    break;
                case IMPORT_WORKER_ORDER:
                    deleteByQueryRequestBuilder.source("case_work_import_order_info_temp").filter(boolQueryBuilder).refresh(true).execute();
                    importOthersDataExcelRecordRepository.save(record);
                    break;
                case  IMPORT_BILL:
                    deleteByQueryRequestBuilder.source("case_bill_import_temp").filter(boolQueryBuilder).refresh(true).execute();
                    importOthersDataExcelRecordRepository.save(record);
                    break;
                case  IMPORT_LEFT_CASE:
                    deleteByQueryRequestBuilder.source("case_leaf_import_temp").filter(boolQueryBuilder).refresh(true).execute();
                    importOthersDataExcelRecordRepository.save(record);
                    break;
                case IMPORT_CHANGE_CITY:
                     deleteByQueryRequestBuilder.source("case_change_city_import_temp").filter(boolQueryBuilder).refresh(true).execute();
                    importOthersDataExcelRecordRepository.save(record);
                    break;
                case IMPORT_FOLLOW_RECORD:
                    deleteByQueryRequestBuilder.source("pre_case_followup_record_temp").filter(boolQueryBuilder).refresh(true).execute();
                    importOthersDataExcelRecordRepository.save(record);
                    break;
                case IMPORT_END_CASE:
                    deleteByQueryRequestBuilder.source("case_end_import_temp").filter(boolQueryBuilder).refresh(true).execute();
                    importOthersDataExcelRecordRepository.save(record);
                    break;
                default:
                    throw new Exception("templateType.is.error");
            }
            return ResponseEntity.ok().body(null);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            throw  new BadRequestException(null,"cancelExcelImpCase", Objects.nonNull(e.getMessage()) ? e.getMessage() : "cancelExcelImpCase.is.fail");
        }
    }


    @PostMapping("/importOthersExcelData")
    @ApiOperation(value = "其他数据导入", notes = "其他数据导入")
    public ResponseEntity importOthersExcelData(@Valid @RequestBody ImportOthersDataExcelRecordCreateRequest request,
                                          @RequestHeader(value = "X-UserToken") @ApiParam("操作者的Token") String token) {
        try{
            OperatorModel operatorModel= dataimpBaseService.getUserByToken(token);
            ImportOthersDataExcelRecord importOthersDataExcelRecord=new ImportOthersDataExcelRecord();
            ModelMapper modelMapper=new ModelMapper();
            modelMapper.addMappings(new PropertyMap<ImportOthersDataExcelRecordCreateRequest, ImportOthersDataExcelRecord>() {
                protected void configure() {
                    skip().setId(null);
                }});
            modelMapper.map(request,importOthersDataExcelRecord);
            importOthersDataExcelRecord.setOperatorName(operatorModel.getFullName());
            importOthersDataExcelRecord.setOperatorUserName(operatorModel.getUsername());
            importOthersDataExcelRecord.setOperatorTime(ZWDateUtil.getNowDateTime());
            String operBatchNumber=batchNumberSeqService.getBatchNumberSeq();
            importOthersDataExcelRecord.setOperBatchNumber(operBatchNumber);
            importOthersDataExcelRecord.setImportDataExcelStatus(ImportDataExcelStatus.IMPORTING);
            ImportExcelConfig importExcelConfig=null;
            //获取模板配置
            try {
                ResponseEntity<ImportExcelConfig>  importExcelConfigResponseEntity=restTemplate.getForEntity(InnerServiceUrl.MANAGEMENT_SERVICE_GETTEMPLATEBYID.concat(request.getTemplateId()),ImportExcelConfig.class);
                importExcelConfig=importExcelConfigResponseEntity.getBody();
            }catch (Exception e){
                logger.error(e.getMessage(),e);
                throw new Exception("template.is fail");
            }
            //获取文件
            String fileId=request.getFileId();
            HttpHeaders headers = new HttpHeaders();
            ResponseEntity<byte[]> response = restTemplate.exchange(InnerServiceUrl.COMMON_SERVICE_GETFILEBYID.concat("/").concat(fileId),
                    HttpMethod.GET, new HttpEntity<byte[]>(headers), byte[].class);
            List<String> content=response.getHeaders().get("Content-Disposition");
            if(Objects.isNull(content) || content.isEmpty()){
                throw new Exception("templateFile.is.illegal");
            }
            if(!content.get(0).endsWith(SaxParseExcelUtil.EXCEL_TYPE_XLSX)){
                logger.error("fileName: {} ",content.get(0));
                throw  new Exception("file.format.error");
            }
            byte[] result = response.getBody();
            InputStream inputStream=new ByteArrayInputStream(result);
            //开始数据导入
            importOthersExcelDataServcie.importOthersExcelData(importOthersDataExcelRecord,operatorModel,importExcelConfig,inputStream);
            importOthersDataExcelRecordRepository.save(importOthersDataExcelRecord);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            throw  new BadRequestException(null, "importOthersExcelData", Objects.isNull(e.getMessage()) ? "importExcelData.is.fail":e.getMessage());
        }
       return ResponseEntity.ok().body(null);
    }

    @ApiOperation(value = "确认导入", notes = "确认导入")
    @DeleteMapping("/confirmOthersExcelImpCase/{id}")
    public ResponseEntity confirmOthersExcelImpCase(@PathVariable String id,@RequestHeader(value = "X-UserToken") String token){
        try {
            OperatorModel operatorModel= dataimpBaseService.getUserByToken(token);
            ImportOthersDataExcelRecord record=importOthersDataExcelRecordRepository.findByIdAndOperatorUserName(id,operatorModel.getUsername());
            record.setImportDataExcelStatus(ImportDataExcelStatus.IMPORT_CONFIRMING);
            importOthersDataExcelRecordRepository.save(record);
            TemplateType templateType=record.getTemplateType();
            switch (templateType){
                case IMPORT_UPDATE_CASE:
                    confirmImportUpdateCaseService.confirmExcelImpOthersCase(record,operatorModel);
                    break;
                case  IMPORT_BILL:
                    confirmImportBillCaseService.confirmExcelImpOthersCase(record,operatorModel);
                    break;
                case IMPORT_WARNING_INFO:
                    confirmImportWarnInfoService.confirmExcelImpOthersCase(record,operatorModel);
                    break;
                case IMPORT_WORKER_ORDER:
                    confirmImportWorkOrderService.confirmExcelImpOthersCase(record,operatorModel);
                    break;
                case  IMPORT_LEFT_CASE:
                    confirmImportCaseLeafService.confirmExcelImpOthersCase(record,operatorModel);
                    break;
                case IMPORT_CHANGE_CITY:
                    confirmImportChangeCityService.confirmExcelImpOthersCase(record,operatorModel);
                    break;
                case IMPORT_FOLLOW_RECORD:
                    confirmImportRecordService.confirmExcelImpOthersCase(record,operatorModel);
                    break;
                case IMPORT_END_CASE:
                    confirmImportCaseEndService.confirmExcelImpOthersCase(record,operatorModel);
                    break;
                default:
                    logger.error("模板类型不合法");
            }
            return ResponseEntity.ok().body(null);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            throw  new BadRequestException(null,"confirmOthersExcelImpCase", Objects.nonNull(e.getMessage()) ? e.getMessage() : "confirmExcelImpCase.is.fail");
        }
    }
}
