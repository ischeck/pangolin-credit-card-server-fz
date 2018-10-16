package cn.fintecher.pangolin.service.management.web;


import cn.fintecher.pangolin.common.enums.PrincipalState;
import cn.fintecher.pangolin.common.model.PrincipalModel;
import cn.fintecher.pangolin.common.utils.BeanUtils;
import cn.fintecher.pangolin.common.web.BaseController;
import cn.fintecher.pangolin.entity.managentment.ContactResult;
import cn.fintecher.pangolin.entity.managentment.Principal;
import cn.fintecher.pangolin.entity.managentment.QContactResult;
import cn.fintecher.pangolin.entity.managentment.QPrincipal;
import cn.fintecher.pangolin.service.management.model.request.CreatePrincipalRequest;
import cn.fintecher.pangolin.service.management.model.request.ModifyPrincipalRequest;
import cn.fintecher.pangolin.service.management.model.request.PrincipalSearchRequest;
import cn.fintecher.pangolin.service.management.repository.ContactResultRepository;
import cn.fintecher.pangolin.service.management.repository.PrincipalRepository;
import cn.fintecher.pangolin.service.management.service.PrincipalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.IteratorUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * @Author huyanmin
 * @Date 2018/06/26
 * @Dessciption 委托方相关接口
 */
@RestController
@RequestMapping("/api/principal")
@Api(value = "委托方相关", description = "委托方相关")
public class PrincipalController extends BaseController {

    private final Logger log = LoggerFactory.getLogger(PrincipalController.class);

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PrincipalRepository principalRepository;
    @Autowired
    private PrincipalService principalService;
    @Autowired
    private ContactResultRepository contactResultRepository;

    @ApiOperation(value = "新增委托方", notes = "新增委托方")
    @PostMapping(value = "/createPrincipal")
    public ResponseEntity<PrincipalModel> createPrincipal(@Valid @RequestBody CreatePrincipalRequest createPrincipalRequest,
                                                          @RequestHeader(value = "X-UserToken") String token) throws URISyntaxException {

        log.debug("REST request to create principal : {}", createPrincipalRequest);
        //新增委托方
        Principal result = principalService.createPrincipal(token, createPrincipalRequest);
        //新增委托方时生成相对应的联络结果
        principalService.generalConfigResult(result);
        PrincipalModel principalModel = modelMapper.map(result, PrincipalModel.class);
        return ResponseEntity.created(new URI("/api/principal" + result.getId()))
                .body(principalModel);
    }

    @ApiOperation(value = "委托方查询", notes = "委托方查询")
    @GetMapping("/principalQuery")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<PrincipalModel>> principalQuery(@ApiIgnore Pageable pageable,
                                                               PrincipalSearchRequest request) {
        log.debug("REST request to query principal");
        Sort sort = new Sort(Sort.Direction.DESC, "operatorTime");
        pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
        Page<PrincipalModel> allList = principalRepository.findAll(request.generateQueryBuilder(), pageable).map(principal -> {
            PrincipalModel response = modelMapper.map(principal, PrincipalModel.class);
            return response;
        });
        return new ResponseEntity<>(allList, HttpStatus.OK);
    }

    @ApiOperation(value = "修改委托方", notes = "修改委托方")
    @PostMapping(value = "/principalModify")
    public ResponseEntity<PrincipalModel> principalModify(@Valid @RequestBody ModifyPrincipalRequest modifyPrincipalRequest) {
        log.debug("REST request to modify principal : {}", modifyPrincipalRequest);
        Principal principal = principalRepository.findById(modifyPrincipalRequest.getId()).get();
        BeanUtils.copyProperties(modifyPrincipalRequest, principal);
        Principal result = principalRepository.save(principal);
        PrincipalModel principalModel = modelMapper.map(result, PrincipalModel.class);
        return ResponseEntity.ok().body(principalModel);
    }

    @ApiOperation(value = "获取所有委托方", notes = "获取所有委托方")
    @GetMapping("/findAll")
    public ResponseEntity<List<PrincipalModel>> findAll() {
        log.debug("REST request to find all");
        Type listType = new TypeToken<List<PrincipalModel>>(){}.getType();
        List<PrincipalModel> list = modelMapper.map(principalRepository.findAll(QPrincipal.principal.state.eq(PrincipalState.ENABLED)), listType);
        return ResponseEntity.ok().body(list);
    }


    @ApiOperation(value = "获取委托方详情", notes = "获取委托方详情")
    @GetMapping("/getPrincipal/{id}")
    public ResponseEntity<PrincipalModel> getPrincipal(@PathVariable String id) {
        log.debug("REST request to get principal : {}", id);
        Principal principal = principalRepository.findById(id).get();
        return Optional.ofNullable(modelMapper.map(principal, PrincipalModel.class))
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "删除委托方", notes = "删除委托方")
    @DeleteMapping("/deletePrincipal/{id}")
    public ResponseEntity deletePrincipal(@PathVariable String id) {
        log.debug("REST request to delete principal : {}", id);
        //需要验证是否该委托方有关联案件
        Principal principal = principalRepository.findById(id).get();
        principal.setState(PrincipalState.DISABLED);
        principalRepository.save(principal);
        //删除委托方，需要将对应的联络结果删除
        Iterable<ContactResult> all = contactResultRepository.findAll(QContactResult.contactResult.principalId.eq(id));
        if(all.iterator().hasNext()){
            contactResultRepository.deleteAll(IteratorUtils.toList(all.iterator()));
        }
        return ResponseEntity.ok().body(null);
    }
}
