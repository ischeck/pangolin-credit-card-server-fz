package cn.fintecher.pangolin.service.domain.service;

import cn.fintecher.pangolin.common.enums.ApplyType;
import cn.fintecher.pangolin.common.enums.ManagementType;
import cn.fintecher.pangolin.common.enums.PaymentStatus;
import cn.fintecher.pangolin.common.enums.AssistApprovedResult;
import cn.fintecher.pangolin.common.enums.AssistApprovedStatus;
import cn.fintecher.pangolin.common.enums.AssistFlag;
import cn.fintecher.pangolin.common.enums.FollowType;
import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.model.response.LoginResponse;
import cn.fintecher.pangolin.common.utils.Snowflake;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.domain.*;
import cn.fintecher.pangolin.service.domain.model.request.ApplyCaseRequest;
import cn.fintecher.pangolin.service.domain.model.request.CaseFindRecordModel;
import cn.fintecher.pangolin.service.domain.model.request.CaseOtherFollowModel;
import cn.fintecher.pangolin.service.domain.model.request.CreateFollowRecordModel;
import cn.fintecher.pangolin.service.domain.model.response.FollowRecordCountModel;
import cn.fintecher.pangolin.service.domain.respository.*;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;


/**
 * Created by BBG on 2018/8/2.
 */
@Service("caseFollowService")
public class CaseFollowService {

    Logger log = LoggerFactory.getLogger(CaseFollowService.class);

    @Autowired
    CaseFollowupRecordRepository caseFollowupRecordRepository;

    @Autowired
    CaseFindRecordRepository caseFindRecordRepository;

    @Autowired
    CaseOtherFollowRecordRepository caseOtherFollowRecordRepository;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    PersonalContactRepository personalContactRepository;

    @Autowired
    BaseCaseRepository baseCaseRepository;

    @Autowired
    CaseApplyService caseApplyService;

    @Autowired
    PaymentRecordRepository paymentRecordRepository;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    FollowRemindRecordRepository followRemindRecordRepository;

