package cn.fintecher.pangolin.service.dataimp.web;

import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.model.response.LoginResponse;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.service.dataimp.client.OperatorClient;
import cn.fintecher.pangolin.service.dataimp.model.WaitHandleListModel;
import cn.fintecher.pangolin.service.dataimp.model.response.CommentModel;
import cn.fintecher.pangolin.service.dataimp.model.response.CommentResponse;
import cn.fintecher.pangolin.service.dataimp.model.response.StarInfoResponse;
import cn.fintecher.pangolin.service.dataimp.repository.BalancePayRecordRepository;
import cn.fintecher.pangolin.service.dataimp.service.HomeCollectorService;
import cn.fintecher.pangolin.service.dataimp.service.HomeNoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

@RestController
@RequestMapping("/api/homeNoticeController")
@Api(value = "公告首页", description = "公告首页")
public class HomeNoticeController {

    Logger log = LoggerFactory.getLogger(HomeNoticeController.class);

    @Autowired
    HomeNoticeService homeNoticeService;

    @Autowired
    OperatorClient operatorClient;

    @Autowired
    HomeCollectorService homeCollectorService;

    @Autowired
    BalancePayRecordRepository balancePayRecordRepository;


    @GetMapping("/getStarCollector")
    @ApiOperation(notes = "明星催收员", value = "明星催收员")
    public ResponseEntity<List<StarInfoResponse>> getStarCollector() {
        List<StarInfoResponse> responses = new ArrayList<>();
        BoolQueryBuilder nowBuilder = QueryBuilders.boolQuery();
        nowBuilder.must(rangeQuery("payDate").gte(ZWDateUtil.getMonthFirstDay(0).getTime()));
        StarInfoResponse nowResponse = homeNoticeService.getStarCollector(nowBuilder);
        nowResponse.setMonth("本月");
        responses.add(nowResponse);
        BoolQueryBuilder lastBuilder = QueryBuilders.boolQuery();
        lastBuilder.must(rangeQuery("payDate").lt(ZWDateUtil.getMonthFirstDay(0).getTime()));
        lastBuilder.must(rangeQuery("payDate").gte(ZWDateUtil.getMonthFirstDay(-1).getTime()));
        StarInfoResponse lastResponse = homeNoticeService.getStarCollector(lastBuilder);
        lastResponse.setMonth("上月");
        responses.add(lastResponse);
        return ResponseEntity.ok().body(responses);
    }


    @GetMapping("/getStarOrganization")
    @ApiOperation(notes = "明星催收部门", value = "明星催收部门")
    public ResponseEntity<List<StarInfoResponse>> getStarOrganization() {
        List<StarInfoResponse> responses = new ArrayList<>();
        BoolQueryBuilder nowBuilder = QueryBuilders.boolQuery();
        nowBuilder.must(rangeQuery("payDate").gte(ZWDateUtil.getMonthFirstDay(0).getTime()));
        StarInfoResponse nowResponse = homeNoticeService.getStarOrganization(nowBuilder);
        nowResponse.setMonth("本月");
        responses.add(nowResponse);
        BoolQueryBuilder lastBuilder = QueryBuilders.boolQuery();
        lastBuilder.must(rangeQuery("payDate").lt(ZWDateUtil.getMonthFirstDay(0).getTime()));
        lastBuilder.must(rangeQuery("payDate").gte(ZWDateUtil.getMonthFirstDay(-1).getTime()));
        StarInfoResponse lastResponse = homeNoticeService.getStarOrganization(lastBuilder);
        lastResponse.setMonth("上月");
        responses.add(lastResponse);
        return ResponseEntity.ok().body(responses);
    }

    @GetMapping("/getComment")
    @ApiOperation(value = "查询备忘录", notes = "查询备忘录")
    public ResponseEntity<List<CommentModel>> getComment(@RequestHeader(value = "X-UserToken") String token) {
        LoginResponse userByToken = operatorClient.getUserByToken(token).getBody();
        OperatorModel operator = userByToken.getUser();
        List<CommentModel> responses = homeNoticeService.getComment(operator);
        return ResponseEntity.ok().body(responses);
    }

    @GetMapping("/getWaitHandleList")
    @ApiOperation(value = "查询待办事项", notes = "查询待办事项")
    public ResponseEntity<WaitHandleListModel> getWaitHandleList(@RequestHeader(value = "X-UserToken") String token) {
        LoginResponse userByToken = operatorClient.getUserByToken(token).getBody();
        OperatorModel operator = userByToken.getUser();
        WaitHandleListModel model = homeNoticeService.getWaitHandleList(operator);
        return ResponseEntity.ok().body(model);
    }

}
