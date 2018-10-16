package cn.fintecher.pangolin.service.management.web;

import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.ApproveFlowConfigModel;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.managentment.ApproveFlowConfig;
import cn.fintecher.pangolin.entity.managentment.QApproveFlowConfig;
import cn.fintecher.pangolin.service.management.model.request.ConfigFlowSearchRequest;
import cn.fintecher.pangolin.service.management.model.request.CreateConfigFlowRequest;
import cn.fintecher.pangolin.service.management.model.request.ModifyConfigFlowRequest;
import cn.fintecher.pangolin.service.management.repository.ConfigFlowRepository;
import cn.fintecher.pangolin.service.management.service.OperatorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by huyanmin on 2018/7/24
 */
@RestController
@RequestMapping("/api/configFlowController")
@Api(value = "审批流程配置", description = "审批流程配置")
public class ConfigFlowController {
    private final Logger log = LoggerFactory.getLogger(ConfigFlowController.class);
    @Autowired
    OperatorService operatorService;
    @Autowired
    private ConfigFlowRepository configFlowRepository;
    @Autowired
    private ModelMapper modelMapper;

    @ApiOperation(value = "新建配置流程", notes = "新建配置流程")
    @PostMapping("/createConfigFlow")
    public ResponseEntity<ApproveFlowConfigModel> createConfigFlow(@RequestBody CreateConfigFlowRequest request,
                                                                   @RequestHeader(value = "X-UserToken") String token) throws BadRequestException {
        log.debug("REST create to configuration flow");
        OperatorModel operator = operatorService.getSessionByToken(token);
        if (Objects.isNull(token)) {
            throw new BadRequestException(null, "login", "operator.not.login");
        }
        ApproveFlowConfig config = new ApproveFlowConfig();
        Optional<ApproveFlowConfig> one = configFlowRepository.findOne(QApproveFlowConfig.approveFlowConfig.configType.eq(request.getConfigType()));
        if(one.isPresent()){
            throw new BadRequestException(null, "configFlow", "configFlow.has.exist.approvalType");
        }
        BeanUtils.copyProperties(request, config);
        config.setLevel(request.getConfigMap().size());
        config.setOperator(operator.getId());
        config.setOperatorTime(ZWDateUtil.getNowDate());
        configFlowRepository.save(config);
        ApproveFlowConfigModel model = modelMapper.map(config, ApproveFlowConfigModel.class);
        return ResponseEntity.ok().body(model);
    }

    @ApiOperation(value = "配置流程查询", notes = "配置流程查询")
    @GetMapping("/configFlowQuery")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<ApproveFlowConfigModel>> configFlowQuery(@ApiIgnore Pageable pageable,
                                                                       ConfigFlowSearchRequest request) throws URISyntaxException {
        log.debug("REST request to query configFlow");

        Page<ApproveFlowConfigModel> allList = configFlowRepository.findAll(request.generateQueryBuilder(), pageable).map(approveFlowConfig -> {
            ApproveFlowConfigModel response = modelMapper.map(approveFlowConfig, ApproveFlowConfigModel.class);
            return response;
        });
        return new ResponseEntity<>(allList, HttpStatus.OK);
    }

    @ApiOperation(value = "查询所有配置流程", notes = "查询所有配置流程")
    @GetMapping("/findConfigFlow")
    public ResponseEntity<List<ApproveFlowConfigModel>> findConfigFlow() throws URISyntaxException {
        log.debug("REST request to find configFlow");
        Type listType = new TypeToken<List<ApproveFlowConfigModel>>() {
        }.getType();
        List<ApproveFlowConfigModel> approveFlowConfigModels = modelMapper.map(configFlowRepository.findAll(), listType);
        return Optional.ofNullable(approveFlowConfigModels)
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "修改配置流程", notes = "修改配置流程")
    @PostMapping("/modifyConfigFlow")
    public ResponseEntity<ApproveFlowConfigModel> modifyConfigFlow(@RequestBody ModifyConfigFlowRequest request,
                                                                   @RequestHeader(value = "X-UserToken") String token) throws URISyntaxException{
        log.debug("REST create to configuration flow");
        OperatorModel operator = operatorService.getSessionByToken(token);
        if (Objects.isNull(operator)) {
            throw new BadRequestException(null, "login", "operator.not.login");
        }
        Optional<ApproveFlowConfig> byId = configFlowRepository.findById(request.getId());
        byId.orElseThrow(()->new BadRequestException(null, "configFlow","configFlow.is.not.exist"));
        ApproveFlowConfig config = byId.get();
        BeanUtils.copyProperties(request, config);
        config.setOperator(operator.getId());
        config.setOperatorTime(ZWDateUtil.getNowDate());
        configFlowRepository.save(config);
        ApproveFlowConfigModel model = modelMapper.map(config, ApproveFlowConfigModel.class);
        return ResponseEntity.ok().body(model);
    }

    @ApiOperation(value = "删除配置流程", notes = "删除配置流程")
    @DeleteMapping("/deleteConfigFlow/{id}")
    public ResponseEntity deletePrincipal(@PathVariable String id) {
        log.debug("REST request to delete configFlow : {}", id);
        //需要验证是否该委托方有关联案件
        Optional<ApproveFlowConfig> byId = configFlowRepository.findById(id);
        byId.orElseThrow(() -> new BadRequestException(null, "configFlow", "configFlow.is.not.exist"));
        configFlowRepository.deleteById(id);
        return ResponseEntity.ok().body(null);
    }

}