    public void saveFollowRecoed(CreateFollowRecordModel model, LoginResponse loginResponse) {
        OperatorModel operator = loginResponse.getUser();
        Optional<BaseCase> temp = baseCaseRepository.findById(model.getCaseId());
        BaseCase baseCase = temp.orElseThrow(() -> new BadRequestException(null, "assistApply", "baseCase.is.not.exist"));
        CaseFollowupRecord record = new CaseFollowupRecord();
        BeanUtils.copyProperties(model, record);
        record.setPersonalId(baseCase.getPersonal().getId());
        record.setOperator(operator.getId());
        record.setOperatorName(operator.getFullName());
        record.setOperatorTime(ZWDateUtil.getNowDateTime());
        record.setFollowTime(record.getOperatorTime());
        record.setOperatorDeptName(loginResponse.getOrganizationModel().getName());
        StringBuilder builder = new StringBuilder();
        String contantView = null;
        if (Objects.equals(FollowType.TEL.name(), model.getType().name())) {
            contantView = builder.append(model.getTargetName()).append("|")
                    .append(model.getTarget()).append("|")
                    .append(model.getContactPhone()).append("|")
                    .append(model.getContent()).toString();
        } else if (Objects.equals(FollowType.ADDR.name(), model.getType())) {
            contantView = builder.append(model.getTargetName()).append("|")
                    .append(model.getTarget()).append("|")
                    .append(model.getDetail()).append("|")
                    .append(model.getContent()).toString();
        }
        if(Objects.nonNull(model.getFollNextDate())){
            Snowflake snowflake = new Snowflake((int) (Thread.currentThread().getId() % 1024));
            FollowRemindRecord remindRecord = new FollowRemindRecord();
            remindRecord.setId(String.valueOf(snowflake.next()));
            remindRecord.setCaseId(baseCase.getId());
            remindRecord.setDate(model.getFollNextDate());
            followRemindRecordRepository.save(remindRecord);
        }
        record.setContentView(contantView);
        caseFollowupRecordRepository.save(record);
        baseCase.setOperator(operator.getId());
        if (Objects.equals(model.getCollectionWay(), 1)) {
            if (Objects.nonNull(baseCase.getCollectionRecordCount())) {
                baseCase.setCollectionRecordCount(baseCase.getCollectionRecordCount() + 1);
                baseCase.setCollectionTotalRecordCount(baseCase.getCollectionTotalRecordCount() + 1);
            } else {
                baseCase.setCollectionRecordCount(1);
                baseCase.setCollectionTotalRecordCount(1);
            }
            baseCase.setFollowTime(record.getOperatorTime());
            baseCase.setContactResult(model.getContactResult());
            //同步电话状态和联络结果
            if (StringUtils.isNotEmpty(model.getContactPhone()) && StringUtils.isNotEmpty(model.getPersonalContactId())) {
                List<PersonalContact> contactList = new ArrayList<>();
                Optional<PersonalContact> personalContactList = personalContactRepository.findById(model.getPersonalContactId());
                PersonalContact personalContact1 = personalContactList.get();
                Set<PersonalPerCall> personalPerCalls = personalContact1.getPersonalPerCalls();
                if(personalPerCalls.size()>0){
                    personalPerCalls.forEach(personalPerCall -> {
                        if(personalPerCall.getPhoneNo().equals(model.getContactPhone())){
                            personalPerCall.setDialPhoneCount(personalPerCall.getDialPhoneCount() == null ? 1 : personalPerCall.getDialPhoneCount() + 1);
                            personalPerCall.setPhoneState(model.getContactState());
                            personalPerCall.setContactResult(model.getContactResult());
                        }
                    });
                    contactList.add(personalContact1);
                }
                if (!contactList.isEmpty()) {
                    personalContactRepository.saveAll(contactList);
                }
            }
            //根据催记中承诺金额不为空，则自动生成PTP还款记录，不生成查账申请
            if (Objects.nonNull(record.getPromiseAmt())) {
                BoolQueryBuilder qb = QueryBuilders.boolQuery();
                qb.must(matchPhraseQuery("caseId", baseCase.getId())).must(matchPhraseQuery("paymentStatus", PaymentStatus.WAIT_CONFIRMED.toString()));
                Iterable<PaymentRecord> search = paymentRecordRepository.search(qb);
                if(search.iterator().hasNext()){
                    PaymentRecord next = search.iterator().next();
                    next.setPromiseAmt(record.getPromiseAmt());
                    next.setPromiseDate(record.getPromiseDate());
                    next.setOperatorDate(ZWDateUtil.getNowDateTime());
                    paymentRecordRepository.save(next);
                }else {
                    generalPTPRecord(baseCase.getPersonal(),record, PaymentStatus.WAIT_CONFIRMED);
                }
            }
            //根据已还款金额，生成查账申请和CP记录
            if (Objects.nonNull(record.getHasPaymentAmt())) {
                BoolQueryBuilder qb = QueryBuilders.boolQuery();
                qb.must(matchPhraseQuery("caseId", baseCase.getId())).must(matchPhraseQuery("paymentStatus", PaymentStatus.WAIT_CONFIRMED.toString()));
                Iterable<PaymentRecord> search = paymentRecordRepository.search(qb);
                //如果存在PTP记录未转CP记录，则不生成查账申请，否则生成查账申请
                if (!search.iterator().hasNext()) {
                    PaymentRecord paymentRecord = generalPTPRecord(baseCase.getPersonal(),record, PaymentStatus.CONFIRMING);
                    generalCPApply(baseCase.getPersonal(),record, operator, paymentRecord);
                }
            }
        }
        baseCase.setOperator(operator.getId());
        baseCase.setOperatorTime(record.getOperatorTime());
        baseCaseRepository.save(baseCase);
    }

    public void saveFindRecoed(CaseFindRecordModel model, OperatorModel operator) {
        Optional<BaseCase> temp = baseCaseRepository.findById(model.getCaseId());
        BaseCase baseCase = temp.orElseThrow(() -> new BadRequestException(null, "assistApply", "baseCase.is.not.exist"));
        CaseFindRecord record = new CaseFindRecord();
        BeanUtils.copyProperties(model, record);
        record.setPersonalId(baseCase.getPersonal().getId());
        record.setOperator(operator.getId());
        record.setOperatorName(operator.getFullName());
        record.setOperatorTime(ZWDateUtil.getNowDateTime());
        record.setOperatorDeptName(operator.getOrganization());
        caseFindRecordRepository.save(record);
        baseCase.setOperator(operator.getId());
        baseCase.setFollowTime(record.getFindTime());
        if (Objects.nonNull(baseCase.getCollectionRecordCount())) {
            baseCase.setCollectionRecordCount(baseCase.getCollectionRecordCount() + 1);
            baseCase.setCollectionTotalRecordCount(baseCase.getCollectionTotalRecordCount());
        } else {
            baseCase.setCollectionRecordCount(1);
            baseCase.setCollectionTotalRecordCount(1);
        }
        baseCase.setOperator(operator.getId());
        baseCase.setOperatorTime(record.getOperatorTime());
        baseCaseRepository.save(baseCase);
    }

