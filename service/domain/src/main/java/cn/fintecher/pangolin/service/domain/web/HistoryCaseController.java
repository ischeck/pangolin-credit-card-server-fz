package cn.fintecher.pangolin.service.domain.web;

import cn.fintecher.pangolin.entity.domain.HisCase;
import cn.fintecher.pangolin.service.domain.model.request.CaseStateQueryRequest;
import cn.fintecher.pangolin.service.domain.model.response.CaseQueryResponse;
import cn.fintecher.pangolin.service.domain.model.response.CollectionCaseSearchResponse;
import cn.fintecher.pangolin.service.domain.respository.HisCaseRepository;
import cn.fintecher.pangolin.service.domain.service.DomainBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/historyCase")
@Api(value = "历史案件", description = "历史案件")
public class HistoryCaseController {

    @Autowired
    HisCaseRepository hisCaseRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    DomainBaseService domainBaseService;

    @GetMapping("/getHisCaseForCaseManage")
    @ApiOperation(value = "历史案件查询（案件管理）", notes = "历史案件查询（案件管理）")
    public ResponseEntity<Page<CaseQueryResponse>> getHisCaseForCaseManage(Pageable pageable,
                                                                                   CaseStateQueryRequest request,
                                                                                   @RequestHeader(value = "X-UserToken") String token) {
        BoolQueryBuilder qb = request.generateQueryBuilder();
        Page<HisCase> hisCases = hisCaseRepository.search(qb, pageable);
        Page<CaseQueryResponse> responses = hisCases.map(hisCase -> {
            CaseQueryResponse response = new CaseQueryResponse();
            BeanUtils.copyProperties(hisCase, response);
            response.setPrincipalName(hisCase.getPrincipal().getPrincipalName());
            response.setCertificateNo(hisCase.getPersonal().getCertificateNo());
            response.setSelfPhoneNo(hisCase.getPersonal().getSelfPhoneNo());
            response.setPersonalName(hisCase.getPersonal().getPersonalName());
            return response;
        });
        return ResponseEntity.ok().body(responses);
    }

    @GetMapping("/getHisCaseForCollManage")
    @ApiOperation(value = "历史案件查询（催收管理）", notes = "历史案件查询（催收管理）")
    public ResponseEntity<Page<CollectionCaseSearchResponse>> getHisCaseForCollManage(Pageable pageable,
                                                                                      CaseStateQueryRequest request,
                                                                                      @RequestHeader(value = "X-UserToken") String token) {
        SortBuilder sortBuilder = SortBuilders.fieldSort("operatorTime").unmappedType("Date").order(SortOrder.DESC);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(request.generateQueryBuilder()).withSort(sortBuilder).build();
        Page<CollectionCaseSearchResponse> search = hisCaseRepository.search(searchQuery).map(collectionCase -> {
            CollectionCaseSearchResponse caseSearchResponse = modelMapper.map(collectionCase, CollectionCaseSearchResponse.class);
            return caseSearchResponse;
        });
        return ResponseEntity.ok().body(search);
    }

    @GetMapping("/getHisCaseAmtForCollManage")
    @ApiOperation(value = "历史案件金额查询（催收管理）", notes = "历史案件金额查询（催收管理）")
    public ResponseEntity<Double> getHisCaseAmtForCollManage(CaseStateQueryRequest request) {
        Double amt = domainBaseService.getTotalAmt(request.generateQueryBuilder());
        return ResponseEntity.ok().body(amt);
    }

}
