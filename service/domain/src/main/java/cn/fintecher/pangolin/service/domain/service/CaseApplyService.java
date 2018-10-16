package cn.fintecher.pangolin.service.domain.service;


import cn.fintecher.pangolin.common.enums.*;
import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.ApproveFlowConfigModel;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.model.OperatorSearchModel;
import cn.fintecher.pangolin.common.utils.InnerServiceUrl;
import cn.fintecher.pangolin.common.utils.Snowflake;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.domain.*;
import cn.fintecher.pangolin.entity.managentment.Operator;
import cn.fintecher.pangolin.service.domain.client.OperatorClient;
import cn.fintecher.pangolin.service.domain.model.request.ApplyCaseApproveRequest;
import cn.fintecher.pangolin.service.domain.model.request.ApplyCaseRequest;
import cn.fintecher.pangolin.service.domain.model.request.AssistApplyApproveModel;
import cn.fintecher.pangolin.service.domain.model.request.AssistCaseApplyRequest;
import cn.fintecher.pangolin.service.domain.model.response.BasicCaseApplyResponse;
import cn.fintecher.pangolin.service.domain.respository.*;
import org.apache.commons.collections4.IteratorUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.AbstractDoubleSearchScript;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

/**
 * Created by huyanmin on 2018/07/16.
 */
@Service("caseApplyService")
public class CaseApplyService {

    final Logger log = LoggerFactory.getLogger(CaseApplyService.class);

    @Autowired
    private BaseCaseRepository baseCaseRepository;

    @Autowired
    private AssistCaseApplyRepository assistCaseApplyRepository;

    @Autowired
    private AssistCaseRepository assistCaseRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PublicCaseRepository publicCaseRepository;

    @Autowired
    private BasicCaseApplyRepository basicCaseApplyRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PersonalContactRepository personalContactRepository;

    @Autowired
    private PaymentRecordRepository paymentRecordRepository;

    @Autowired
    OperatorClient operatorClient;

    @Autowired
    DomainBaseService domainBaseService;


    /***
     * 协催申请
     * @param request
     * @param operatorModel
     * @return
     * @throws Exception
     */
    public void setAssistCaseApply(AssistCaseApplyRequest request, OperatorModel operatorModel) throws BadRequestException {

        //验证改案件是否存在
        BaseCase baseCase = validCaseExist(request.getCaseId());
        //标记地址外放是否收回
        if (request.getAssistFlag().equals(AssistFlag.LOCAL_OUT_ASSIST) || request.getAssistFlag().equals(AssistFlag.OFFSITE_OUT_ASSIST)) {
            //验证该地址是否存在申请
            vaildPersonalAddressApply(request);
        } else if (request.getAssistFlag().equals(AssistFlag.OFFSITE_PHONE_ASSIST)) {
            //验证该电话是否存在申请
            if (AssistFlag.HAS_ASSIST.equals(baseCase.getAssistFlag())) {
                throw new BadRequestException(null, "assistCase", "assistCaseApply.is.applied");
            }
        } else {
            //验证信函
            validAssistCaseApply(request);
        }
        AssistCaseApply assistCaseApply = setAssistCaseApply(request, operatorModel, baseCase);
        assistCaseApplyRepository.save(assistCaseApply);
        if (request.getAssistFlag().equals(AssistFlag.LOCAL_OUT_ASSIST) || request.getAssistFlag().equals(AssistFlag.OFFSITE_OUT_ASSIST)) {
            setPersonalPerAddressState(assistCaseApply.getPersonalContactId(), request.getPersonalAddressId(), AssistFlag.HAS_ASSIST);
        }
        if (request.getAssistFlag().equals(AssistFlag.OFFSITE_PHONE_ASSIST)) {
            baseCase.setAssistFlag(AssistFlag.HAS_ASSIST);
            baseCaseRepository.save(baseCase);
        }
    }

    /***
     * 验证是否存在协催申请
     * @param request
     */
    private void validAssistCaseApply(AssistCaseApplyRequest request) {
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        qb.must(matchPhraseQuery("caseId", request.getCaseId()))
                .must(matchPhraseQuery("assistFlag", request.getAssistFlag().toString()))
                .must(matchPhraseQuery("approveStatus", AssistApprovedStatus.LOCAL_WAIT_APPROVAL.toString()))
                .must(matchPhraseQuery("addressDetail", request.getAddressDetail()));
        //本地信函申请和外放申请
        Iterable<AssistCaseApply> local = assistCaseApplyRepository.search(qb);
        if (local.iterator().hasNext()) {
            throw new BadRequestException(null, "assistCase", "assistCaseApply.is.applied");
        }
    }


    /**
     * 验证地址是否存在申请
     *
     * @param request
     */
    private void vaildPersonalAddressApply(AssistCaseApplyRequest request) {
        PersonalContact personalContact = vaildPersonalAddress(request.getPersonalContactId());
        Set<PersonalPerAddr> personalPerAddrs = personalContact.getPersonalPerAddrs();
        if (personalPerAddrs.iterator().hasNext()) {
           for(PersonalPerAddr personalPerAddr: personalPerAddrs){
               if (personalPerAddr.getId().equals(request.getPersonalAddressId()) && AssistFlag.HAS_ASSIST.equals(personalPerAddr.getAssistAddressFlag())) {
                   throw new BadRequestException(null, "personalAddress", "assistCaseApply.is.applied");
               }
           }
        }
    }

    /***
     * 设置公共申请值
     * @param request
     * @param operatorModel
     * @param baseCase
     * @return
     */
    public AssistCaseApply setAssistCaseApply(AssistCaseApplyRequest request, OperatorModel operatorModel,
                                              BaseCase baseCase) {
        //根据申请人的组织id，获取该组织对应分公司下的所有公司
        Set<String> organizationSet = getOrganization(operatorModel.getOrganization());
        //根据申请的部门去获取所有协助部门的子部门
        Set<String> approveOrgList = new LinkedHashSet<>();
        if (Objects.nonNull(request.getAssistDeptId())) {
            approveOrgList = getOrganization(request.getAssistDeptId());
        }
        ApproveFlowConfigModel configModel = searchConfig(ApplyType.LOCAL_ASSIST_APPLY);
        AssistCaseApply caseApply = new AssistCaseApply();
        if (Objects.nonNull(configModel.getId())) {
            BeanUtils.copyProperties(request, caseApply);
            caseApply.setAssistDeptIds(approveOrgList);
            caseApply.setCurrentApprovalLevel(1);
            caseApply.setConfigFlowApproval(configModel);
            //添加审批流程中的一级角色
            caseApply.setRoleIds(caseApply.getConfigFlowApproval().getConfigMap().get(caseApply.getCurrentApprovalLevel()).getRoleIds());
            caseApply.setRoleHistoryIds(caseApply.getConfigFlowApproval().getConfigMap().get(caseApply.getCurrentApprovalLevel()).getRoleIds());
            caseApply.setApprovedDeptIds(organizationSet);
            caseApply.setApplyRealName(operatorModel.getFullName());
            caseApply.setApplyUserName(operatorModel.getUsername());
            caseApply.setPersonalName(baseCase.getPersonal().getPersonalName());
            caseApply.setTargetName(request.getPersonalName());
            caseApply.setApplyDeptIds(organizationSet);
            caseApply.setRelation(request.getRelationShip());
            caseApply.setApproveStatus(AssistApprovedStatus.LOCAL_WAIT_APPROVAL);
            caseApply.setOperator(operatorModel.getId());
            caseApply.setOperatorDate(ZWDateUtil.getNowDateTime());
            caseApply.setIdCard(baseCase.getPersonal().getCertificateNo());
            caseApply.setPrincipalName(baseCase.getPrincipal().getPrincipalName());
            caseApply.setPrincipalId(baseCase.getPrincipal().getId());
            caseApply.setBatchNumber(baseCase.getBatchNumber());
            caseApply.setApplyDate(Objects.nonNull(request.getApplyTime()) ? request.getApplyTime() : ZWDateUtil.getNowDateTime());
            Snowflake snowflake = new Snowflake((int) (Thread.currentThread().getId() % 1024));
            caseApply.setId(String.valueOf(snowflake.next()));
        } else {
            throw new BadRequestException(null, "configFlow", "configFlow.is.not.exist");
        }
        sendManyAssistMessage(caseApply);
        return caseApply;
    }

