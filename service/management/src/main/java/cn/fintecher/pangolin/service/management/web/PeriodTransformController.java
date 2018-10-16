package cn.fintecher.pangolin.service.management.web;

import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.utils.BeanUtils;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.managentment.PeriodTransformTemplate;
import cn.fintecher.pangolin.entity.managentment.QPeriodTransformTemplate;
import cn.fintecher.pangolin.service.management.model.request.CreatePeriodTransformRequest;
import cn.fintecher.pangolin.service.management.model.request.ModifyPeriodTransformRequest;
import cn.fintecher.pangolin.service.management.model.request.PeriodTransformSearchRequest;
import cn.fintecher.pangolin.service.management.model.response.PeriodTransformResponse;
import cn.fintecher.pangolin.service.management.repository.PeriodTransformTemplateRepository;
import cn.fintecher.pangolin.service.management.service.OperatorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/periodTransform")
@Api(value = "期数转换管理", description = "期数转换管理")
public class PeriodTransformController {

    Logger log = LoggerFactory.getLogger(PeriodTransformController.class);

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PeriodTransformTemplateRepository transformTemplateRepository;

    @Autowired
    OperatorService operatorService;

    @GetMapping("/getAllTransformTemplate")
    @ApiOperation(notes = "获取所有配置", value = "获取所有配置")
    public ResponseEntity<Page<PeriodTransformResponse>> getAllTransformTemplate(PeriodTransformSearchRequest request,
                                                                                 Pageable pageable){
        log.info("获取所有配置");
        Page<PeriodTransformResponse> page = transformTemplateRepository.findAll(request.generateQueryBuilder(),pageable)
                .map(sensitiveWord -> {
                    return modelMapper.map(sensitiveWord,PeriodTransformResponse.class);
                }
        );
        return ResponseEntity.ok().body(page);
    }

    @GetMapping("/getOneTransformTemplate")
    @ApiOperation(notes = "获取指定委托方配置", value = "获取指定委托方配置")
    public ResponseEntity<Map<String,String>> getOneTransformTemplate(@RequestParam String principalId){
        log.info("获取指定委托方配置{}",principalId);
        Map<String,String> map = new LinkedHashMap<>();
        PeriodTransformTemplate template = transformTemplateRepository.findOne(QPeriodTransformTemplate.periodTransformTemplate.principalId.eq(principalId)).get();
        if(Objects.nonNull(template)){
            map = template.getTramsformMap();
        }
        return ResponseEntity.ok().body(map);
    }

    @PostMapping("/createTransformTemplate")
    @ApiOperation(notes = "新增配置", value = "新增配置")
    public ResponseEntity<Void> createTransformTemplate(@RequestBody CreatePeriodTransformRequest request,
                                                                                 @RequestHeader(value = "X-UserToken") String token){
        log.info("新增配置{}",request);
        boolean flag = transformTemplateRepository.exists(QPeriodTransformTemplate.periodTransformTemplate.principalId.eq(request.getPrincipalId()));
        if(flag){
            throw new BadRequestException(null, "PeriodTransform", "this.principal.has.template");
        }
        OperatorModel operator = operatorService.getSessionByToken(token);
        PeriodTransformTemplate template = new PeriodTransformTemplate();
        BeanUtils.copyProperties(request, template);
        template.setOperatorTime(ZWDateUtil.getNowDateTime());
        template.setOperatorName(operator.getFullName());
        transformTemplateRepository.save(template);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/modifyTransformTemplate")
    @ApiOperation(notes = "修改配置", value = "修改配置")
    public ResponseEntity<Void> modifyTransformTemplate(@RequestBody ModifyPeriodTransformRequest request,
                                                        @RequestHeader(value = "X-UserToken") String token){
        log.info("修改配置{}",request);
        OperatorModel operator = operatorService.getSessionByToken(token);
        PeriodTransformTemplate template = transformTemplateRepository.findById(request.getId()).get();
        template.setTramsformMap(request.getTramsformMap());
        template.setOperatorTime(ZWDateUtil.getNowDateTime());
        template.setOperatorName(operator.getFullName());
        transformTemplateRepository.save(template);
        return ResponseEntity.ok().body(null);
    }

    @DeleteMapping("/deleteTemplateById")
    @ApiOperation(notes = "删除配置", value = "删除配置")
    public ResponseEntity<Void> deleteTemplateById(@RequestParam String id){
        log.info("删除配置{}",id);
        transformTemplateRepository.deleteById(id);
        return ResponseEntity.ok().body(null);
    }

}
