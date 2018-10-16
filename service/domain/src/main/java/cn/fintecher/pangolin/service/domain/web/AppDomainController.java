package cn.fintecher.pangolin.service.domain.web;

import cn.fintecher.pangolin.common.enums.FollowType;
import cn.fintecher.pangolin.common.enums.ManagementType;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.model.response.LoginResponse;
import cn.fintecher.pangolin.common.utils.BeanUtils;
import cn.fintecher.pangolin.entity.domain.*;
import cn.fintecher.pangolin.service.domain.model.request.AppCaseSearchRequest;
import cn.fintecher.pangolin.service.domain.model.request.CreateFollowRecordModel;
import cn.fintecher.pangolin.service.domain.model.response.*;
import cn.fintecher.pangolin.service.domain.respository.*;
import cn.fintecher.pangolin.service.domain.service.AppDomainService;
import cn.fintecher.pangolin.service.domain.service.CaseFollowService;
import cn.fintecher.pangolin.service.domain.service.DomainBaseService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

/**
 * Created by BBG on 2018/8/28.
 */

@RestController
@RequestMapping("/api/appDomain")
@Api(value = "APP相关操作", description = "APP相关操作")
public class AppDomainController {


    @Autowired
    DomainBaseService domainBaseService;
    @Autowired
    AssistCaseRepository assistCaseRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    BaseCaseRepository baseCaseRepository;
    @Autowired
    PersonalContactRepository personalContactRepository;
    @Autowired
    PersonalAddressRepository personalAddressRepository;
    @Autowired
    CaseFollowupRecordRepository caseFollowupRecordRepository;
    @Autowired
    CaseFollowService caseFollowService;
    @Autowired
    AppDomainService appDomainService;

    @GetMapping("/getAllCase")
    @ApiOperation(value = "获取所有案件", notes = "获取所有案件")
    public ResponseEntity getAllCase(AppCaseSearchRequest request,
                                     @RequestHeader(value = "X-UserToken") String token,
                                     Pageable pageable){
        BoolQueryBuilder qb = request.generateQueryBuilder();
        OperatorModel operator = domainBaseService.getOperator(token);
        if(operator.getIsManager().equals(ManagementType.YES)){
            qb.must(matchPhraseQuery("currentCollector.organization", operator.getOrganization()));
        }else {
            qb.must(matchPhraseQuery("currentCollector.id", operator.getId()));
        }
        if (Objects.nonNull(request.getCollectionRecordCount())) {
            if (Objects.equals(request.getCollectionRecordCount(), 0)) {
                qb.must(rangeQuery("collectionRecordCount").lte(0));
            } else {
                qb.must(rangeQuery("collectionRecordCount").gt(0));
            }
        }
        Page<AssistCollectionCase> assistCollectionCases = assistCaseRepository.search(qb,pageable);
        List<AppCaseResponse> responsesList = appDomainService.getAllCase(assistCollectionCases.getContent(),request.getCollectionRecordCount());
        PageInfo<AppCaseResponse> pageInfo = new PageInfo<>(responsesList);
        Page<AppCaseResponse> page = new PageImpl<>(responsesList, pageable, pageInfo.getTotal());
        return ResponseEntity.ok().body(page);
    }

    @GetMapping("/getBaseInfo")
    @ApiOperation(value = "获取基本信息", notes = "获取基本信息")
    public ResponseEntity<AppBaseInfoResponse> getBaseInfo(@RequestParam String assistId){
        AppBaseInfoResponse response = appDomainService.getBaseInfo(assistId);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/getCaseInfo")
    @ApiOperation(value = "获取案件信息", notes = "获取案件信息")
    public ResponseEntity<AppCaseInfoResponse> getCaseInfo(@RequestParam String caseId,
                                                           @RequestHeader(value = "X-UserToken") String token){
        AppCaseInfoResponse response = new AppCaseInfoResponse();
        BaseCase baseCase = baseCaseRepository.findById(caseId).get();
        BeanUtils.copyPropertiesIgnoreNull(baseCase,response);
        response.setPayAmt(baseCase.getLeftAmt()-baseCase.getLeftAmt());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/saveFollowRecord")
    @ApiOperation(value = "保存催记", notes = "保存催记")
    public ResponseEntity<Void> saveFollowRecord(@RequestBody CreateFollowRecordModel request,
                                                 @RequestHeader(value = "X-UserToken") String token){
        LoginResponse loginResponse = domainBaseService.getLoginResponse(token);
        appDomainService.saveFollowRecoed(request, loginResponse);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/getFollowRecord")
    @ApiOperation(value = "获取催记信息", notes = "获取催记信息")
    public ResponseEntity<Page<AppFollowRecordResponse>> getFollowRecord(@RequestParam String caseId,
                                                                         Pageable pageable){
        BoolQueryBuilder qb = new BoolQueryBuilder();
        qb.must(matchPhraseQuery("caseId", caseId));
        Page<AppFollowRecordResponse> page = caseFollowupRecordRepository.search(qb,pageable).map(
                caseFollowupRecord -> {
                    AppFollowRecordResponse appFollowRecordResponse = new AppFollowRecordResponse();
                    BeanUtils.copyProperties(caseFollowupRecord,appFollowRecordResponse);
                    return appFollowRecordResponse;
                });
        return ResponseEntity.ok().body(page);
    }
}
