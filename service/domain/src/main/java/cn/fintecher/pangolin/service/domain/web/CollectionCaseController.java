package cn.fintecher.pangolin.service.domain.web;

import cn.fintecher.pangolin.common.enums.CaseDataStatus;
import cn.fintecher.pangolin.common.enums.CaseIssuedFlag;
import cn.fintecher.pangolin.common.enums.CaseLeaveFlag;
import cn.fintecher.pangolin.common.enums.ManagementType;
import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.model.PrincipalSearchModel;
import cn.fintecher.pangolin.common.utils.InnerServiceUrl;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.common.utils.ZWStringUtils;
import cn.fintecher.pangolin.entity.domain.*;
import cn.fintecher.pangolin.entity.managentment.CustConfig;
import cn.fintecher.pangolin.service.domain.model.CommentRequest;
import cn.fintecher.pangolin.service.domain.model.request.*;
import cn.fintecher.pangolin.service.domain.model.response.*;
import cn.fintecher.pangolin.service.domain.respository.*;

import cn.fintecher.pangolin.service.domain.service.CollectionCaseService;
import cn.fintecher.pangolin.service.domain.service.DomainBaseService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Created by huyanmin on 2018/7/11.
 */
@RestController
@RequestMapping("/api/collectionCase")
@Api(value = "案件相关操作", description = "案件相关操作")
public class CollectionCaseController {

    private final Logger log = LoggerFactory.getLogger(CollectionCaseController.class);
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BaseCaseRepository baseCaseRepository;
    @Autowired
    private PersonalRepository personalRepository;
    @Autowired
    private CollectionCaseService collectionCaseService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    private DomainBaseService domainBaseService;
    @Autowired
    PersonalContactRepository personalContactRepository;
    @Autowired
    AssistCaseRepository assistCaseRepository;
    @Autowired
    HisCaseRepository hisCaseRepository;

    @GetMapping("/caseCollectionSearch/{id}")
    @ApiOperation(value = "执行页案件查询", notes = "执行页案件查询")
    public ResponseEntity<CaseInfoSearchResponse> caseCollectionSearch(@PathVariable String id) {

        CaseInfoSearchResponse caseInfoSearchResponse = new CaseInfoSearchResponse();
        Optional<BaseCase> byId = baseCaseRepository.findById(id);
        byId.orElseThrow(() -> new BadRequestException(null, "collectionCase", "collectionCase.is.not.exist"));
        BaseCase baseCase = byId.get();
        if (Objects.nonNull(baseCase)) {
            BeanUtils.copyProperties(baseCase, caseInfoSearchResponse);
            Period p = new Period(LocalDate.now(), new LocalDate(caseInfoSearchResponse.getEndCaseDate()), PeriodType.days());
            int days = p.getDays();
            //退案天数
            caseInfoSearchResponse.setReturnDays(days);
        }
        //年龄
        DateTime dateTime = new DateTime(caseInfoSearchResponse.getPersonal().getBirthday());
        DateTime currentTiem = new DateTime(ZWDateUtil.getNowDate());
        Integer age = currentTiem.getYear() - dateTime.getYear();
        caseInfoSearchResponse.setAge(age);
        return new ResponseEntity<>(caseInfoSearchResponse, HttpStatus.OK);
    }

    @GetMapping("/casePersonalContact")
    @ApiOperation(value = "查询联系人", notes = "查询联系人")
    public ResponseEntity<List<PersonalContactModel>> casePersonalContact(@RequestParam String personalId) {
        log.debug("search Activity : {} ", personalId);
        List<PersonalContactModel> personalContactModels = collectionCaseService.searchPersonalContact(personalId);
        /*if(personalContactModels.size() > 0){
            Collections.sort(personalContactModels, Comparator.comparing(PersonalContactModel::getOperatorTime));
        }*/
        return new ResponseEntity<>(personalContactModels, HttpStatus.OK);
    }


