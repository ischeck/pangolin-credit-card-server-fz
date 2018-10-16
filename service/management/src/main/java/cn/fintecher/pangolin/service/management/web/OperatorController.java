package cn.fintecher.pangolin.service.management.web;


import cn.fintecher.pangolin.common.enums.OperatorState;
import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.*;
import cn.fintecher.pangolin.common.model.response.LoginResponse;
import cn.fintecher.pangolin.common.utils.BeanUtils;
import cn.fintecher.pangolin.common.web.BaseController;
import cn.fintecher.pangolin.entity.managentment.*;
import cn.fintecher.pangolin.service.management.model.request.*;
import cn.fintecher.pangolin.service.management.repository.OperatorRepository;
import cn.fintecher.pangolin.service.management.repository.OrganizationRepository;
import cn.fintecher.pangolin.service.management.repository.ResourceRepository;
import cn.fintecher.pangolin.service.management.repository.RoleRepository;
import cn.fintecher.pangolin.service.management.service.OperatorService;
import cn.fintecher.pangolin.service.management.service.UserRegisterService;
import cn.fintecher.pangolin.service.management.validator.OperatorCreateRequestValidator;
import cn.fintecher.pangolin.service.management.validator.OperatorModifyRequestValidator;
import cn.fintecher.pangolin.service.management.validator.OperatorUpdatePasswordValidator;
import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import io.swagger.annotations.*;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;


/**
 * Created by ChenChang on 2017/10/11.
 */
@RestController
@RequestMapping("/api/operators")
@Api(value = "系统用户相关", description = "系统用户相关")
public class OperatorController extends BaseController {
    private final Logger log = LoggerFactory.getLogger(OperatorController.class);
    @Autowired
    private OperatorRepository operatorRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private OperatorCreateRequestValidator operatorCreateRequestValidator;
    @Autowired
    private OperatorModifyRequestValidator operatorModifyRequestValidator;
    @Autowired
    private OperatorUpdatePasswordValidator updatePasswrodValidator;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private OperatorService operatorService;
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private UserRegisterService userRegisterService;

    @Autowired
    private RedisTemplate<String,Object> jsonRedisTemplate;

    @ApiOperation(value = "创建管理用户", notes = "创建管理用户")
    @PostMapping(value = "/createOperator")
    public ResponseEntity<OperatorModel> createOperator(@Valid @RequestBody CreateOperatorRequest createOperatorRequest) throws URISyntaxException {
        log.debug("REST request to create Operator : {}", createOperatorRequest);

        Operator operator = new Operator();
        //检验该用户名是否唯一
        operatorService.checkUsername(createOperatorRequest.getUsername());
        //检验该工号是否唯一
        operatorService.checkOperatorEmployeeNumber(createOperatorRequest.getEmployeeNumber());
        BeanUtils.copyProperties(createOperatorRequest, operator);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        operator.setPassword(passwordEncoder.encode("888888"));
        operator.setLastLoginTime(new Date());
        operator.setCreateDateTime(new Date());
        operator.setPasswordInvalidTime(new Date());
        operator.setIsManager(createOperatorRequest.getIsManager());
        operator.setState(OperatorState.ENABLED);
        Operator result = operatorRepository.save(operator);
        OperatorModel operatorModel = modelMapper.map(result, OperatorModel.class);
        return ResponseEntity.created(new URI("/api/operators/" + result.getId()))
                .body(operatorModel);
    }

