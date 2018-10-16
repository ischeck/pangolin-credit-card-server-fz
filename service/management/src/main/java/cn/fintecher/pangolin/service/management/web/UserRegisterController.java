package cn.fintecher.pangolin.service.management.web;

import cn.fintecher.pangolin.common.enums.OperatorState;
import cn.fintecher.pangolin.common.enums.UserState;
import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.FileModel;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.utils.BeanUtils;
import cn.fintecher.pangolin.common.web.BaseController;
import cn.fintecher.pangolin.entity.managentment.Operator;
import cn.fintecher.pangolin.entity.managentment.QUser;
import cn.fintecher.pangolin.entity.managentment.User;
import cn.fintecher.pangolin.service.management.model.UserRegisteredModel;
import cn.fintecher.pangolin.service.management.model.request.*;
import cn.fintecher.pangolin.service.management.model.response.UserResponse;
import cn.fintecher.pangolin.service.management.repository.OperatorRepository;
import cn.fintecher.pangolin.service.management.repository.UserRegisterRepository;
import cn.fintecher.pangolin.service.management.service.OperatorService;
import cn.fintecher.pangolin.service.management.service.UserRegisterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by huyanmin on 2018/8/29.
 */
@RestController
@RequestMapping("/api/userRegister")
@Api(value = "人员花名册录入", description = "人员花名册录入")
public class UserRegisterController extends BaseController {
    private final Logger log = LoggerFactory.getLogger(UserRegisterController.class);
    @Autowired
    private UserRegisterRepository userRegisterRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserRegisterService userRegisterService;
    @Autowired
    private OperatorRepository operatorRepository;
    @Autowired
    private OperatorService operatorService;

    @ApiOperation(value = "创建人员花名册", notes = "创建人员花名册")
    @PostMapping(value = "/createUser")
    public ResponseEntity<OperatorModel> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) throws URISyntaxException {
        log.debug("REST request to create User : {}", createUserRequest);
        User user = new User();
        //检验该工号是否唯一
        userRegisterService.checkEmployeeNumber(createUserRequest.getEmployeeNumber());
        BeanUtils.copyProperties(createUserRequest, user);
        user.setState(UserState.INCUMBENCY);
        User result = userRegisterRepository.save(user);
        OperatorModel operatorModel = modelMapper.map(result, OperatorModel.class);
        return ResponseEntity.created(new URI("/api/operators/" + result.getId()))
                .body(operatorModel);
    }

    @ApiOperation(value = "用户查询", notes = "用户查询")
    @GetMapping("/queryUser")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<UserResponse>> queryUser(Pageable pageable, UserSearchRequest request) {
        log.debug("REST request to query User");

        Page<UserResponse> allList = userRegisterRepository.findAll(request.generateQueryBuilder(), pageable).map(operator -> {
            UserResponse response = modelMapper.map(operator, UserResponse.class);
            return response;
        });
        return new ResponseEntity<>(allList, HttpStatus.OK);
    }

    @ApiOperation(value = "修改用户", notes = "修改用户")
    @PostMapping(value = "/modifyUser")
    public ResponseEntity<User> modifyUser(@Valid @RequestBody ModifyUserRequest modifyUserRequest) {
        log.debug("REST request to modify User : {}", modifyUserRequest);
        User user = userRegisterRepository.findById(modifyUserRequest.getId()).get();
        String oldOrg = user.getOrganization();
        BeanUtils.copyProperties(modifyUserRequest, user);
        //修改组织需要同步系统账号中的用户组织
        if (!oldOrg.equals(user.getOrganization())) {
            Operator operator = userRegisterService.checkHasCollectionCase(user);
            if (Objects.nonNull(operator)) {
                operator.setOrganization(user.getEmployeeNumber());
                operatorRepository.save(operator);
            }
        }
        //验证用户是否停用,停用需要检查系统是否存在催收案件
        if (user.getState().equals(OperatorState.DISABLED)) {
            userRegisterService.checkHasCollectionCase(user);
        }
        User result = userRegisterRepository.save(user);
        return ResponseEntity.ok().body(result);
    }

    @ApiOperation(value = "获取系统用户详情", notes = "获取系统用户详情")
    @GetMapping("/getUser")
    public ResponseEntity<UserResponse> getUser(@RequestParam String id) {
        log.debug("REST request to get operator : {}", id);
        Optional<User> byId = userRegisterRepository.findById(id);
        byId.orElseThrow(() -> new BadRequestException(null, "operator", "operator.not.exist"));
        User user = byId.get();
        return Optional.ofNullable(modelMapper.map(user, UserResponse.class))
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "通过工号获取员工详情", notes = "通过工号获取员工详情")
    @GetMapping("/getUserByEmployeeNumber")
    public ResponseEntity<UserResponse> getUserByEmployeeNumber(@RequestParam String employeeNumber) {
        log.debug("REST request to get User : {}", employeeNumber);
        Iterable<User> all = userRegisterRepository.findAll(QUser.user.employeeNumber.eq(employeeNumber).and(QUser.user.state.eq(UserState.INCUMBENCY)));
        User user = new User();
        if (all.iterator().hasNext()) {
            user = all.iterator().next();
        } else {
            new BadRequestException(null, "operator", "operator.not.exist");
        }
        return Optional.ofNullable(modelMapper.map(user, UserResponse.class))
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "保存文件", notes = "保存文件")
    @GetMapping(value = "/saveFileId")
    public ResponseEntity<User> saveFileId(FileSaveRequest request) {
        User user = userRegisterRepository.findById(request.getUserId()).get();
        FileModel fileModel = new FileModel();
        fileModel.setFileId(request.getFileId());
        fileModel.setFileName(request.getFileName());
        fileModel.setFilePath(request.getFilePath());
        user.getFileContent().add(fileModel);
        userRegisterRepository.save(user);
        return ResponseEntity.ok().body(user);
    }


    @PostMapping("/userImport")
    @ApiOperation(value = "用户花名册导入", notes = "用户花名册导入")
    public ResponseEntity userImport(@RequestBody UserRegisteredModel model,
                                     @RequestHeader(value = "X-UserToken") String token) {
        //获取文件
        InputStream inputStream = userRegisterService.readFile(model.getFileId());
        OperatorModel operatorModel = operatorService.getSessionByToken(token);
        userRegisterService.userImport(inputStream, operatorModel);
        return ResponseEntity.ok().body(null);
    }

}
