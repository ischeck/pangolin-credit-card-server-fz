package cn.fintecher.pangolin.service.domain.web;

import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.entity.domain.PublicCase;
import cn.fintecher.pangolin.service.domain.model.request.PublicCaseSearchRequest;
import cn.fintecher.pangolin.service.domain.model.response.PublicCaseSearchResponse;
import cn.fintecher.pangolin.service.domain.respository.BaseCaseRepository;
import cn.fintecher.pangolin.service.domain.respository.PublicCaseRepository;
import cn.fintecher.pangolin.service.domain.service.DomainBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.IteratorUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

/**
 * @Author:胡艳敏
 * @Desc: 公共案件查询
 * @Date:Create 2018/8/10
 */
@RestController
@RequestMapping("/api/publicCollectionCaseController")
@Api(value = "公共案件查询", description = "公共案件查询")
public class PublicCollectionCaseController {
    Logger logger = LoggerFactory.getLogger(PublicCollectionCaseController.class);
    @Autowired
    private DomainBaseService domainBaseService;

    @Autowired
    private PublicCaseRepository publicCaseRepository;

    @Autowired
    private BaseCaseRepository baseCaseRepository;

    @GetMapping("/queryPublicCase")
    @ApiOperation(value = "查询公共案件", notes = "查询公共案件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<PublicCaseSearchResponse>> queryPublicCase(Pageable pageable,
                                                                          PublicCaseSearchRequest request,
                                                                          @RequestHeader(value = "X-UserToken") String token) {
        logger.debug("查询公共案件 查询条件{}", request.toString());
        OperatorModel operator = domainBaseService.getOperator(token);
        BoolQueryBuilder boolQueryBuilder = request.generateQueryBuilder();
        if(Objects.isNull(request.getFlag())){
            boolQueryBuilder.must(matchPhraseQuery("departments", operator.getOrganization()));
        }
        Iterable<PublicCase> search = publicCaseRepository.search(boolQueryBuilder);
        List<PublicCase> list = new ArrayList<>();
        List<String> caseList = new ArrayList<>();
        Map<String, String> hashMap = new HashMap<>();
        if(search.iterator().hasNext()){
            list = IteratorUtils.toList(search.iterator());
            list.forEach(publicCase -> {
                hashMap.put(publicCase.getCaseId(), publicCase.getId());
                caseList.add(publicCase.getCaseId());
            });
        }
        Page<PublicCaseSearchResponse> page = Page.empty();
        if(caseList.size()>0){
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            caseList.forEach(item-> queryBuilder.should(matchPhraseQuery("id", item)));
            SearchQuery searchQuery = new NativeSearchQueryBuilder()
                    .withPageable(pageable)
                    .withQuery(queryBuilder).build();
            page = baseCaseRepository.search(searchQuery).map(baseCase -> {
                PublicCaseSearchResponse response = new PublicCaseSearchResponse();
                BeanUtils.copyProperties(baseCase, response);
                if(hashMap.containsKey(baseCase.getId())){
                    response.setPublicId(hashMap.get(baseCase.getId()));
                }
                response.setCaseId(baseCase.getId());
                response.setPersonalName(baseCase.getPersonal().getPersonalName());
                response.setCertificateNo(baseCase.getPersonal().getCertificateNo());
                response.setSelfPhoneNo(baseCase.getPersonal().getSelfPhoneNo());
                return response;
            });
        }
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @GetMapping("/queryPublicCaseAmt")
    @ApiOperation(value = "查询公共案件(金额)", notes = "查询公共案件(金额)")
    public ResponseEntity<Double> getHisCaseAmtForCollManage(PublicCaseSearchRequest request,
                                                             @RequestHeader(value = "X-UserToken") String token) {
        logger.debug("查询公共案件 查询条件{}", request.toString());
        OperatorModel operator = domainBaseService.getOperator(token);
        BoolQueryBuilder boolQueryBuilder = request.generateQueryBuilder();
        if(Objects.isNull(request.getFlag())){
            boolQueryBuilder.must(matchPhraseQuery("departments", operator.getOrganization()));
        }
        Iterable<PublicCase> search = publicCaseRepository.search(boolQueryBuilder);
        List<PublicCase> list = new ArrayList<>();
        List<String> caseList = new ArrayList<>();
        Map<String, String> hashMap = new HashMap<>();
        if(search.iterator().hasNext()){
            list = IteratorUtils.toList(search.iterator());
            list.forEach(publicCase -> {
                hashMap.put(publicCase.getCaseId(), publicCase.getId());
                caseList.add(publicCase.getCaseId());
            });
        }
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        Double amt = 0.0;
        if(caseList.size()>0) {
            caseList.forEach(item -> queryBuilder.should(matchPhraseQuery("id", item)));
            amt = domainBaseService.getTotalAmt(request.generateQueryBuilder());
        }
        return ResponseEntity.ok().body(amt);
    }
}
