package cn.fintecher.pangolin.service.dataimp.web;

import cn.fintecher.pangolin.common.enums.ImportDataExcelStatus;
import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.entity.domain.ImportDataExcelRecord;
import cn.fintecher.pangolin.service.dataimp.model.request.BaseCaseAllImportExcelTempRequest;
import cn.fintecher.pangolin.service.dataimp.model.request.ImportDataExcelRecordRequest;
import cn.fintecher.pangolin.service.dataimp.model.response.BaseCaseAllImportExcelTempResponse;
import cn.fintecher.pangolin.service.dataimp.model.response.ImportDataExcelRecordResponse;
import cn.fintecher.pangolin.service.dataimp.repository.BaseCaseImportExcelTempRepository;
import cn.fintecher.pangolin.service.dataimp.repository.BasePersonalImportExcelTempRepository;
import cn.fintecher.pangolin.service.dataimp.repository.ImportDataExcelRecordRepository;
import cn.fintecher.pangolin.service.dataimp.repository.MergeDataModelRepository;
import cn.fintecher.pangolin.service.dataimp.service.ConfirmExcelImpCaseService;
import cn.fintecher.pangolin.service.dataimp.service.DataimpBaseService;
import cn.fintecher.pangolin.service.dataimp.service.MergeDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;


import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

/**
 * @Author:peishouwen
 * @Desc: Excel导入数据记录
 * @Date:Create in 18:24 2018/7/28
 */
@RestController
@RequestMapping("/api/importDataExcelRecordController")
@Api(value = "查看数据导入记录", description = "查看数据导入记录")
public class ImportDataExcelRecordController {
    Logger logger= LoggerFactory.getLogger(ImportDataExcelRecordController.class);
    @Autowired
    ImportDataExcelRecordRepository importDataExcelRecordRepository;
    @Autowired
    DataimpBaseService dataimpBaseService;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    BaseCaseImportExcelTempRepository baseCaseImportExcelTempRepository;


    @Autowired
    ConfirmExcelImpCaseService confirmExcelImpCaseService;

    @Autowired
    MergeDataService mergeDataService;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    MergeDataModelRepository mergeDataModelRepository;

    @GetMapping("/getImportDataExcelList")
    @ApiOperation(value = "查询导入结果", notes = "查询导入结果")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<ImportDataExcelRecordResponse>> getImportDataExcelList(Pageable pageable, ImportDataExcelRecordRequest importDataExcelRecordRequest,
                                                                                @RequestHeader(value = "X-UserToken") String token) throws BadRequestException{
        try {
         OperatorModel operatorModel= dataimpBaseService.getUserByToken(token);
            BoolQueryBuilder queryBuilder=(BoolQueryBuilder)importDataExcelRecordRequest.generateQueryBuilder();
            queryBuilder.must(matchPhraseQuery("operatorUserName",operatorModel.getUsername()));
            SearchQuery searchQuery = new NativeSearchQueryBuilder()
                    .withPageable(pageable)
                    .withQuery(queryBuilder)
                   .build();
           logger.debug("search Activity : {} query :{}", importDataExcelRecordRequest.toString(), searchQuery.getQuery().toString());

           Page<ImportDataExcelRecord> page= importDataExcelRecordRepository.search(searchQuery);
          Page<ImportDataExcelRecordResponse> responses=modelMapper.map(page,new TypeToken<Page<ImportDataExcelRecordResponse>>() {}.getType());
          return ResponseEntity.ok().body(responses);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            throw  new BadRequestException(null,"getImportDataExcelList", Objects.nonNull(e.getMessage()) ? e.getMessage() : "getImportDataExcelList.is.fail");
        }
    }


