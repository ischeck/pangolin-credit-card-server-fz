package cn.fintecher.pangolin.service.domain.service;

import cn.fintecher.pangolin.common.enums.*;
import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.model.WebSocketMessageModel;
import cn.fintecher.pangolin.common.model.response.LoginResponse;
import cn.fintecher.pangolin.common.utils.InnerServiceUrl;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.domain.AssistCaseApply;
import cn.fintecher.pangolin.entity.domain.AssistCollectionCase;
import cn.fintecher.pangolin.entity.domain.BasicCaseApply;
import cn.fintecher.pangolin.entity.domain.PublicCase;
import cn.fintecher.pangolin.service.domain.client.WebSocketClient;
import cn.fintecher.pangolin.service.domain.model.request.RetractAssistCaseRequest;
import cn.fintecher.pangolin.service.domain.respository.AssistCaseApplyRepository;
import cn.fintecher.pangolin.service.domain.respository.AssistCaseRepository;
import cn.fintecher.pangolin.service.domain.respository.BasicCaseApplyRepository;
import cn.fintecher.pangolin.service.domain.respository.PublicCaseRepository;
import org.apache.commons.collections4.IteratorUtils;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.List;
import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

/**
 * Created by BBG on 2018/8/2.
 */
@Service("domainBaseService")
public class DomainBaseService {

    Logger log = LoggerFactory.getLogger(DomainBaseService.class);

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    WebSocketClient webSocketClient;

    @Autowired
    private AssistCaseRepository assistCaseRepository;

    @Autowired
    private AssistCaseApplyRepository assistCaseApplyRepository;

    @Autowired
    private BasicCaseApplyRepository caseApplyRepository;

    @Autowired
    private PublicCaseRepository publicCaseRepository;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    /***
     * 获取用户model
     * @param token
     * @return
     */
    public OperatorModel getOperator(String token){
        ResponseEntity<LoginResponse> exchange = restTemplate.exchange(InnerServiceUrl.MANAGEMENT_SERVICE_GETBYTOKEN.concat(token), HttpMethod.GET, null, LoginResponse.class);
        OperatorModel operator = exchange.getBody().getUser();
        if (Objects.isNull(operator)) {
            throw new BadRequestException(null, "login", "operator.not.login");
        }
        return operator;
    }

    /***
     * 获取用户model
     * @param token
     * @return
     */
    public LoginResponse getLoginResponse(String token){
        ResponseEntity<LoginResponse> exchange = restTemplate.exchange(InnerServiceUrl.MANAGEMENT_SERVICE_GETBYTOKEN.concat(token), HttpMethod.GET, null, LoginResponse.class);
        OperatorModel operator = exchange.getBody().getUser();
        if (Objects.isNull(operator)) {
            throw new BadRequestException(null, "login", "operator.not.login");
        }
        return exchange.getBody();
    }

    /***
     * 判断操作人是否是manager
     * @param operator
     * @param qb
     * @return BoolQueryBuilder
     */
    public BoolQueryBuilder vaildManager(OperatorModel operator, BoolQueryBuilder qb, String leaveFlag, String groupType){
        if(operator.getIsManager().equals(ManagementType.YES) || GroupType.GROUP_CASE.toString().equals(groupType)){
            qb.must(termQuery("departments", operator.getOrganization()));
        }else {
            qb.must(matchPhraseQuery("currentCollector.id", operator.getId()));
        }
        qb = searchCondition(leaveFlag, qb);
        return qb;
    }

    /***
     * 查询条件
     * @return BoolQueryBuilder
     */
    public BoolQueryBuilder searchCondition(String leaveFlag, BoolQueryBuilder qb){
        //查询留案时传入留案标识
        if(Objects.nonNull(leaveFlag)){
            qb.must(matchPhraseQuery("leaveFlag", CaseLeaveFlag.HAS_LEAVE.toString()));
        }else {
            qb.mustNot(matchPhraseQuery("leaveFlag", CaseLeaveFlag.HAS_LEAVE.toString()));
        }
        qb.mustNot(matchPhraseQuery("caseDataStatus", CaseDataStatus.OUT_POOL.toString()))
                .mustNot(matchPhraseQuery("caseDataStatus", CaseDataStatus.PAUSE.toString()));
        qb.must(matchPhraseQuery("issuedFlag", CaseIssuedFlag.PERSONAL_HAS_ISSUED.toString()));
        return qb;
    }

