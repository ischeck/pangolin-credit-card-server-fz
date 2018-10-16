package cn.fintecher.pangolin.service.management.web;

import cn.fintecher.pangolin.common.enums.OrganizationType;
import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.model.OrganizationModel;
import cn.fintecher.pangolin.common.utils.BeanUtils;
import cn.fintecher.pangolin.common.utils.ShortUUID;
import cn.fintecher.pangolin.common.utils.ZWStringUtils;
import cn.fintecher.pangolin.entity.managentment.Operator;
import cn.fintecher.pangolin.entity.managentment.Organization;
import cn.fintecher.pangolin.entity.managentment.QOperator;
import cn.fintecher.pangolin.entity.managentment.QOrganization;
import cn.fintecher.pangolin.service.management.model.request.CreateOrganizationRequest;
import cn.fintecher.pangolin.service.management.model.request.ModifyOrganizationRequest;
import cn.fintecher.pangolin.service.management.model.request.OperatorSearchRequest;
import cn.fintecher.pangolin.service.management.model.request.UserSearchRequest;
import cn.fintecher.pangolin.service.management.model.response.OrganizationBranchResponse;
import cn.fintecher.pangolin.service.management.model.response.UserResponse;
import cn.fintecher.pangolin.service.management.repository.OperatorRepository;
import cn.fintecher.pangolin.service.management.repository.OrganizationRepository;
import cn.fintecher.pangolin.service.management.repository.UserRegisterRepository;
import cn.fintecher.pangolin.service.management.service.OrganizationService;
import cn.fintecher.pangolin.service.management.validator.OrganizationCreateRequestValidator;
import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.aspectj.weaver.ast.Or;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by ChenChang on 2018/6/8.
 */
@RestController
@RequestMapping("/api/organization")
@Api(value = "组织机构相关", description = "组织机构相关")
public class OrganizationController {
    private final Logger log = LoggerFactory.getLogger(OrganizationController.class);
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private OrganizationCreateRequestValidator organizationCreateRequestValidator;
    @Autowired
    private OperatorRepository operatorRepository;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private UserRegisterRepository userRegisterRepository;

    @ApiOperation(value = "创建组织机构", notes = "创建组织机构")
    @PostMapping(value = "/createOrganization")
    public ResponseEntity<OrganizationModel> createOrganization(@Valid @RequestBody CreateOrganizationRequest createOrganizationRequest) throws URISyntaxException {
        log.debug("REST request to create Organization : {}", createOrganizationRequest);

        Organization organization = new Organization();
        BeanUtils.copyProperties(createOrganizationRequest, organization);
        //机构级别+1
        organization.setLevel(organization.getLevel()+1);
        organization.setCreateTime(new Date());
        if(ZWStringUtils.isNotEmpty(organization.getParent())) {
            Organization parent = organizationRepository.findById(organization.getParent()).get();
            organization.setClockConfigId(parent.getClockConfigId());
            organization.setDepartmentCode(parent.getDepartmentCode()+"_"+ ShortUUID.generateShortUuid());
        }
        Organization result = organizationRepository.save(organization);
        OrganizationModel organizationModel = modelMapper.map(result, OrganizationModel.class);
        return ResponseEntity.created(new URI("/api/organizations/" + result.getId()))
                .body(organizationModel);
    }

    @ApiOperation(value = "修改组织机构", notes = "修改组织机构")
    @PostMapping(value = "/modify")
    public ResponseEntity<OrganizationModel> modify(@Valid @RequestBody ModifyOrganizationRequest modifyOrganizationRequest) {
        log.debug("REST request to modify Organization : {}", modifyOrganizationRequest);
        Organization organization = organizationRepository.findById(modifyOrganizationRequest.getId()).get();
        BeanUtils.copyProperties(modifyOrganizationRequest, organization);
        Organization result = organizationRepository.save(organization);
        OrganizationModel organizationModel = modelMapper.map(result, OrganizationModel.class);
        return ResponseEntity.ok().body(organizationModel);
    }

