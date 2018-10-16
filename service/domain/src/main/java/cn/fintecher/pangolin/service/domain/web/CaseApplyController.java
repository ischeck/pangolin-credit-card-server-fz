package cn.fintecher.pangolin.service.domain.web;

import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.entity.domain.BasicCaseApply;
import cn.fintecher.pangolin.service.domain.model.request.*;
import cn.fintecher.pangolin.service.domain.model.response.AssistCaseApplyResponse;
import cn.fintecher.pangolin.service.domain.model.response.BasicCaseApplyResponse;
import cn.fintecher.pangolin.service.domain.respository.AssistCaseApplyRepository;
import cn.fintecher.pangolin.service.domain.respository.BaseCaseRepository;
import cn.fintecher.pangolin.service.domain.respository.BasicCaseApplyRepository;
import cn.fintecher.pangolin.service.domain.service.CaseApplyService;
import cn.fintecher.pangolin.service.domain.service.DomainBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.IteratorUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Created by huyanmin on 2018/7/11.
 */
@RestController
@RequestMapping("/api/caseApplyOperate")
@Api(value = "申请相关操作", description = "申请相关操作")
public class CaseApplyController {
    private final Logger log = LoggerFactory.getLogger(CaseApplyController.class);
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    BasicCaseApplyRepository basicCaseApplyRepository;
    @Autowired
    private AssistCaseApplyRepository assistCaseApplyRepository;
    @Autowired
    private BaseCaseRepository baseCaseRepository;
    @Autowired
    private CaseApplyService applyService;
    @Autowired
    private DomainBaseService domainBaseService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/assistApplySearch")
    @ApiOperation(value = "协催申请记录查询", notes = "协催申请记录查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<AssistCaseApplyResponse>> assistApplySearch(@RequestParam String caseId,
                                                                           Pageable pageable) {

        log.debug("search assistCase apply record {}", caseId);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(matchPhraseQuery("caseId", caseId)).build();
        Page<AssistCaseApplyResponse> search = assistCaseApplyRepository.search(searchQuery).map(assistCaseApply -> {
            AssistCaseApplyResponse response = modelMapper.map(assistCaseApply, AssistCaseApplyResponse.class);
            return response;
        });
        return new ResponseEntity<>(search, HttpStatus.OK);
    }

