package cn.fintecher.pangolin.service.management.web;

import cn.fintecher.pangolin.common.enums.ComplianceState;
import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.OrganizationModel;
import cn.fintecher.pangolin.common.model.PrincipalModel;
import cn.fintecher.pangolin.common.model.ResourceModel;
import cn.fintecher.pangolin.common.utils.BeanUtils;
import cn.fintecher.pangolin.entity.managentment.ComplianceConfig;
import cn.fintecher.pangolin.entity.managentment.Organization;
import cn.fintecher.pangolin.entity.managentment.Principal;
import cn.fintecher.pangolin.entity.managentment.Resource;
import cn.fintecher.pangolin.service.management.model.ComplianceConfigListModel;
import cn.fintecher.pangolin.service.management.model.ComplianceConfigModel;
import cn.fintecher.pangolin.service.management.model.request.*;
import cn.fintecher.pangolin.service.management.repository.ComplianceConfigRepository;
import cn.fintecher.pangolin.service.management.repository.OrganizationRepository;
import cn.fintecher.pangolin.service.management.repository.PrincipalRepository;
import cn.fintecher.pangolin.service.management.repository.ResourceRepository;
import cn.fintecher.pangolin.service.management.service.OperatorService;
import com.google.common.collect.Sets;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by ChenChang on 2018/8/31.
 */
