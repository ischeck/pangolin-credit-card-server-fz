package cn.fintecher.pangolin.service.domain.web;

import cn.fintecher.pangolin.common.enums.*;
import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.entity.domain.*;
import cn.fintecher.pangolin.entity.managentment.Operator;
import cn.fintecher.pangolin.service.domain.model.AssistCollectionCaseSearchModel;
import cn.fintecher.pangolin.service.domain.model.request.AssistCaseSearchRequest;
import cn.fintecher.pangolin.service.domain.model.request.RetractAssistCaseRequest;
import cn.fintecher.pangolin.service.domain.model.response.AssistTelCaseSearchResponse;
import cn.fintecher.pangolin.service.domain.respository.*;
import cn.fintecher.pangolin.service.domain.service.AssistCollectionCaseService;
import cn.fintecher.pangolin.service.domain.service.CaseApplyService;
import cn.fintecher.pangolin.service.domain.service.DomainBaseService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

/**
 * @Author:胡艳敏
 * @Desc: 协催案件查询
 * @Date:Create 2018/8/7
 */
@RestController
@RequestMapping("/api/assistCollectionCaseController")
@Api(value = "协催案件查询记录", description = "协催案件查询记录")
public class AssistCollectionCaseController {
    Logger logger = LoggerFactory.getLogger(AssistCollectionCaseController.class);

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AssistCaseRepository assistCaseRepository;

    @Autowired
    private DomainBaseService domainBaseService;

    @Autowired
    private AssistCollectionCaseService assistCollectionCaseService;

    @Autowired
    private AssistCaseApplyRepository assistCaseApplyRepository;

    @Autowired
    private BaseCaseRepository baseCaseRepository;

    @Autowired
    private PersonalAddressRepository personalAddressRepository;

    @Autowired
    private PersonalContactRepository personalContactRepository;

    @Autowired
    private CaseApplyService applyService;

    @ApiOperation(value = "协催管理查外访案件", notes = "协催管理查外访案件")
    @GetMapping("/searchAssistCase")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<AssistCollectionCaseSearchModel>> searchAssistCase(AssistCaseSearchRequest request,
                                                                                  Pageable pageable,
                                                                                  @RequestHeader(value = "X-UserToken") String token) {
        logger.info("search wait distributed case");
        OperatorModel operatorModel = domainBaseService.getOperator(token);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(request.generateQueryBuilder().must(matchPhraseQuery("departments", operatorModel.getOrganization()))).build();
        Page<AssistCollectionCaseSearchModel> search = assistCaseRepository.search(searchQuery).map(assistCollectionCase -> {
            AssistCollectionCaseSearchModel model = new AssistCollectionCaseSearchModel();
            BeanUtils.copyProperties(assistCollectionCase,model);
            model.setId(assistCollectionCase.getId());
            model.setPrincipalName(assistCollectionCase.getPrincipal().getPrincipalName());
            model.setPrincipalId(assistCollectionCase.getPrincipal().getId());
            return model;
        });
        return ResponseEntity.ok().body(search);
    }

    @ApiOperation(value = "催收管理查询协催案件", notes = "催收管理查询协催案件")
    @GetMapping("/searchAssistCollectionCase")
    public ResponseEntity<Page<AssistTelCaseSearchResponse>> searchAssistCollectionCase(AssistCaseSearchRequest request,
                                                                                  @RequestHeader(value = "X-UserToken") String token) {
        logger.info("search wait collection cases");
        PageHelper.startPage(request.getPage()+1, request.getSize());
        List<AssistTelCaseSearchResponse> list = assistCollectionCaseService.searchAssistCase(request, token);
        PageInfo<AssistTelCaseSearchResponse> pageInfo = new PageInfo<>(list);
        Pageable pageable = new PageRequest(request.getPage(), request.getSize());
        Page<AssistTelCaseSearchResponse> page = new PageImpl<>(list, pageable, pageInfo.getTotal());
        return ResponseEntity.ok().body(page);
    }

    @PutMapping("/endAssistCaseCollection/{assistId}")
    @ApiOperation(value = "结束协催", notes = "结束协催")
    public ResponseEntity endAssistCaseCollection(@PathVariable String assistId){

        Optional<AssistCollectionCase> byId = assistCaseRepository.findById(assistId);
        byId.orElseThrow(()->new BadRequestException(null, "assistCase","assistCase.is.not.exist"));
        AssistCollectionCase assistCollectionCase = byId.get();
        assistCollectionCase.setAssistStatus(AssistStatus.ASSIST_COMPLETED);
        if(Objects.nonNull(assistCollectionCase.getPersonalAddressId())){
            applyService.setPersonalPerAddressState(assistCollectionCase.getPersonalContactId(),
                                                       assistCollectionCase.getPersonalAddressId(), AssistFlag.NO_ASSIST);
        }
        if(AssistFlag.OFFSITE_PHONE_ASSIST.equals(assistCollectionCase.getAssistFlag())){
            BaseCase baseCase = applyService.validCaseExist(assistCollectionCase.getCaseId());
            baseCase.setAssistFlag(AssistFlag.NO_ASSIST);
            baseCaseRepository.save(baseCase);
        }
        domainBaseService.sendMessageToOne(MessageType.ASSIST_CALL_BACK, "协催撤回", "协催人员[" + assistCollectionCase.getCurrentCollector().getFullName() + "]撤回了欠款人["+assistCollectionCase.getPersonalName()+"的协催案件", assistCollectionCase.getCurrentCollector().getUsername());
        assistCaseRepository.save(assistCollectionCase);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/retractAssistCase")
    @ApiOperation(value = "撤回协催", notes = "撤回协催")
    public ResponseEntity retractAssistCase(@RequestBody RetractAssistCaseRequest request){

        assistCollectionCaseService.retractAssistCase(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @ApiOperation(value = "修改协催案件", notes = "修改协催案件")
    @GetMapping("/updateAssistCollectionCase")
    public ResponseEntity updateAssistCollectionCase(String id, AssistFlag assistFlag,
                                                     @RequestHeader(value = "X-UserToken") String token) {
        logger.info("search wait collection cases");
        OperatorModel operator = domainBaseService.getOperator(token);
        Operator operator1 = new Operator();
        BeanUtils.copyProperties(operator,operator1);
        Optional<AssistCollectionCase> byId = assistCaseRepository.findById(id);
        AssistCollectionCase assistCollectionCase = byId.get();
        assistCollectionCase.setCurrentCollector(operator1);
        assistCollectionCase.setAssistStatus(AssistStatus.ASSIST_COLLECTING);
        assistCaseRepository.save(assistCollectionCase);
        return ResponseEntity.ok().body(assistCollectionCase);
    }

}
