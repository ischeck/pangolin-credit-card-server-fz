package cn.fintecher.pangolin.service.management.web;


import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.common.web.BaseController;
import cn.fintecher.pangolin.entity.managentment.ImportExcelConfig;
import cn.fintecher.pangolin.service.management.model.request.*;
import cn.fintecher.pangolin.service.management.model.response.ImportExcelConfigParseResponse;
import cn.fintecher.pangolin.service.management.model.response.ModifyImportExcelConfigResponse;
import cn.fintecher.pangolin.service.management.model.response.TemplateDataSelect;
import cn.fintecher.pangolin.service.management.repository.ImportExcelConfigRepository;
import cn.fintecher.pangolin.service.management.service.ImportExcelConfigService;
import cn.fintecher.pangolin.service.management.model.response.ImportExcelConfigResponse;
import cn.fintecher.pangolin.service.management.service.OperatorService;
import cn.fintecher.pangolin.service.management.validator.ImportExcelConfigRequestValidator;
import cn.fintecher.pangolin.service.management.validator.ModifyImportExcelConfigRequestValidator;
import com.google.common.collect.Lists;
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
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @Author huyanmin
 * @Date 2018/07/03
 * @Dessciption 模板配置
 */
@RestController
@RequestMapping("/api/templateDataController")
@Api(value = "模板配置", description = "模板配置")
public class ImportExcelConfigController extends BaseController {

    private final Logger log = LoggerFactory.getLogger(ImportExcelConfigController.class);
    @Autowired
    ImportExcelConfigRequestValidator importExcelConfigRequestValidator;

    @Autowired
    ModifyImportExcelConfigRequestValidator modifyImportExcelConfigRequestValidator;

    @Autowired
    ImportExcelConfigService importExcelConfigService;

    @Autowired
    OperatorService operatorService;

    @Autowired
    ImportExcelConfigRepository importExcelConfigRepository;

    @ApiOperation(value = "解析Excel数据模板头信息", notes = "解析Excel数据模板头信息")
    @PostMapping(value = "/parserTemplateHeader")
    public ResponseEntity<List<ImportExcelConfigParseResponse>> parserTemplateHeader(@Valid @RequestBody ImportExcelConfigParseRequest importExcelConfigParseRequest) {
        try {
            log.debug("REST request to create templateData : {}", importExcelConfigParseRequest);
            List<ImportExcelConfigParseResponse> responses = importExcelConfigService.parserTemplateHeader(importExcelConfigParseRequest);
            return ResponseEntity.ok().body(responses);
        } catch (Exception e) {
            throw new BadRequestException(null, "parserTemplateHeader", Objects.isNull(e.getMessage()) ? "parserTemplateHeader.is.error" : e.getMessage());
        }
    }

    @ApiOperation(value = "Excel数据导入模板", notes = "Excel数据导入模板")
    @PostMapping(value = "/createImportExcelConfig")
    public ResponseEntity createImportExcelConfig(@Valid @RequestBody ImportExcelConfigRequest importExcelConfigRequest,
                                                  @RequestHeader(value = "X-UserToken") String token) {
        try {
            log.debug("REST request to create templateData : {}", importExcelConfigRequest);
            OperatorModel operatorModel = operatorService.getSessionByToken(token);
            importExcelConfigService.createImportExcelConfig(importExcelConfigRequest, operatorModel);
        } catch (Exception e) {
            throw new BadRequestException(null, "createImportExcelConfig", Objects.isNull(e.getMessage()) ? "createImportExcelConfig.is.error" : e.getMessage());
        }
        return ResponseEntity.ok().body(null);
    }

    @ApiOperation(value = "模板查询", notes = "模板查询")
    @GetMapping("/templateDataQuery")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<ImportExcelConfigResponse>> templateDataQuery(@ApiIgnore Pageable pageable,
                                                                             ImportExcelConfigSearchRequest request) {
        log.debug("REST request to query template");
        ModelMapper modelMapper = new ModelMapper();
        Page<ImportExcelConfigResponse> dataList = importExcelConfigRepository.findAll(request.generateQueryBuilder(), pageable).map(
                importExcelConfig -> {
                    ImportExcelConfigResponse response = modelMapper.map(importExcelConfig, ImportExcelConfigResponse.class);
                    return response;
                });
        return new ResponseEntity<>(dataList, HttpStatus.OK);
    }