    /***
     * 获取审批流程配置
     * @param applyType
     * @return
     */
    private ApproveFlowConfigModel searchConfig(ApplyType applyType) {
        List<ApproveFlowConfigModel> flowConfigModel = getFlowConfigModel();
        ApproveFlowConfigModel configModel = new ApproveFlowConfigModel();
        if (flowConfigModel.size() > 0) {
            for (ApproveFlowConfigModel config : flowConfigModel) {
                if (Objects.equals(config.getConfigType(), applyType)) {
                    configModel = config;
                }
            }
        }
        return configModel;
    }

    /***
     * 异地协催审批
     * @param request
     * @param operatorModel
     */
    public void assistApplyApproval(AssistApplyApproveModel request, OperatorModel operatorModel) {

        List<AssistCaseApply> list = new ArrayList<>();
        List<AssistCollectionCase> collectionCases = new ArrayList<>();
        if (request.getIdList().size() > 0) {
            for (String id : request.getIdList()) {
                validRepeatAssistDiffApprove(id);
                //验证该协催类型是否存在
                AssistCaseApply assistCaseApply = validAssistCaseExist(id);
                //验证案件是否存在
                BaseCase baseCase = validCaseExist(assistCaseApply.getCaseId());
                ApprovalResult approvedResult = request.getApproveResult();
                //创建协催案件
                if (Objects.equals(approvedResult, ApprovalResult.APPROVED_PASS)) {
                    if (Objects.equals(assistCaseApply.getApproveStatus(), AssistApprovedStatus.LOCAL_WAIT_APPROVAL)) {
                        //判断本地审批流程是否结束
                        if (assistCaseApply.getCurrentApprovalLevel().equals(assistCaseApply.getConfigFlowApproval().getLevel())) {
                            ApproveFlowConfigModel configModel = searchConfig(ApplyType.DIFFERENT_ASSIST_APPLY);
                            if (Objects.nonNull(configModel.getId())) {
                                assistCaseApply.setConfigFlowApproval(configModel);
                                //本地流程审批完成
                                assistCaseApply.setApproveStatus(AssistApprovedStatus.ASSIST_WAIT_APPROVAL);
                                assistCaseApply.setApproveResult(AssistApprovedResult.LOCAL_PASS);
                                assistCaseApply.setApproveMemo(request.getApproveMemo());
                                assistCaseApply.setCurrentApprovalLevel(1);
                                assistCaseApply.getApprovedDeptIds().clear();
                                assistCaseApply.getRoleIds().clear();
                                //如果当前角色为空，则存储上一个level的角色
                                assistCaseApply.setRoleIds(configModel.getConfigMap().get(assistCaseApply.getCurrentApprovalLevel()).getRoleIds());
                                assistCaseApply.getRoleHistoryIds().addAll(assistCaseApply.getRoleIds());
                                assistCaseApply.setApprovedDeptIds(assistCaseApply.getAssistDeptIds());
                                sendManyAssistMessage(assistCaseApply);
                            } else {
                                throw new BadRequestException(null, "configFlow", "configFlow.is.not.exist");
                            }
                        } else {
                            assistCaseApply.setCurrentApprovalLevel(assistCaseApply.getCurrentApprovalLevel() + 1);
                            assistCaseApply.getRoleIds().clear();
                            assistCaseApply.setRoleIds(assistCaseApply.getConfigFlowApproval().getConfigMap().get(assistCaseApply.getCurrentApprovalLevel()).getRoleIds());
                            assistCaseApply.getRoleHistoryIds().addAll(assistCaseApply.getRoleIds());
                            sendManyAssistMessage(assistCaseApply);
                        }

                    } else if (Objects.equals(assistCaseApply.getApproveStatus(), AssistApprovedStatus.ASSIST_WAIT_APPROVAL)) {
                        //异地的审批流程
                        if (assistCaseApply.getCurrentApprovalLevel().equals(assistCaseApply.getConfigFlowApproval().getLevel())) {
                            assistCaseApply.setApproveStatus(AssistApprovedStatus.ASSIST_COMPLETED);
                            assistCaseApply.setApproveResult(AssistApprovedResult.ASSIST_PASS);
                            assistCaseApply.setApproveMemo(request.getApproveMemo());
                            //如果当前角色为空，则存储上一个level的角色
                            assistCaseApply.getApprovedDeptIds().addAll(assistCaseApply.getApplyDeptIds());
                            //如果当前角色为空，则存储上一个level的角色
                            assistCaseApply.getRoleIds().addAll(assistCaseApply.getRoleHistoryIds());
                            AssistCollectionCase assistCollectionCase = generalAssistCollection(assistCaseApply, baseCase, operatorModel);
                            collectionCases.add(assistCollectionCase);
                            sendOneAssistMessage(assistCaseApply);
                        } else {
                            assistCaseApply.setCurrentApprovalLevel(assistCaseApply.getCurrentApprovalLevel() + 1);
                            assistCaseApply.getRoleIds().clear();
                            assistCaseApply.setRoleIds(assistCaseApply.getConfigFlowApproval().getConfigMap().get(assistCaseApply.getCurrentApprovalLevel()).getRoleIds());
                            assistCaseApply.getRoleHistoryIds().addAll(assistCaseApply.getRoleIds());
                            sendManyAssistMessage(assistCaseApply);
                        }
                    }
                } else {
                    if (Objects.equals(assistCaseApply.getApproveStatus(), AssistApprovedStatus.LOCAL_WAIT_APPROVAL)) {
                        assistCaseApply.setApproveStatus(AssistApprovedStatus.LOCAL_COMPLETED);
                        assistCaseApply.setApproveResult(AssistApprovedResult.LOCAL_REJECT);
                    } else if (Objects.equals(assistCaseApply.getApproveStatus(), AssistApprovedStatus.ASSIST_WAIT_APPROVAL)) {
                        assistCaseApply.setApproveStatus(AssistApprovedStatus.ASSIST_COMPLETED);
                        assistCaseApply.setApproveResult(AssistApprovedResult.ASSIST_REJECT);
                    }
                    //将地址协助类型去掉
                    if (assistCaseApply.getAssistFlag().equals(AssistFlag.OFFSITE_PHONE_ASSIST)) {
                        baseCase.setAssistFlag(AssistFlag.NO_ASSIST);
                        baseCaseRepository.save(baseCase);
                    }else {
                        setPersonalPerAddressState(assistCaseApply.getPersonalContactId(), assistCaseApply.getPersonalAddressId(), AssistFlag.NO_ASSIST);
                    }
                    sendOneAssistMessage(assistCaseApply);
                }
                assistCaseApply.setApproveName(operatorModel.getFullName());
                assistCaseApply.setApproveID(operatorModel.getId());
                assistCaseApply.setApproveMemo(request.getApproveMemo());
                assistCaseApply.setApproveTime(ZWDateUtil.getNowDateTime());
                assistCaseApply.setOperator(operatorModel.getId());
                assistCaseApply.setOperatorName(operatorModel.getFullName());
                assistCaseApply.setOperatorDate(ZWDateUtil.getNowDateTime());
                list.add(assistCaseApply);
            }
            assistCaseApplyRepository.saveAll(list);
            if (collectionCases.size() > 0) {
                assistCaseRepository.saveAll(collectionCases);
            }
        }
    }

