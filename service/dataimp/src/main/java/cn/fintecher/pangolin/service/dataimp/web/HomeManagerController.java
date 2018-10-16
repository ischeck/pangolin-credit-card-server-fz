package cn.fintecher.pangolin.service.dataimp.web;

import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.model.response.LoginResponse;
import cn.fintecher.pangolin.service.dataimp.client.OperatorClient;
import cn.fintecher.pangolin.service.dataimp.model.response.CountInfoResponse;
import cn.fintecher.pangolin.service.dataimp.model.response.StockModel;
import cn.fintecher.pangolin.service.dataimp.model.response.StockResponse;
import cn.fintecher.pangolin.service.dataimp.service.HomeCollectorService;
import cn.fintecher.pangolin.service.dataimp.service.HomeManagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/homeManagerController")
@Api(value = "管理员首页", description = "管理员首页")
public class HomeManagerController {
    Logger log = LoggerFactory.getLogger(HomeManagerController.class);

    @Autowired
    OperatorClient operatorClient;

    @Autowired
    HomeCollectorService homeCollectorService;

    @Autowired
    HomeManagerService homeManagerService;

    @GetMapping("/getCountInfo")
    @ApiOperation(notes = "获取统计数据", value = "获取统计数据")
    public ResponseEntity<CountInfoResponse> getCountInfo(@RequestHeader(value = "X-UserToken") String token){
        LoginResponse userByToken = operatorClient.getUserByToken(token).getBody();
        OperatorModel operator = userByToken.getUser();
        CountInfoResponse response = homeManagerService.getCountInfo(operator);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/getStock")
    @ApiOperation(notes = "总存量", value = "总存量")
    public ResponseEntity<StockModel> getStock(@RequestHeader(value = "X-UserToken") String token){
        LoginResponse userByToken = operatorClient.getUserByToken(token).getBody();
        OperatorModel operator = userByToken.getUser();
        StockModel model = homeCollectorService.getStock(operator);
        return ResponseEntity.ok().body(model);
    }
}