    @ApiOperation(value = "获取模板选项", notes = "获取模板选项")
    @GetMapping("/getTemplateDataSelect")
    public ResponseEntity<List<TemplateDataSelect>> getTemplateDataSelect(ImportExcelConfigSelectRequest importExcelConfigSelectRequest) {
        log.debug("getTemplateDataSelect,{}", importExcelConfigSelectRequest.generateQueryBuilder());
        try {
            ModelMapper modelMapper = new ModelMapper();
            Iterable<ImportExcelConfig> importExcelConfigs = importExcelConfigRepository.findAll(importExcelConfigSelectRequest.generateQueryBuilder());
            List<ImportExcelConfig> importExcelConfigList = Lists.newArrayList(importExcelConfigs.iterator());
            List<TemplateDataSelect> responseList = modelMapper.map(importExcelConfigList, new TypeToken<List<TemplateDataSelect>>() {
            }.getType());
            return ResponseEntity.ok().body(responseList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BadRequestException(null, "getTemplateDataSelect", "getTemplateDataSelect.is.fail");
        }

    }

    @ApiOperation(value = "获取模板详情", notes = "获取模板详情")
    @GetMapping("/getTemplateData/{id}")
    public ResponseEntity<ImportExcelConfig> getTemplateData(@PathVariable String id) {
        log.debug("REST request to get templateData : {}", id);
        try {
            ImportExcelConfig importExcelConfig = importExcelConfigRepository.findById(id).get();
            return new ResponseEntity<>(importExcelConfig, HttpStatus.OK);
        } catch (Exception e) {
            throw new BadRequestException(null, "getTemplateData", "getTemplateDataById.is.fail");
        }

    }


    @ApiOperation(value = "修改模板配置", notes = "修改模板配置")
    @PostMapping(value = "/importExcelConfigModify")
    public ResponseEntity importExcelConfigModify(@Valid @RequestBody ModifyImportExcelConfigRequest modifyTemplateDataInfoRequest,
                                                  @RequestHeader(value = "X-UserToken") String token) {
        log.debug("REST request to modify templateDataModify : {}", modifyTemplateDataInfoRequest);
        ImportExcelConfig importExcelConfig = importExcelConfigRepository.findById(modifyTemplateDataInfoRequest.getId()).get();
        if (Objects.isNull(importExcelConfig)) {
            throw new BadRequestException(null, "importExcelConfigModify", "importExcelConfigModify.is.null");
        }
        try {
            OperatorModel operatorModel = operatorService.getSessionByToken(token);
            ModelMapper modelMapper = new ModelMapper();
            modelMapper.map(modifyTemplateDataInfoRequest, importExcelConfig);
            importExcelConfig.setOperator(operatorModel.getFullName());
            importExcelConfig.setCreateTime(ZWDateUtil.getNowDateTime());
            importExcelConfigRepository.save(importExcelConfig);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BadRequestException(null, "importExcelConfigModify", "importExcelConfigModify.is.fail");
        }
        return ResponseEntity.ok().body(null);
    }


    @ApiOperation(value = "删除模板", notes = "删除模板")
    @DeleteMapping("/deleteTemplateData/{id}")
    public ResponseEntity deleteTemplateData(@PathVariable String id) {
        log.debug("REST request to deleteTemplateData : {}", id);
        //删除模板
        importExcelConfigRepository.deleteById(id);
        return ResponseEntity.ok().body(null);
    }


    @InitBinder("importExcelConfigRequest")
    public void setupCreateOperatorRequestBinder(WebDataBinder binder) {
        // binder.addValidators(importExcelConfigRequestValidator);
    }

    @InitBinder("modifyImportExcelConfigRequest")
    public void setupModifyOperatorRequestBinder(WebDataBinder binder) {
        // binder.addValidators(modifyImportExcelConfigRequestValidator);
    }

}