    /***
     * 本地协催审批
     * @param request
     * @param operatorModel
     */
    public void localApplyApproval(AssistApplyApproveModel request, OperatorModel operatorModel) {
        List<BaseCase> baseCases = new ArrayList<>();
        List<AssistCaseApply> assistLocalList = new ArrayList<>();
        List<AssistCollectionCase> assistlist = new ArrayList<>();
        if (request.getIdList().size() > 0) {
            for (String id : request.getIdList()) {
                //防止重复申请
                validRepeatAssistApprove(id);
                AssistCaseApply assistCaseApply = validAssistCaseExist(id);
                BaseCase baseCase = validCaseExist(assistCaseApply.getCaseId());
                ApprovalResult approvedResult = request.getApproveResult();
                //设置本地协催申请的状态
                if (Objects.equals(approvedResult, ApprovalResult.APPROVED_PASS)) {
                    //当前申请流程的级数与设置的审批流程相同，则审批完成，否则状态依然是待审批
                    if (assistCaseApply.getCurrentApprovalLevel().equals(assistCaseApply.getConfigFlowApproval().getLevel())) {
                        assistCaseApply.setApproveStatus(AssistApprovedStatus.LOCAL_COMPLETED);
                        assistCaseApply.setApproveResult(AssistApprovedResult.LOCAL_PASS);
                        assistCaseApply.setRoleIds(assistCaseApply.getRoleHistoryIds());
                        //生成协催案件
                        AssistCollectionCase assistCollectionCase = generalAssistCollection(assistCaseApply, baseCase, operatorModel);
                        assistlist.add(assistCollectionCase);
                        sendOneAssistMessage(assistCaseApply);
                    } else {
                        //继续获取下一级的角色Id
                        assistCaseApply.setCurrentApprovalLevel(assistCaseApply.getCurrentApprovalLevel() + 1);
                        assistCaseApply.getRoleIds().clear();
                        assistCaseApply.setRoleIds(assistCaseApply.getConfigFlowApproval().getConfigMap().get(assistCaseApply.getCurrentApprovalLevel()).getRoleIds());
                        assistCaseApply.setRoleHistoryIds(assistCaseApply.getConfigFlowApproval().getConfigMap().get(assistCaseApply.getCurrentApprovalLevel()).getRoleIds());
                        sendOneAssistMessage(assistCaseApply);
                    }
                } else {
                    assistCaseApply.setApproveStatus(AssistApprovedStatus.LOCAL_COMPLETED);
                    assistCaseApply.setApproveResult(AssistApprovedResult.LOCAL_REJECT);
                    //将地址协助类型去掉
                    if (assistCaseApply.getAssistFlag().equals(AssistFlag.LOCAL_OUT_ASSIST)) {
                        setPersonalPerAddressState(assistCaseApply.getPersonalContactId(), assistCaseApply.getPersonalAddressId(), AssistFlag.NO_ASSIST);
                    }
                    sendOneAssistMessage(assistCaseApply);
                }
                assistCaseApply.setApproveName(operatorModel.getFullName());
                assistCaseApply.setApproveID(operatorModel.getId());
                assistCaseApply.setApproveMemo(request.getApproveMemo());
                assistCaseApply.setOperator(operatorModel.getId());
                assistCaseApply.setOperatorName(operatorModel.getFullName());
                assistCaseApply.setOperatorDate(ZWDateUtil.getNowDateTime());
                assistCaseApply.setApproveTime(ZWDateUtil.getNowDateTime());
                baseCases.add(baseCase);
                assistLocalList.add(assistCaseApply);
            }
            assistCaseApplyRepository.saveAll(assistLocalList);
            if (assistlist.size() > 0) {
                assistCaseRepository.saveAll(assistlist);
            }
            baseCaseRepository.saveAll(baseCases);
        }


    }

    /***
     * 验证地址记录是否存在
     * @param id
     */
    public PersonalContact vaildPersonalAddress(String id) {
        Optional<PersonalContact> byId = personalContactRepository.findById(id);
        byId.orElseThrow(() -> new BadRequestException(null, "personalContact", "personalContact.is.not.exist"));
        PersonalContact personalContact = byId.get();
        return personalContact;
    }

    public void setPersonalPerAddressState(String id, String addressId, AssistFlag assistFlag) {
        PersonalContact personalContact = vaildPersonalAddress(id);
        Set<PersonalPerAddr> personalPerAddrs = personalContact.getPersonalPerAddrs();
        personalPerAddrs.forEach(personalPerAddr -> {
            if (personalPerAddr.getId().equals(addressId) && assistFlag.equals(AssistFlag.NO_ASSIST)) {
                personalPerAddr.setAssistAddressFlag(AssistFlag.NO_ASSIST);
            }
            if(personalPerAddr.getId().equals(addressId) && assistFlag.equals(AssistFlag.HAS_ASSIST)){
                personalPerAddr.setAssistAddressFlag(AssistFlag.HAS_ASSIST);
            }
        });
        personalContactRepository.save(personalContact);
    }


    /**
     * 生成协催案件
     *
     * @param assistCaseApply
     * @param operatorModel
     * @return
     */
    private AssistCollectionCase generalAssistCollection(AssistCaseApply assistCaseApply, BaseCase baseCase, OperatorModel operatorModel) {
        AssistCollectionCase assistCollectionCase = new AssistCollectionCase();
        BeanUtils.copyProperties(assistCaseApply, assistCollectionCase);
        assistCollectionCase.setAssistStatus(AssistStatus.ASSIST_WAIT_ASSIGN);
        Set<String> organization = new HashSet<>();
        organization.add(operatorModel.getOrganization());
        assistCollectionCase.setPersonalName(baseCase.getPersonal().getPersonalName());
        assistCollectionCase.setIdCard(baseCase.getPersonal().getCertificateNo());
        assistCollectionCase.setBatchNumber(baseCase.getBatchNumber());
        Set<CardInformation> cardInformationMap = baseCase.getCardInformationSet();
        assistCollectionCase.setCardInformationSet(cardInformationMap);
        assistCollectionCase.setDepartments(organization);
        assistCollectionCase.setPrincipal(baseCase.getPrincipal());
        assistCollectionCase.setOperator(operatorModel.getId());
        assistCollectionCase.setOperatorTime(ZWDateUtil.getNowDateTime());
        return assistCollectionCase;
    }

