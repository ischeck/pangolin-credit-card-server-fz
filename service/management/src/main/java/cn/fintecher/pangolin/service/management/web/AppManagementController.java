package cn.fintecher.pangolin.service.management.web;


import cn.fintecher.pangolin.common.enums.ConfigState;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.model.response.CaseFollowRecordMatchResponse;
import cn.fintecher.pangolin.common.model.response.LoginResponse;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.managentment.ContactResult;
import cn.fintecher.pangolin.entity.managentment.MobilePosition;
import cn.fintecher.pangolin.entity.managentment.Operator;
import cn.fintecher.pangolin.entity.managentment.QContactResult;
import cn.fintecher.pangolin.service.management.model.request.MobileLocateRequest;
import cn.fintecher.pangolin.service.management.model.request.LoginRequest;
import cn.fintecher.pangolin.service.management.model.response.AppLoginResponse;
import cn.fintecher.pangolin.service.management.repository.*;
import cn.fintecher.pangolin.service.management.service.CustConfigService;
import cn.fintecher.pangolin.service.management.service.OperatorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/appManegement")
@Api(value = "APP相关接口", description = "APP相关接口")
public class AppManagementController {

    @Autowired
    private OperatorRepository operatorRepository;
    @Autowired
    private OperatorService operatorService;
    @Autowired
    private ContactResultRepository contactResultRepository;
    @Autowired
    private CustConfigService custConfigService;
    @Autowired
    MobilePositionRepository mobilePositionRepository;

    @Autowired
    private RedisTemplate<String, Object> jsonRedisTemplate;

    @ApiOperation(value = "用户登录", notes = "用户登录")
    @PostMapping(value = "/login")
    public ResponseEntity<AppLoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = operatorService.operatorLogin(loginRequest);
        AppLoginResponse response = new AppLoginResponse();
        response.setFullName(loginResponse.getUser().getFullName());
        response.setOperatorId(loginResponse.getUser().getId());
        response.setToken(loginResponse.getToken());
        response.setHeadPic(loginResponse.getUser().getHeadPic());
        jsonRedisTemplate.opsForValue().set(loginResponse.getToken(), loginResponse);
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "获取催记配置", notes = "获取催记配置")
    @GetMapping(value = "/getFollowConfig")
    public ResponseEntity<List<CaseFollowRecordMatchResponse>> getFollowConfig(@RequestParam String principalId,
                                                                               @RequestParam(required = false) String type) {
        List<CaseFollowRecordMatchResponse> responses = new ArrayList<>();
        Iterable<ContactResult> all = contactResultRepository.findAll((QContactResult.contactResult.principalId.eq(principalId)
                .and(QContactResult.contactResult.configState.eq(ConfigState.ENABLED)))
                .and((QContactResult.contactResult.level.gt(2)).or(QContactResult.contactResult.propertyType.eq("SELECT"))));
        responses = custConfigService.getFollowRecordFields(all,principalId,type);
        responses.sort(Comparator.comparing(CaseFollowRecordMatchResponse::getId));
        return ResponseEntity.ok().body(responses);
    }

    @ApiOperation(value = "修改头像", notes = "修改头像")
    @GetMapping(value = "/updatePic")
    public ResponseEntity<String> updatePic(@RequestParam String url,
                                          @RequestParam String operatorId) {
        Operator operator = operatorRepository.findById(operatorId).get();
        operator.setHeadPic(url);
        operatorRepository.save(operator);
        return ResponseEntity.ok().body(url);
    }

    @ApiOperation(value = "APP定位", notes = "APP定位")
    @PostMapping(value = "/mobileLocate")
    public ResponseEntity<Void> mobileLocate(@RequestHeader(value = "X-UserToken") String token,
                                          @RequestBody MobileLocateRequest request) {
        OperatorModel operator = operatorService.getSessionByToken(token);
        MobilePosition mobilePosition = new MobilePosition();
        BeanUtils.copyProperties(request, mobilePosition);
        mobilePosition.setOperator(operator.getId());
        mobilePosition.setOperatorName(operator.getFullName());
        mobilePosition.setOrganization(operator.getOrganization());
        mobilePosition.setDate(ZWDateUtil.getNowDateTime());
        mobilePositionRepository.save(mobilePosition);
        return ResponseEntity.ok().body(null);
    }

}

