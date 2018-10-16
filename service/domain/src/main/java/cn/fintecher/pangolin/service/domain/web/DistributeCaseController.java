package cn.fintecher.pangolin.service.domain.web;

import cn.fintecher.pangolin.common.enums.CaseDataStatus;
import cn.fintecher.pangolin.common.enums.CaseIssuedFlag;
import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.entity.domain.BaseCase;
import cn.fintecher.pangolin.service.domain.model.request.*;
import cn.fintecher.pangolin.service.domain.model.response.CaseCommonProResponse;
import cn.fintecher.pangolin.service.domain.model.response.CaseDistributeResponse;
import cn.fintecher.pangolin.service.domain.model.response.DistributeCaseSearchResponse;
import cn.fintecher.pangolin.service.domain.respository.BaseCaseRepository;
import cn.fintecher.pangolin.service.domain.service.DomainBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryAction;
import org.elasticsearch.index.reindex.UpdateByQueryRequestBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.InternalValueCount;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountAggregationBuilder;
import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

/**
 * Created by BBG on 2018/8/7.
 */
@RestController
@RequestMapping("/api/distributeCase")
@Api(value = "分案信息查询", description = "分案信息查询")
public class DistributeCaseController {

    private final Logger log = LoggerFactory.getLogger(DistributeCaseController.class);

    @Autowired
    private DomainBaseService domainBaseService;


    @Autowired
    private BaseCaseRepository baseCaseRepository;


    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;


    @GetMapping("/searchDetalCountDisCase")
    @ApiOperation(value = "地区分案明细查询", notes = "地区分案明细查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<DistributeCaseSearchResponse>> searchDetalCountDisCase(Pageable pageable,
                                                                                      DistributeSearchRequest request) {
        BoolQueryBuilder qb = request.generateQueryBuilder();

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(qb).build();
        log.debug("search Activity : {} query :{}", request.toString(), searchQuery.getQuery().toString());
        ModelMapper modelMapper=new ModelMapper();
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        Page<DistributeCaseSearchResponse> search = baseCaseRepository.search(searchQuery).map((BaseCase collectionCase) -> {
            DistributeCaseSearchResponse caseSearchResponse = modelMapper.map(collectionCase, DistributeCaseSearchResponse.class);
            caseSearchResponse.setCertificateNo(collectionCase.getPersonal().getCertificateNo());
            caseSearchResponse.setPersonalName(collectionCase.getPersonal().getPersonalName());
            caseSearchResponse.setPrincipalName(collectionCase.getPrincipal().getPrincipalName());
            return caseSearchResponse;
        });
        return new ResponseEntity<>(search, HttpStatus.OK);
    }

    @GetMapping("/searchDetalPersonalDisCase")
    @ApiOperation(value = "个人分案明细查询", notes = "个人分案明细查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<DistributeCaseSearchResponse>> searchDetalPersonalDisCase(Pageable pageable,
                                                                                         DistributeSearchRequest request,
                                                                                         @RequestHeader(value = "X-UserToken") String token) {
        OperatorModel operator = domainBaseService.getOperator(token);
        BoolQueryBuilder qb = request.generateQueryBuilder();
        qb.must(termQuery("departments", operator.getOrganization()));
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(qb).build();
        log.debug("search Activity : {} query :{}", request.toString(), searchQuery.getQuery().toString());
        ModelMapper modelMapper=new ModelMapper();
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        Page<DistributeCaseSearchResponse> search = baseCaseRepository.search(searchQuery).map(collectionCase -> {
            DistributeCaseSearchResponse caseSearchResponse = modelMapper.map(collectionCase, DistributeCaseSearchResponse.class);
            caseSearchResponse.setCertificateNo(collectionCase.getPersonal().getCertificateNo());
            caseSearchResponse.setPersonalName(collectionCase.getPersonal().getPersonalName());
            caseSearchResponse.setPrincipalName(collectionCase.getPrincipal().getPrincipalName());
            caseSearchResponse.setCurrentCollector(Objects.nonNull(collectionCase.getCurrentCollector()) ? collectionCase.getCurrentCollector().getFullName() :null);
            return caseSearchResponse;
        });
        return new ResponseEntity<>(search, HttpStatus.OK);
    }

