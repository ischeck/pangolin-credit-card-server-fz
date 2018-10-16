package cn.fintecher.pangolin.service.dataimp.web;


import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.model.response.LoginResponse;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.service.dataimp.client.OperatorClient;
import cn.fintecher.pangolin.service.dataimp.model.CollStateModel;
import cn.fintecher.pangolin.service.dataimp.model.PaymentRecordModel;
import cn.fintecher.pangolin.service.dataimp.model.response.CommentResponse;
import cn.fintecher.pangolin.service.dataimp.model.response.MonthScheduleResponse;
import cn.fintecher.pangolin.service.dataimp.model.response.StockModel;
import cn.fintecher.pangolin.service.dataimp.model.response.StockResponse;
import cn.fintecher.pangolin.service.dataimp.repository.ImpCommentRepository;
import cn.fintecher.pangolin.service.dataimp.service.HomeCollectorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

@RestController
@RequestMapping("/api/homePageController")
@Api(value = "催收员首页", description = "催收员首页")
public class HomeCollectorController {

    Logger log = LoggerFactory.getLogger(HomeCollectorController.class);

    @Autowired
    OperatorClient operatorClient;

    @Autowired
    HomeCollectorService homeCollectorService;

    @Autowired
    ImpCommentRepository impCommentRepository;

    @GetMapping("/getGroupMonthSchedule")
    @ApiOperation(value = "小组月进度", notes = "小组月进度")
    public ResponseEntity<MonthScheduleResponse> getGroupMonthSchedule(@RequestHeader(value = "X-UserToken") String token){
        LoginResponse userByToken = operatorClient.getUserByToken(token).getBody();
        OperatorModel operator = userByToken.getUser();
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        qb.must(matchPhraseQuery("detaptId", operator.getOrganization()));
        MonthScheduleResponse monthScheduleResponse = homeCollectorService.getMonthSchedule(qb);
        return ResponseEntity.ok().body(monthScheduleResponse);
    }

    @GetMapping("/getPersonalMonthSchedule")
    @ApiOperation(value = "个人月进度", notes = "个人月进度")
    public ResponseEntity<MonthScheduleResponse> getPersonalMonthSchedule(@RequestHeader(value = "X-UserToken") String token){
        LoginResponse userByToken = operatorClient.getUserByToken(token).getBody();
        OperatorModel operator = userByToken.getUser();
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        qb.must(matchPhraseQuery("currentCollector.id", operator.getId()));
        MonthScheduleResponse monthScheduleResponse = homeCollectorService.getMonthSchedule(qb);
        return ResponseEntity.ok().body(monthScheduleResponse);
    }

    @GetMapping("/getComment")
    @ApiOperation(value = "查询备忘录", notes = "查询备忘录")
    public ResponseEntity<List<CommentResponse>> getComment(@RequestHeader(value = "X-UserToken") String token){
        LoginResponse userByToken = operatorClient.getUserByToken(token).getBody();
        OperatorModel operator = userByToken.getUser();
        List<CommentResponse> responses = homeCollectorService.getComment(operator);
        return ResponseEntity.ok().body(responses);
    }

    @GetMapping("/getPTPRecord")
    @ApiOperation(value = "承诺还款记录", notes = "承诺还款记录")
    public ResponseEntity<PaymentRecordModel> getPTPRecord(@RequestHeader(value = "X-UserToken") String token){
        LoginResponse userByToken = operatorClient.getUserByToken(token).getBody();
        OperatorModel operator = userByToken.getUser();
        PaymentRecordModel model = homeCollectorService.getPTPRecord(operator);
        return ResponseEntity.ok().body(model);
    }

    @GetMapping("/getCollState")
    @ApiOperation(value = "账户标记", notes = "账户标记")
    public ResponseEntity<CollStateModel> getCollState(@RequestHeader(value = "X-UserToken") String token){
        LoginResponse userByToken = operatorClient.getUserByToken(token).getBody();
        OperatorModel operator = userByToken.getUser();
        CollStateModel model = homeCollectorService.getCollState(operator);
        return ResponseEntity.ok().body(model);
    }

    @GetMapping("/getStock")
    @ApiOperation(value = "总存量", notes = "总存量")
    public ResponseEntity<StockModel> getStock(@RequestHeader(value = "X-UserToken") String token){
        LoginResponse userByToken = operatorClient.getUserByToken(token).getBody();
        OperatorModel operator = userByToken.getUser();
        StockModel model = homeCollectorService.getStock(operator);
        return ResponseEntity.ok().body(model);
    }
}
