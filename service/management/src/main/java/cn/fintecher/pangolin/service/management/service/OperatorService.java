package cn.fintecher.pangolin.service.management.service;


import cn.fintecher.pangolin.common.enums.OperatorState;
import cn.fintecher.pangolin.common.enums.UserState;
import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.model.OrganizationModel;
import cn.fintecher.pangolin.common.model.ResourceModel;
import cn.fintecher.pangolin.common.model.response.LoginResponse;
import cn.fintecher.pangolin.common.utils.MD5;
import cn.fintecher.pangolin.entity.managentment.*;
import cn.fintecher.pangolin.service.management.model.request.LoginRequest;
import cn.fintecher.pangolin.service.management.model.request.UpdateRequestPassword;
import cn.fintecher.pangolin.service.management.repository.*;
import org.apache.commons.collections4.IteratorUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by ChenChang on 2017/12/20.
 */
@Service("operatorService")
public class OperatorService {
    final Logger log = LoggerFactory.getLogger(OperatorService.class);
    @Autowired
    private OperatorRepository operatorRepository;
    @Autowired
    private RedisTemplate<String, LoginResponse> tokenStoreTemplate;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private RedisTemplate<String, Object> jsonRedisTemplate;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private UserRegisterRepository userRegisterRepository;

    /**
     * 检查是否存在用户
     *
     * @param username
     * @return
     */
    public Boolean checkUsername(String username) {
        QOperator qOperator = QOperator.operator;
        Boolean exists = operatorRepository.exists(qOperator.username.eq(username));
        return exists;
    }


    /**
     * 检查是否存在用户
     *
     * @param employeeNumber
     * @return
     */
    public void checkOperatorEmployeeNumber(String employeeNumber) {
        QOperator qOperator = QOperator.operator;
        Boolean exists = operatorRepository.exists(qOperator.employeeNumber.eq(employeeNumber).and(qOperator.state.eq(OperatorState.ENABLED)));
        if(exists){
            throw new BadRequestException(null, "employeeNumber","employeeNumber.is.exist");
        }
    }

    /**
     * 检查是否存在用户
     *
     * @param fullName
     * @return
     */
    public void checkEmployeeNumber(String fullName, String employeeNumber) {
        QUser qUser = QUser.user;
        Boolean exists = userRegisterRepository.exists(qUser.employeeNumber.eq(employeeNumber)
                                                        .and(qUser.state.eq(UserState.INCUMBENCY)));
        if(!exists){
            throw new BadRequestException(null, "employeeNumber","employeeNumber.is.not.register");
        }
        Boolean exists1 = userRegisterRepository.exists(qUser.fullName.eq(fullName).and(qUser.employeeNumber.eq(employeeNumber))
                          .and(qUser.state.eq(UserState.INCUMBENCY)));
        if(!exists1){
            throw new BadRequestException(null, "employeeNumber","employeeNumber.is.not.matched");
        }
    }

    public Operator findOperatorByUsername(String username) {
        QOperator qOperator = QOperator.operator;
        return operatorRepository.findAll(qOperator.username.eq(username)).iterator().next();
    }

    public Operator findOperatorById(String id) {
        return operatorRepository.findById(id).get();
    }

    public LoginResponse findOperatorByToken(String token) {
        LoginResponse operatorModel = tokenStoreTemplate.opsForValue().get("token:" + token);
        return operatorModel;
    }

    /***
     * 用户登录
     * @param loginRequest
     * @return
     */
    public LoginResponse operatorLogin(LoginRequest loginRequest) {
        QOperator qOperator = QOperator.operator;
        Optional<Operator> operator = operatorRepository.findOne(qOperator.username.eq(loginRequest.getUsername()));
        operator.orElseThrow(() -> new BadRequestException(null, "login", "login.wrong.username"));

        if (operator.get().getState().equals(OperatorState.DISABLED)) {
            throw new BadRequestException(null, "login", "operator.is.disable");
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(loginRequest.getPassword(), operator.get().getPassword())) {
            throw new BadRequestException(null, "login", "login.wrong.password");
        }
        OperatorModel operatorModel = modelMapper.map(operator.get(), OperatorModel.class);
        OrganizationModel organizationModel = modelMapper.map(organizationRepository.findById(operator.get().getOrganization()).get(), OrganizationModel.class);
        Iterable<Role> all = roleRepository.findAll(QRole.role.id.in(operatorModel.getRole()));
        List<Role> roles;
        Set<String> resourceSet = new HashSet<>();
        if (all.iterator().hasNext()) {
            roles = IteratorUtils.toList(all.iterator());
            roles.forEach(role -> {
                if (Objects.nonNull(role.getResources())) {
                    resourceSet.addAll(role.getResources());
                }
            });
        }
        Sort sort = new Sort(Sort.Direction.ASC, "sort");
        Iterable<Resource> all1 = resourceRepository.findAll(QResource.resource.id.in(resourceSet), sort);
        List<ResourceModel> resourceModels = new ArrayList<>();
        if (all1.iterator().hasNext()) {
            List<Resource> resources = IteratorUtils.toList(all1.iterator());
            Type resourceType = new TypeToken<List<ResourceModel>>() {
            }.getType();
            resourceModels = modelMapper.map(resources, resourceType);
        }
        operatorModel.setMenu(resourceModels);
        operatorModel.setResource(resourceSet);
        String token = MD5.MD5Encode(loginRequest.getUsername().concat(loginRequest.getPassword()));
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setUser(operatorModel);
        loginResponse.setOrganizationModel(organizationModel);
        loginResponse.setToken(token);
        jsonRedisTemplate.opsForValue().set(token, loginResponse);
        return loginResponse;
    }

    public void operatorSignOut(String token) {
        tokenStoreTemplate.delete("token:" + token);
    }

    /**
     * 获取登录账号的Session
     *
     * @param token
     * @return
     */
    public OperatorModel getSessionByToken(String token) {
        LoginResponse loginResponse = (LoginResponse) jsonRedisTemplate.opsForValue().get(token);
        if(Objects.isNull(loginResponse)){
            throw new BadRequestException(null,"Operator","token.is.error");
        }
        OperatorModel operatorModel = loginResponse.getUser();
        return operatorModel;
    }


    /**
     * @param
     * @Description 重置或修改密码
     */
    public Operator setPassword(UpdateRequestPassword requestPassword) {
        //密码加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Operator operator = operatorRepository.findById(requestPassword.getUserId()).get();
        //原始密码不为空，则进行密码修改，否则进行重置
        if (Objects.nonNull(requestPassword.getOldPassword())) {
            if (!passwordEncoder.matches(requestPassword.getOldPassword(), operator.getPassword())) {
                throw new BadRequestException(null, "login", "oldPassword.is.wrong");
            }
            operator.setPassword(passwordEncoder.encode(requestPassword.getNewPassword()));
        } else {
            operator.setPassword(passwordEncoder.encode("888888"));
        }
        operator.setPasswordInvalidTime(new Date());
        operator = operatorRepository.save(operator);
        return operator;
    }

}