    /***
     * 防止重复操作
     * @param id
     */
    private void validRepeatAssistApprove(String id) {
        BoolQueryBuilder qb = QueryBuilders
                .boolQuery();
        qb.must(matchPhraseQuery("id", id))
                .must(matchPhraseQuery("approveStatus", AssistApprovedStatus.LOCAL_COMPLETED.toString()));
        Iterable<AssistCaseApply> search = assistCaseApplyRepository.search(qb);
        if (search.iterator().hasNext()) {
            throw new BadRequestException(null, "baseCaseApply", "baseCaseApply.approve.repeat");
        }
    }

    /***
     * 防止重复操作
     * @param id
     */
    private void validRepeatAssistDiffApprove(String id) {
        BoolQueryBuilder qb = QueryBuilders
                .boolQuery();
        qb.must(matchPhraseQuery("id", id))
                .must(matchPhraseQuery("approveStatus", AssistApprovedStatus.ASSIST_COMPLETED.toString()));
        Iterable<AssistCaseApply> search = assistCaseApplyRepository.search(qb);
        if (search.iterator().hasNext()) {
            throw new BadRequestException(null, "baseCaseApply", "baseCaseApply.approve.repeat");
        }
    }

    /***
     * 补款申请
     * @param request
     * @param operatorModel
     * @return
     */
    public BasicCaseApply setSupAmountApply(ApplyCaseRequest request, OperatorModel operatorModel) {

        Iterable<BasicCaseApply> search = basicCaseApplyRepository.search(request.generateQueryBuilder());
        if (search.iterator().hasNext()) {
            throw new BadRequestException(null, "supplementApply", "supplementCase.is.applying");
        }
        BasicCaseApply basicCaseApply = setBasicCaseApply(request, operatorModel);
        basicCaseApply.setFileId(request.getFileId());
        basicCaseApply.setFileName(request.getFileName());
        basicCaseApply.setSupplementAmount(Double.valueOf(request.getApplyAmount()));
        return basicCaseApply;
    }


    /***
     * 减免申请
     * @param request
     * @param operatorModel
     * @return
     */
    public BasicCaseApply setDerateCaseApply(ApplyCaseRequest request, OperatorModel operatorModel) {

        Iterable<BasicCaseApply> search = basicCaseApplyRepository.search(request.generateQueryBuilder());
        if (search.iterator().hasNext()) {
            throw new BadRequestException(null, "derateCaseApply", "derateCaseApply.is.applying");
        }
        BasicCaseApply basicCaseApply = setBasicCaseApply(request, operatorModel);
        basicCaseApply.setFileId(request.getFileId());
        basicCaseApply.setFileName(request.getFileName());
        basicCaseApply.setDerateAmount(Double.valueOf(request.getApplyAmount()));
        return basicCaseApply;
    }


    /***
     * 公共案件申请
     * @param request
     * @param operatorModel
     * @return
     */
    public BasicCaseApply setPublicCaseApply(ApplyCaseRequest request, OperatorModel operatorModel) {

        //公共案件申请验证
        checkPublicApply(request);
        BoolQueryBuilder qb = QueryBuilders
                .boolQuery();
        qb.must(matchPhraseQuery("caseId", request.getCaseId())).must(matchPhraseQuery("publicCaseStatus", PublicCaseStatus.WAIT_GET_CASE.toString()));
        Iterable<PublicCase> publicCases = publicCaseRepository.search(qb);
        if (publicCases.iterator().hasNext()) {
            throw new BadRequestException(null, "publicCaseApply", "publicCaseApply.is.wait.distribute");
        }
        BasicCaseApply basicCaseApply = setBasicCaseApply(request, operatorModel);
        return basicCaseApply;

    }

    /***
     * 公共案件申请验证
     * @param request
     * @return
     */
    public void checkPublicApply(ApplyCaseRequest request) {
        Iterable<BasicCaseApply> search = basicCaseApplyRepository.search(request.generateQueryBuilder());
        if (search.iterator().hasNext()) {
            throw new BadRequestException(null, "publicCaseApply", "publicCaseApply.is.applying");
        }
    }

    /***
     * 公共案件分配申请
     * @param request
     * @param operatorModel
     * @return
     */
    public BasicCaseApply setPublicDistributeCaseApply(ApplyCaseRequest request, OperatorModel operatorModel) {
        //公共案件申请验证
        checkPublicApply(request);
        BasicCaseApply basicCaseApply = setBasicCaseApply(request, operatorModel);
        return basicCaseApply;
    }

    /***
     * 减免/补款/公共案件申请
     * @param request
     * @param operatorModel
     */
    public void setObjectCaseApply(ApplyCaseRequest request, OperatorModel operatorModel) {

        BasicCaseApply basicCaseApply = new BasicCaseApply();
        log.info("协催管理各类申请request："+request);
        if (Objects.equals(request.getApplyType(), ApplyType.DERATE_APPLY)) {
            //减免申请
            basicCaseApply = setDerateCaseApply(request, operatorModel);
        } else if (Objects.equals(request.getApplyType(), ApplyType.SUPPLEMENT_APPLY)) {
            //补款案件申请
            basicCaseApply = setSupAmountApply(request, operatorModel);
        } else if (Objects.equals(request.getApplyType(), ApplyType.PUBLIC_CASE_APPLY)) {
            //公共案件申请
            basicCaseApply = setPublicCaseApply(request, operatorModel);
        } else if (Objects.equals(request.getApplyType(), ApplyType.PUBLIC_DISTRIBUTE_CASE_APPLY)) {
            //公共案件分配申请
            basicCaseApply = setPublicDistributeCaseApply(request, operatorModel);
        } else if (Objects.equals(request.getApplyType(), ApplyType.REPORT_CASE_APPLY)) {
            //报案申请
            basicCaseApply = reportCaseApplyApply(request, operatorModel);
        } else if (Objects.equals(request.getApplyType(), ApplyType.CHECK_OVERDUE_AMOUNT_APPLY)) {
            //查账申请
            basicCaseApply = checkAmountCaseApply(request, operatorModel);
        } else if (Objects.equals(request.getApplyType(), ApplyType.CHECK_MATERIAL_APPLY)) {
            //资料审核
            basicCaseApply = checkMaterialApply(request, operatorModel);
        } else if (Objects.equals(request.getApplyType(), ApplyType.LEAVE_CASE_APPLY)) {
            //留案申请
            basicCaseApply = checkLeaveCaseApply(request, operatorModel);
        }else {
            return;
        }
        basicCaseApplyRepository.save(basicCaseApply);
        log.info("协催管理"+request.getApplyType()+"申请成功");
        sendManyBasisMessage(basicCaseApply);
    }


