package cn.fintecher.pangolin.service.domain.web;

import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.model.response.LoginResponse;
import cn.fintecher.pangolin.entity.domain.BaseCase;
import cn.fintecher.pangolin.entity.domain.CaseFollowupRecord;
import cn.fintecher.pangolin.service.domain.model.CaseFollowRecordModel;
import cn.fintecher.pangolin.service.domain.model.request.*;
import cn.fintecher.pangolin.service.domain.model.response.*;
import cn.fintecher.pangolin.service.domain.respository.*;
import cn.fintecher.pangolin.service.domain.service.CaseFollowService;
import cn.fintecher.pangolin.service.domain.service.DomainBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.IterableUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;


/**
 * Created by BBG on 2018/8/2.
 */
@RestController
@RequestMapping("/api/caseFollowController")
@Api(value = "案件催记操作", description = "案件催记操作")
public class CaseFollowController {

    Logger logger = LoggerFactory.getLogger(CaseFollowController.class);

    @Autowired
    CaseFollowupRecordRepository caseFollowupRecordRepository;

    @Autowired
    CaseFindRecordRepository caseFindRecordRepository;

    @Autowired
    CaseOtherFollowRecordRepository caseOtherFollowRecordRepository;

    @Autowired
    PreCaseFollowupRecordRepository preCaseFollowupRecordRepository;
    @Autowired
    CaseFollowService caseFollowService;
    @Autowired
    DomainBaseService domainBaseService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    BaseCaseRepository baseCaseRepository;

    @GetMapping("/getFollowRecord")
    @ApiOperation(notes = "查询催记", value = "查询催记")
    public ResponseEntity<Page<CaseFollowRecordResponse>> getFollowRecord(CaseFollowRecordSearchRequest request,
                                                                          @RequestHeader(value = "X-UserToken") String token,
                                                                          Pageable pageable) {
        SortBuilder sortBuilder = SortBuilders.fieldSort("operatorTime").unmappedType("date").order(SortOrder.DESC);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withSort(sortBuilder)
                .withQuery(request.generateQueryBuilder()).build();
        Page<CaseFollowRecordResponse> page = caseFollowupRecordRepository.search(searchQuery).map(caseFollowupRecord -> {
            CaseFollowRecordResponse recordResponse = modelMapper.map(caseFollowupRecord, CaseFollowRecordResponse.class);
            return recordResponse;
        });
        return ResponseEntity.ok().body(page);
    }