    @ApiOperation(value = "用户登录", notes = "用户登录")
    @PostMapping(value = "/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = operatorService.operatorLogin(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    @ApiOperation(value = "用户查询", notes = "用户查询")
    @GetMapping("/query")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<OperatorModel>> query(Pageable pageable, OperatorSearchRequest request) {
        log.debug("REST request to query Operator");

        Page<OperatorModel> allList = operatorRepository.findAll(request.generateQueryBuilder(), pageable).map(operator -> {
            OperatorModel response = modelMapper.map(operator, OperatorModel.class);
            return response;
        });
        return new ResponseEntity<>(allList, HttpStatus.OK);
    }

    @ApiOperation(value = "修改用户", notes = "修改用户")
    @PostMapping(value = "/modify")
    public ResponseEntity<OperatorModel> modify(@Valid @RequestBody ModifyOperatorRequest modifyOperatorRequest) {
        log.debug("REST request to modify Operator : {}", modifyOperatorRequest);
        Operator operator = operatorRepository.findById(modifyOperatorRequest.getId()).get();
        BeanUtils.copyProperties(modifyOperatorRequest, operator, "password");
        //验证用户是否停用,停用需要检查系统是否存在催收案件
        if(operator.getState().equals(OperatorState.DISABLED)){
            userRegisterService.checkOperatorHasCollectionCase(operator.getId());
        }
        Operator result = operatorRepository.save(operator);
        OperatorModel operatorModel = modelMapper.map(result, OperatorModel.class);
        return ResponseEntity.ok().body(operatorModel);
    }

    @ApiOperation(value = "获取系统用户详情", notes = "获取系统用户详情")
    @GetMapping("/{id}")
    public ResponseEntity<OperatorModel> getOperator(@PathVariable String id) {
        log.debug("REST request to get operator : {}", id);
        Optional<Operator> byId = operatorRepository.findById(id);
        byId.orElseThrow(()->new BadRequestException(null, "operator","operator.not.exist"));
        Operator operator = byId.get();
        return Optional.ofNullable(modelMapper.map(operator, OperatorModel.class))
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "分配角色", notes = "分配角色")
    @PostMapping(value = "/operatorAddRole")
    public ResponseEntity operatorAddRole(@RequestBody ModifyDistributedRequest modifyDistributedRequest) {
        log.debug("REST request to distribute role for operator : {}", modifyDistributedRequest);

        Iterable<Operator> all = operatorRepository.findAll(QOperator.operator.id.in(modifyDistributedRequest.getOperatorId()));
        if (all.iterator().hasNext()) {
            List<Operator> list = Lists.newArrayList(all.iterator());
            list.forEach(operator -> {
                operator.setRole(modifyDistributedRequest.getRoleId());
            });
            operatorRepository.saveAll(list);
        }
        return ResponseEntity.ok().body(null);

    }

    /**
     * @Description : 修改/重置密码
     */
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ApiOperation(value = "修改/重置密码", notes = "修改/重置密码")
    public ResponseEntity<OperatorModel> updatePassword(@Validated @RequestBody @ApiParam("修改的用户密码") UpdateRequestPassword request) {

        Operator operator = operatorService.setPassword(request);
        return Optional.ofNullable(modelMapper.map(operator, OperatorModel.class))
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    @GetMapping("/getUserByToken/{token}")
    @ApiOperation(value = "通过Token获取用户信息", notes = "通过Token获取用户信息")
    public ResponseEntity<LoginResponse> getUserByToken(@PathVariable String token){
        LoginResponse loginResponse= (LoginResponse)jsonRedisTemplate.opsForValue().get(token);
     return Optional.ofNullable(loginResponse)
             .map(result -> new ResponseEntity<>(
                     result,
                     HttpStatus.OK))
             .orElse(new ResponseEntity<>(HttpStatus.FORBIDDEN));

    }

    @GetMapping("/getUserRoleId")
    @ApiOperation(value = "通过roleId获取用户信息", notes = "通过roleId获取用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<OperatorModel>> getUserRoleId(Pageable pageable, OperatorSearchRequest request){

        Page<OperatorModel> allList = operatorRepository.findAll(request.generateQueryBuilder(), pageable).map(operator -> {
            OperatorModel response = modelMapper.map(operator, OperatorModel.class);
            return response;
        });
        return new ResponseEntity<>(allList, HttpStatus.OK);

    }

    @GetMapping("/getUserList")
    @ApiOperation(value = "获取用户列表", notes = "获取用户列表")
    public ResponseEntity<List<OperatorModel>> getUserList(OperatorSearchModel request){
        BooleanBuilder qb = new BooleanBuilder();
        if(Objects.nonNull(request.getRoleIds())){
            qb.and(QOperator.operator.role.any().in(request.getRoleIds()));
        }
        if(Objects.nonNull(request.getOrganizationIds())){
            qb.and(QOperator.operator.organization.in(request.getOrganizationIds()));
        }
        qb.and(QOperator.operator.state.eq(OperatorState.ENABLED));
        List<Operator> allList = IterableUtils.toList(operatorRepository.findAll(qb));
        Type listMap = new TypeToken<List<OperatorModel>>() {
        }.getType();
        List<OperatorModel> response = modelMapper.map(allList,listMap);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }



    private void checkPassword(String password, Operator operator) {
        if (StringUtils.equals(operator.getPassword(), password)) {
            return;
        }
        if (StringUtils.isNotBlank(password)) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            operator.setPassword(passwordEncoder.encode(password));
        }
    }

    @InitBinder("createOperatorRequest")
    public void setupCreateOperatorRequestBinder(WebDataBinder binder) {
        binder.addValidators(operatorCreateRequestValidator);
    }

    @InitBinder("modifyOperatorRequest")
    public void setupModifyOperatorRequestBinder(WebDataBinder binder) {
        binder.addValidators(operatorModifyRequestValidator);
    }

    @InitBinder("updatePassword")
    public void setupdatePassword(WebDataBinder binder) {
        binder.addValidators(updatePasswrodValidator);
    }
}