    /***
     * 减免/补款/公共案件审批
     * @param request
     * @param operatorModel
     */
    public void setObjectCaseApprove(ApplyCaseApproveRequest request, OperatorModel operatorModel) {

        if (request.getIdList().size() > 0) {
            Iterable<BasicCaseApply> search = basicCaseApplyRepository.findAllById(request.getIdList());
            List<BasicCaseApply> list = new ArrayList<>();
            if (search.iterator().hasNext()) {
                list = IteratorUtils.toList(search.iterator());
            }
            log.info("协催管理审批开始，案件id："+list);
            if (list.size() > 0) {
                for (BasicCaseApply basicCaseApply : list) {
                    validRepeatApprove(basicCaseApply.getId());
                    basicCaseApply = setBasicCaseApproved(basicCaseApply, request, operatorModel);
                    if (Objects.equals(request.getApplyType(), ApplyType.CHECK_OVERDUE_AMOUNT_APPLY)) {
                        //查账
                        basicCaseApply.setLatestOverdueAmount(Double.valueOf(request.getLatestOverdueAmount()));
                        basicCaseApply.setApprovedResult(ApprovalResult.APPROVED_PASS);
                        basicCaseApply.setHasPayAmount(Double.valueOf(request.getHasPayAmount()));
                    }
                    //查账申请为PTP或CP时，需要对还款记录进行同步
                    if (Objects.equals(request.getApplyType(), ApplyType.CHECK_OVERDUE_AMOUNT_APPLY)) {
                        if (Objects.nonNull(basicCaseApply.getPaymentRecordId())) {
                            log.info("协催管理生成PTP记录。。。");
                            Optional<PaymentRecord> payment = paymentRecordRepository.findById(basicCaseApply.getPaymentRecordId());
                            if (payment.isPresent()) {
                                PaymentRecord paymentRecord = payment.get();
                                paymentRecord.setHasPaymentAmt(basicCaseApply.getHasPayAmount());
                                paymentRecord.setHasPaymentDate(Objects.nonNull(paymentRecord.getHasPaymentDate()) ? paymentRecord.getHasPaymentDate() : ZWDateUtil.getNowDateTime());
                                if (Objects.equals(request.getHasPayAmount(), "0")) {
                                    paymentRecord.setIsBouncedCheck(ManagementType.YES);
                                }
                                paymentRecord.setPaymentStatus(PaymentStatus.CONFIRMED);
                                paymentRecord.setFallBackAmount(Double.parseDouble(request.getHasPayAmount()));
                                paymentRecord.setRemark(basicCaseApply.getApprovedMemo());
                                paymentRecord.setOperatorDate(ZWDateUtil.getNowDateTime());
                                paymentRecordRepository.save(paymentRecord);
                            }

                        }
                    }
                }
                basicCaseApplyRepository.saveAll(list);
                log.info("协催管理审批结束。。。");
            } else {
                throw new BadRequestException(null, "BasicCaseApply", "BasicCaseApply.is.not.found");
            }
        }
    }

    /***
     * 防止重复操作
     * @param id
     */
    private void validRepeatApprove(String id) {
        BoolQueryBuilder qb = QueryBuilders
                .boolQuery();
        qb.must(matchPhraseQuery("id", id))
                .must(matchPhraseQuery("approvalStatus", ApprovalStatus.APPROVED_COMPLETED.toString()));
        Iterable<BasicCaseApply> search = basicCaseApplyRepository.search(qb);
        if (search.iterator().hasNext()) {
            throw new BadRequestException(null, "baseCaseApply", "baseCaseApply.approve.repeat");
        }
    }

    /***
     *申请查询
     * @param applyType
     * @param caseId
     * @return
     */
    public Page<BasicCaseApplyResponse> queryApply(String applyType, String caseId, Pageable pageable) {

        BoolQueryBuilder qb = QueryBuilders
                .boolQuery();
        qb.must(matchPhraseQuery("caseId", caseId)).must(matchPhraseQuery("applyType", applyType));
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(qb).build();
        Page<BasicCaseApplyResponse> search = basicCaseApplyRepository.search(searchQuery).map(response -> {
            BasicCaseApplyResponse apply = new BasicCaseApplyResponse();
            BeanUtils.copyProperties(response, apply);
            apply.setIdCard(response.getCertificateNo());
            return apply;
        });
        return search;
    }

    /***
     * 报案申请
     * @param request
     * @param operatorModel
     */
    public BasicCaseApply reportCaseApplyApply(ApplyCaseRequest request, OperatorModel operatorModel) {

        Iterable<BasicCaseApply> search = basicCaseApplyRepository.search(request.generateQueryBuilder());
        if (search.iterator().hasNext()) {
            throw new BadRequestException(null, "reportCaseApply", "reportCaseApply.is.applying");
        }
        BasicCaseApply basicCaseApply = setBasicCaseApply(request, operatorModel);
        return basicCaseApply;
    }

    /***
     * 查账申请
     * @param request
     * @param operatorModel
     */
    public BasicCaseApply checkAmountCaseApply(ApplyCaseRequest request, OperatorModel operatorModel) {

        Iterable<BasicCaseApply> search = basicCaseApplyRepository.search(request.generateQueryBuilder());
        if (search.iterator().hasNext()) {
            throw new BadRequestException(null, "checkCaseApply", "checkCaseApply.is.applying");
        }
        BasicCaseApply basicCaseApply = setBasicCaseApply(request, operatorModel);
        basicCaseApply.setPaymentRecordId(request.getPaymentRecordId());
        return basicCaseApply;

    }

    /***
     * 资料审核
     * @param request
     * @param operatorModel
     */
    public BasicCaseApply checkMaterialApply(ApplyCaseRequest request, OperatorModel operatorModel) {
        BoolQueryBuilder queryBuilder = request.generateQueryBuilder();
        queryBuilder.must(matchPhraseQuery("applyContent", request.getApplyContent().toString()));
        Iterable<BasicCaseApply> search = basicCaseApplyRepository.search(queryBuilder);
        if (search.iterator().hasNext()) {
            throw new BadRequestException(null, "checkMaterialApply", "checkMaterialApply.is.applying");
        }
        BasicCaseApply basicCaseApply = setBasicCaseApply(request, operatorModel);
        basicCaseApply.setApplyContent(request.getApplyContent());
        basicCaseApply.setApplyFileDepartId(request.getGetFileDeptId());
        basicCaseApply.setApplyFileDepartName(request.getApplyFileDepartName());
        return basicCaseApply;
    }

    /***
     * 检查该类型资料是否被申请过
     * @param request
     */
    public BasicCaseApply validCheckMaterialApply(ApplyCaseRequest request) {
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        builder.must(matchPhraseQuery("applyType", request.getApplyType().toString()))
                .must(matchPhraseQuery("applyContent", request.getApplyContent().toString()))
                .must(matchPhraseQuery("approvedResult", ApprovalResult.APPROVED_PASS.toString()))
                .must(matchPhraseQuery("approvalStatus", ApprovalStatus.APPROVED_COMPLETED.toString()))
                .must(matchPhraseQuery("caseId", request.getCaseId()));
        Iterable<BasicCaseApply> search = basicCaseApplyRepository.search(builder);
        if (search.iterator().hasNext()) {
            List<BasicCaseApply> list = IteratorUtils.toList(search.iterator());
            list.sort(Comparator.comparing(BasicCaseApply::getApplyDate));
            BasicCaseApply basicCaseApply = list.get(0);
            return basicCaseApply;
        }
        return null;
    }