    public void saveOtherFollowRecoed(CaseOtherFollowModel model, OperatorModel operator) {
        Optional<BaseCase> temp = baseCaseRepository.findById(model.getCaseId());
        BaseCase baseCase = temp.orElseThrow(() -> new BadRequestException(null, "assistApply", "baseCase.is.not.exist"));
        CaseOtherFollowRecord record = new CaseOtherFollowRecord();
        BeanUtils.copyProperties(model, record);
        record.setPersonalId(baseCase.getPersonal().getId());
        record.setOperator(operator.getId());
        record.setOperatorName(operator.getFullName());
        record.setOperatorTime(ZWDateUtil.getNowDateTime());
        record.setOperatorDeptName(operator.getOrganization());
        caseOtherFollowRecordRepository.save(record);
        baseCase.setOperator(operator.getId());
        baseCase.setFollowTime(record.getFollowTime());
        if (Objects.nonNull(baseCase.getCollectionRecordCount())) {
            baseCase.setCollectionRecordCount(baseCase.getCollectionRecordCount() + 1);
            baseCase.setCollectionTotalRecordCount(baseCase.getCollectionTotalRecordCount());
        } else {
            baseCase.setCollectionRecordCount(1);
            baseCase.setCollectionTotalRecordCount(1);
        }
        baseCase.setOperator(operator.getId());
        baseCase.setOperatorTime(record.getOperatorTime());
        baseCaseRepository.save(baseCase);
    }

    /***
     * 生成还款记录
     * @param record
     * @param paymentStatus
     */
    public PaymentRecord generalPTPRecord(Personal personal, CaseFollowupRecord record, PaymentStatus paymentStatus) {
        PaymentRecord paymentRecord = new PaymentRecord();
        BeanUtils.copyProperties(record, paymentRecord);
        paymentRecord.setOperatorDate(ZWDateUtil.getNowDate());
        paymentRecord.setIsBouncedCheck(ManagementType.NO);
        paymentRecord.setPaymentStatus(paymentStatus);
        paymentRecord.setPersonalName(personal.getPersonalName());
        paymentRecord.setCertificateNo(personal.getCertificateNo());
        paymentRecordRepository.save(paymentRecord);
        return paymentRecord;
    }

    /***
     * 生成CP查账记录
     * @param record
     * @param operator
     */
    public void generalCPApply(Personal personal,CaseFollowupRecord record, OperatorModel operator, PaymentRecord paymentRecord) {
        //如果已还款金额不为空，则为CP, 需要自动发起一条查账申请
        if (Objects.nonNull(record.getHasPaymentAmt())) {
            ApplyCaseRequest request = new ApplyCaseRequest();
            request.setCaseId(record.getCaseId());
            request.setApplyType(ApplyType.CHECK_OVERDUE_AMOUNT_APPLY);
            request.setPaymentRecordId(paymentRecord.getId());
            request.setApplyRemark("催记添加CP,自动生成查账申请");
            request.setPersonalName(personal.getPersonalName());
            request.setCertificateNo(personal.getCertificateNo());
            caseApplyService.setObjectCaseApply(request, operator);
        }
    }


