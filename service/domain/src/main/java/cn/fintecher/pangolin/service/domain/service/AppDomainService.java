package cn.fintecher.pangolin.service.domain.service;

import cn.fintecher.pangolin.common.enums.CaseDataStatus;
import cn.fintecher.pangolin.common.enums.FollowType;
import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.model.response.LoginResponse;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.domain.*;
import cn.fintecher.pangolin.service.domain.model.request.CreateFollowRecordModel;
import cn.fintecher.pangolin.service.domain.model.response.AppBaseInfoResponse;
import cn.fintecher.pangolin.service.domain.model.response.AppCaseResponse;
import cn.fintecher.pangolin.service.domain.model.response.AppPersonalAddressModel;
import cn.fintecher.pangolin.service.domain.model.response.AppPersonalContactModel;
import cn.fintecher.pangolin.service.domain.respository.AssistCaseRepository;
import cn.fintecher.pangolin.service.domain.respository.BaseCaseRepository;
import cn.fintecher.pangolin.service.domain.respository.CaseFollowupRecordRepository;
import cn.fintecher.pangolin.service.domain.respository.PersonalContactRepository;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

@Service("appDomainService")
public class AppDomainService {

    @Autowired
    BaseCaseRepository baseCaseRepository;
    @Autowired
    CaseFollowupRecordRepository caseFollowupRecordRepository;
    @Autowired
    PersonalContactRepository personalContactRepository;
    @Autowired
    AssistCaseRepository assistCaseRepository;
    @Autowired
    ModelMapper modelMapper;

    Logger log = LoggerFactory.getLogger(AppDomainService.class);

    public void saveFollowRecoed(CreateFollowRecordModel model, LoginResponse loginResponse) {
        OperatorModel operator = loginResponse.getUser();
        AssistCollectionCase assistCollectionCase = assistCaseRepository.findById(model.getAssistId()).get();
        if (Objects.isNull(assistCollectionCase.getCollectionRecordCount())) {
            assistCollectionCase.setCollectionRecordCount(1);
        } else {
            assistCollectionCase.setCollectionRecordCount(assistCollectionCase.getCollectionRecordCount() + 1);
        }
        assistCaseRepository.save(assistCollectionCase);
        model.setType(FollowType.ADDR);
        Optional<BaseCase> temp = baseCaseRepository.findById(model.getCaseId());
        BaseCase baseCase = temp.orElseThrow(() -> new BadRequestException(null, "assistApply", "baseCase.is.not.exist"));
        CaseFollowupRecord record = new CaseFollowupRecord();
        BeanUtils.copyProperties(model, record);
        record.setPersonalId(baseCase.getPersonal().getId());
        record.setOperator(operator.getId());
        record.setOperatorName(model.getVisitors());
        record.setOperatorTime(ZWDateUtil.getNowDateTime());
        record.setFollowTime(record.getOperatorTime());
        record.setOperatorDeptName(loginResponse.getOrganizationModel().getName());
        StringBuilder builder = new StringBuilder();
        String contantView = null;
        contantView = builder.append(model.getTargetName()).append("|")
                .append(model.getTarget()).append("|")
                .append(model.getDetail()).append("|")
                .append(model.getContent()).toString();
        record.setContentView(contantView);
        caseFollowupRecordRepository.save(record);
        baseCase.setOperator(operator.getId());
        baseCase.setFollowTime(record.getOperatorTime());
        baseCase.setContactResult(model.getContactResult());
        baseCase.setContactResult(model.getContactResult());
        baseCase.setFollowTime(ZWDateUtil.getNowDate());
        //同步电话状态和联络结果
        baseCase.setOperator(operator.getId());
        baseCase.setOperatorTime(record.getOperatorTime());
        baseCaseRepository.save(baseCase);
    }

