package cn.fintecher.pangolin.service.domain.service;


import cn.fintecher.pangolin.common.enums.*;
import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.model.PrincipalSearchModel;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.domain.*;
import cn.fintecher.pangolin.service.domain.model.request.*;
import cn.fintecher.pangolin.service.domain.model.response.*;
import cn.fintecher.pangolin.service.domain.respository.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.max.InternalMax;
import org.elasticsearch.search.aggregations.metrics.max.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.min.InternalMin;
import org.elasticsearch.search.aggregations.metrics.min.MinAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.InternalValueCount;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountAggregationBuilder;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Created by huyanmin on 2018/07/23.
 */
@Service("collectionCaseService")
public class CollectionCaseService {

    final Logger log = LoggerFactory.getLogger(CollectionCaseService.class);

    @Autowired
    private PersonalContactRepository personalContactRepository;

    @Autowired
    private PersonalAddressRepository personalAddressRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private HisCaseRepository hisCaseRepository;

    @Autowired
    private BaseCaseRepository baseCaseRepository;

    @Autowired
    private DomainBaseService domainBaseService;

    @Autowired
    private AssistCaseRepository assistCaseRepository;

    @Autowired
    FollowRemindRecordRepository followRemindRecordRepository;

    @Autowired
    PaymentRecordRepository paymentRecordRepository;


    /***
     * 执行页面联系人查询
     * @param personalId
     * @return
     */
    public List<PersonalContactModel> searchPersonalContact(String personalId) {

        List<PersonalContactModel> list = new ArrayList<>();
        BoolQueryBuilder qb = QueryBuilders
                .boolQuery();
        qb.must(matchPhraseQuery("personalId", personalId))
                .must(QueryBuilders.boolQuery().should(termQuery("sort", 1))
                        .should(termQuery("sort", 2))
                        .should(termQuery("sort", 3)));
        Iterable<PersonalContact> search = personalContactRepository.search(qb);
        if (search.iterator().hasNext()) {
            List<PersonalContact> personalContacts = IteratorUtils.toList(search.iterator());
            Type listMap = new TypeToken<List<PersonalContactModel>>() {
            }.getType();
            list = modelMapper.map(personalContacts, listMap);
        }
        return list;
    }


