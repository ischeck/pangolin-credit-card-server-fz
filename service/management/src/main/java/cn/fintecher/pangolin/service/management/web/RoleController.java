package cn.fintecher.pangolin.service.management.web;

import cn.fintecher.pangolin.common.enums.ManagementType;
import cn.fintecher.pangolin.common.enums.OperatorState;
import cn.fintecher.pangolin.common.enums.RoleState;
import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.RoleModel;
import cn.fintecher.pangolin.common.utils.BeanUtils;
import cn.fintecher.pangolin.entity.managentment.Operator;
import cn.fintecher.pangolin.entity.managentment.QOperator;
import cn.fintecher.pangolin.entity.managentment.Role;
import cn.fintecher.pangolin.service.management.model.request.CreateRoleRequest;
import cn.fintecher.pangolin.service.management.model.request.ModifyRoleRequest;
import cn.fintecher.pangolin.service.management.model.request.RoleSearchRequest;
import cn.fintecher.pangolin.service.management.repository.OperatorRepository;
import cn.fintecher.pangolin.service.management.repository.RoleRepository;
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

import javax.validation.Valid;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by ChenChang on 2018/6/8.
 */
@RestController
@RequestMapping("/api/role")
@Api(value = "角色相关", description = "角色相关")
public class RoleController {
    private final Logger log = LoggerFactory.getLogger(RoleController.class);
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private OperatorRepository operatorRepository;

    @ApiOperation(value = "创建角色", notes = "创建角色")
    @PostMapping(value = "/createRole")
    public ResponseEntity<RoleModel> createRole(@Valid @RequestBody CreateRoleRequest createRoleRequest) throws URISyntaxException {
        log.debug("REST request to create Role : {}", createRoleRequest);

        Role role = new Role();
        BeanUtils.copyProperties(createRoleRequest, role);
        role.setCreateTime(new Date());
        Role result = roleRepository.save(role);
        RoleModel roleModel = modelMapper.map(result, RoleModel.class);
        return ResponseEntity.created(new URI("/api/roles/" + result.getId()))
                .body(roleModel);
    }

    @ApiOperation(value = "修改角色", notes = "修改角色")
    @PostMapping(value = "/modify")
    public ResponseEntity<RoleModel> modify(@Valid @RequestBody ModifyRoleRequest modifyRoleRequest) {
        log.debug("REST request to modify Role : {}", modifyRoleRequest);
        Role role = roleRepository.findById(modifyRoleRequest.getId()).get();
        BeanUtils.copyProperties(modifyRoleRequest, role);
        if(role.getState().equals(RoleState.DISABLED)){
            validOperatorByRoleId(role.getId());
        }
        Role result = roleRepository.save(role);
        RoleModel roleModel = modelMapper.map(result, RoleModel.class);
        return ResponseEntity.ok().body(roleModel);
    }

    @ApiOperation(value = "获取所有角色", notes = "获取所有角色")
    @GetMapping("/findAll")
    public ResponseEntity<List<RoleModel>> getAbility() {
        log.debug("findAll Role");
        Type listType = new TypeToken<List<RoleModel>>() {
        }.getType();
        List<RoleModel> roleResponses = modelMapper.map(roleRepository.findAll(), listType);
        return Optional.ofNullable(roleResponses)
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "根据roleId获取该角色对应的资源", notes = "根据roleId获取该角色对应的资源")
    @GetMapping("/findByRoleId/{roleId}")
    public ResponseEntity<Set<Integer>> getResourceByRoleId(@PathVariable String roleId) {
        log.debug("find resource by Role Id");
        Optional<Role> byId = roleRepository.findById(roleId);
        byId.orElseThrow(() -> new BadRequestException(null, "role", "role.not.exist"));
        Type listType = new TypeToken<Set<Integer>>(){}.getType();
        Set<Integer> resource = new LinkedHashSet<>();
        if(Objects.nonNull(byId.get().getResources())){
            resource  = modelMapper.map(byId.get().getResources(), listType);

        }
        return ResponseEntity.ok().body(resource);
    }

    @ApiOperation(value = "角色查询", notes = "角色查询")
    @GetMapping("/query")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<RoleModel>> query(Pageable pageable, RoleSearchRequest request) {
        log.debug("REST request to query Operator");

        Page<RoleModel> allList = roleRepository.findAll(request.generateQueryBuilder(), pageable).map(role -> {
            RoleModel response = modelMapper.map(role, RoleModel.class);
            return response;
        });
        return new ResponseEntity<>(allList, HttpStatus.OK);
    }

    @ApiOperation(value = "删除角色", notes = "删除角色")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteResource(@PathVariable String id) {
        log.debug("REST request to delete role : {}", id);
        if (!roleRepository.existsById(id)) {
            throw new BadRequestException(null, "id", "role.not.exist");
        }
        validOperatorByRoleId(id);
        roleRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /**
     *
     * 验证角色是否存在用户
     * @param roleId
     */
    private void validOperatorByRoleId(String roleId){
        QOperator qOperator = QOperator.operator;
        Iterable<Operator> all = operatorRepository.findAll(qOperator.role.contains(roleId).and(qOperator.state.eq(OperatorState.ENABLED)));
        if(all.iterator().hasNext()){
            throw new BadRequestException(null, "role", "role.has.user");
        }
    }
}