    @PostMapping("/assistApply")
    @ApiOperation(value = "协催申请", notes = "协催申请")
    public ResponseEntity assistApply(@RequestBody AssistCaseApplyRequest request,
                                      @RequestHeader(value = "X-UserToken") String token) throws URISyntaxException {
        log.debug("search assistCase apply {}", request);
        applyService.setAssistCaseApply(request, domainBaseService.getOperator(token));
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("/assistCaseApproveSearch")
    @ApiOperation(value = "协催审批查询", notes = "协催审批查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity assistCaseApplySearch(Pageable pageable,
                                                AssistCaseApplySearchRequest request,
                                                @RequestHeader(value = "X-UserToken") String token) throws URISyntaxException {

        log.debug("search assistCase apply {}", request);
        OperatorModel operator = domainBaseService.getOperator(token);
        BoolQueryBuilder qb = request.generateQueryBuilder().must(matchQuery("roleIds", operator.getRole()));
        if(Objects.nonNull(request.getAssistFlag())){
            qb.must(matchQuery("applyDeptIds", operator.getOrganization()));
        }
        if(Objects.nonNull(request.getAssistFlags())){
            qb.must(matchQuery("approvedDeptIds", operator.getOrganization()));
        }
        //根据审批人的角色以及组织机构搜索待审批的协催审批案件
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(qb).build();
        Page<AssistCaseApplyResponse> search = assistCaseApplyRepository.search(searchQuery).map(assistCaseApply -> {
            AssistCaseApplyResponse response = modelMapper.map(assistCaseApply, AssistCaseApplyResponse.class);
            return response;
        });
        return new ResponseEntity<>(search, HttpStatus.OK);
    }

    @PostMapping("/assistApplyApprove")
    @ApiOperation(value = "异地协催审批", notes = "异地协催审批")
    public ResponseEntity assistApplyApprove(@RequestBody AssistApplyApproveModel request,
                                             @RequestHeader(value = "X-UserToken") String token) throws URISyntaxException {

        log.debug("search assistCase apply assist approved {}", request);
        applyService.assistApplyApproval(request, domainBaseService.getOperator(token));
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PostMapping("/localApplyApprove")
    @ApiOperation(value = "本地协催审批", notes = "本地协催审批")
    public ResponseEntity localApplyApprove(@RequestBody AssistApplyApproveModel request,
                                            @RequestHeader(value = "X-UserToken") String token) throws URISyntaxException {

        log.debug("search assistCase apply local approved {}", request);
        applyService.localApplyApproval(request, domainBaseService.getOperator(token));
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("/caseApplySearch")
    @ApiOperation(value = "申请查询", notes = "申请查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<BasicCaseApplyResponse>> caseApplySearch(@RequestParam String caseId,
                                                                        @RequestParam String applyType, Pageable pageable) throws URISyntaxException {

        log.debug("search case apply record{}", caseId);
        Page<BasicCaseApplyResponse> basicCaseApplyResponses = applyService.queryApply(applyType, caseId, pageable);
        return new ResponseEntity<>(basicCaseApplyResponses, HttpStatus.OK);
    }

    @PostMapping("/caseApplyOperated")
    @ApiOperation(value = "申请操作", notes = "申请操作")
    public ResponseEntity caseApplyOperated(@RequestBody ApplyCaseRequest request,
                                        @RequestHeader(value = "X-UserToken") String token) throws URISyntaxException {

        log.debug("case apply {}", request);
        applyService.setObjectCaseApply(request, domainBaseService.getOperator(token));
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PostMapping("/caseApplyApprove")
    @ApiOperation(value = "审批操作", notes = "审批操作")
    public ResponseEntity caseApplyApprove(@RequestBody ApplyCaseApproveRequest request,
                                           @RequestHeader(value = "X-UserToken") String token) throws URISyntaxException {

        log.debug("case apply approve {}", request);
        applyService.setObjectCaseApprove(request, domainBaseService.getOperator(token));
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("/caseApprovalSearch")
    @ApiOperation(value = "各类审批查询", notes = "各类审批查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity caseApprovalSearch(Pageable pageable, CaseApplySearchRequest request,
                                          @RequestHeader(value = "X-UserToken") String token) throws URISyntaxException {

        log.debug("search case apply {}", request);
        OperatorModel operator = domainBaseService.getOperator(token);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(request.generateQueryBuilder().must(termsQuery("roles", operator.getRole())).must(termsQuery("organizationList", operator.getOrganization()))).build();
        log.debug("search Activity : {} query :{}", request.toString(), searchQuery.getQuery().toString());
        Page<BasicCaseApplyResponse> searchPageResults = basicCaseApplyRepository.search(searchQuery).map(basicCaseApply -> {
            BasicCaseApplyResponse response = new BasicCaseApplyResponse();
            BeanUtils.copyProperties(basicCaseApply, response);
            response.setIdCard(basicCaseApply.getCertificateNo());
            return response;
        });
        return new ResponseEntity<>(searchPageResults, HttpStatus.OK);
    }

    @PostMapping("/validCheckMaterialApply")
    @ApiOperation(value = "验证资料是否申请过", notes = "验证资料是否申请过")
    public ResponseEntity validCheckMaterialApply(@RequestBody ApplyCaseRequest request) throws URISyntaxException {

        log.debug("case apply approve {}", request);
        BasicCaseApply basicCaseApply = applyService.validCheckMaterialApply(request);
        return new ResponseEntity<>(basicCaseApply, HttpStatus.OK);
    }

}