    /***
     * 按照批次号查询全部案件
     * @param token
     * @return
     */
    public List<CollectionCaseAggregationModel> queryBatchNumber(String token, SearchCollectionCaseRequest request) {
        BoolQueryBuilder qb = request.generateQueryBuilder();
        OperatorModel operator = domainBaseService.getOperator(token);
        qb = domainBaseService.vaildManager(operator, qb, null, Objects.nonNull(request.getGroupType()) ? request.getGroupType().toString() : null);
        if (Objects.nonNull(request.getPrincipalId())) {
            qb.must(matchPhraseQuery("principal.id", request.getPrincipalId()));
        }
        //根据不同的字段进行聚合统计
        TermsAggregationBuilder field = AggregationBuilders.terms("count").field("batchNumber.keyword").size(50);
        SumAggregationBuilder sumBuilder = AggregationBuilders.sum("sum").field("leftAmt");
        MaxAggregationBuilder field1 = AggregationBuilders.max("delegationDate").field("delegationDate");
        MinAggregationBuilder field2 = AggregationBuilders.min("endCaseDate").field("endCaseDate");
        MinAggregationBuilder field3 = AggregationBuilders.min("remindersDate").field("remindersDate");
        TermsAggregationBuilder principal = AggregationBuilders.terms("principal").field("principal.principalName.keyword");
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices("base_case")
                .withTypes("base_case")
                .withSearchType(SearchType.DEFAULT)
                .withQuery(qb)
                .addAggregation(field.subAggregation(sumBuilder)
                        .subAggregation(field1)
                        .subAggregation(field2)
                        .subAggregation(field3)
                        .subAggregation(principal)).build();

        log.debug("search Activity : query :{}, agg: {}", searchQuery.getQuery().toString(), searchQuery.getAggregations());
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, response -> response.getAggregations());
        List<CollectionCaseAggregationModel> buffList = new ArrayList<>();
        Map<String, Aggregation> map = aggregations.asMap();
        if (map.get("count") instanceof StringTerms) {
            StringTerms count = (StringTerms) map.get("count");
            for (StringTerms.Bucket bucket : count.getBuckets()) {
                CollectionCaseAggregationModel model = new CollectionCaseAggregationModel();
                model.setCaseCount(bucket.getDocCount());
                model.setBatchNumber(bucket.getKeyAsString());
                InternalSum sum = bucket.getAggregations().get("sum");
                model.setTotalAmount(sum.getValue());
                InternalMax delegationDate = bucket.getAggregations().get("delegationDate");
                model.setDelegationDate(new Date(new Double(delegationDate.getValue()).longValue()));
                InternalMin endCaseDate = bucket.getAggregations().get("endCaseDate");
                model.setEndCaseDate(new Date(new Double(endCaseDate.getValue()).longValue()));
                InternalMin remindersDate = bucket.getAggregations().get("remindersDate");
                if (Objects.nonNull(remindersDate.getValue())) {
                    model.setRemindersDate(new Date(new Double(remindersDate.getValue()).longValue()));
                }
                StringTerms principalName = bucket.getAggregations().get("principal");
                List<StringTerms.Bucket> buckets = principalName.getBuckets();
                model.setPrincipalName(buckets.get(0).getKeyAsString());
                buffList.add(model);
            }
        }
        return buffList;
    }

    /***
     * 获取不同委托方的催收案件和协催案件
     * @param token
     * @return
     */
    public Set<PrincipalSearchModel> searchPrincipal(String token) {
        Set<PrincipalSearchModel> set = new LinkedHashSet<>();
        BoolQueryBuilder qb = QueryBuilders
                .boolQuery();
        OperatorModel operator = domainBaseService.getOperator(token);
        qb = domainBaseService.vaildManager(operator, qb, null, null);
        //获取BaseCase的正常案件
        set = searchPrincipal("base_case", qb, set);

        BoolQueryBuilder builder = QueryBuilders
                .boolQuery();
        builder.must(matchPhraseQuery("currentCollector.id", operator.getId()));
        //获取协催案件中不同委托方的案件
        set = searchPrincipal("assist_collection_case", builder, set);
        return set;
    }

    /***
     * 根据用户登录去查询用户催收的委托方
     * @param index
     * @return
     */
    public Set<PrincipalSearchModel> searchPrincipal(String index, BoolQueryBuilder qb, Set<PrincipalSearchModel> set) {

        TermsAggregationBuilder principal = AggregationBuilders.terms("principalId").field("principal.id.keyword");
        TermsAggregationBuilder principalName = AggregationBuilders.terms("principalName").field("principal.principalName.keyword");

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(index)
                .withTypes(index)
                .withSearchType(SearchType.DEFAULT)
                .withQuery(qb)
                .addAggregation(principal.subAggregation(principalName)).build();
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, response -> response.getAggregations());
        Map<String, Aggregation> map = aggregations.asMap();
        if (map.get("principalId") instanceof StringTerms) {
            StringTerms principalCount = (StringTerms) map.get("principalId");
            if (principalCount.getBuckets().size() > 0) {
                for (StringTerms.Bucket bucket : principalCount.getBuckets()) {
                    PrincipalSearchModel principalSearchModel = new PrincipalSearchModel();
                    StringTerms principalName11 = bucket.getAggregations().get("principalName");
                    List<StringTerms.Bucket> buckets = principalName11.getBuckets();
                    principalSearchModel.setId(bucket.getKeyAsString());
                    principalSearchModel.setName(buckets.get(0).getKeyAsString());
                    set.add(principalSearchModel);
                }
            }
        }
        return set;
    }

    /***
     * 根据姓名和身份证号查询公债案件数量
     * @param idCard
     * @param principalId
     * @return
     */
    public Map<String, Long> searchDebtCaseCount(String idCard, String principalId) {

        //添加查询条件
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        //qb = domainBaseService.searchCondition(null, qb);
        qb.must(matchPhraseQuery("personal.certificateNo", idCard));
        TermsAggregationBuilder principal = AggregationBuilders.terms("principalId").field("principal.id.keyword");
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices("base_case")
                .withTypes("base_case")
                .withSearchType(SearchType.DEFAULT)
                .withQuery(qb)
                .addAggregation(principal).build();
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, response -> response.getAggregations());
        Map<String, Aggregation> map = aggregations.asMap();
        Map<String, Long> mapBuff = new HashMap<>();
        StringTerms principalCount = (StringTerms) map.get("principalId");
        for (StringTerms.Bucket bucket : principalCount.getBuckets()) {
            Long count = bucket.getDocCount();
            String keyAsString = bucket.getKeyAsString();
            if (keyAsString.equals(principalId)) {
                if (Objects.nonNull(count)) {
                    mapBuff.put("samePrincipal", count - 1);
                }
            } else {
                if (Objects.nonNull(count)) {
                    mapBuff.put("differentPrincipal", count);
                }
            }
        }
        if (!mapBuff.containsKey("differentPrincipal")) {
            mapBuff.put("differentPrincipal", Long.valueOf("0"));
        }
        return mapBuff;
    }

    /***
     * 查询共债案件
     * @param request
     * @return
     */
    public Page<CollectionDebtCaseResponse> searchDebtCase(CollectionDebtRequest request) {

        Iterable<BaseCase> cases = baseCaseRepository.search(request.generateQueryBuilder());
        List<CollectionDebtCaseResponse> responses = new ArrayList<>();
        PageHelper.startPage(request.getPage() + 1, request.getSize());
        Iterator<BaseCase> iteratorCase = cases.iterator();
        while (iteratorCase.hasNext()) {
            BaseCase next = iteratorCase.next();
            if (!request.getCollectionId().equals(next.getId())) {
                CollectionDebtCaseResponse response = new CollectionDebtCaseResponse();
                BeanUtils.copyProperties(next, response);
                response.setPersonalName(next.getPersonal().getPersonalName());
                response.setFullName(Objects.nonNull(next.getCurrentCollector()) ? next.getCurrentCollector().getFullName() : null);
                response.setPrincipalName(Objects.nonNull(next.getPrincipal()) ? next.getPrincipal().getPrincipalName() : null);
                Set<CardInformation> cardInformationSet = next.getCardInformationSet();
                Set<String> cardNos = new LinkedHashSet<>();
                if (cardInformationSet.size() > 0) {
                    cardInformationSet.forEach((cardInformation) -> cardNos.add(cardInformation.getCardNo()));
                }
                response.setCardNos(cardNos);
                responses.add(response);
            }
        }
        PageInfo<CollectionDebtCaseResponse> pageInfo = new PageInfo<>(responses);
        Pageable pageable = new PageRequest(request.getPage(), request.getSize());
        Page<CollectionDebtCaseResponse> page = new PageImpl<>(responses, pageable, pageInfo.getTotal());
        return page;
    }

    public List<CaseInfoStatusResponse> caseCollectionStatus(CaseCollectionStatusRequest request, String token) {

        OperatorModel operator = domainBaseService.getOperator(token);
        List<CaseInfoStatusResponse> list = new ArrayList<>();
        Type listType = new TypeToken<List<CaseInfoStatusResponse>>() {
        }.getType();
        BoolQueryBuilder queryBuilder = request.generateQueryBuilder();
        if (operator.getIsManager().equals(ManagementType.YES)) {
            queryBuilder.must(termQuery("departments", operator.getOrganization()));
        } else {
            queryBuilder.must(matchPhraseQuery("currentCollector.id", operator.getId()));
        }
        //获取协催案件的催收列表
        if (Objects.nonNull(request.getAssistFlag())) {
            //查询协催相关的案件
            List<CaseInfoStatusResponse> listContent = new ArrayList<>();
            Iterable<AssistCollectionCase> search = assistCaseRepository.search(queryBuilder);
            if (search.iterator().hasNext()) {
                List<AssistCollectionCase> assistCases = IteratorUtils.toList(search.iterator());
                List<String> caseIds = new ArrayList<>();
                assistCases.forEach(assistCase -> {
                    CaseInfoStatusResponse response = new CaseInfoStatusResponse();
                    response.setCaseId(assistCase.getCaseId());
                    response.setId(assistCase.getId());
                    listContent.add(response);
                    caseIds.add(assistCase.getCaseId());
                });
                BoolQueryBuilder qb = QueryBuilders.boolQuery();
                qb.must(matchQuery("id", caseIds));
                Iterable<BaseCase> search1 = baseCaseRepository.search(qb);
                //返回model中必须查询原案件去获取相应的值
                if (search.iterator().hasNext()) {
                    List<BaseCase> baseCases = IteratorUtils.toList(search1.iterator());
                    baseCases.forEach(baseCase -> {
                        listContent.forEach(response -> {
                            if (response.getCaseId().equals(baseCase.getId())) {
                                response.setPersonal(baseCase.getPersonal());
                                response.setCollectionStatus(baseCase.getCollectionStatus());
                                response.setLeftAmt(baseCase.getLeftAmt());
                            }
                        });
                    });
                }
            }
            return listContent;
        } else {
            Iterable<BaseCase> search = baseCaseRepository.search(queryBuilder);
            if (search.iterator().hasNext()) {
                List<BaseCase> baseCases = IteratorUtils.toList(search.iterator());
                list = modelMapper.map(baseCases, listType);
            }
        }
        return list;
    }

    /***
     * 生成Personal Contact
     * @param personalContactCreate
     * @return
     */
    public PersonalPerCall setPersonalPerCell(CreatePersonalContactRequest personalContactCreate) {
        PersonalPerCall personalPerCall = new PersonalPerCall();
        BeanUtils.copyProperties(personalContactCreate, personalPerCall);
        personalPerCall.setId(UUID.randomUUID().toString().replace("-", ""));
        personalPerCall.setPhoneState("未知");
        personalPerCall.setSource(Source.REPAIRED);
        return personalPerCall;
    }

    /***
     * 生成Personal Address
     * @param personalAddressRequest
     * @return
     */
    public PersonalPerAddr setPersonalPerAddress(CreatePersonalAddressRequest personalAddressRequest) {
        PersonalPerAddr addr = new PersonalPerAddr();
        BeanUtils.copyProperties(personalAddressRequest, addr);
        addr.setId(UUID.randomUUID().toString().replace("-", ""));
        addr.setSource(Source.REPAIRED);
        addr.setAddressState("未知");
        return addr;
    }

    /***
     * 验证联系人记录是否存在
     * @param id
     */
    public PersonalContact validPersonalContact(String id){
        Optional<PersonalContact> byId = personalContactRepository.findById(id);
        byId.orElseThrow(()->new BadRequestException(null, "personalContact","personalContact.is.not.exist"));
        PersonalContact personalContact = byId.get();
        return personalContact;
    }

    public CollectionCountResponse getCollectionCount(CollectionSearchRequest request, OperatorModel operator) {
        CollectionCountResponse countResponse = new CollectionCountResponse();
        Set<String> caseIds = new HashSet<>();
        BoolQueryBuilder boolQueryBuilder = domainBaseService.vaildManager(operator, request.generateQueryBuilder(),
                Objects.nonNull(request.getCaseLeaveFlag()) ? request.getCaseLeaveFlag().toString() : null, Objects.nonNull(request.getGroupType()) ? request.getGroupType().toString() : null);
        baseCaseRepository.search(boolQueryBuilder).forEach(baseCase -> {
            caseIds.add(baseCase.getId());
        });
        //今日跟催
        BoolQueryBuilder todayFollowBuilder = QueryBuilders.boolQuery();
        todayFollowBuilder.must(rangeQuery("date").gte(ZWDateUtil.getNowDate().getTime()));
        todayFollowBuilder.must(rangeQuery("date").lt(ZWDateUtil.getNightTime(0).getTime()));
        todayFollowBuilder.must(termsQuery("caseId.keyword", caseIds));
        countResponse.setTodayFollow(getCollectionCountDetail(todayFollowBuilder, "follow_remind_record"));
        //明日跟催
        BoolQueryBuilder tomorrowFollowBuilder = QueryBuilders.boolQuery();
        tomorrowFollowBuilder.must(rangeQuery("date").gte(ZWDateUtil.getNightTime(0).getTime()));
        tomorrowFollowBuilder.must(rangeQuery("date").lt(ZWDateUtil.getNightTime(1).getTime()));
        tomorrowFollowBuilder.must(termsQuery("caseId.keyword", caseIds));
        countResponse.setTomorrowFollow(getCollectionCountDetail(tomorrowFollowBuilder, "follow_remind_record"));
        //ptp
        BoolQueryBuilder ptpBuilder = QueryBuilders.boolQuery();
        ptpBuilder.must(matchQuery("paymentStatus", PaymentStatus.WAIT_CONFIRMED.name()));
        ptpBuilder.must(termsQuery("caseId.keyword", caseIds));
        countResponse.setPtpNumber(getCollectionCountDetail(ptpBuilder, "payment_record"));
        //重点跟进
        BoolQueryBuilder majorBuilder = QueryBuilders.boolQuery();
        majorBuilder.must(matchQuery("isMajor", ManagementType.YES.name()));
        majorBuilder.must(termsQuery("caseId.keyword", caseIds));
        countResponse.setMajorFollow(getCollectionCountDetail(majorBuilder, "base_case"));
        //1-3天未跟进
        BoolQueryBuilder oneToThreeNoFollowBuilder = QueryBuilders.boolQuery();
        oneToThreeNoFollowBuilder.must(QueryBuilders.boolQuery().should(rangeQuery("followTime").lt(ZWDateUtil.getNightTime(-4).getTime()))
                .should(QueryBuilders.boolQuery().mustNot(existsQuery("followTime"))));
        oneToThreeNoFollowBuilder.must(boolQueryBuilder);
        countResponse.setOneToThreeNoFollow(getCollectionCountDetail(oneToThreeNoFollowBuilder, "base_case"));
        //4-6天未跟进
        BoolQueryBuilder fourToSixNoFollowBuilder = QueryBuilders.boolQuery();
        fourToSixNoFollowBuilder.must(QueryBuilders.boolQuery().should(rangeQuery("followTime").lt(ZWDateUtil.getNightTime(-7).getTime()))
                .should(QueryBuilders.boolQuery().mustNot(existsQuery("followTime"))));
        fourToSixNoFollowBuilder.must(boolQueryBuilder);
        countResponse.setFourToSixNoFollow(getCollectionCountDetail(fourToSixNoFollowBuilder, "base_case"));
        //3天内退案
        BoolQueryBuilder threeDaysLeftBuilder = QueryBuilders.boolQuery();
        threeDaysLeftBuilder.must(rangeQuery("endCaseDate").lt(ZWDateUtil.getNightTime(3).getTime()));
        threeDaysLeftBuilder.must(boolQueryBuilder);
        countResponse.setThreeDaysLeft(getCollectionCountDetail(threeDaysLeftBuilder, "base_case"));
        //7天内退案
        BoolQueryBuilder sevenDaysLeftBuilder = QueryBuilders.boolQuery();
        sevenDaysLeftBuilder.must(rangeQuery("endCaseDate").lt(ZWDateUtil.getNightTime(7).getTime()));
        sevenDaysLeftBuilder.must(boolQueryBuilder);
        countResponse.setSevenDaysLeft(getCollectionCountDetail(sevenDaysLeftBuilder, "base_case"));
        return countResponse;
    }

    public long getCollectionCountDetail(BoolQueryBuilder qb, String indexName) {
        ValueCountAggregationBuilder field = AggregationBuilders.count("count").field("id.keyword");
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(indexName)
                .withTypes(indexName)
                .withSearchType(SearchType.DEFAULT)
                .withQuery(qb)
                .addAggregation(field).build();
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, response -> response.getAggregations());
        InternalValueCount count = aggregations.get("count");
        return count.getValue();
    }

    public BoolQueryBuilder queryCollectionByPrincipal(CollectionSearchRequest request, OperatorModel operator) {
        BoolQueryBuilder tempBuilder = QueryBuilders.boolQuery();
        BoolQueryBuilder qb = domainBaseService.vaildManager(operator, request.generateQueryBuilder(),
                Objects.nonNull(request.getCaseLeaveFlag()) ? request.getCaseLeaveFlag().toString() : null, Objects.nonNull(request.getGroupType()) ? request.getGroupType().toString() : null);
        if (Objects.nonNull(request.getCaseFiller())) {
            if (Objects.equals(request.getCaseFiller(), CaseFiller.TODAY_FOLLOW)) {
                //今日跟催
                tempBuilder.must(rangeQuery("date").gte(ZWDateUtil.getNowDate().getTime()));
                tempBuilder.must(rangeQuery("date").lt(ZWDateUtil.getNightTime(0).getTime()));
                Set<String> caseIds = new HashSet<>();
                followRemindRecordRepository.search(tempBuilder).forEach(followRemindRecord -> {
                    caseIds.add(followRemindRecord.getCaseId());
                });
                qb.must(termsQuery("id.keyword", caseIds));
            } else if (Objects.equals(request.getCaseFiller(), CaseFiller.TOMORROW_FOLLOW)) {
                //明日跟催
                tempBuilder.must(rangeQuery("date").gte(ZWDateUtil.getNightTime(0).getTime()));
                tempBuilder.must(rangeQuery("date").lt(ZWDateUtil.getNightTime(1).getTime()));
                Set<String> caseIds = new HashSet<>();
                followRemindRecordRepository.search(tempBuilder).forEach(followRemindRecord -> {
                    caseIds.add(followRemindRecord.getCaseId());
                });
                qb.must(termsQuery("id.keyword", caseIds));
            } else if (Objects.equals(request.getCaseFiller(), CaseFiller.PTP)) {
                //PTP
                tempBuilder.must(matchQuery("paymentStatus", PaymentStatus.WAIT_CONFIRMED.name()));
                Set<String> caseIds = new HashSet<>();
                paymentRecordRepository.search(tempBuilder).forEach(paymentRecord -> {
                    caseIds.add(paymentRecord.getCaseId());
                });
                qb.must(termsQuery("id.keyword", caseIds));
            } else if (Objects.equals(request.getCaseFiller(), CaseFiller.MAJOR_FOLLOW)) {
                //重点跟进
                tempBuilder.must(matchQuery("isMajor", ManagementType.YES.name()));
                qb.must(tempBuilder);
            } else if (Objects.equals(request.getCaseFiller(), CaseFiller.ONE_TO_THREE_NO_FOLLOW)) {
                //1-3天未跟
                tempBuilder.must(QueryBuilders.boolQuery().should(rangeQuery("followTime").lt(ZWDateUtil.getNightTime(-4).getTime()))
                        .should(QueryBuilders.boolQuery().mustNot(existsQuery("followTime"))));
                qb.must(tempBuilder);
            } else if (Objects.equals(request.getCaseFiller(), CaseFiller.FOUR_TO_SIX_NO_FOLLOW)) {
                //4-6天未跟
                tempBuilder.must(QueryBuilders.boolQuery().should(rangeQuery("followTime").lt(ZWDateUtil.getNightTime(-7).getTime()))
                        .should(QueryBuilders.boolQuery().mustNot(existsQuery("followTime"))));
                qb.must(tempBuilder);
            } else if (Objects.equals(request.getCaseFiller(), CaseFiller.THREE_DAYS_LEFT)) {
                //3天内退案
                tempBuilder.must(rangeQuery("endCaseDate").lt(ZWDateUtil.getNightTime(3).getTime()));
                qb.must(tempBuilder);
            } else if (Objects.equals(request.getCaseFiller(), CaseFiller.SEVEN_DAYS_LEFT)) {
                //7天内退案
                tempBuilder.must(rangeQuery("endCaseDate").lt(ZWDateUtil.getNightTime(7).getTime()));
                qb.must(tempBuilder);
            }
        }
        return qb;
    }

    public void hisCaseTrans() {
        try {
            log.info("退案案件转移");
            BoolQueryBuilder pauseBuilder = QueryBuilders.boolQuery();
            pauseBuilder.must(rangeQuery("endCaseDate").gte(ZWDateUtil.getNowDate().getTime()));
            pauseBuilder.must(rangeQuery("endCaseDate").lt(ZWDateUtil.getNightTime(0).getTime()));
            List<BaseCase> baseCases = IterableUtils.toList(baseCaseRepository.search(pauseBuilder));
            List<HisCase> hisCases = new ArrayList<>();
            List<String> listIds = new ArrayList<>();
            baseCases.forEach(baseCase -> {
                HisCase hisCase = new HisCase();
                BeanUtils.copyProperties(baseCase, hisCase);
                hisCase.setCaseDataStatus(CaseDataStatus.OUT_POOL);
                listIds.add(baseCase.getId());
            });
            if (listIds.size() > 0) {
                domainBaseService.endApplyCase(listIds, "案件到期退案");
            }
            if (hisCases.size() > 0) {
                hisCaseRepository.saveAll(hisCases);
            }
            if (baseCases.size() > 0) {
                baseCaseRepository.deleteAll(baseCases);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        try {
            log.info("删除案件转移");
            BoolQueryBuilder pauseBuilder = QueryBuilders.boolQuery();
            pauseBuilder.must(rangeQuery("deleteCaseDateEnd").gte(ZWDateUtil.getNowDate().getTime()));
            pauseBuilder.must(rangeQuery("deleteCaseDateEnd").lt(ZWDateUtil.getNightTime(0).getTime()));
            List<HisCase> hisCases = IterableUtils.toList(hisCaseRepository.search(pauseBuilder));
            hisCases.forEach(hisCase -> {
                hisCase.setCaseDataStatus(CaseDataStatus.DELETE);
            });
            if (Objects.nonNull(hisCases) && !hisCases.isEmpty()) {
                hisCaseRepository.saveAll(hisCases);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void caseRecordHandle() {
        try {
            log.info("提醒记录处理");
            BoolQueryBuilder followRemindBuilder = QueryBuilders.boolQuery();
            followRemindBuilder.must(rangeQuery("date").gte(ZWDateUtil.getNowDate().getTime()));
            followRemindBuilder.must(rangeQuery("date").lt(ZWDateUtil.getNightTime(0).getTime()));
            Iterable remindRecords = followRemindRecordRepository.search(followRemindBuilder);
            if (Objects.nonNull(remindRecords) && remindRecords.iterator().hasNext()) {
                followRemindRecordRepository.deleteAll(remindRecords);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 手工删除案件
     */
    public void deleteCaseByManual(DeleteCollectionCaseModel model) {
        List<HisCase> hisCaseList = new ArrayList<>();
        List<BaseCase> baseCases = IterableUtils.toList(baseCaseRepository.search(model.generateQueryBuilder()));
        baseCases.forEach(baseCase -> {
                    HisCase hisCase = new HisCase();
                    BeanUtils.copyProperties(baseCase, hisCase);
                    hisCase.setCaseDataStatus(CaseDataStatus.DELETE_MANUAL);
                    hisCaseList.add(hisCase);
                }
        );
        if (Objects.nonNull(hisCaseList) && !hisCaseList.isEmpty()) {
            hisCaseRepository.saveAll(hisCaseList);
        }
        //删除案子
        DeleteByQueryRequestBuilder deleteByQueryRequestBuilder = DeleteByQueryAction.INSTANCE.
                newRequestBuilder(elasticsearchTemplate.getClient());
        deleteByQueryRequestBuilder.source("base_case").filter(model.generateQueryBuilder()).refresh(true).get();
    }
}