    /***
     * 留案申请
     * @param request
     * @param operatorModel
     */
    public BasicCaseApply checkLeaveCaseApply(ApplyCaseRequest request, OperatorModel operatorModel) {

        BaseCase baseCase = validCaseExist(request.getCaseId());
        if(CaseLeaveFlag.HAS_LEAVE.equals(baseCase.getLeaveFlag()) || CaseLeaveFlag.WAIT_CONFIRMED_LEAVE.equals(baseCase.getLeaveFlag())){
            throw new BadRequestException(null, "leaveCase", "leaveCase.has.leave");
        }
        BasicCaseApply basicCaseApply = setBasicCaseApply(request, operatorModel);
        baseCase.setLeaveFlag(CaseLeaveFlag.WAIT_CONFIRMED_LEAVE);
        baseCaseRepository.save(baseCase);
        return basicCaseApply;
    }

    /***
     * 基础审批
     * @param basicCaseApply
     * @param request
     * @param operatorModel
     * @return
     */
    public BasicCaseApply setBasicCaseApproved(BasicCaseApply basicCaseApply, ApplyCaseApproveRequest request, OperatorModel operatorModel) {
        if (Objects.equals(ApprovalResult.APPROVED_PASS, request.getApprovalResult())) {
            if (Objects.equals(basicCaseApply.getConfigFlowApprovalLevel(), basicCaseApply.getCurrentApprovalLevel())) {
                basicCaseApply.setApprovalStatus(ApprovalStatus.APPROVED_COMPLETED);
                basicCaseApply.setRoles(basicCaseApply.getRoleHistory());
                if (Objects.equals(basicCaseApply.getApplyType(), ApplyType.PUBLIC_CASE_APPLY)) {
                    BaseCase baseCase = validCaseExist(basicCaseApply.getCaseId());
                    PublicCase publicCase = new PublicCase();
                    Set<String> organization = getOrganization(operatorModel.getOrganization());
                    BeanUtils.copyProperties(baseCase, publicCase);
                    baseCase.setLatelyCollector(baseCase.getCurrentCollector());
                    //公共案件审批通过时，原案件不再归属任何催收员
                    baseCase.setCurrentCollector(null);
                    baseCase.setCollectionRecordCount(0);
                    publicCase.setCaseId(baseCase.getId());
                    publicCase.setDepartments(new LinkedHashSet<>(organization));
                    publicCase.setPublicCaseStatus(PublicCaseStatus.WAIT_GET_CASE);
                    publicCase.setOperator(operatorModel.getFullName());
                    publicCase.setOperatorDate(ZWDateUtil.getNowDateTime());
                    publicCaseRepository.save(publicCase);
                    baseCaseRepository.save(baseCase);
                }
                if (Objects.equals(basicCaseApply.getApplyType(), ApplyType.PUBLIC_DISTRIBUTE_CASE_APPLY)) {
                    BaseCase baseCase = validCaseExist(basicCaseApply.getCaseId());
                    baseCase.setLatelyCollector(baseCase.getCurrentCollector());
                    baseCase.setCurrentCollector(basicCaseApply.getApply());
                    baseCase.setCollectionRecordCount(0);
                    baseCase.setFollowTime(null);
                    baseCase.getDepartments().clear();
                    Set<String> organization = getParentId(basicCaseApply.getApply().getOrganization());
                    baseCase.setDepartments(organization);
                    baseCaseRepository.save(baseCase);
                    //删除公共案件池中的案件
                    publicCaseRepository.deleteById(basicCaseApply.getPublicCaseId());
                }

                if(ApplyType.LEAVE_CASE_APPLY.equals(request.getApplyType())){
                    BaseCase baseCase = validCaseExist(basicCaseApply.getCaseId());
                    baseCase.setLeaveFlag(CaseLeaveFlag.HAS_LEAVE);
                    baseCaseRepository.save(baseCase);
                }
                sendOneBasisMessage(basicCaseApply);
            } else {
                ApproveFlowConfigModel configModel = basicCaseApply.getConfigModel();
                basicCaseApply.setApprovalStatus(ApprovalStatus.WAIT_APPROVAL);
                basicCaseApply.setCurrentApprovalLevel(basicCaseApply.getCurrentApprovalLevel() + 1);
                basicCaseApply.getRoles().clear();
                basicCaseApply.setRoles(configModel.getConfigMap().get(basicCaseApply.getCurrentApprovalLevel()).getRoleIds());
                basicCaseApply.setRoleHistory(configModel.getConfigMap().get(basicCaseApply.getCurrentApprovalLevel()).getRoleIds());
                Set<String> organizationSet = getOrganization(configModel, basicCaseApply.getCurrentApprovalLevel());
                basicCaseApply.setOrganizationList(Objects.equals(configModel.getOrganizationApproveType(), OrganizationApproveType.HEADQUARTERS) ? organizationSet : basicCaseApply.getOrganizationList());
                sendManyBasisMessage(basicCaseApply);
            }
        } else {
            basicCaseApply.setApprovalStatus(ApprovalStatus.APPROVED_COMPLETED);
            if(ApplyType.LEAVE_CASE_APPLY.equals(request.getApplyType())){
                BaseCase baseCase = validCaseExist(basicCaseApply.getCaseId());
                baseCase.setLeaveFlag(CaseLeaveFlag.NO_LEAVE);
                baseCaseRepository.save(baseCase);
            }
            sendOneBasisMessage(basicCaseApply);
        }
        basicCaseApply.setApprovedResult(request.getApprovalResult());
        basicCaseApply.setApprovedMemo(request.getApproveRemark());
        basicCaseApply.setApprovedName(operatorModel.getFullName());
        basicCaseApply.setApprovedTime(ZWDateUtil.getNowDateTime());
        basicCaseApply.setOperator(operatorModel.getId());
        basicCaseApply.setOperatorDate(ZWDateUtil.getNowDateTime());
        return basicCaseApply;
    }

    /***
     * 设置基础申请数据
     * @param request
     * @param operatorModel
     * @return
     */
    private BasicCaseApply setBasicCaseApply(ApplyCaseRequest request, OperatorModel operatorModel) {
        BaseCase baseCase = validCaseExist(request.getCaseId());
        BasicCaseApply caseApply = new BasicCaseApply();
        Set<String> organization = getOrganization(operatorModel.getOrganization());
        ApproveFlowConfigModel configModel = searchConfig(request.getApplyType());
        if (Objects.nonNull(configModel.getId())) {
            caseApply.setConfigModel(configModel);
            caseApply.setConfigFlowApprovalLevel(configModel.getLevel());
            caseApply.setCurrentApprovalLevel(1);
            caseApply.setRoles(configModel.getConfigMap().get(caseApply.getCurrentApprovalLevel()).getRoleIds());
            caseApply.setRoleHistory(configModel.getConfigMap().get(caseApply.getCurrentApprovalLevel()).getRoleIds());
            caseApply.setApplyDate(ZWDateUtil.getNowDate());
            BeanUtils.copyProperties(baseCase, caseApply);
            caseApply.setPersonalName(Objects.nonNull(baseCase.getPersonal()) ? baseCase.getPersonal().getPersonalName() : null);
            caseApply.setCertificateNo(Objects.nonNull(baseCase.getPersonal()) ? baseCase.getPersonal().getCertificateNo() : null);
            caseApply.setCaseId(baseCase.getId());
            caseApply.setPrincipal(baseCase.getPrincipal());
            caseApply.setPrincipalName(Objects.nonNull(baseCase.getPrincipal()) ? baseCase.getPrincipal().getPrincipalName() : null);
            Operator operator = new Operator();
            BeanUtils.copyProperties(operatorModel, operator);
            caseApply.setApply(operator);
            caseApply.setApplyName(operatorModel.getFullName());
            caseApply.setApplyType(request.getApplyType());
            caseApply.setApplyRemark(request.getApplyRemark());
            caseApply.setExportState(ExportState.WAIT_EXPORT);
            Set<String> organization1 = getOrganization(configModel, caseApply.getCurrentApprovalLevel());
            caseApply.setOrganizationList(Objects.equals(configModel.getOrganizationApproveType(), OrganizationApproveType.HEADQUARTERS) ? organization1 : organization);
            caseApply.setApprovalStatus(ApprovalStatus.WAIT_APPROVAL);
            caseApply.setOperator(operatorModel.getId());
            caseApply.setPublicCaseId(request.getPublicCaseId());
            caseApply.setOperatorDate(ZWDateUtil.getNowDateTime());
            Snowflake snowflake = new Snowflake((int) (Thread.currentThread().getId() % 1024));
            caseApply.setId(String.valueOf(snowflake.next()));
        } else {
            throw new BadRequestException(null, "configFlow", "configFlow.is.not.exist");
        }

        return caseApply;
    }