    public FollowRecordCountModel getFollowRecordCount(String caseId) {
        FollowRecordCountModel model = new FollowRecordCountModel();
        BoolQueryBuilder qbTel = QueryBuilders
                .boolQuery();
        qbTel.must(matchPhraseQuery("caseId", caseId));
        qbTel.must(matchPhraseQuery("type", FollowType.TEL.toString()));
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("caseId").field("caseId.keyword");
        SearchQuery searchQueryTel = new NativeSearchQueryBuilder()
                .withIndices("case_followup_record")
                .withTypes("case_followup_record")
                .withSearchType(SearchType.DEFAULT)
                .withQuery(qbTel)
                .addAggregation(aggregationBuilder).build();
        Aggregations aggregationsTel = elasticsearchTemplate.query(searchQueryTel, response -> response.getAggregations());
        Map<String, Aggregation> mapTel = aggregationsTel.asMap();
        if (mapTel.get("caseId") instanceof StringTerms) {
            StringTerms principalCountTel = (StringTerms) mapTel.get("caseId");
            for (StringTerms.Bucket bucket : principalCountTel.getBuckets()) {
                model.setTelNum(bucket.getDocCount());
            }
        } else {
            model.setTelNum(0L);
        }

        BoolQueryBuilder qbAddr = QueryBuilders
                .boolQuery();
        qbAddr.must(matchPhraseQuery("caseId", caseId));
        qbAddr.must(matchPhraseQuery("type", FollowType.ADDR.toString()));
        SearchQuery searchQueryAddr = new NativeSearchQueryBuilder()
                .withIndices("case_followup_record")
                .withTypes("case_followup_record")
                .withSearchType(SearchType.DEFAULT)
                .withQuery(qbAddr)
                .addAggregation(aggregationBuilder).build();
        Aggregations aggregationsAddr = elasticsearchTemplate.query(searchQueryAddr, response -> response.getAggregations());
        Map<String, Aggregation> mapAddr = aggregationsAddr.asMap();
        if (mapAddr.get("caseId") instanceof StringTerms) {
            StringTerms principalCountAddr = (StringTerms) mapAddr.get("caseId");
            for (StringTerms.Bucket bucket : principalCountAddr.getBuckets()) {
                model.setAddrNum(bucket.getDocCount());
            }
        } else {
            model.setAddrNum(0L);
        }

        BoolQueryBuilder qbFind = QueryBuilders
                .boolQuery();
        qbFind.must(matchPhraseQuery("caseId", caseId));
        SearchQuery searchQueryFind = new NativeSearchQueryBuilder()
                .withIndices("case_find_record")
                .withTypes("case_find_record")
                .withSearchType(SearchType.DEFAULT)
                .withQuery(qbFind)
                .addAggregation(aggregationBuilder).build();
        Aggregations aggregationsFind = elasticsearchTemplate.query(searchQueryFind, response -> response.getAggregations());
        Map<String, Aggregation> mapFind = aggregationsFind.asMap();
        if (mapFind.get("caseId") instanceof StringTerms) {
            StringTerms principalCountFind = (StringTerms) mapFind.get("caseId");
            for (StringTerms.Bucket bucket : principalCountFind.getBuckets()) {
                model.setFindNum(bucket.getDocCount());
            }
        } else {
            model.setFindNum(0L);
        }

        BoolQueryBuilder qbLetter = QueryBuilders
                .boolQuery();
        qbLetter.must(matchPhraseQuery("caseId", caseId));
        qbLetter.must(matchPhraseQuery("assistFlag", AssistFlag.LETTER_ASSIST.toString()));
        qbLetter.must(matchPhraseQuery("approveStatus", AssistApprovedStatus.LOCAL_COMPLETED.toString()));
        qbLetter.must(matchPhraseQuery("approveResult", AssistApprovedResult.LOCAL_PASS.toString()));
        SearchQuery searchQueryLetter = new NativeSearchQueryBuilder()
                .withIndices("assist_case_apply")
                .withTypes("assist_case_apply")
                .withSearchType(SearchType.DEFAULT)
                .withQuery(qbLetter)
                .addAggregation(aggregationBuilder).build();
        Aggregations aggregationsLetter = elasticsearchTemplate.query(searchQueryLetter, response -> response.getAggregations());
        Map<String, Aggregation> mapLetter = aggregationsLetter.asMap();
        if (mapLetter.get("caseId") instanceof StringTerms) {
            StringTerms principalCountLetter = (StringTerms) mapLetter.get("caseId");
            for (StringTerms.Bucket bucket : principalCountLetter.getBuckets()) {
                model.setLetterNum(bucket.getDocCount());
            }
        } else {
            model.setLetterNum(0L);
        }

        return model;
    }
}


