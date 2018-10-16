package cn.fintecher.pangolin.service.management.web;

import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.managentment.ExportConfig;
import cn.fintecher.pangolin.entity.managentment.ExportConfigItem;
import cn.fintecher.pangolin.entity.managentment.ImportExcelConfig;
import cn.fintecher.pangolin.service.management.model.request.*;
import cn.fintecher.pangolin.service.management.model.response.ExportConfigResponse;
import cn.fintecher.pangolin.service.management.model.response.ExportConfigSelectResponse;
import cn.fintecher.pangolin.service.management.repository.ExportConfigRepository;
import cn.fintecher.pangolin.service.management.service.ExportConfigService;
import cn.fintecher.pangolin.service.management.service.OperatorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.IterableUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by huyanmin on 2018/8/31
 */
@RestController
@RequestMapping("/api/exportConfigController")
@Api(value = "导出配置", description = "导出配置")
public class ExportConfigController {
    private final Logger log = LoggerFactory.getLogger(ExportConfigController.class);

    @Autowired
    ExportConfigService exportConfigService;
    @Autowired
    ExportConfigRepository exportConfigRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    OperatorService operatorService;

    @ApiOperation(value = "获取各个委托方主键关联字段", notes = "获取各个委托方主键关联字段")
    @GetMapping("/getPrimaryProperty")
    public ResponseEntity<List<ExportConfigItem>> getPrimaryProperty(@RequestParam String principalId) {
        List<ExportConfigItem> importKeys = exportConfigService.getImportKeys(principalId);
        return ResponseEntity.ok().body(importKeys);
    }

    @ApiOperation(value = "获取各个委托方催记字段", notes = "获取各个委托方催记字段")
    @GetMapping("/getFollowConfigItems")
    public ResponseEntity<List<ExportConfigItem>> getFollowConfigItems(@RequestParam String principalId) {
        List<ExportConfigItem> importKeys = exportConfigService.getAllConfigItems(principalId);
        return ResponseEntity.ok().body(importKeys);
    }

    @GetMapping("/getAllTemplate")
    @ApiOperation(value = "获取所有模板", notes = "获取所有模板")
    public ResponseEntity<Page<ExportConfigResponse>> getAllTemplate(ExportConfigSearchRequest request,
                                                                     Pageable pageable) {
        Page<ExportConfigResponse> page = exportConfigRepository.findAll(request.generateQueryBuilder(), pageable).map(exportConfig -> {
            ExportConfigResponse response = modelMapper.map(exportConfig, ExportConfigResponse.class);
            return response;
        });
        return ResponseEntity.ok().body(page);
    }

    @GetMapping("/getAllConfigs")
    @ApiOperation(value = "获取所有模板选项", notes = "获取所有模板选项")
    public ResponseEntity<List<ExportConfigSelectResponse>> getAllConfigs(ExportConfigSearchRequest request) {
        List<ExportConfig> exportConfigs = IterableUtils.toList(exportConfigRepository.findAll(request.generateQueryBuilder()));
        Type listMap = new TypeToken<List<ExportConfigResponse>>() {
        }.getType();
        List<ExportConfigSelectResponse> response = modelMapper.map(exportConfigs,listMap);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/getTemplate")
    @ApiOperation(value = "获取指定模板", notes = "获取指定模板")
    public ResponseEntity<ExportConfig> getTemplate(@RequestParam String id) {
        ExportConfig exportConfig  = exportConfigRepository.findById(id).get();
        return ResponseEntity.ok().body(exportConfig);
    }

    @ApiOperation(value = "新建模板", notes = "新建模板")
    @PostMapping(value = "/createExportConfig")
    public ResponseEntity createExportConfig(@Valid @RequestBody ExportConfigCreateRequest request,
                                             @RequestHeader(value = "X-UserToken") String token) {
        try {
            log.info("REST request to create templateData : {}", request);
            OperatorModel operatorModel = operatorService.getSessionByToken(token);
            ExportConfig exportConfig = new ExportConfig();
            ModelMapper modelMapper = new ModelMapper();
            modelMapper.addMappings(new PropertyMap<ImportExcelConfigRequest, ImportExcelConfig>() {
                protected void configure() {
                    skip().setId(null);
                }
            });
            modelMapper.map(request, exportConfig);
            exportConfig.setOperator(operatorModel.getId());
            exportConfig.setOperatorName(operatorModel.getFullName());
            exportConfig.setCreateTime(ZWDateUtil.getNowDateTime());
            exportConfigRepository.save(exportConfig);
            return ResponseEntity.ok().body(null);
        } catch (Exception e) {
            throw new BadRequestException(null, "createExportConfig", "createImportExcelConfig.is.error");
        }
    }

    @ApiOperation(value = "修改模板配置", notes = "修改模板配置")
    @PostMapping(value = "/exportConfigModify")
    public ResponseEntity exportConfigModify(@Valid @RequestBody ModifyExportConfigRequest request,
                                                  @RequestHeader(value = "X-UserToken") String token) {
        try {
            log.debug("REST request to modify templateDataModify : {}", request);
            ExportConfig exportConfig = exportConfigRepository.findById(request.getId()).get();
            OperatorModel operatorModel = operatorService.getSessionByToken(token);
            modelMapper.map(request, exportConfig);
            exportConfig.setOperator(operatorModel.getId());
            exportConfig.setOperatorName(operatorModel.getFullName());
            exportConfig.setCreateTime(ZWDateUtil.getNowDateTime());
            exportConfigRepository.save(exportConfig);
            return ResponseEntity.ok().body(null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BadRequestException(null, "createExportConfig", "createImportExcelConfig.is.error");
        }
    }

    @ApiOperation(value = "删除模板", notes = "删除模板")
    @DeleteMapping("/deleteExportConfig")
    public ResponseEntity deleteExportConfig(@RequestParam String id) {
        log.debug("REST request to deleteExportConfig : {}", id);
        //删除模板
        exportConfigRepository.deleteById(id);
        return ResponseEntity.ok().body(null);
    }
}
