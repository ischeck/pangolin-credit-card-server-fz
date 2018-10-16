package cn.fintecher.pangolin.service.management.web;


import cn.fintecher.pangolin.common.utils.BeanUtils;
import cn.fintecher.pangolin.common.web.BaseController;
import cn.fintecher.pangolin.entity.managentment.SysParam;
import cn.fintecher.pangolin.service.management.model.request.ModifySystemParamRequest;
import cn.fintecher.pangolin.service.management.model.request.SysParamRequest;
import cn.fintecher.pangolin.service.management.repository.SystemParamRepository;
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
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

/**
 * @Author huyanmin
 * @Date 2018/06/27
 * @Dessciption 系统参数
 */
@RestController
@RequestMapping("/api/systemParam")
@Api(value = "系统参数", description = "系统参数")
public class SystemParamController extends BaseController {

    private final Logger log = LoggerFactory.getLogger(SystemParamController.class);
    @Autowired
    private SystemParamRepository systemParamRepository;
    @Autowired
    private ModelMapper modelMapper;

    @ApiOperation(value = "系统参数查询", notes = "系统参数查询")
    @GetMapping("/systemParamQuery")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<SysParam>> systemParamQuery(@ApiIgnore Pageable pageable, SysParamRequest request) {

        log.debug("REST request to query systemParam");
        Page<SysParam> allList = systemParamRepository.findAll(request.generateQueryBuilder(), pageable);
        return new ResponseEntity<>(allList, HttpStatus.OK);
    }


    @ApiOperation(value = "修改参数表", notes = "修改参数表")
    @PostMapping(value = "/systemParamModify")
    public ResponseEntity<SysParam> systemParamModify(@Valid @RequestBody ModifySystemParamRequest modifySystemParamRequest) {
        log.debug("REST request to modify systemParam : {}", modifySystemParamRequest);
        SysParam sysParam = systemParamRepository.findById(modifySystemParamRequest.getId()).get();
        BeanUtils.copyProperties(modifySystemParamRequest, sysParam);
        SysParam result = systemParamRepository.save(sysParam);
        return ResponseEntity.ok().body(result);
    }

}
