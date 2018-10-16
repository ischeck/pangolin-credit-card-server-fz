package cn.fintecher.pangolin.service.management.web;

import cn.fintecher.pangolin.common.enums.ManagementType;
import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.managentment.QTemplate;
import cn.fintecher.pangolin.entity.managentment.Template;
import cn.fintecher.pangolin.service.management.model.request.CreateTemplateRequest;
import cn.fintecher.pangolin.service.management.model.request.ModifyTemplateRequest;
import cn.fintecher.pangolin.service.management.model.request.TemplateSearchRequest;
import cn.fintecher.pangolin.service.management.model.response.TemplateResponse;
import cn.fintecher.pangolin.service.management.repository.TemplateRepository;
import cn.fintecher.pangolin.service.management.service.OperatorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;

@RestController
@RequestMapping("/api/template")
@Api(value = "模板配置", description = "模板配置")
public class TemplateController {

    Logger log = LoggerFactory.getLogger(TemplateController.class);

    @Autowired
    TemplateRepository templateRepository;

    @Autowired
    OperatorService operatorService;

    @Autowired
    ModelMapper modelMapper;
    @GetMapping("/getAllTemplates")
    @ApiOperation(notes = "模板查询",value = "模板查询")
    public ResponseEntity<Page<TemplateResponse>> getAllTempletes(TemplateSearchRequest request,
                                                          Pageable pageable){
        Page<Template> page = templateRepository.findAll(request.generateQueryBuilder(),pageable);
        Page<TemplateResponse> response = modelMapper.map(page, new TypeToken<Page<TemplateResponse>>() {
        }.getType());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/createTemplate")
    @ApiOperation(notes = "新增模板", value = "新增模板")
    public ResponseEntity<Void> createTemplete(@Valid @RequestBody CreateTemplateRequest request,
                                               @RequestHeader(value = "X-UserToken") String token){
        OperatorModel operator = operatorService.getSessionByToken(token);
        if (Objects.isNull(operator)) {
            throw new BadRequestException(null, "login", "operator.not.login");
        }
        Boolean exist = templateRepository.exists(QTemplate.template.principalId.eq(request.getPrincipalId())
                .and(QTemplate.template.templateName.eq(request.getTemplateName())
                .and(QTemplate.template.type.eq(request.getType()))));
        if(exist){
            throw new BadRequestException(null, "login", "templete.name.repeat");
        }
        Template templete = new Template();
        BeanUtils.copyProperties(request, templete);
        templete.setOperator(operator.getId());
        templete.setOperatorName(operator.getFullName());
        templete.setOperatorTime(ZWDateUtil.getNowDateTime());
        Boolean existDefault = templateRepository.exists(QTemplate.template.principalId.eq(request.getPrincipalId())
                        .and(QTemplate.template.type.eq(request.getType())));
        if(!existDefault){
            templete.setIsDefault(ManagementType.YES);
        }else if(Objects.equals(request.getIsDefault(),ManagementType.YES)){
            Template oldTemp = templateRepository.findOne(QTemplate.template.principalId.eq(request.getPrincipalId())
                    .and(QTemplate.template.type.eq(request.getType()))
            .and(QTemplate.template.isDefault.eq(ManagementType.YES))).get();
            oldTemp.setIsDefault(ManagementType.NO);
            templateRepository.save(oldTemp);
        }
        templateRepository.save(templete);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/modifyTemplate")
    @ApiOperation(notes = "修改模板", value = "修改模板")
    public ResponseEntity<Void> modifyTemplete(@Valid @RequestBody ModifyTemplateRequest request,
                                               @RequestHeader(value = "X-UserToken") String token){
        OperatorModel operator = operatorService.getSessionByToken(token);
        if (Objects.isNull(operator)) {
            throw new BadRequestException(null, "login", "operator.not.login");
        }
        Boolean exist = templateRepository.exists(QTemplate.template.principalId.eq(request.getPrincipalId())
                .and(QTemplate.template.templateName.eq(request.getTemplateName()))
                .and(QTemplate.template.type.eq(request.getType()))
                .and(QTemplate.template.id.ne(request.getId())));
        if(exist){
            throw new BadRequestException(null, "login", "templete.name.repeat");
        }
        Template oldTemp = templateRepository.findOne(QTemplate.template.id.eq(request.getId())).get();
        if(Objects.equals(oldTemp.getIsDefault(), ManagementType.YES)
            && Objects.equals(request.getIsDefault(), ManagementType.NO)){
            throw new BadRequestException(null, "login", "please.set.default.templete");
        }else if(Objects.equals(request.getIsDefault(),ManagementType.YES)
            && Objects.equals(oldTemp.getIsDefault(), ManagementType.NO)){
            Template oldDefTemp = templateRepository.findOne(QTemplate.template.principalId.eq(request.getPrincipalId())
                    .and(QTemplate.template.type.eq(request.getType()))
                    .and(QTemplate.template.isDefault.eq(ManagementType.YES))).get();
            oldDefTemp.setIsDefault(ManagementType.NO);
            templateRepository.save(oldDefTemp);
        }
        Template templete = new Template();
        BeanUtils.copyProperties(request, templete);
        templete.setOperator(operator.getId());
        templete.setOperatorName(operator.getFullName());
        templete.setOperatorTime(ZWDateUtil.getNowDateTime());
        templateRepository.save(templete);
        return ResponseEntity.ok().body(null);
    }

    @DeleteMapping("/deleteTemplate")
    @ApiOperation(notes = "删除模板", value = "删除模板")
    public ResponseEntity<Void> deleteTemplete(@RequestParam String id){
        Template templete = templateRepository.findById(id).get();
        if(Objects.equals(templete.getIsDefault(), ManagementType.YES)){
            throw new BadRequestException(null, "login", "please.set.default.templete");
        }
        templateRepository.deleteById(id);
        return ResponseEntity.ok().body(null);
    }
}
