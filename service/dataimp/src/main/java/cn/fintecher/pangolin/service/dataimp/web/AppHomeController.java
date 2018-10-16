package cn.fintecher.pangolin.service.dataimp.web;

import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.model.response.LoginResponse;
import cn.fintecher.pangolin.service.dataimp.client.OperatorClient;
import cn.fintecher.pangolin.service.dataimp.model.response.AppHomePageResponse;
import cn.fintecher.pangolin.service.dataimp.service.AppHomeService;
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

@RestController
@RequestMapping("/api/appHome")
@Api(value = "APP首页", description = "APP首页")
public class AppHomeController {

    Logger log = LoggerFactory.getLogger(AppHomeController.class);

    @Autowired
    OperatorClient operatorClient;
    @Autowired
    AppHomeService appHomeService;

    @GetMapping("/getHomePage")
    @ApiOperation(notes = "APP首页信息获取",value = "APP首页信息获取")
    public ResponseEntity<AppHomePageResponse> getHomePage(@RequestHeader(value = "X-UserToken") String token){
        LoginResponse userByToken = operatorClient.getUserByToken(token).getBody();
        OperatorModel operator = userByToken.getUser();
        AppHomePageResponse response = appHomeService.getHomePage(operator.getId());
        return ResponseEntity.ok().body(response);
    }

}