    /***
     * 发消息给单个催收员
     * @param type
     * @param title
     * @param content
     * @param userName
     */
    public void sendMessageToOne(MessageType type,String title, String content, String userName) {
        try {
            WebSocketMessageModel webSocketMessageModel = new WebSocketMessageModel();
            webSocketMessageModel.setMessageType(type);
            webSocketMessageModel.setTitle(title);
            webSocketMessageModel.setContent(content);
            webSocketMessageModel.setMsgDate(ZWDateUtil.getNowDateTime());
            webSocketMessageModel.setMessageMode(MessageMode.POPUP);
            webSocketClient.sendMsgByUserId(webSocketMessageModel, userName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /***
     * 发消息给一组审批人员
     * @param type
     * @param title
     * @param content
     * @param userNames
     */
    public void sendMessageToMany(MessageType type,String title, String content, List<String> userNames) {
        try {
            WebSocketMessageModel webSocketMessageModel = new WebSocketMessageModel();
            webSocketMessageModel.setMessageType(type);
            webSocketMessageModel.setTitle(title);
            webSocketMessageModel.setContent(content);
            webSocketMessageModel.setMsgDate(ZWDateUtil.getNowDateTime());
            webSocketMessageModel.setMessageMode(MessageMode.POPUP);
            userNames.forEach(name ->webSocketClient.sendMsgByUserId(webSocketMessageModel, name));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /***
     * 结清/退案/停催时结束各个申请以及案件
     * @param caseIds
     * @param approvedMemo
     */
    public void endApplyCase(List<String> caseIds, String approvedMemo){
        //检查是否存在各类申请
        endBasicApply(caseIds, approvedMemo);
        //检查是否存在公共案件, 然后删除公共案件
        endPublicCase(caseIds);
        //检查是否存在协催案件申请
        retractAssistCaseApply(null,null,caseIds);
        //检查是否存在协催案件
        retractAssistCollectionCase(null,null,caseIds);
    }

    /***
     * 结束协催申请
     * @param request
     */
    public void retractAssistCaseApply(RetractAssistCaseRequest request, MessageType messageType, List<String> caseIds){
        log.info("结束协催申请开始...");
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        if(Objects.nonNull(messageType)){
            if(Objects.nonNull(request.getPersonalAddressId())){
                qb.must(matchPhraseQuery("personalAddressId", request.getPersonalAddressId()));
                qb.must(QueryBuilders.boolQuery().should(matchPhraseQuery("assistFlag", AssistFlag.OFFSITE_OUT_ASSIST.toString()))
                        .should(matchPhraseQuery("assistFlag", AssistFlag.LOCAL_OUT_ASSIST.toString())));
            }
            if(Objects.nonNull(request.getCaseId())) {
                qb.must(matchPhraseQuery("caseId", request.getCaseId()));
                qb.must(matchPhraseQuery("assistFlag",AssistFlag.OFFSITE_PHONE_ASSIST.toString() ));
            }
        }else {
            qb.must(termsQuery("caseId", caseIds));
        }
        qb.must(QueryBuilders.boolQuery().should(matchPhraseQuery("approveStatus", AssistApprovedStatus.ASSIST_WAIT_APPROVAL.toString()))
                .should(matchPhraseQuery("approveStatus", AssistApprovedStatus.LOCAL_WAIT_APPROVAL.toString())));
        //撤回协催申请案件
        Iterable<AssistCaseApply> search = assistCaseApplyRepository.search(qb);
        log.debug(qb.toString());
        if(search.iterator().hasNext()){
            List<AssistCaseApply> applies = IteratorUtils.toList(search.iterator());
            applies.forEach(assistCaseApply -> {
                if(assistCaseApply.getAssistFlag().equals(AssistFlag.OFFSITE_OUT_ASSIST) ||
                        assistCaseApply.getAssistFlag().equals(AssistFlag.OFFSITE_PHONE_ASSIST)){
                    assistCaseApply.setApproveStatus(AssistApprovedStatus.ASSIST_COMPLETED);
                    assistCaseApply.setApproveResult(AssistApprovedResult.ASSIST_REJECT);
                }else{
                    assistCaseApply.setApproveStatus(AssistApprovedStatus.LOCAL_COMPLETED);
                    assistCaseApply.setApproveResult(AssistApprovedResult.LOCAL_REJECT);
                }
                assistCaseApply.setApproveMemo("原催收员撤回该案件");
            });
            assistCaseApplyRepository.saveAll(applies);
        }
    }

    /**
     * 结束协催案件
     * @param request
     */
    public void retractAssistCollectionCase(RetractAssistCaseRequest request, MessageType messageType, List<String> caseIds){
        log.info("结束协催案件开始...");
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        if(Objects.nonNull(messageType)){
            if(Objects.nonNull(request.getPersonalAddressId())){
                qb.must(matchPhraseQuery("personalAddressId", request.getPersonalAddressId()));
                qb.must(QueryBuilders.boolQuery().should(matchPhraseQuery("assistFlag", AssistFlag.OFFSITE_OUT_ASSIST.toString()))
                        .should(matchPhraseQuery("assistFlag", AssistFlag.LOCAL_OUT_ASSIST.toString())));
            }
            if(Objects.nonNull(request.getCaseId())) {
                qb.must(matchPhraseQuery("caseId", request.getCaseId()));
                qb.must(matchPhraseQuery("assistFlag",AssistFlag.OFFSITE_PHONE_ASSIST.toString() ));
            }
        }else {
            qb.must(termsQuery("caseId", caseIds));
        }
        qb.mustNot(matchPhraseQuery("assistStatus", AssistStatus.ASSIST_COMPLETED.toString()));
        //撤回协催案件
        Iterable<AssistCollectionCase> assistStatus = assistCaseRepository.search(qb);
        if(assistStatus.iterator().hasNext()){
            List<AssistCollectionCase> applies = IteratorUtils.toList(assistStatus.iterator());
            applies.forEach(assistCase -> {
                assistCase.setAssistStatus(AssistStatus.ASSIST_COMPLETED);
                if(Objects.nonNull(messageType)){
                    sendMessageToOne(MessageType.ASSIST_CALL_BACK, "协催撤回", "协催人员[" + assistCase.getCurrentCollector().getFullName() + "]撤回了欠款人["+assistCase.getPersonalName()+"的协催案件", assistCase.getCurrentCollector().getUsername());
                }
            });
            assistCaseRepository.saveAll(applies);
        }
    }

    /***
     * 结束各类申请
     * @param caseIds
     * @param approvedMemo
     */
    public void endBasicApply(List<String> caseIds, String approvedMemo){
        log.info("结束各类申请开始...");
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        builder.must(termsQuery("caseId", caseIds)).must(matchPhraseQuery("approvalStatus", ApprovalStatus.WAIT_APPROVAL.toString()));
        Iterable<BasicCaseApply> search = caseApplyRepository.search(builder);
        if(search.iterator().hasNext()){
            List<BasicCaseApply> basicCaseApplies = IteratorUtils.toList(search.iterator());
            basicCaseApplies.forEach(basicCaseApply -> {
                basicCaseApply.setApprovalStatus(ApprovalStatus.APPROVED_COMPLETED);
                basicCaseApply.setApprovedMemo(approvedMemo);
                basicCaseApply.setApprovedResult(ApprovalResult.APPROVED_REJECT);
            });
            caseApplyRepository.saveAll(basicCaseApplies);
        }
    }

    /***
     * 结束公共案件
     * @param caseIds
     */
    public void endPublicCase(List<String> caseIds){
        log.info("结束公共案件开始...");
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        builder.must(termsQuery("caseId", caseIds));
        Iterable<PublicCase> search = publicCaseRepository.search(builder);
        if(search.iterator().hasNext()){
            List<PublicCase> publicCases = IteratorUtils.toList(search.iterator());
            publicCaseRepository.deleteAll(publicCases);
        }
    }

    /**
     * 获取案件金额统计
     */
    public Double getTotalAmt(QueryBuilder qb){
        SumAggregationBuilder sumBuilder = AggregationBuilders.sum("sum").field("leftAmt");
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices("base_case")
                .withTypes("base_case")
                .withSearchType(SearchType.DEFAULT)
                .withQuery(qb)
                .addAggregation(sumBuilder).build();
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, response -> response.getAggregations());
        InternalSum sum = aggregations.get("sum");
        return sum.getValue();
    }
}