    @ApiOperation(value = "获取所有组织机构", notes = "获取所有组织机构")
    @GetMapping("/findAll")
    public ResponseEntity<List<OrganizationModel>> getAbility() {
        log.debug("findAll Organization");
        Type listType = new TypeToken<List<OrganizationModel>>() {
        }.getType();
        List<OrganizationModel> organizationResponses = modelMapper.map(organizationRepository.findAll(), listType);
        return Optional.ofNullable(organizationResponses)
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "获取组织机构", notes = "获取组织机构")
    @GetMapping("/{id}")
    public ResponseEntity<Organization> get(@PathVariable String id) {
        log.debug("REST request to delete organization : {}", id);
        Optional<Organization> o = organizationRepository.findById(id);
        return ResponseEntity.ok().body(o.get());
    }

    @ApiOperation(value = "获取组织机构", notes = "获取组织机构")
    @GetMapping("/getByIds/{ids}")
    public ResponseEntity<List<Organization>> getByIds(@PathVariable("ids")  Set<String> ids) {
        log.debug("REST request to delete organization : {}", ids);
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(QOrganization.organization.id.in(ids));
        Iterable<Organization> response=  organizationRepository.findAll(booleanBuilder);
        return ResponseEntity.ok().body(Lists.newArrayList(response.iterator()));
    }

    @ApiOperation(value = "删除组织机构", notes = "删除组织机构")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteResource(@PathVariable String id) {
        log.debug("REST request to delete organization : {}", id);
        QOrganization qOrganization = QOrganization.organization;
        if (!organizationRepository.existsById(id)) {
            throw new BadRequestException(null, "id", "org.not.exist");
        }
        if (organizationRepository.exists(qOrganization.parent.eq(id))) {
            throw new BadRequestException(null, "id", "org.has.children");
        }
        if (operatorRepository.exists(QOperator.operator.organization.eq(id))) {
            throw new BadRequestException(null, "id", "org.has.operator");

        }
        organizationRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }


    @InitBinder("createOrganizationRequest")
    public void setupCreateOrganizationRequestBinder(WebDataBinder binder) {
        binder.addValidators(organizationCreateRequestValidator);
    }

    @ApiOperation(value = "获取所有组织的父/子机构", notes = "获取所有组织的父/子机构")
    @GetMapping("/findParentOrSon")
    public ResponseEntity<List<OrganizationModel>> findParentOrSon(@RequestParam String orgId) {
        log.debug("find Organization by orgId {}", orgId);
        Type listType = new TypeToken<List<OrganizationModel>>() {
        }.getType();
        List<Organization> all = organizationRepository.findAll();
        List<OrganizationModel> organizationResponses = modelMapper.map(organizationService.getOrganization(organizationService.getOrganizationID(orgId,all)), listType);
        return Optional.ofNullable(organizationResponses)
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "获取所有组织的父机构ID", notes = "获取所有组织的父机构ID")
    @GetMapping("/findParentId")
    public ResponseEntity<Set<String>> findParentId(@RequestParam String orgId) {
        log.debug("find Organization by orgId {}", orgId);
        Type setType = new TypeToken<Set<String>>() {
        }.getType();
        List<String> parentOrSonId = new ArrayList<>();
        parentOrSonId.add(orgId);
        List<Organization> all = organizationRepository.findAll();
        Set<String> organizationResponses = new HashSet<>();
        organizationResponses = modelMapper.map(organizationService.getParentId(all, organizationResponses, parentOrSonId), setType);
        return Optional.ofNullable(organizationResponses)
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "根据组织机构id获取对应用户", notes = "根据组织机构id获取对应用户")
    @GetMapping(value = "/queryByOrganizationId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<OperatorModel>> queryByOrganizationId(Pageable pageable,
                                                                     OperatorSearchRequest request) {
        log.debug("REST request to Operator : {}", request);
        List<Organization> all = organizationRepository.findAll();
        Map<String,Organization> organizationMap=all.stream().collect(Collectors.toMap(Organization::getId,organization ->organization));
        Page<OperatorModel> allList = operatorRepository.findAll(request.generateQueryBuilder().
                and(QOperator.operator.organization.in(organizationService.getOrganizationID(request.getOrganizationId(), all))), pageable).map(operator -> {
            OperatorModel response = modelMapper.map(operator, OperatorModel.class);
            response.setDetaptName(organizationMap.get(response.getOrganization()).getName());
            return response;
        });
        return new ResponseEntity<>(allList, HttpStatus.OK);
    }

