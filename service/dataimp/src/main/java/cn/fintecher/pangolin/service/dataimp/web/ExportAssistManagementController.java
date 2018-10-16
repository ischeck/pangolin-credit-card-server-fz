package cn.fintecher.pangolin.service.dataimp.web;

import cn.fintecher.pangolin.common.enums.ExportType;
import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.utils.ZWStringUtils;
import cn.fintecher.pangolin.entity.managentment.ExportConfigItem;
import cn.fintecher.pangolin.service.dataimp.model.ExportCaseModel;
import cn.fintecher.pangolin.service.dataimp.model.request.AssistManagementImportRequest;
import cn.fintecher.pangolin.service.dataimp.model.request.ExportAssistManagementRequest;
import cn.fintecher.pangolin.service.dataimp.model.request.ExportSearchRequest;
import cn.fintecher.pangolin.service.dataimp.repository.BaseCaseRepository;
import cn.fintecher.pangolin.service.dataimp.service.BaseCaseImportExcelTempService;
import cn.fintecher.pangolin.service.dataimp.service.DataimpBaseService;
import cn.fintecher.pangolin.service.dataimp.service.ExportAssistManagementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cn.fintecher.pangolin.common.utils.InnerServiceUrl.MANAGEMENT_SERVICE_FIND_EXPORT_ITEM;

/**
 * @Author:胡艳敏
 * @Desc: 协催管理相关导出
 * @Date:Create 2018/8/31
 */
@RestController
@RequestMapping("/api/exportAssistManagement")
@Api(value = "协催管理相关导出", description = "协催管理相关导出")
public class ExportAssistManagementController {
    Logger logger = LoggerFactory.getLogger(ExportAssistManagementController.class);

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BaseCaseImportExcelTempService caseImportExcelTempService;

    @Autowired
    private ExportAssistManagementService managementService;

    @Autowired
    private BaseCaseRepository baseCaseRepository;

    @Autowired
    private DataimpBaseService dataimpBaseService;

    @Autowired
    RestTemplate restTemplate;

    @ApiOperation(value = "协催管理导出", notes = "协催管理导出")
    @PostMapping("/exportAssistManagement")
    public ResponseEntity exportAssistManagement(@RequestBody ExportAssistManagementRequest request,
                                                 @RequestHeader(value = "X-UserToken") String token) throws Exception {
        managementService.getExportTitle(request, token);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/exportFolllowRecord")
    @ApiOperation(notes = "导出在案案件催记", value = "导出在案案件催记")
    public ResponseEntity<Void> exportFolllowRecord(@RequestBody ExportSearchRequest request,
                                                    @RequestHeader(value = "X-UserToken") String token) {
        OperatorModel operator = dataimpBaseService.getUserByToken(token);
        managementService.exportInPoolCaseFollowRecord(request.getConfigId(), request, operator);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/exportHisCaseFolllowRecord")
    @ApiOperation(notes = "导出历史案件催记", value = "导出历史案件催记")
    public ResponseEntity<Void> exportHisCaseFolllowRecord(@RequestBody ExportSearchRequest request,
                                                           @RequestHeader(value = "X-UserToken") String token) {
        OperatorModel operator = dataimpBaseService.getUserByToken(token);
        managementService.exportHisCaseFollowRecord(request.getConfigId(), request, operator);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/getExportProperty")
    @ApiOperation(value = "获取导出内置字段", notes = "获取导出内置字段")
    public ResponseEntity<List<ExportConfigItem>> getExportProperty(@RequestParam(name = "exportType", required = true) ExportType exportType,
                                                                    @RequestParam(name = "principalId", required = false) String principalId) {
        List<ExportConfigItem> responses = null;
        if (Objects.equals(exportType, ExportType.CASE)) {
            List<Class<?>> objClassList = new ArrayList<>();
            objClassList.add(ExportCaseModel.class);
            responses = managementService.getObjejctPro(objClassList);
        } else if (ZWStringUtils.isNotEmpty(principalId)) {
            responses = restTemplate.getForEntity(MANAGEMENT_SERVICE_FIND_EXPORT_ITEM + principalId, List.class).getBody();
        } else {
            throw new BadRequestException(null,"ExportConfigItem","principal.is.required");
        }
        return ResponseEntity.ok().body(responses);
    }

    @PostMapping("/exportInPoolCase")
    @ApiOperation(notes = "导出在案案件", value = "导出在案案件")
    public ResponseEntity<Void> exportInPoolCase(@RequestBody ExportSearchRequest request,
                                                 @RequestHeader(value = "X-UserToken") String token) {
        OperatorModel operator = dataimpBaseService.getUserByToken(token);
        managementService.exportInPoolCase(request.getConfigId(), request, operator);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/exportHisCase")
    @ApiOperation(notes = "导出历史案件", value = "导出历史案件")
    public ResponseEntity<Void> exportHisCase(@RequestBody ExportSearchRequest request,
                                                 @RequestHeader(value = "X-UserToken") String token) {
        OperatorModel operator = dataimpBaseService.getUserByToken(token);
        managementService.exportHisCase(request.getConfigId(), request, operator);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/importExcelAssistManagementData")
    @ApiOperation(value = "协催管理导入", notes = "协催管理导入")
    public ResponseEntity importExcelAssistManagementData(@Valid @RequestBody AssistManagementImportRequest request,
                                                          @RequestHeader(value = "X-UserToken") String token) {

        //获取文件
        InputStream inputStream = caseImportExcelTempService.readFile(request.getFileId());
        managementService.importExcelAssistManagementData(request, inputStream, token);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/testUpload")
    @ApiOperation(value = "协催管理导入", notes = "协催管理导入")
    public ResponseEntity testUpload(@RequestParam String filePath) {

        //获取文件
        dataimpBaseService.getFileUrl(filePath);
        return ResponseEntity.ok().body(null);
    }

}