    private Set<String> getOrganization(ApproveFlowConfigModel configModel, Integer level) {
        List<String> organizationList = configModel.getConfigMap().get(level).getOrganizationList();
        Set<String> setOrganization = new HashSet<>();
        if (organizationList.size() > 0) {
            setOrganization.addAll(new LinkedHashSet<>(organizationList));
        }
        return setOrganization;
    }

    /***
     * 验证协催申请案件是否存在
     * @param id
     * @return
     */
    private AssistCaseApply validAssistCaseExist(String id) {
        Optional<AssistCaseApply> byId = assistCaseApplyRepository.findById(id);
        byId.orElseThrow(() -> new BadRequestException(null, "assistApply", "assistCaseApply.is.not.exist"));
        AssistCaseApply assistDifferentCaseApply = byId.get();
        return assistDifferentCaseApply;
    }


    /***
     * 验证案件是否存在
     * @param caseId
     * @return
     */
    public BaseCase validCaseExist(String caseId) {
        Optional<BaseCase> optional = baseCaseRepository.findById(caseId);
        optional.orElseThrow(() -> new BadRequestException(null, "collectionCase", "collectionCase.is.not.exist"));
        BaseCase baseCase = optional.get();
        return baseCase;
    }

    /**
     * 审批时需要根据申请人的机构获取分公司的机构ID，再根据分公司的机构ID获取分公司下的所有组
     *
     * @param orgId
     * @return
     */
    public Set<String> getOrganization(String orgId) {

        ParameterizedTypeReference<Set<String>> typeRef = new ParameterizedTypeReference<Set<String>>() {
        };
        ResponseEntity<Set<String>> exchange = restTemplate.exchange(InnerServiceUrl.MANAGEMENT_SERVICE_FINDOPERATORORGANIZATION.concat(orgId), HttpMethod.GET, null, typeRef);
        Set<String> returnOrganization = new LinkedHashSet<>();
        if (exchange.hasBody()) {
            returnOrganization = exchange.getBody();
        }
        return returnOrganization;
    }

    /**
     * 审批时需要根据申请人的机构获取父类机构ID
     *
     * @param orgId
     * @return
     */
    public Set<String> getParentId(String orgId) {

        ParameterizedTypeReference<Set<String>> typeRef = new ParameterizedTypeReference<Set<String>>() {
        };
        ResponseEntity<Set<String>> exchange = restTemplate.exchange(InnerServiceUrl.MANAGEMENT_SERVICE_FIND_PARENT_ID.concat(orgId), HttpMethod.GET, null, typeRef);
        Set<String> returnOrganization = new LinkedHashSet<>();
        if (exchange.hasBody()) {
            returnOrganization = exchange.getBody();
        }
        return returnOrganization;
    }

    /***
     * 获取流程配置
     * @return
     */
    public List<ApproveFlowConfigModel> getFlowConfigModel() {
        ParameterizedTypeReference<List<ApproveFlowConfigModel>> typeRef = new ParameterizedTypeReference<List<ApproveFlowConfigModel>>() {
        };
        ResponseEntity<List<ApproveFlowConfigModel>> exchange = restTemplate.exchange(InnerServiceUrl.MANAGEMENT_SERVICE_FINDCONFIGFLOW, HttpMethod.GET, null, typeRef);
        List<ApproveFlowConfigModel> returnModel = new ArrayList<>();
        if (exchange.hasBody()) {
            returnModel = exchange.getBody();
        }
        return returnModel;
    }


    private void sendManyAssistMessage(AssistCaseApply apply) {
        try {
            OperatorSearchModel model = new OperatorSearchModel();
            model.setRoleIds(apply.getRoleIds());
            model.setOrganizationIds(apply.getApprovedDeptIds());
            List<Operator> operatorList = operatorClient.getUserList(model).getBody();
            List<String> userNames = new ArrayList<>();
            operatorList.forEach(
                    operator -> {
                        userNames.add(operator.getUsername());
                    }
            );
            log.debug("审批人用户名："+userNames);
            switch (apply.getAssistFlag()) {
                case OFFSITE_OUT_ASSIST:
                    domainBaseService.sendMessageToMany(MessageType.APPLY_APPROVE_MSG, "异地外访申请", "欠款人[" + apply.getPersonalName() + "]的异地外访申请，请处理！", userNames);
                    break;
                case LOCAL_OUT_ASSIST:
                    domainBaseService.sendMessageToMany(MessageType.APPLY_APPROVE_MSG, "本地外访申请", "欠款人[" + apply.getPersonalName() + "]的本地外访申请，请处理！", userNames);
                    break;
                case OFFSITE_PHONE_ASSIST:
                    domainBaseService.sendMessageToMany(MessageType.APPLY_APPROVE_MSG, "异地电话申请", "欠款人[" + apply.getPersonalName() + "]的异地电话申请，请处理！", userNames);
                    break;
                case LETTER_ASSIST:
                    domainBaseService.sendMessageToMany(MessageType.APPLY_APPROVE_MSG, "信函打印申请", "欠款人[" + apply.getPersonalName() + "]的信函打印申请，请处理！", userNames);
                    break;
                default:
                    return;
            }
        }catch(Exception e){
            log.error(e.getMessage(),e);
        }
    }

    private void sendOneAssistMessage(AssistCaseApply apply) {
        try {
            String result = null;
            if (Objects.equals(apply.getApproveResult(), AssistApprovedResult.LOCAL_PASS)
                    || Objects.equals(apply.getApproveResult(), AssistApprovedResult.ASSIST_PASS)) {
                result = "已通过";
            } else {
                result = "已拒绝";
            }
            switch (apply.getAssistFlag()) {
                case OFFSITE_OUT_ASSIST:
                    domainBaseService.sendMessageToOne(MessageType.APPLY_APPROVE_MSG, "异地外访申请", "欠款人[" + apply.getPersonalName() + "]的异地外访申请" + result, apply.getApplyUserName());
                    break;
                case LOCAL_OUT_ASSIST:
                    domainBaseService.sendMessageToOne(MessageType.APPLY_APPROVE_MSG, "本地外访申请", "欠款人[" + apply.getPersonalName() + "]的本地外访申请" + result, apply.getApplyUserName());
                    break;
                case OFFSITE_PHONE_ASSIST:
                    domainBaseService.sendMessageToOne(MessageType.APPLY_APPROVE_MSG, "异地电话申请", "欠款人[" + apply.getPersonalName() + "]的异地电话申请" + result, apply.getApplyUserName());
                    break;
                case LETTER_ASSIST:
                    domainBaseService.sendMessageToOne(MessageType.APPLY_APPROVE_MSG, "信函打印申请", "欠款人[" + apply.getPersonalName() + "]的信函打印申请" + result, apply.getApplyUserName());
                    break;
                default:
                    return;
            }
        }catch(Exception e){
            log.error(e.getMessage(),e);
        }
    }