    public List<AppCaseResponse> getAllCase(List<AssistCollectionCase> assistCollectionCases, Integer collCount) {
        List<AppCaseResponse> responsesList = new ArrayList<>();
        assistCollectionCases.forEach(assistCollectionCase -> {
            AppCaseResponse appCaseResponse = modelMapper.map(assistCollectionCase, AppCaseResponse.class);
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            boolQueryBuilder.must(matchPhraseQuery("id", assistCollectionCase.getCaseId()));
            boolQueryBuilder.must(matchPhraseQuery("caseDataStatus", CaseDataStatus.IN_POOL.name()));
            Iterable<BaseCase> iterable = baseCaseRepository.search(boolQueryBuilder);
            BaseCase baseCase = null;
            if (iterable.iterator().hasNext()) {
                baseCase = iterable.iterator().next();
            } else {
                return;
            }
            appCaseResponse.setHandsNumber(baseCase.getHandsNumber());
            appCaseResponse.setLeaveFlag(baseCase.getLeaveFlag());
            appCaseResponse.setSex(baseCase.getPersonal().getSex());
            DateTime dateTime = new DateTime(baseCase.getPersonal().getBirthday());
            DateTime currentTiem = new DateTime(ZWDateUtil.getNowDate());
            Integer age = currentTiem.getYear() - dateTime.getYear();
            appCaseResponse.setAge(age);
            appCaseResponse.setLeftAmt(baseCase.getLeftAmt());
            appCaseResponse.setContactResult(baseCase.getContactResult());
            appCaseResponse.setPhone(baseCase.getPersonal().getSelfPhoneNo());
            appCaseResponse.setPrincipalId(baseCase.getPrincipal().getId());
            responsesList.add(appCaseResponse);
        });
        return responsesList;
    }

    public AppBaseInfoResponse getBaseInfo(String assistId){
        AppBaseInfoResponse response = new AppBaseInfoResponse();
        AssistCollectionCase assistCollectionCase = assistCaseRepository.findById(assistId).get();
        Personal personal = baseCaseRepository.findById(assistCollectionCase.getCaseId()).get().getPersonal();
        response.setCertificateNo(personal.getCertificateNo());
        response.setPersonalName(personal.getPersonalName());
        response.setPhone(personal.getSelfPhoneNo());
        response.setSex(personal.getSex());
        Sort sort = new Sort(Sort.Direction.DESC,"sort");
        Pageable pageable = new PageRequest(0,10000,sort);
        Page<PersonalContact> page = personalContactRepository.search(QueryBuilders.boolQuery().must(matchPhraseQuery("personalId",personal.getId())),pageable);
        List<AppPersonalContactModel> personalContactModels = new ArrayList<>();
        page.forEach(personalContact -> {
            if(Objects.nonNull(personalContact.getPersonalPerCalls())){
                personalContact.getPersonalPerCalls().forEach(personalPerCall -> {
                    AppPersonalContactModel appPersonalContactModel = new AppPersonalContactModel();
                    appPersonalContactModel.setName(personalContact.getName());
                    appPersonalContactModel.setPhoneNo(personalPerCall.getPhoneNo());
                    appPersonalContactModel.setPhoneState(personalPerCall.getPhoneState());
                    appPersonalContactModel.setRelation(personalContact.getRelation());
                    appPersonalContactModel.setOperatorTime(personalContact.getOperatorTime());
                    personalContactModels.add(appPersonalContactModel);
                });
            }
            if(Objects.nonNull(personalContact.getPersonalPerAddrs())){
                personalContact.getPersonalPerAddrs().forEach(personalPerAddr -> {
                    if(Objects.equals(personalPerAddr.getId(),assistCollectionCase.getPersonalAddressId())){
                        AppPersonalAddressModel addressModel = new AppPersonalAddressModel();
                        addressModel.setAddressDetail(personalPerAddr.getAddressDetail());
                        addressModel.setAddressState(personalPerAddr.getAddressState());
                        addressModel.setAddressType(personalPerAddr.getAddressType());
                        addressModel.setSource(personalPerAddr.getSource().name());
                        addressModel.setContactName(personalContact.getName());
                        addressModel.setRelation(personalContact.getRelation());
                        addressModel.setOperatorTime(personalContact.getOperatorTime());
                        response.setAddress(addressModel);
                    }
                });
            }
        });
        response.setContacts(personalContactModels);
        return  response;
    }

}