    @PostMapping("/issuedCase")
    @ApiOperation(value = "地区案件确认下发", notes = "地区案件确认下发")
    public ResponseEntity issuedCase(@RequestHeader(value = "X-UserToken") String token,
                                           @RequestBody IssuedCaseRequest request) {
        OperatorModel operator = domainBaseService.getOperator(token);
        BoolQueryBuilder qb = request.generateQueryBuilder();
        qb.must(matchPhraseQuery("issuedFlag", CaseIssuedFlag.AREA_UN_ISSUED.name()));
        qb.must(matchPhraseQuery("caseDataStatus", CaseDataStatus.IN_POOL.name()));
        UpdateByQueryRequestBuilder updateByQuery = UpdateByQueryAction.INSTANCE.newRequestBuilder(elasticsearchTemplate.getClient());
        Script script = new Script("ctx._source.issuedFlag = '" + CaseIssuedFlag.AREA_HAS_ISSUED.name() + "';" +
                "ctx._source.operator='" + operator.getUsername() + "';" +
                "ctx._source.operatorTime='" + DateTime.now().toDate().getTime() + "'");
        BulkByScrollResponse response = updateByQuery.source("base_case").script(script).filter(qb).refresh(true).get();
        log.debug(response.toString());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/issuedCaseBatch")
    @ApiOperation(value = "地区案件批量下发", notes = "地区案件批量下发")
    public ResponseEntity issuedCaseBatch(@RequestHeader(value = "X-UserToken") String token,
                                     @RequestBody CaseDistributeRequest request) {
        OperatorModel operator = domainBaseService.getOperator(token);
        BoolQueryBuilder qb = request.generateQueryBuilder();
        qb.must(matchPhraseQuery("caseDataStatus", CaseDataStatus.IN_POOL.name()));
        UpdateByQueryRequestBuilder updateByQuery = UpdateByQueryAction.INSTANCE.newRequestBuilder(elasticsearchTemplate.getClient());
        Script script = new Script("ctx._source.issuedFlag = '" + CaseIssuedFlag.AREA_HAS_ISSUED.name() + "';" +
                "ctx._source.operator='" + operator.getUsername() + "';" +
                "ctx._source.operatorTime='" + DateTime.now().toDate().getTime() + "'");
        BulkByScrollResponse response = updateByQuery.source("base_case").script(script).filter(qb).refresh(true).get();
        log.debug(response.toString());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/issuedPersonalCase")
    @ApiOperation(value = "个人案件确认下发", notes = "个人案件确认下发")
    public ResponseEntity issuedPersonalCase(@RequestHeader(value = "X-UserToken") String token,
                                                   @RequestBody IssuedCaseRequest request) {
        OperatorModel operator = domainBaseService.getOperator(token);
        BoolQueryBuilder qb = request.generateQueryBuilder();
        qb.must(termQuery("departments", operator.getOrganization()));
        qb.must(matchPhraseQuery("issuedFlag", CaseIssuedFlag.PERSONAL_UN_ISSUED.name()));
        qb.must(matchPhraseQuery("caseDataStatus", CaseDataStatus.IN_POOL.name()));
        UpdateByQueryRequestBuilder updateByQuery = UpdateByQueryAction.INSTANCE.newRequestBuilder(elasticsearchTemplate.getClient());
        Script script = new Script("ctx._source.issuedFlag = '" + CaseIssuedFlag.PERSONAL_HAS_ISSUED.name() + "';" +
                "ctx._source.operator='" + operator.getUsername()+ "';" +
                "ctx._source.operatorTime='" + DateTime.now().toDate().getTime() + "'");
        BulkByScrollResponse response = updateByQuery.source("base_case").script(script).filter(qb).refresh(true).get();
        log.debug(response.toString());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/issuedPersonalCaseBatch")
    @ApiOperation(value = "个人案件批量下发", notes = "个人案件批量下发")
    public ResponseEntity issuedPersonalCaseBatch(@RequestHeader(value = "X-UserToken") String token,
                                             @RequestBody CaseDistributeRequest request) {
        OperatorModel operator = domainBaseService.getOperator(token);
        BoolQueryBuilder qb = request.generateQueryBuilder();
        qb.must(termQuery("departments", operator.getOrganization()));
        qb.must(matchPhraseQuery("caseDataStatus", CaseDataStatus.IN_POOL.name()));
        UpdateByQueryRequestBuilder updateByQuery = UpdateByQueryAction.INSTANCE.newRequestBuilder(elasticsearchTemplate.getClient());
        Script script = new Script("ctx._source.issuedFlag = '" + CaseIssuedFlag.PERSONAL_HAS_ISSUED.name() + "';" +
                "ctx._source.operator='" + operator.getUsername() + "';" +
                "ctx._source.operatorTime='" + DateTime.now().toDate().getTime() + "'");
        BulkByScrollResponse response = updateByQuery.source("base_case").script(script).filter(qb).refresh(true).get();
        log.debug(response.toString());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/getBatchNumberByCaseIssuedFlag")
    @ApiOperation(value = "获取需要操作的批次号", notes = "获取需要操作的批次号")
    public ResponseEntity<Set<String>> getBatchNumberByCaseIssuedFlag(@RequestParam(required = false) String request) throws BadRequestException{
        Set<String> resList=new HashSet<>();
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        if(!StringUtils.isBlank(request)){
            qb.must(matchPhraseQuery("issuedFlag.keyword", request));
        }
        Iterator<BaseCase> baseCaseIterator=baseCaseRepository.search(qb).iterator();
        while (baseCaseIterator.hasNext()){
            resList.add(baseCaseIterator.next().getBatchNumber());
        }
        return ResponseEntity.ok().body(resList);
    }

    @GetMapping("/getCaseCommonPro")
    @ApiOperation(value = "获取案件常用属性", notes = "获取案件常用属性")
    public ResponseEntity<CaseCommonProResponse> getCaseCommonPro(CaseCommonProRequest request, @RequestHeader(value = "X-UserToken") String token) throws BadRequestException{
        OperatorModel operator = domainBaseService.getOperator(token);
        BoolQueryBuilder qb = request.generateQueryBuilder();
        //个人分案时需要权限控制
        if(CaseIssuedFlag.AREA_HAS_ISSUED.equals(request.getIssuedFlag())|| CaseIssuedFlag.PERSONAL_UN_ISSUED.equals(request.getIssuedFlag())){
            qb.must(termQuery("departments", operator.getOrganization()));
        }
        CaseCommonProResponse response=new CaseCommonProResponse();
        Iterator<BaseCase> baseCaseIterator=baseCaseRepository.search(qb).iterator();
        while (baseCaseIterator.hasNext()){
            BaseCase baseCase=baseCaseIterator.next();
            response.getCitys().add(baseCase.getCity());
            response.getCollectors().add(Objects.nonNull(baseCase.getCurrentCollector()) ? baseCase.getCurrentCollector().getFullName():"");
            response.getDeparts().add(baseCase.getDetaptName());
        }
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/getBaseCaseNumberAndAmt")
    @ApiOperation(value = "根据条件获取案件数量及金额", notes = "根据条件获取案件数量及金额")
    public ResponseEntity<CaseDistributeResponse> getBaseCaseNumberAndAmt(CaseDistributeRequest request,@RequestHeader(value = "X-UserToken") String token) throws BadRequestException{
        OperatorModel operator = domainBaseService.getOperator(token);
        BoolQueryBuilder qb = request.generateQueryBuilder();
        //个人分案时需要权限控制
        if(request.getIssuedFlag().equals(CaseIssuedFlag.AREA_HAS_ISSUED) || request.getIssuedFlag().equals(CaseIssuedFlag.PERSONAL_UN_ISSUED)){
            qb.must(termQuery("departments", operator.getOrganization()));
        }
        ValueCountAggregationBuilder field = AggregationBuilders.count("count").field("id.keyword");
        SumAggregationBuilder sumBuilder = AggregationBuilders.sum("sum").field("overdueAmtTotal");
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices("base_case")
                .withTypes("base_case")
                .withSearchType(SearchType.DEFAULT)
                .withQuery(qb)
                .addAggregation(field)
                .addAggregation(sumBuilder).build();
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, response -> response.getAggregations());
        InternalSum sum = aggregations.get("sum");
        InternalValueCount count = aggregations.get("count");
        CaseDistributeResponse response=new CaseDistributeResponse();
        response.setCaseNumber(count.getValue());
        response.setCaseAmt(sum.getValue());
        return ResponseEntity.ok().body(response);
    }

    @ApiOperation(notes = "TEST", value = "TEST")
    @GetMapping("/test")
    public ResponseEntity test() {
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        qb.must(matchPhraseQuery("caseNumber","0029152084"));
        UpdateByQueryRequestBuilder updateByQuery = UpdateByQueryAction.INSTANCE.newRequestBuilder(elasticsearchTemplate.getClient());
        Script script = new Script("ctx._source.followInTime = '" + DateTime.now().toDate().getTime() + "';");
        BulkByScrollResponse response = updateByQuery.source("base_case").filter(qb).script(script).refresh(true).get();
        return null;
    }

}