    @ApiOperation(value = "获取导入后的案件详情列表", notes = "获取导入后的案件详情列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    @GetMapping("/getImportDataExcelCaseDetailList")
    public ResponseEntity<Page<BaseCaseAllImportExcelTempResponse>> getImportDataExcelCaseDetailList(Pageable pageable, BaseCaseAllImportExcelTempRequest baseCaseAllImportExcelTempRequest,
                                                                                                 @RequestHeader(value = "X-UserToken") String token){
        try {
            OperatorModel operatorModel= dataimpBaseService.getUserByToken(token);
            BoolQueryBuilder queryBuilder= (BoolQueryBuilder) baseCaseAllImportExcelTempRequest.generateQueryBuilder();
            queryBuilder.must(matchPhraseQuery("operator",operatorModel.getUsername()));
            SearchQuery searchQuery = new NativeSearchQueryBuilder()
                    .withPageable(pageable)
                    .withQuery(queryBuilder).build();
            logger.debug("search Activity : {} query :{}", baseCaseAllImportExcelTempRequest.toString(), searchQuery.getQuery().toString());
            Page<BaseCaseAllImportExcelTempResponse> page= baseCaseImportExcelTempRepository.search(searchQuery).map(baseCaseAllImportExcelTemp -> {
                BaseCaseAllImportExcelTempResponse response= modelMapper.map(baseCaseAllImportExcelTemp,BaseCaseAllImportExcelTempResponse.class);
                return response;
            });
            return ResponseEntity.ok().body(page);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            throw  new BadRequestException(null,"getImportDataExcelCaseDetailList", Objects.nonNull(e.getMessage()) ? e.getMessage() : "getImportDataExcelCaseDetailList.is.fail");
        }
    }

    @ApiOperation(value = "取消导入", notes = "取消导入")
    @DeleteMapping("/cancelExcelImpCase/{id}")
    public ResponseEntity cancelExcelImpCase(@PathVariable String id,@RequestHeader(value = "X-UserToken") String token){
        try {
            ImportDataExcelRecord importDataExcelRecord= importDataExcelRecordRepository.findById(id).get();
            BoolQueryBuilder queryBuilder= QueryBuilders.boolQuery();
            queryBuilder.must().add(matchPhraseQuery("batchNumber.keyword",importDataExcelRecord.getBatchNumber()));
            DeleteByQueryRequestBuilder deleteByQueryRequestBuilder= DeleteByQueryAction.INSTANCE.
                    newRequestBuilder(elasticsearchTemplate.getClient());
            deleteByQueryRequestBuilder.source("base_case_all_import_excel_temp").filter(queryBuilder).refresh(true).execute();
            deleteByQueryRequestBuilder.source("base_personal_import_excel_temp").filter(queryBuilder).refresh(true).execute();
            importDataExcelRecord.setImportDataExcelStatus(ImportDataExcelStatus.IMPORT_CANCEL);
            importDataExcelRecordRepository.save(importDataExcelRecord);
            return ResponseEntity.ok().body(null);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            throw  new BadRequestException(null,"cancelExcelImpCase", Objects.nonNull(e.getMessage()) ? e.getMessage() : "cancelExcelImpCase.is.fail");
        }
    }

    @ApiOperation(value = "确认导入", notes = "确认导入")
    @PutMapping("/confirmExcelImpCase/{id}")
    public ResponseEntity confirmExcelImpCase(@PathVariable String id,@RequestHeader(value = "X-UserToken") String token) throws BadRequestException{
           ImportDataExcelRecord importDataExcelRecord= importDataExcelRecordRepository.findById(id).get();
           OperatorModel operatorModel= dataimpBaseService.getUserByToken(token);
           importDataExcelRecord.setImportDataExcelStatus(ImportDataExcelStatus.IMPORT_CONFIRMING);
           importDataExcelRecordRepository.save(importDataExcelRecord);
           //货币合并
            mergeDataService.currencyMergeData(importDataExcelRecord);
           //卡信息合并
           mergeDataService.mergeData(importDataExcelRecord);
           //数据确认（异步操作）
           confirmExcelImpCaseService.confirmExcelImpCase(importDataExcelRecord,operatorModel);
           return ResponseEntity.ok().body(null);
    }

    @ApiOperation(value = "案件合并", notes = "案件合并")
    @PutMapping("/mergeExcelImpCase/{id}")
    public ResponseEntity mergeExcelImpCase(@PathVariable String id){
        try{
            ImportDataExcelRecord importDataExcelRecord= importDataExcelRecordRepository.findById(id).get();
            mergeDataService.mergeData(importDataExcelRecord);
            return ResponseEntity.ok().body(null);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            throw  new BadRequestException(null,"mergeExcelImpCase", Objects.nonNull(e.getMessage()) ? e.getMessage() : "mergeExcelImpCase.is.fail");
        }
    }

    @ApiOperation(value = "获取导入后的案件详情列表", notes = "获取导入后的案件详情列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    @GetMapping("/getMergeDataList")
    public ResponseEntity<Page<BaseCaseAllImportExcelTempResponse>> getMergeDataList(Pageable pageable, BaseCaseAllImportExcelTempRequest baseCaseAllImportExcelTempRequest,
                                                                                                     @RequestHeader(value = "X-UserToken") String token) throws BadRequestException{
            OperatorModel operatorModel= dataimpBaseService.getUserByToken(token);
            BoolQueryBuilder queryBuilder= (BoolQueryBuilder) baseCaseAllImportExcelTempRequest.generateQueryBuilder();
            queryBuilder.must(matchPhraseQuery("operator",operatorModel.getUsername()));
            SearchQuery searchQuery = new NativeSearchQueryBuilder()
                    .withPageable(pageable)
                    .withQuery(queryBuilder).build();
            logger.debug("search Activity : {} query :{}", baseCaseAllImportExcelTempRequest.toString(), searchQuery.getQuery().toString());
            Page<BaseCaseAllImportExcelTempResponse> page= mergeDataModelRepository.search(searchQuery).map(mergeDataModel -> {
                BaseCaseAllImportExcelTempResponse response= modelMapper.map(mergeDataModel,BaseCaseAllImportExcelTempResponse.class);
                return response;
            });
            return ResponseEntity.ok().body(page);
    }

}