    @GetMapping("/caseCollectionStatus")
    @ApiOperation(value = "查询催收员催收的案件", notes = "查询催收员催收的案件")
    public ResponseEntity<List<CaseInfoStatusResponse>> caseCollectionStatus(CaseCollectionStatusRequest request,
                                                                             @RequestHeader(value = "X-UserToken") String token) {
        log.debug("search Activity : {} ", request.toString());
        List<CaseInfoStatusResponse> list = collectionCaseService.caseCollectionStatus(request, token);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping("/leaveCollectionCase")
    @ApiOperation(value = "案件留案", notes = "案件留案")
    public ResponseEntity leaveCollectionCase(@RequestBody LeaveCaseRequest request) {

        Optional<BaseCase> byId = baseCaseRepository.findById(request.getId());
        byId.orElseThrow(() -> new BadRequestException(null, "collectionCase", "collectionCase.is.not.exist"));
        BaseCase baseCase = byId.get();
        if (CaseLeaveFlag.WAIT_CONFIRMED_LEAVE.equals(baseCase.getLeaveFlag()) ||
                CaseLeaveFlag.HAS_LEAVE.equals(baseCase.getLeaveFlag())) {
            throw new BadRequestException(null, "leaveCase", "leaveCase.has.leave");
        } else {
            baseCase.setLeaveFlag(CaseLeaveFlag.WAIT_CONFIRMED_LEAVE);
            baseCaseRepository.save(baseCase);
        }
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("/queryCollectionByPrincipal")
    @ApiOperation(value = "按照批次号和委托方查询案件", notes = "按照批次号和委托方查询案件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<CollectionCaseSearchResponse>> queryCollectionByPrincipal(Pageable pageable,
                                                                                         CollectionSearchRequest request,
                                                                                         @RequestHeader(value = "X-UserToken") String token) {
        OperatorModel operator = domainBaseService.getOperator(token);
        SortBuilder sortBuilder = SortBuilders.fieldSort("operatorTime").unmappedType("Date").order(SortOrder.DESC);
        BoolQueryBuilder boolQueryBuilder = collectionCaseService.queryCollectionByPrincipal(request, operator);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(boolQueryBuilder).withSort(sortBuilder).build();
        log.debug("search Activity : {} query :{}", request.toString(), searchQuery.getQuery().toString());
        Page<CollectionCaseSearchResponse> search = baseCaseRepository.search(searchQuery).map(collectionCase -> {
            CollectionCaseSearchResponse caseSearchResponse = modelMapper.map(collectionCase, CollectionCaseSearchResponse.class);
            return caseSearchResponse;
        });
        return new ResponseEntity<>(search, HttpStatus.OK);
    }

    @GetMapping("/queryCollectionAmtByPrincipal")
    @ApiOperation(value = "按照批次号和委托方查询案件金额", notes = "按照批次号和委托方查询案件金额")
    public ResponseEntity<Double> queryCollectionAmtByPrincipal(CollectionSearchRequest request,
                                                                                         @RequestHeader(value = "X-UserToken") String token) {
        OperatorModel operator = domainBaseService.getOperator(token);
        BoolQueryBuilder boolQueryBuilder = collectionCaseService.queryCollectionByPrincipal(request, operator);
        Double amt = domainBaseService.getTotalAmt(boolQueryBuilder);
        return new ResponseEntity<>(amt, HttpStatus.OK);
    }

    @GetMapping("/queryCollectionCount")
    @ApiOperation(value = "案件统计", notes = "案件统计")
    public ResponseEntity<CollectionCountResponse> queryCollectionCount(CollectionSearchRequest request,
                                                                        @RequestHeader(value = "X-UserToken") String token) {
        OperatorModel operator = domainBaseService.getOperator(token);
        CollectionCountResponse response = collectionCaseService.getCollectionCount(request, operator);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/queryBatchNumberCollection")
    @ApiOperation(value = "查询不同委托方的全部案件", notes = "查询不同委托方的全部案件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<CollectionCaseAggregationModel>> queryBatchNumberCollection(Pageable pageable,
                                                                                           SearchCollectionCaseRequest request,
                                                                                           @RequestHeader(value = "X-UserToken") String token) throws Exception {
        List<CollectionCaseAggregationModel> list = collectionCaseService.queryBatchNumber(token, request);
        PageInfo<CollectionCaseAggregationModel> pageInfo = new PageInfo<>(list);
        Page<CollectionCaseAggregationModel> page = new PageImpl<>(list, pageable, pageInfo.getTotal());
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @GetMapping("/searchPrincipal")
    @ApiOperation(value = "根据催收员或管理员组织查询相关的案件", notes = "根据催收员或管理员组织查询相关的案件")
    public ResponseEntity searchPrincipal(String token) throws Exception {

        Set<PrincipalSearchModel> principalSearchModels = collectionCaseService.searchPrincipal(token);
        return new ResponseEntity<>(principalSearchModels, HttpStatus.OK);
    }

    @GetMapping("/searchDebtCaseCount")
    @ApiOperation(value = "根据客户信息查询公债案件的数量", notes = "根据客户信息查询公债案件的数量")
    public ResponseEntity searchDebtCaseCount(String idCard, String principalId) throws Exception {

        Map<String, Long> stringLongMap = collectionCaseService.searchDebtCaseCount(idCard, principalId);
        return new ResponseEntity<>(stringLongMap
                , HttpStatus.OK);
    }

    @GetMapping("/searchDebtCase")
    @ApiOperation(value = "根据客户信息查询公债案件", notes = "根据客户信息查询公债案件")
    public ResponseEntity<Page<CollectionDebtCaseResponse>> searchDebtCase(CollectionDebtRequest request) throws Exception {
        Page<CollectionDebtCaseResponse> responses = collectionCaseService.searchDebtCase(request);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PostMapping("/insertComment")
    @ApiOperation(value = "添加评语/批注/记事本", notes = "添加评语/批注/记事本")
    public ResponseEntity insertComment(@RequestBody CreateCommentRequest request,
                                        @RequestHeader(value = "X-UserToken") String token) throws Exception {

        Comment comment = new Comment();
        OperatorModel operator = domainBaseService.getOperator(token);
        BeanUtils.copyProperties(request, comment);
        comment.setOperatorTime(ZWDateUtil.getNowDate());
        comment.setOperator(operator.getId());
        comment.setOperatorName(operator.getFullName());
        comment.setOperatorUserName(operator.getUsername());
        commentRepository.save(comment);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @GetMapping("/searchComment")
    @ApiOperation(value = "查询评语/批注/备忘录", notes = "查询评语/批注/备忘录")
    public ResponseEntity<List<Comment>> searchComment(CommentRequest request) throws Exception {

        log.info("Search comment {} ", request.getCommentTypeList());
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(matchQuery("commentType", request.getCommentTypeList())).must(matchPhraseQuery("caseId", request.getCaseId()));
        Iterable<Comment> search = commentRepository.search(queryBuilder);
        List<Comment> list = new ArrayList<>();
        if (search.iterator().hasNext()) {
            list = IteratorUtils.toList(search.iterator());
            list.sort(Comparator.comparing(Comment::getOperatorTime));
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/deleteComment")
    @ApiOperation(value = "删除评语/批注/备忘录", notes = "删除评语/批注/备忘录")
    public ResponseEntity deleteComment(@RequestParam String id) throws Exception {
        commentRepository.deleteById(id);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PostMapping("/signCaseState")
    @ApiOperation(value = "标记案件状态", notes = "标记案件状态")
    public ResponseEntity signCaseState(@RequestBody CaseSignModel model,
                                        @RequestHeader(value = "X-UserToken") String token) {
        OperatorModel operator = domainBaseService.getOperator(token);
        BaseCase baseCase = baseCaseRepository.findById(model.getCaseId()).get();
        String colVal = baseCase.getColor();
        String state = model.getCollState();
        if (ZWStringUtils.isEmpty(model.getCollState())) {
            throw new BadRequestException(null, "", "please.chose.config");
        }
        if (Objects.nonNull(baseCase.getCollectionStatus()) && baseCase.getCollectionStatus().contains(model.getCollState())) {
            throw new BadRequestException(null, "", "state.has.exist");
        }
        CustConfig oldState = null;
        CustConfig newState = null;
        if (Objects.isNull(baseCase.getCollectionStatus())) {
            Set<String> collStates = new HashSet<>();
            baseCase.setCollectionStatus(collStates);
        }
        baseCase.getCollectionStatus().add(model.getCollState());
        ParameterizedTypeReference<List<CustConfig>> responseType = new ParameterizedTypeReference<List<CustConfig>>() {
        };
        ResponseEntity<List<CustConfig>> entity = restTemplate.exchange(InnerServiceUrl.MANAGEMENT_SERVICE_CASESTATUS.concat(baseCase.getPrincipal().getId()),
                HttpMethod.GET, null, responseType);
        if (Objects.nonNull(entity) && Objects.nonNull(entity.getBody()))
            for (CustConfig custConfig : entity.getBody()) {
                if (Objects.nonNull(state) && Objects.equals(colVal, custConfig.getColor())) {
                    oldState = custConfig;
                }
                if (Objects.equals(state, custConfig.getName())) {
                    newState = custConfig;
                }
            }
        if (Objects.isNull(oldState) || (newState.getSort() < oldState.getSort())) {
            baseCase.setColor(newState.getColor());
        }
        if (Objects.equals(baseCase.getIsMajor(), ManagementType.NO)
                && Objects.equals(newState.getIsMajor(), ManagementType.YES)) {
            baseCase.setIsMajor(ManagementType.YES);
        }
        baseCase.setOperatorTime(ZWDateUtil.getNowDateTime());
        baseCase.setOperator(operator.getId());
        baseCaseRepository.save(baseCase);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/getCaseByState")
    @ApiOperation(value = "案件查询", notes = "案件查询")
    public ResponseEntity<Page<CaseQueryResponse>> getCaseByState(Pageable pageable,
                                                                  CaseStateQueryRequest request,
                                                                  @RequestHeader(value = "X-UserToken") String token) {
        BoolQueryBuilder qb = request.generateQueryBuilder();
        Page<BaseCase> baseCasePage = baseCaseRepository.search(qb, pageable);
        Page<CaseQueryResponse> responses = baseCasePage.map(baseCase -> {
            CaseQueryResponse response = new CaseQueryResponse();
            BeanUtils.copyProperties(baseCase, response);
            response.setPrincipalName(baseCase.getPrincipal().getPrincipalName());
            response.setCertificateNo(baseCase.getPersonal().getCertificateNo());
            response.setSelfPhoneNo(baseCase.getPersonal().getSelfPhoneNo());
            response.setPersonalName(baseCase.getPersonal().getPersonalName());
            return response;
        });
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/queryAdjustCase")
    @ApiOperation(value = "查询个案调整", notes = "查询个案调整")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<CaseStateQueryResponse>> queryAdjustCase(Pageable pageable,
                                                                        CaseStateQueryRequest request,
                                                                        @RequestHeader(value = "X-UserToken") String token) throws Exception {
        BoolQueryBuilder qb = request.generateQueryBuilder();
        qb.must(matchPhraseQuery("leaveFlag", CaseLeaveFlag.NO_LEAVE.toString()))
                .must(matchPhraseQuery("issuedFlag", CaseIssuedFlag.PERSONAL_HAS_ISSUED.toString()));
        qb.mustNot(matchPhraseQuery("caseDataStatus", CaseDataStatus.OUT_POOL.toString()))
                .mustNot(matchPhraseQuery("caseDataStatus", CaseDataStatus.PAUSE.toString()));
        OperatorModel operator = domainBaseService.getOperator(token);
        qb.must(matchPhraseQuery("departments", operator.getOrganization()));
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(qb).build();
        Page<CaseStateQueryResponse> page = baseCaseRepository.search(searchQuery).map(baseCase -> {
            CaseStateQueryResponse model = new CaseStateQueryResponse();
            BeanUtils.copyProperties(baseCase, model);
            model.setPersonalName(baseCase.getPersonal().getPersonalName());
            model.setCertificateNo(baseCase.getPersonal().getCertificateNo());
            model.setSelfPhoneNo(baseCase.getPersonal().getSelfPhoneNo());
            model.setCurrentCollectorName(Objects.nonNull(baseCase.getCurrentCollector()) ? baseCase.getCurrentCollector().getFullName() : null);
            model.setPrincipalName(Objects.nonNull(baseCase.getPrincipal()) ? baseCase.getPrincipal().getPrincipalName() : null);
            return model;
        });
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @GetMapping("/queryAllCollectionCase")
    @ApiOperation(value = "全部案件", notes = "全部案件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<CaseStateQueryResponse>> queryAllCollectionCase(Pageable pageable,
                                                                               CaseStateQueryRequest request) throws Exception {
        BoolQueryBuilder qb = request.generateQueryBuilder();
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(qb).build();
        Page<CaseStateQueryResponse> page = baseCaseRepository.search(searchQuery).map(baseCase -> {
            CaseStateQueryResponse model = new CaseStateQueryResponse();
            BeanUtils.copyProperties(baseCase, model);
            model.setPersonalName(baseCase.getPersonal().getPersonalName());
            model.setCertificateNo(baseCase.getPersonal().getCertificateNo());
            model.setSelfPhoneNo(baseCase.getPersonal().getSelfPhoneNo());
            model.setPrincipalName(Objects.nonNull(baseCase.getPrincipal()) ? baseCase.getPrincipal().getPrincipalName() : null);
            model.setCurrentCollectorName(Objects.nonNull(baseCase.getCurrentCollector()) ? baseCase.getCurrentCollector().getFullName() : null);
            return model;
        });
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @GetMapping("/quickSearch")
    @ApiOperation(value = "快速查询", notes = "快速查询")
    public ResponseEntity<Page<CaseQueryResponse>> quickSearch(Pageable pageable,
                                                               CaseFindQueryRequest request) {
        if (ZWStringUtils.isEmpty(request.getCaseNumber())
                && ZWStringUtils.isEmpty(request.getPhone())
                && ZWStringUtils.isEmpty(request.getCertificateNo())
                && ZWStringUtils.isEmpty(request.getPersonalName())) {
            throw new BadRequestException(null, "", "params.is.empty");
        }
        BoolQueryBuilder qb = request.generateQueryBuilder();
        List<String> personalIds = new ArrayList<>();
        if (ZWStringUtils.isNotEmpty(request.getPhone())) {
            List<PersonalContact> personalContacts = IterableUtils.toList(personalContactRepository.search(QueryBuilders.boolQuery().must(matchPhraseQuery("personalPerCalls.phoneNo", request.getPhone()))));
            personalContacts.forEach(personalContact -> personalIds.add(personalContact.getPersonalId()));
            qb.must(termsQuery("personal.id.keyword", personalIds));
        }
        Page<BaseCase> baseCasePage = baseCaseRepository.search(qb, pageable);
        Page<CaseQueryResponse> responses = baseCasePage.map(baseCase -> {
            CaseQueryResponse response = new CaseQueryResponse();
            BeanUtils.copyProperties(baseCase, response);
            response.setPrincipalName(baseCase.getPrincipal().getPrincipalName());
            response.setCertificateNo(baseCase.getPersonal().getCertificateNo());
            response.setPersonalName(baseCase.getPersonal().getPersonalName());
            response.setSelfPhoneNo(baseCase.getPersonal().getSelfPhoneNo());
            return response;
        });
        return ResponseEntity.ok().body(responses);
    }

    @GetMapping("/searchBaseCase")
    @ApiOperation(value = "检查催收员id查找催收案件", notes = "检查催收员id查找催收案件")
    public ResponseEntity<List<String>> searchBaseCase(String collector) throws Exception {
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        builder.must(matchPhraseQuery("currentCollector.id", collector));
        Iterable<BaseCase> search = baseCaseRepository.search(builder);
        List<String> list = new ArrayList<>();
        if (search.iterator().hasNext()) {
            BaseCase next = search.iterator().next();
            list.add(next.getId());
        }
        BoolQueryBuilder builder1 = QueryBuilders.boolQuery();
        builder1.must(matchPhraseQuery("currentCollector.id", collector));
        Iterable<AssistCollectionCase> search1 = assistCaseRepository.search(builder1);
        if (search1.iterator().hasNext()) {
            AssistCollectionCase collectionCase = search1.iterator().next();
            list.add(collectionCase.getId());
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping("/stopCollectionCase")
    @ApiOperation(value = "停催", notes = "停催")
    public ResponseEntity stopCollectionCase(@RequestBody StopCollectionCaseModel model,
                                             @RequestHeader(value = "X-UserToken") String token) {
        log.info("进入停催案件" + model);
        Iterable<BaseCase> allById = baseCaseRepository.findAllById(model.getIdList());
        OperatorModel operator = domainBaseService.getOperator(token);
        List<String> listIds = new ArrayList<>();
        if (allById.iterator().hasNext()) {
            List<BaseCase> list = IteratorUtils.toList(allById.iterator());
            list.forEach(baseCase -> {
                if (Objects.nonNull(model.getCaseDataStatus())) {
                    baseCase.setCaseDataStatus(CaseDataStatus.IN_POOL);
                } else {
                    baseCase.setCaseDataStatus(CaseDataStatus.PAUSE);
                }
                baseCase.setOperator(operator.getId());
                baseCase.setOperatorTime(ZWDateUtil.getNowDateTime());
                baseCase.setStopTime(ZWDateUtil.getNowDateTime());
                listIds.add(baseCase.getId());
            });
            if (listIds.size() > 0) {
                domainBaseService.endApplyCase(listIds, "手动停催案件");
            }
            baseCaseRepository.saveAll(list);
        } else {
            throw new BadRequestException(null, "baseCase", "baseCase.is.not.exist");
        }
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/backCollectionCase")
    @ApiOperation(value = "退案", notes = "退案")
    public ResponseEntity backCollectionCase(@RequestBody StopCollectionCaseModel model,
                                             @RequestHeader(value = "X-UserToken") String token) {
        log.info("退案开始" + model);
        Iterable<BaseCase> allById = baseCaseRepository.findAllById(model.getIdList());
        OperatorModel operator = domainBaseService.getOperator(token);
        List<HisCase> hisCases = new ArrayList<>();
        List<String> listIds = new ArrayList<>();
        if (allById.iterator().hasNext()) {
            List<BaseCase> list = IteratorUtils.toList(allById.iterator());
            list.forEach(baseCase -> {
                HisCase hisCase = new HisCase();
                baseCase.setCaseDataStatus(CaseDataStatus.OUT_POOL);
                baseCase.setOperator(operator.getId());
                baseCase.setOperatorTime(ZWDateUtil.getNowDateTime());
                listIds.add(baseCase.getId());
                BeanUtils.copyProperties(baseCase, hisCase);
                hisCases.add(hisCase);
            });
            if (listIds.size() > 0) {
                domainBaseService.endApplyCase(listIds, "手动退案自动结束申请");
            }
            if (Objects.nonNull(hisCases)) {
                hisCaseRepository.saveAll(hisCases);
            }
            if (list.size() > 0) {
                baseCaseRepository.deleteAll(list);
            }
        } else {
            throw new BadRequestException(null, "baseCase", "baseCase.is.not.exist");
        }
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/hisCaseTrans")
    @ApiOperation(value = "历史案件转移", notes = "历史案件转移")
    public ResponseEntity hisCaseTrans() {
        log.info("历史案件转移" + ZWDateUtil.getDate());
        collectionCaseService.hisCaseTrans();
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/caseRecordHandle")
    @ApiOperation(value = "案件记录处理", notes = "案件记录处理")
    public ResponseEntity caseRecordHandle() {
        log.info("案件记录处理" + ZWDateUtil.getDate());
        collectionCaseService.caseRecordHandle();
        return ResponseEntity.ok().body(null);
    }

    /**
     * 案件移动到手工历史表
     *
     * @return
     */
    @GetMapping("/deleteCaseByIds")
    @ApiOperation(value = "案件删除", notes = "案件删除")
    public ResponseEntity deleteCaseByIds(DeleteCollectionCaseModel model) {
        collectionCaseService.deleteCaseByManual(model);
        return ResponseEntity.ok().body(null);
    }

}