    @PostMapping("/updateFollowContent")
    @ApiOperation(notes = "修改催记跟进内容", value = "修改催记跟进内容")
    public ResponseEntity<Void> updateFollowContent(@RequestBody UpdateFollowRecord updateFollowRecord) {

        logger.info("Update the follow record content", updateFollowRecord);
        Optional<CaseFollowupRecord> byId = caseFollowupRecordRepository.findById(updateFollowRecord.getId());
        if (byId.isPresent()) {
            CaseFollowupRecord caseFollowupRecord = byId.get();
            if(Objects.isNull(caseFollowupRecord.getRemark())){
                caseFollowupRecord.setRemark(updateFollowRecord.getRemark());
            }else {
                caseFollowupRecord.setRemark(caseFollowupRecord.getRemark().concat(updateFollowRecord.getRemark()));
            }
            caseFollowupRecordRepository.save(caseFollowupRecord);
        }
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/saveFollowRecord")
    @ApiOperation(notes = "添加催记", value = "添加催记")
    public ResponseEntity<Void> saveFollowRecord(@RequestBody CreateFollowRecordModel createFollowRecordModel,
                                                 @RequestHeader(value = "X-UserToken") String token) {

        LoginResponse loginResponse = domainBaseService.getLoginResponse(token);
        caseFollowService.saveFollowRecoed(createFollowRecordModel, loginResponse);

        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/getFindRecord")
    @ApiOperation(notes = "查询查找记录", value = "查询查找记录")
    public ResponseEntity<Page<CaseFindRecordResponse>> getFindRecord(CaseFindRecordSearchRequest request,
                                                                      @RequestHeader(value = "X-UserToken") String token,
                                                                      Pageable pageable) {
        SortBuilder sortBuilder = SortBuilders.fieldSort("findTime").unmappedType("date").order(SortOrder.DESC);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withSort(sortBuilder)
                .withQuery(request.generateQueryBuilder()).build();
        Page<CaseFindRecordResponse> page = caseFindRecordRepository.search(searchQuery).map(caseFindRecord -> {
            CaseFindRecordResponse recorrdResponse = modelMapper.map(caseFindRecord, CaseFindRecordResponse.class);
            return recorrdResponse;
        });
        return ResponseEntity.ok().body(page);
    }

    @PostMapping("/saveFindRecord")
    @ApiOperation(notes = "添加查找记录", value = "添加查找记录")
    public ResponseEntity<Void> saveFindRecord(@RequestBody CaseFindRecordModel caseFindRecordModel,
                                               @RequestHeader(value = "X-UserToken") String token) {

        OperatorModel operator = domainBaseService.getOperator(token);
        caseFollowService.saveFindRecoed(caseFindRecordModel, operator);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/getOtherFollowRecord")
    @ApiOperation(notes = "查询其他跟进记录", value = "查询其他跟进记录")
    public ResponseEntity<Page<CaseOtherFollowRecordResponse>> getOtherFollowRecord(OtherFollowRecordSearchRequest request,
                                                                                    @RequestHeader(value = "X-UserToken") String token,
                                                                                    Pageable pageable) {
        SortBuilder sortBuilder = SortBuilders.fieldSort("followTime").unmappedType("date").order(SortOrder.DESC);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withSort(sortBuilder)
                .withQuery(request.generateQueryBuilder()).build();
        Page<CaseOtherFollowRecordResponse> page = caseOtherFollowRecordRepository.search(searchQuery).map(caseOtherFollowRecord -> {
            CaseOtherFollowRecordResponse recorrdResponse = modelMapper.map(caseOtherFollowRecord, CaseOtherFollowRecordResponse.class);
            return recorrdResponse;
        });
        return ResponseEntity.ok().body(page);
    }

    @PostMapping("/saveOtherFollowRecord")
    @ApiOperation(notes = "添加其他跟进记录", value = "添加其他跟进记录")
    public ResponseEntity<Void> saveOtherFollowRecord(@RequestBody CaseOtherFollowModel caseOtherFollowModel,
                                                      @RequestHeader(value = "X-UserToken") String token) {

        OperatorModel operator = domainBaseService.getOperator(token);
        caseFollowService.saveOtherFollowRecoed(caseOtherFollowModel, operator);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/searchPersonalFollowRecord")
    @ApiOperation(notes = "根据电话号码查询联系人催记", value = "根据电话号码查询联系人催记")
    public ResponseEntity<List<CaseFollowRecordModel>> searchPersonalFollowRecord(String phoneNumber, String targetName, Pageable pageable) {

        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        SortBuilder sortBuilder = SortBuilders.fieldSort("operatorTime").unmappedType("Date").order(SortOrder.DESC);
        qb.must(matchPhraseQuery("contactPhone", phoneNumber)).must(matchPhraseQuery("targetName", targetName));
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(qb).withSort(sortBuilder).build();
        Page<CaseFollowRecordModel> search = caseFollowupRecordRepository.search(searchQuery).map(caseFollowupRecord -> {
            CaseFollowRecordModel recordResponse = modelMapper.map(caseFollowupRecord, CaseFollowRecordModel.class);
            return recordResponse;
        });
        List<CaseFollowRecordModel> content = search.getContent();
        return ResponseEntity.ok().body(content);
    }

    @GetMapping("/getPreFollowRecord")
    @ApiOperation(notes = "查询委前跟进记录", value = "查询委前跟进记录")
    public ResponseEntity<Page<PreFollowRecordResponse>> getPreFollowRecord(PreFollowRecordSearchRequest request,
                                                                            @RequestHeader(value = "X-UserToken") String token,
                                                                            Pageable pageable) {
        SortBuilder sortBuilder = SortBuilders.fieldSort("followTime").unmappedType("date").order(SortOrder.DESC);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withSort(sortBuilder)
                .withQuery(request.generateQueryBuilder()).build();
        Page<PreFollowRecordResponse> page = preCaseFollowupRecordRepository.search(searchQuery).map(preCaseFollowupRecord -> {
            PreFollowRecordResponse recorrdResponse = modelMapper.map(preCaseFollowupRecord, PreFollowRecordResponse.class);
            return recorrdResponse;
        });
        return ResponseEntity.ok().body(page);
    }

    @GetMapping("/getFollowRecordCount")
    @ApiOperation(notes = "查询跟进记录统计", value = "查询跟进记录统计")
    public ResponseEntity<FollowRecordCountModel> getFollowRecordCount(@RequestParam String caseId,
                                                                       @RequestHeader(value = "X-UserToken") String token) {

        FollowRecordCountModel model = caseFollowService.getFollowRecordCount(caseId);
        return ResponseEntity.ok().body(model);
    }


}