@RestController
@RequestMapping("/api/complianceConfig")
@Api(value = "合规配置api", description = "合规配置api")
public class ComplianceConfigController {
    private final Logger log = LoggerFactory.getLogger(ComplianceConfigController.class);
    @Autowired
    private ComplianceConfigRepository complianceConfigRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private PrincipalRepository principalRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private OperatorService operatorService;
    @Autowired
    private ResourceRepository resourceRepository;


    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/query")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<ComplianceConfigListModel>> configFlowQuery(@ApiIgnore Pageable pageable,
                                                                           QueryComplianceConfigRequest request) {
        log.debug("REST request to query configFlow");

        Page<ComplianceConfigListModel> allList = complianceConfigRepository.findAll(request.generateQueryBuilder(), pageable).map(config -> {
            ComplianceConfigListModel response = modelMapper.map(config, ComplianceConfigListModel.class);
            return response;
        });
        return new ResponseEntity<>(allList, HttpStatus.OK);
    }

    @ApiOperation(value = "创建合规配置", notes = "创建合规配置")
    @PostMapping(value = "/createComplianceConfig")
    public ResponseEntity<ComplianceConfigModel> createComplianceConfig(@Valid @RequestBody CreateComplianceConfigRequest request, @RequestHeader(value = "X-UserToken") String token) {
        log.debug("REST request to create ComplianceConfig : {}", request);
        ComplianceConfig complianceConfig = new ComplianceConfig();
        BeanUtils.copyProperties(request, complianceConfig);
        if (!organizationRepository.existsById(request.getOrganizationId())) {
            throw new BadRequestException(null, "id is not exists", "org.not.exist");
        }
        Organization organization = organizationRepository.findById(request.getOrganizationId()).get();
        OrganizationModel organizationModel = modelMapper.map(organization, OrganizationModel.class);
        complianceConfig.setOrganization(organizationModel);
        //选择需要检查的委托方
        complianceConfig.setEnablePrincipals(request.getEnablePrincipals());
        //选择需要关闭的资源
        complianceConfig.setDisableResources(request.getDisableResources());
        complianceConfig.setOperator(operatorService.getSessionByToken(token));
        complianceConfig.setCreateTime(new Date());
        complianceConfig.setState(ComplianceState.DISABLED);
        complianceConfig = complianceConfigRepository.save(complianceConfig);
        ComplianceConfigModel operatorModel = modelMapper.map(complianceConfig, ComplianceConfigModel.class);
        return ResponseEntity.ok().body(operatorModel);
    }

    @ApiOperation(value = "修改合规配置", notes = "创建合规配置")
    @PostMapping(value = "/modifyComplianceConfig")
    public ResponseEntity<ComplianceConfigModel> createComplianceConfig(@Valid @RequestBody ModifyComplianceConfigRequest request, @RequestHeader(value = "X-UserToken") String token) {
        log.debug("REST request to create ComplianceConfig : {}", request);
        if (!complianceConfigRepository.existsById(request.getId())) {
            throw new BadRequestException(null, "id is not exists", "id.not.exist");
        }
        ComplianceConfig complianceConfig = complianceConfigRepository.findById(request.getId()).get();
        complianceConfig.setName(request.getName());
        complianceConfig.setState(request.getState());
        if (!organizationRepository.existsById(request.getOrganizationId())) {
            throw new BadRequestException(null, "org is not exists", "org.not.exist");
        }
        Organization organization = organizationRepository.findById(request.getOrganizationId()).get();
        OrganizationModel organizationModel = modelMapper.map(organization, OrganizationModel.class);
        complianceConfig.setOrganization(organizationModel);
        complianceConfig.setOperator(operatorService.getSessionByToken(token));
        complianceConfig.setCreateTime(new Date());
        complianceConfig.setState(ComplianceState.DISABLED);
        complianceConfig = complianceConfigRepository.save(complianceConfig);
        ComplianceConfigModel operatorModel = modelMapper.map(complianceConfig, ComplianceConfigModel.class);
        return ResponseEntity.ok().body(operatorModel);
    }

    @ApiOperation(value = "修改允许的委托方", notes = "修改允许的委托方")
    @PutMapping(value = "/modifyAllowedPrincipals")
    public ResponseEntity<ComplianceConfigModel> modifyAllowedPrincipals(@Valid @RequestBody ModifyAllowedPrincipals request) {
        log.debug("REST request to modifyAllowedPrincipals  : {}", request);
        if (!complianceConfigRepository.existsById(request.getComplianceConfigId())) {
            throw new BadRequestException(null, "id is not exists", "id.not.exist");
        }
        ComplianceConfig complianceConfig = complianceConfigRepository.findById(request.getComplianceConfigId()).get();
        Set<Principal> principalSet = Sets.newConcurrentHashSet(principalRepository.findAllById(request.getPrincipalIds()));
        Type listType = new TypeToken<List<PrincipalModel>>() {
        }.getType();
        Set<PrincipalModel> modelSet = modelMapper.map(principalSet, listType);
        complianceConfig.setEnablePrincipals(modelSet);
        complianceConfig = complianceConfigRepository.save(complianceConfig);
        ComplianceConfigModel operatorModel = modelMapper.map(complianceConfig, ComplianceConfigModel.class);
        return ResponseEntity.ok().body(operatorModel);
    }

    @ApiOperation(value = "修改需要隐藏的资源", notes = "修改需要隐藏的资源")
    @PutMapping(value = "/hiddenResources")
    public ResponseEntity<ComplianceConfigModel> modifyDisableResources(@Valid @RequestBody ModifyDisableResourcesRequest request) {
        log.debug("REST request to modifyDisableResources  : {}", request);
        if (!complianceConfigRepository.existsById(request.getComplianceConfigId())) {
            throw new BadRequestException(null, "id is not exists", "id.not.exist");
        }
        ComplianceConfig complianceConfig = complianceConfigRepository.findById(request.getComplianceConfigId()).get();
        Set<Resource> resourceSet = Sets.newConcurrentHashSet(resourceRepository.findAllById(request.getResourceIds()));
        Type listType = new TypeToken<List<ResourceModel>>() {
        }.getType();
        Set<ResourceModel> modelSet = modelMapper.map(resourceSet, listType);
        complianceConfig.setDisableResources(modelSet);
        complianceConfig = complianceConfigRepository.save(complianceConfig);
        ComplianceConfigModel operatorModel = modelMapper.map(complianceConfig, ComplianceConfigModel.class);
        return ResponseEntity.ok().body(operatorModel);
    }


    @ApiOperation(value = "启用/禁用 配置", notes = "启用/禁用 配置 切换")
    @PutMapping(value = "/changeState/{id}")
    public ResponseEntity<ComplianceConfigModel> changeState(@PathVariable String id) {
        log.debug("REST request to changeState  : {}", id);
        if (!complianceConfigRepository.existsById(id)) {
            throw new BadRequestException(null, "id is not exists", "id.not.exist");
        }
        ComplianceConfig complianceConfig = complianceConfigRepository.findById(id).get();
        switch (complianceConfig.getState()) {
            case ENABLED:
                complianceConfig.setState(ComplianceState.DISABLED);
                break;
            case DISABLED:
                complianceConfig.setState(ComplianceState.ENABLED);
                break;
            default:
                break;
        }
        complianceConfig = complianceConfigRepository.save(complianceConfig);
        ComplianceConfigModel operatorModel = modelMapper.map(complianceConfig, ComplianceConfigModel.class);
        return ResponseEntity.ok().body(operatorModel);
    }

    @ApiOperation(value = "删除", notes = "删除")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deletePrincipal(@PathVariable String id) {
        log.debug("REST request to delete configFlow : {}", id);
        if (!complianceConfigRepository.existsById(id)) {
            throw new BadRequestException(null, "id is not exists", "id.not.exist");
        }
        complianceConfigRepository.deleteById(id);
        return ResponseEntity.ok().body(null);
    }
}
