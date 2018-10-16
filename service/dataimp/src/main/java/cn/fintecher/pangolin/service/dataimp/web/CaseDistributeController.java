package cn.fintecher.pangolin.service.dataimp.web;

import cn.fintecher.pangolin.common.enums.CaseIssuedFlag;
import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.response.LoginResponse;
import cn.fintecher.pangolin.entity.domain.BaseCase;
import cn.fintecher.pangolin.entity.managentment.Organization;
import cn.fintecher.pangolin.service.dataimp.client.OperatorClient;
import cn.fintecher.pangolin.service.dataimp.client.OrganizationClient;
import cn.fintecher.pangolin.service.dataimp.model.request.AreaCaseDistributeBatchRequest;
import cn.fintecher.pangolin.service.dataimp.model.request.AreaCaseDistributeRequest;
import cn.fintecher.pangolin.service.dataimp.model.request.GroupBatchDistributeRequest;
import cn.fintecher.pangolin.service.dataimp.model.request.GroupCaseDistributeRequest;
import cn.fintecher.pangolin.service.dataimp.model.response.*;
import cn.fintecher.pangolin.service.dataimp.repository.ImportBaseCaseRepository;
import cn.fintecher.pangolin.service.dataimp.repository.ImportDataExcelRecordRepository;
import cn.fintecher.pangolin.service.dataimp.service.CaseStrategyDistributionService;
import cn.fintecher.pangolin.service.dataimp.service.DistributeCaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.elasticsearch.index.query.QueryBuilders.existsQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * @Author:peishouwen
 * @Desc: 小组案件分配
 * @Date:Create in 14:22 2018/8/9
 */
@RestController
@RequestMapping("/api/caseDistributeController")
@Api(value = "案件分配", description = "案件分配")
public class CaseDistributeController {

    Logger logger= LoggerFactory.getLogger(CaseDistributeController.class);

    @Autowired
    DistributeCaseService distributeCaseService;

    @Autowired
    ImportBaseCaseRepository importBaseCaseRepository;

    @Autowired
    CaseStrategyDistributionService caseStrategyDistributionService;
    @Autowired
    private OrganizationClient organizationClient;

    @Autowired
    private OperatorClient operatorClient;