    private void sendManyBasisMessage(BasicCaseApply apply) {
        try {
            OperatorSearchModel model = new OperatorSearchModel();
            model.setRoleIds(apply.getRoles());
            model.setOrganizationIds(apply.getOrganizationList());
            List<Operator> operatorList = operatorClient.getUserList(model).getBody();
            List<String> userNames = new ArrayList<>();
            operatorList.forEach(
                    operator -> {
                        userNames.add(operator.getUsername());
                    }
            );
            log.debug("审批人用户名："+userNames);
            log.info("SendTo:"+userNames);
            String result = null;
            if (Objects.equals(apply.getApprovedResult(), ApprovalResult.APPROVED_PASS)) {
                result = "已通过";
            } else {
                result = "已拒绝";
            }
            switch (apply.getApplyType()) {
                case LOCAL_ASSIST_APPLY:
                    domainBaseService.sendMessageToMany(MessageType.APPLY_APPROVE_MSG, "本地协催审批", "欠款人[" + apply.getPersonalName() + "]的本地协催审批,请处理" + result, userNames);
                    break;
                case DIFFERENT_ASSIST_APPLY:
                    domainBaseService.sendMessageToMany(MessageType.APPLY_APPROVE_MSG, "异地协催审批", "欠款人[" + apply.getPersonalName() + "]的异地协催审批,请处理" + result, userNames);
                    break;
                case DERATE_APPLY:
                    domainBaseService.sendMessageToMany(MessageType.APPLY_APPROVE_MSG, "减免申请", "欠款人[" + apply.getPersonalName() + "]的减免申请,请处理" + result, userNames);
                    break;
                case SUPPLEMENT_APPLY:
                    domainBaseService.sendMessageToMany(MessageType.APPLY_APPROVE_MSG, "补款申请", "欠款人[" + apply.getPersonalName() + "]的补款申请,请处理" + result, userNames);
                    break;
                case REPORT_CASE_APPLY:
                    domainBaseService.sendMessageToMany(MessageType.APPLY_APPROVE_MSG, "协催报案审批", "欠款人[" + apply.getPersonalName() + "]的报案申请,请处理" + result, userNames);
                    break;
                case PUBLIC_CASE_APPLY:
                    domainBaseService.sendMessageToMany(MessageType.APPLY_APPROVE_MSG, "公告案件申请", "欠款人[" + apply.getPersonalName() + "]的公告案件申请,请处理" + result, userNames);
                    break;
                case PUBLIC_DISTRIBUTE_CASE_APPLY:
                    domainBaseService.sendMessageToMany(MessageType.APPLY_APPROVE_MSG, "公共案件分配申请", "欠款人[" + apply.getPersonalName() + "]的公共案件分配申请,请处理" + result, userNames);
                    break;
                case CHECK_OVERDUE_AMOUNT_APPLY:
                    domainBaseService.sendMessageToMany(MessageType.APPLY_APPROVE_MSG, "查账申请", "欠款人[" + apply.getPersonalName() + "]的公告案件申请,请处理" + result, userNames);
                    break;
                case CHECK_MATERIAL_APPLY:
                    domainBaseService.sendMessageToMany(MessageType.APPLY_APPROVE_MSG, "资料复核申请", "欠款人[" + apply.getPersonalName() + "]的资料复核申请,请处理" + result, userNames);
                    break;
                case LEAVE_CASE_APPLY:
                    domainBaseService.sendMessageToMany(MessageType.APPLY_APPROVE_MSG, "留案申请", "欠款人[" + apply.getPersonalName() + "]的留案申请,请处理" + result, userNames);
                    break;
                default:
                    return;
            }
        }catch(Exception e){
            log.error(e.getMessage(),e);
        }
    }

    private void sendOneBasisMessage(BasicCaseApply apply) {
        try {
            switch (apply.getApplyType()) {
                case LOCAL_ASSIST_APPLY:
                    domainBaseService.sendMessageToOne(MessageType.APPLY_APPROVE_MSG, "本地协催审批", "欠款人[" + apply.getPersonalName() + "]的本地协催审批", apply.getApply().getUsername());
                    break;
                case DIFFERENT_ASSIST_APPLY:
                    domainBaseService.sendMessageToOne(MessageType.APPLY_APPROVE_MSG, "异地协催审批", "欠款人[" + apply.getPersonalName() + "]的异地协催审批", apply.getApply().getUsername());
                    break;
                case DERATE_APPLY:
                    domainBaseService.sendMessageToOne(MessageType.APPLY_APPROVE_MSG, "减免申请", "欠款人[" + apply.getPersonalName() + "]的减免申请", apply.getApply().getUsername());
                    break;
                case SUPPLEMENT_APPLY:
                    domainBaseService.sendMessageToOne(MessageType.APPLY_APPROVE_MSG, "补款申请", "欠款人[" + apply.getPersonalName() + "]的补款申请", apply.getApply().getUsername());
                    break;
                case REPORT_CASE_APPLY:
                    domainBaseService.sendMessageToOne(MessageType.APPLY_APPROVE_MSG, "协催报案审批", "欠款人[" + apply.getPersonalName() + "]的补款申请", apply.getApply().getUsername());
                    break;
                case PUBLIC_CASE_APPLY:
                    domainBaseService.sendMessageToOne(MessageType.APPLY_APPROVE_MSG, "公告案件申请", "欠款人[" + apply.getPersonalName() + "]的补款申请", apply.getApply().getUsername());
                    break;
                case PUBLIC_DISTRIBUTE_CASE_APPLY:
                    domainBaseService.sendMessageToOne(MessageType.APPLY_APPROVE_MSG, "公共案件分配申请", "欠款人[" + apply.getPersonalName() + "]的补款申请", apply.getApply().getUsername());
                    break;
                case CHECK_OVERDUE_AMOUNT_APPLY:
                    domainBaseService.sendMessageToOne(MessageType.APPLY_APPROVE_MSG, "查账申请", "欠款人[" + apply.getPersonalName() + "]的补款申请", apply.getApply().getUsername());
                    break;
                case CHECK_MATERIAL_APPLY:
                    domainBaseService.sendMessageToOne(MessageType.APPLY_APPROVE_MSG, "资料复核申请", "欠款人[" + apply.getPersonalName() + "]的补款申请", apply.getApply().getUsername());
                    break;
                case LEAVE_CASE_APPLY:
                    domainBaseService.sendMessageToOne(MessageType.APPLY_APPROVE_MSG, "留案申请", "欠款人[" + apply.getPersonalName() + "]的补款申请", apply.getApply().getUsername());
                    break;
                default:
                    return;
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
}