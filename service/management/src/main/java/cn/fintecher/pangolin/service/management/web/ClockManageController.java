package cn.fintecher.pangolin.service.management.web;


import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.common.utils.ZWStringUtils;
import cn.fintecher.pangolin.entity.managentment.*;
import cn.fintecher.pangolin.service.management.model.request.ClockRecordSearchRequest;
import cn.fintecher.pangolin.service.management.model.request.CreateClockConfigRequest;
import cn.fintecher.pangolin.service.management.model.request.CreateClockRecordRequest;
import cn.fintecher.pangolin.service.management.model.request.UpdateClockConfigRequest;
import cn.fintecher.pangolin.service.management.model.response.ClockConfigResponse;
import cn.fintecher.pangolin.service.management.model.response.ClockRecordResponse;
import cn.fintecher.pangolin.service.management.repository.ClockConfigRepository;
import cn.fintecher.pangolin.service.management.repository.ClockRecordRepository;
import cn.fintecher.pangolin.service.management.repository.OrganizationRepository;
import cn.fintecher.pangolin.service.management.service.ClockService;
import cn.fintecher.pangolin.service.management.service.OperatorService;
import cn.fintecher.pangolin.service.management.service.OrganizationService;
import com.querydsl.core.BooleanBuilder;
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

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/clockManage")
@Api(value = "打卡管理", description = "打卡管理")
public class ClockManageController {

    Logger log = LoggerFactory.getLogger(ClockManageController.class);

    @Autowired
    OperatorService operatorService;
    @Autowired
    ClockConfigRepository clockConfigRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    OrganizationRepository organizationRepository;
    @Autowired
    ClockService clockService;
    @Autowired
    ClockRecordRepository clockRecordRepository;
    @Autowired
    OrganizationService organizationService;

    @GetMapping("/getClockConfig")
    @ApiOperation(notes = "查询打卡设置", value = "查询打卡设置")
    public ResponseEntity<Page<ClockConfigResponse>> getClockConfig(@RequestHeader(value = "X-UserToken") String token,
                                                                    Pageable pageable) {
        OperatorModel operator = operatorService.getSessionByToken(token);
        Organization organization = organizationRepository.findById(operator.getOrganization()).get();
        Page<ClockConfig> clockConfigs = null;
        if(organization.getLevel() == 0){
             clockConfigs = clockConfigRepository.findAll(pageable);
        }else{
             clockConfigs = clockConfigRepository.findAll(QClockConfig.clockConfig.organization.eq(operator.getOrganization()), pageable);
        }
        Type listMap = new TypeToken<Page<ClockConfigResponse>>() {
        }.getType();
        Page<ClockConfigResponse> responses = modelMapper.map(clockConfigs, listMap);
        return ResponseEntity.ok().body(responses);
    }

    @PostMapping("/createClockConfig")
    @ApiOperation(notes = "新建打卡设置", value = "新建打卡设置")
    public ResponseEntity<Void> createClockConfig(@RequestBody CreateClockConfigRequest request) {

        Boolean exist = clockConfigRepository.exists((QClockConfig.clockConfig.organization.eq(request.getOrganization())));
        if (exist) {
            throw new BadRequestException(null, "clockConfig", "clockConfig.has.exist");
        }
        ClockConfig clockConfig = new ClockConfig();
        BeanUtils.copyProperties(request, clockConfig);
        clockConfig.getClockConfigDetails().forEach(clockConfigDetail -> {
            if(ZWStringUtils.isNotEmpty(clockConfigDetail.getSignTime())) {
                clockConfigDetail.setSignTime(clockConfigDetail.getSignTime() + ":00");
            }
        });
        ClockConfig result = clockConfigRepository.save(clockConfig);
        List<Organization> organizationList = new ArrayList<>();
        clockService.addClockConfig(organizationList, request.getOrganization(), result.getId());
        organizationRepository.saveAll(organizationList);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/updateClockConfig")
    @ApiOperation(notes = "更新打卡设置", value = "更新打卡设置")
    public ResponseEntity<Void> updateClockConfig(@RequestBody UpdateClockConfigRequest request) {
        ClockConfig clockConfig = new ClockConfig();
        BeanUtils.copyProperties(request, clockConfig);
        clockConfigRepository.save(clockConfig);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/getAllClockRecords")
    @ApiOperation(notes = "查询打卡记录", value = "查询打卡记录")
    public ResponseEntity<Page<ClockRecordResponse>> getAllClockRecords(@RequestHeader(value = "X-UserToken") String token,
                                                                        Pageable pageable,
                                                                        @RequestBody ClockRecordSearchRequest request) {
        OperatorModel operator = operatorService.getSessionByToken(token);
        if(Objects.isNull(operator)){
            throw new BadRequestException(null,"","operator.not.login");
        }
        BooleanBuilder qb = request.generateQueryBuilder();
        qb.and(QClockRecord.clockRecord.organizations.contains(operator.getOrganization()));
        Page<ClockRecordResponse> page = clockRecordRepository.findAll(qb, pageable).map(
                clockRecord -> {
                    ClockRecordResponse recordResponse = modelMapper.map(clockRecord, ClockRecordResponse.class);
                    return recordResponse;
                }
        );
        return ResponseEntity.ok().body(page);
    }

    @GetMapping("/getOwnClockRecord")
    @ApiOperation(notes = "查询个人当日打卡记录", value = "查询个人当日打卡记录")
    public ResponseEntity<ClockRecordResponse> getOwnClockRecord(@RequestHeader(value = "X-UserToken") String token) {
        OperatorModel operator = operatorService.getSessionByToken(token);
        if(Objects.isNull(operator)){
            throw new BadRequestException(null,"","operator.not.login");
        }
        ClockRecordResponse recordResponse = new ClockRecordResponse();
        ClockRecord result = null;
        Optional<ClockRecord> recordOptional = clockRecordRepository.findOne(QClockRecord.clockRecord.operator.eq(operator.getId())
                .and(QClockRecord.clockRecord.date.eq(ZWDateUtil.getDate())));
        if (!recordOptional.isPresent()) {
            ClockRecord clockRecord = clockService.createRecord(operator.getOrganization(), ZWDateUtil.getDate(), operator.getId(), operator.getFullName());
            result = clockRecordRepository.save(clockRecord);
        } else {
            result = recordOptional.get();
        }
        BeanUtils.copyProperties(result, recordResponse);
        clockService.getClockType(result,recordResponse);
        return ResponseEntity.ok().body(recordResponse);
    }

    @PostMapping("/clock")
    @ApiOperation(notes = "打卡", value = "打卡")
    public ResponseEntity<Void> clock(@RequestHeader(value = "X-UserToken") String token,
                                      @RequestBody CreateClockRecordRequest request) {
        OperatorModel operator = operatorService.getSessionByToken(token);
        clockService.clock(request, operator);
        return ResponseEntity.ok().body(null);
    }

}