    @PostMapping("/groupCaseDistributePre")
    @ApiOperation(value = "个人分案预览", notes = "个人分案预览")
    public ResponseEntity<GroupCaseDistributeResponse> groupCaseDistributePre(@RequestBody GroupCaseDistributeRequest request) throws BadRequestException{
        logger.info("groupCaseDistributePre request :{} ",request.toString());
        GroupCaseDistributeResponse response= distributeCaseService.manualDistributeCase(request);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/groupConfirmDistributeCase")
    @ApiOperation(value = "个人分案确认", notes = "个人分案确认")
    public ResponseEntity groupConfirmDistributeCase(@RequestBody GroupCaseDistributeResponse request,
                                                     @RequestHeader(value = "X-UserToken") String token)throws BadRequestException{
        logger.info("groupConfirmDistributeCase request :{} ",request.toString());
        ResponseEntity<LoginResponse>  responseEntity=operatorClient.getUserByToken(token);
        distributeCaseService.confirmDistributeCase(request.getDistributeConfigModels(),responseEntity.getBody(),CaseIssuedFlag.PERSONAL_HAS_ISSUED);
        return ResponseEntity.ok().body(null);
    }



    @PostMapping("/groupCaseBatchDistribute")
    @ApiOperation(value = "个人批量分案预览", notes = "个人批量分案预览")
    public ResponseEntity<GroupBatchDistributeResponse> groupCaseBatchDistribute(@RequestBody GroupBatchDistributeRequest request,
                                                                                 @RequestHeader(value = "X-UserToken") String token)throws BadRequestException{
        logger.info("groupCaseDistributePre request :{} ",request.toString());
        ResponseEntity<LoginResponse>  responseEntity=operatorClient.getUserByToken(token);
        BoolQueryBuilder qb = request.generateQueryBuilder();
        //个人分案时需要权限控制
        if(request.getIssuedFlag().equals(CaseIssuedFlag.AREA_HAS_ISSUED) || request.getIssuedFlag().equals(CaseIssuedFlag.PERSONAL_UN_ISSUED)){
            qb.must(termQuery("departments", responseEntity.getBody().getUser().getOrganization()));
        }
        GroupBatchDistributeResponse response= distributeCaseService.groupDistributeBatchCase(request);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/groupBatchConfirmDistribute")
    @ApiOperation(value = "个人批量分案确认", notes = "个人批量分案确认")
    public ResponseEntity groupBatchConfirmDistribute(@RequestBody GroupBatchDistributeResponse request,
                                                      @RequestHeader(value = "X-UserToken") String token)throws BadRequestException{
        logger.info("groupConfirmDistributeCase request :{} ",request.toString());
        ResponseEntity<LoginResponse>  responseEntity=operatorClient.getUserByToken(token);
        distributeCaseService.confirmDistributeCase(request.getDistributeConfigModels(),responseEntity.getBody(),CaseIssuedFlag.PERSONAL_HAS_ISSUED);
        return ResponseEntity.ok().body(null);
    }


    @PostMapping("/areaCaseDistributePre")
    @ApiOperation(value = "区域分案预览", notes = "区域分案预览")
    public ResponseEntity<AreaCaseDistributeResponse> areaCaseDistributePre(@RequestBody AreaCaseDistributeRequest request) throws BadRequestException{
        logger.info("areaCaseDistributePre request :{} ",request.toString());
      AreaCaseDistributeResponse response=  distributeCaseService.manualAreaDistributeCase(request);
      return ResponseEntity.ok().body(response);
    }

    @PostMapping("/areaConfirmDistributeCase")
    @ApiOperation(value = "区域分案确认", notes = "区域分案确认")
    public ResponseEntity areaConfirmDistributeCase(@RequestBody AreaCaseDistributeResponse request,
                                                    @RequestHeader(value = "X-UserToken") String token)throws BadRequestException{
        logger.info("groupConfirmDistributeCase request :{} ",request.toString());
        ResponseEntity<LoginResponse>  responseEntity=operatorClient.getUserByToken(token);
        distributeCaseService.confirmAreaDistributeCase( request.getDistributeConfigModels(),responseEntity.getBody(),CaseIssuedFlag.AREA_UN_ISSUED);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/areaCaseBatchDistribute")
    @ApiOperation(value = "区域批量分案预览", notes = "区域批量分案预览")
    public ResponseEntity<AreaCaseDistributeBatchResponse> areaCaseBatchDistribute(@RequestBody AreaCaseDistributeBatchRequest request) throws BadRequestException{
        logger.info("areaCaseDistributePre request :{} ",request.toString());
        AreaCaseDistributeBatchResponse response=  distributeCaseService.areaDistributeBatchCase(request);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/areaConfirmBatchDistribute")
    @ApiOperation(value = "区域批量分案确认", notes = "区域批量分案确认")
    public ResponseEntity areaConfirmBatchDistribute(@RequestBody AreaCaseDistributeBatchResponse request,
                                                     @RequestHeader(value = "X-UserToken") String token)throws BadRequestException{
        logger.info("groupConfirmDistributeCase request :{} ",request.toString());
        ResponseEntity<LoginResponse>  responseEntity=operatorClient.getUserByToken(token);
        distributeCaseService.confirmAreaDistributeCase( request.getDistributeConfigModels(),responseEntity.getBody(),CaseIssuedFlag.AREA_UN_ISSUED);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/strategyDistributeCase/{batchNumber}")
    @ApiOperation(value = "区域策略方案", notes = "区域策略方案")
   public ResponseEntity strategyDistributeCase(@PathVariable String batchNumber, @RequestHeader(value = "X-UserToken") String token) throws BadRequestException{
        ResponseEntity<LoginResponse>  responseEntity=operatorClient.getUserByToken(token);

        BoolQueryBuilder queryBuilder= QueryBuilders.boolQuery();
        queryBuilder.must(matchPhraseQuery("batchNumber.keyword",batchNumber));
        queryBuilder.must(matchPhraseQuery("issuedFlag.keyword",CaseIssuedFlag.AREA_UN_DIS.name()));
        Iterable<BaseCase> baseCaseIterable=importBaseCaseRepository.search(queryBuilder);
        List<BaseCase> baseCaseList= Lists.newArrayList(baseCaseIterable);
        if(!baseCaseList.isEmpty()){
            caseStrategyDistributionService.distribution(baseCaseList,responseEntity.getBody());
        }
       return ResponseEntity.ok().body(null);
   }

    @PostMapping("/assistCaseDistributePre")
    @ApiOperation(value = "外访分案预览", notes = "外访分案预览")
    public ResponseEntity<GroupCaseDistributeResponse> assistCaseDistributePre(@RequestBody GroupCaseDistributeRequest request)throws BadRequestException{
        logger.info("assistCaseDistributePre request :{} ",request.toString());
        GroupCaseDistributeResponse response= distributeCaseService.manualAssistDistributeCase(request);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/assistConfirmDistributeCase")
    @ApiOperation(value = "外访分案确认", notes = "外访分案确认")
    public ResponseEntity assistConfirmDistributeCase(@RequestBody GroupCaseDistributeResponse request)throws BadRequestException{
        logger.info("groupConfirmDistributeCase request :{} ",request.toString());
        distributeCaseService.confirmAssistDistributeCase(request);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/groupCaseDistributeRenew")
    @ApiOperation(value = "重新分案预览", notes = "重新分案预览")
    public ResponseEntity<GroupCaseDistributeResponse> groupCaseDistributeRenew(@RequestBody GroupCaseDistributeRequest request) throws BadRequestException{
        logger.info("groupCaseDistributePre request :{} ",request.toString());
        GroupCaseDistributeResponse response= distributeCaseService.manualDistributeCase(request);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/groupConfirmCaseDistributeRenew")
    @ApiOperation(value = "重新分案确认", notes = "重新分案确认")
    public ResponseEntity groupConfirmCaseDistributeRenew(@RequestBody GroupCaseDistributeResponse request,
                                                     @RequestHeader(value = "X-UserToken") String token)throws BadRequestException{
        logger.info("groupConfirmDistributeCase request :{} ",request.toString());
        ResponseEntity<LoginResponse>  responseEntity=operatorClient.getUserByToken(token);
        distributeCaseService.confirmDistributeCase(request.getDistributeConfigModels(),responseEntity.getBody(),CaseIssuedFlag.PERSONAL_HAS_ISSUED);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/groupCaseBatchRenew")
    @ApiOperation(value = "个人批量重新分案预览", notes = "个人批量分案预览")
    public ResponseEntity<GroupBatchDistributeResponse> groupCaseBatchRenew(@RequestBody GroupBatchDistributeRequest request,
                                                                                 @RequestHeader(value = "X-UserToken") String token)throws BadRequestException{
        logger.info("groupCaseDistributePre request :{} ",request.toString());
        ResponseEntity<LoginResponse>  responseEntity=operatorClient.getUserByToken(token);
        BoolQueryBuilder qb = request.generateQueryBuilder();
        //个人分案时需要权限控制
        if(request.getIssuedFlag().equals(CaseIssuedFlag.AREA_HAS_ISSUED) || request.getIssuedFlag().equals(CaseIssuedFlag.PERSONAL_UN_ISSUED)){
            qb.must(termQuery("departments", responseEntity.getBody().getUser().getOrganization()));
        }
        GroupBatchDistributeResponse response= distributeCaseService.groupDistributeBatchCase(request);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/groupBatchConfirmDistributeRenew")
    @ApiOperation(value = "个人批量重新分案确认", notes = "个人批量分案确认")
    public ResponseEntity groupBatchConfirmDistributeRenew(@RequestBody GroupBatchDistributeResponse request,
                                                      @RequestHeader(value = "X-UserToken") String token)throws BadRequestException{
        logger.info("groupConfirmDistributeCase request :{} ",request.toString());
        ResponseEntity<LoginResponse>  responseEntity=operatorClient.getUserByToken(token);
        distributeCaseService.confirmDistributeCase(request.getDistributeConfigModels(),responseEntity.getBody(),CaseIssuedFlag.PERSONAL_HAS_ISSUED);
        return ResponseEntity.ok().body(null);
    }


}