    @ApiOperation(value = "根据组织机构id获取对应员工", notes = "根据组织机构id获取对应员工")
    @GetMapping(value = "/queryUserByOrganizationId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<UserResponse>> queryUserByOrganizationId(Pageable pageable,
                                                                        UserSearchRequest request) {
        log.debug("REST request to Operator : {}", request);
        List<Organization> all = organizationRepository.findAll();
        Page<UserResponse> allList = userRegisterRepository.findAll(request.generateQueryBuilder().and(QOperator.operator.organization.in(organizationService.getOrganizationID(request.getOrganizationId(), all))), pageable).map(user -> {
            UserResponse response = modelMapper.map(user, UserResponse.class);
            return response;
        });
        return new ResponseEntity<>(allList, HttpStatus.OK);
    }

    @ApiOperation(value = "获取用户对应分公司下的所有机构", notes = "获取用户对应分公司下的所有机构")
    @GetMapping("/findOperatorOrganization")
    public ResponseEntity<Set<String>> findOperatorOrganization(@RequestParam String orgId) {
        log.debug("find Organization by operator organization {}", orgId);
        Set<String> parentOrg = organizationService.getParentOrg(orgId);
        return ResponseEntity.ok().body(parentOrg);
    }

    @ApiOperation(value = "获取所有的分公司", notes = "获取所有的分公司")
    @GetMapping("/findAllBranch")
    public ResponseEntity<List<OrganizationBranchResponse>> findAllBranch() {
        log.debug("find all Organization");
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(QOrganization.organization.type.eq(OrganizationType.BRANCH));
        Iterable<Organization> all = organizationRepository.findAll(booleanBuilder);
        List<OrganizationBranchResponse> list = new ArrayList<>();
        if (all.iterator().hasNext()) {
            List<Organization> organizations = IteratorUtils.toList(all.iterator());
            Type listType = new TypeToken<List<OrganizationBranchResponse>>() {
            }.getType();
            list = modelMapper.map(organizations, listType);
        }
        return ResponseEntity.ok().body(list);
    }

    @ApiOperation(value = "获取机构级别小于指定值的机构ID", notes = "获取机构级别小于指定值的机构ID")
    @GetMapping("/findAllBranch/{id}")
    public ResponseEntity<List<Organization>> findOrgIdsByLevelLess(@PathVariable String id){
        log.debug("findOrgIdsByLevelLess,level {}",id);
        Organization organization=organizationRepository.findById(id).get();
        if(Objects.nonNull(organization)){
            BooleanBuilder booleanBuilder = new BooleanBuilder();
            booleanBuilder.and(QOrganization.organization.level.lt(organization.getLevel()));
            Iterable<Organization> all =organizationRepository.findAll(booleanBuilder);
            return ResponseEntity.ok().body(Lists.newArrayList(all));
        }
       return  ResponseEntity.ok().body(null);
    }

    @GetMapping("/getUserNumByOrgId/{orgId}")
    @ApiOperation(value = "获取某组织下人员数量", notes = "获取某组织下人员数量")
    public ResponseEntity<Integer> getUserNumByOrgId(@PathVariable String orgId){
        List<Organization> all = organizationRepository.findAll();
        List<Operator> allList = IterableUtils.toList(operatorRepository.findAll(QOperator.operator.organization.in(organizationService.getOrganizationID(orgId, all))));
        return ResponseEntity.ok().body(allList.size());
    }

}
