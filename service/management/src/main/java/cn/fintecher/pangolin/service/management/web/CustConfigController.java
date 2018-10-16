package cn.fintecher.pangolin.service.management.web;

import cn.fintecher.pangolin.common.enums.ConfigState;
import cn.fintecher.pangolin.common.enums.CustConfigType;
import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.CaseFollowRecordMatchModel;
import cn.fintecher.pangolin.common.model.response.CaseFollowRecordMatchResponse;
import cn.fintecher.pangolin.common.utils.ZWStringUtils;
import cn.fintecher.pangolin.entity.managentment.*;
import cn.fintecher.pangolin.service.management.model.CaseFollowMatchModel;
import cn.fintecher.pangolin.service.management.model.request.*;
import cn.fintecher.pangolin.service.management.model.response.CollConfigModel;
import cn.fintecher.pangolin.service.management.model.response.ContactResultResponse;
import cn.fintecher.pangolin.service.management.model.response.CustConfigResponse;
import cn.fintecher.pangolin.service.management.repository.ColorRepository;
import cn.fintecher.pangolin.service.management.repository.ContactResultRepository;
import cn.fintecher.pangolin.service.management.repository.CustConfigRepository;
import cn.fintecher.pangolin.service.management.service.CustConfigService;
import cn.fintecher.pangolin.service.management.service.OperatorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by BBG on 2018/8/1.
 */

@RestController
@RequestMapping("/api/custConfig")
@Api(value = "自定义配置", description = "自定义配置")
public class CustConfigController {

    private final Logger log = LoggerFactory.getLogger(CustConfigController.class);

    @Autowired
    OperatorService operatorService;

    @Autowired
    CustConfigRepository custConfigRepository;

    @Autowired
    ContactResultRepository contactResultRepository;
    @Autowired
    ColorRepository colorRepository;
    @Autowired
    CustConfigService custConfigService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/getAllConfig")
    @ApiOperation(value = "获取所有自定义配置", notes = "获取所有自定义配置")
    public ResponseEntity<Page<CustConfigResponse>> getAllConfig(Pageable pageable,
                                                                 CustConfigSearchRequest request) {
        Sort sort = new Sort("principalId", "custConfigType", "sort");
        Pageable pageable1 = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
        Page<CustConfig> page = custConfigRepository.findAll(request.generateQueryBuilder(), pageable1);
        Page<CustConfigResponse> response = modelMapper.map(page, new TypeToken<Page<CustConfigResponse>>() {
        }.getType());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/saveConfig")
    @ApiOperation(value = "保存自定义配置", notes = "保存自定义配置")
    public ResponseEntity<Void> saveConfig(@RequestBody CreateCustConfigRequest request) {
        CustConfig custConfig = new CustConfig();
        BeanUtils.copyProperties(request, custConfig);
        if (ZWStringUtils.isEmpty(request.getId())) {
            custConfig.setId(null);
        }
        List<CustConfig> custConfigs = IterableUtils.toList(custConfigRepository.findAll(QCustConfig.custConfig.custConfigType.eq(request.getCustConfigType())
                .and(QCustConfig.custConfig.principalId.eq(request.getPrincipalId()))));
        for (CustConfig custConfig1 : custConfigs) {
            if (!Objects.equals(request.getId(), custConfig1.getId()) && Objects.equals(request.getSort(), custConfig1.getSort())) {
                throw new BadRequestException(null, "", "config.sort.repeat");
            }
        }
        custConfigRepository.save(custConfig);
        return ResponseEntity.ok().body(null);
    }

    @DeleteMapping("/deleteConfig")
    @ApiOperation(value = "删除自定义配置", notes = "删除自定义配置")
    public ResponseEntity<Void> deleteConfig(@RequestParam String id) {
        custConfigRepository.deleteById(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/getAllContactResults")
    @ApiOperation(value = "获取所有催收结果", notes = "获取所有催收结果")
    public ResponseEntity<Page<ContactResultResponse>> getAllContactResults(Pageable pageable,
                                                                            ContactResultSearchRequest request) {
        Sort sort = new Sort("principalId", "sort");
        Pageable pageable1 = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
        Page<ContactResult> page = contactResultRepository.findAll(request.generateQueryBuilder(), pageable1);
        Page<ContactResultResponse> response = modelMapper.map(page, new TypeToken<Page<ContactResultResponse>>() {
        }.getType());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/saveContactResult")
    @ApiOperation(value = "新建催收结果", notes = "新建催收结果")
    public ResponseEntity<Void> saveContactResult(@RequestBody CreateContactResultRequest request) {

        boolean exists = contactResultRepository.exists(QContactResult.contactResult.name.eq(request.getName())
                .and(QContactResult.contactResult.principalId.eq(request.getPrincipalId())));
        if (exists) {
            throw new BadRequestException(null, "contactResult", "contactResult.name.is.exist");
        }
        ContactResult contactResult = new ContactResult();
        ContactResult pContactResult;
        BeanUtils.copyProperties(request, contactResult);
        if (Objects.nonNull(request.getPid())) {
            pContactResult = contactResultRepository.findById(request.getPid()).get();
            contactResult.setLevel(pContactResult.getLevel() + 1);
            contactResult.setConfigState(ConfigState.ENABLED);
        }
        contactResultRepository.save(contactResult);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/updateContactResult")
    @ApiOperation(value = "修改催收结果", notes = "修改催收结果")
    public ResponseEntity<Void> updateContactResult(@RequestBody ModifyContactResultRequest request) {

        boolean exists = contactResultRepository.exists(QContactResult.contactResult.name.eq(request.getName())
                .and(QContactResult.contactResult.id.ne(request.getId())).and(QContactResult.contactResult.principalId.eq(request.getPrincipalId())));
        if (exists) {
            throw new BadRequestException(null, "contactResult", "contactResult.name.is.exist");
        }
        ContactResult contactResult = new ContactResult();
        BeanUtils.copyProperties(request, contactResult);
        contactResult.setConfigState(ConfigState.ENABLED);
        contactResultRepository.save(contactResult);
        return ResponseEntity.ok().body(null);
    }

    @DeleteMapping("/deleteContactResult")
    @ApiOperation(value = "删除催收结果", notes = "删除催收结果")
    public ResponseEntity<Void> deleteContactResult(@RequestParam String id) {
        List<ContactResult> contactResultList = contactResultRepository.findByPid(id);
        if (!contactResultList.isEmpty()) {
            throw new BadRequestException(null, null, "remove.children");
        }
        contactResultRepository.deleteById(id);
        return ResponseEntity.ok().body(null);
    }


    @GetMapping("/getConfigByPrin")
    @ApiOperation(value = "查询催记配置", notes = "查询催记配置")
    public ResponseEntity<CollConfigModel> getConfigByPrin(@RequestParam String principalId) {
        CollConfigModel model = new CollConfigModel();
//        model.setContactResults(IterableUtils.toList(contactResultRepository.findAll(QContactResult.contactResult.principalId.eq(principalId)
//                .and(QContactResult.contactResult.level.gt(2)))));
//        model.setPhoneState(IterableUtils.toList(contactResultRepository.findAll(QContactResult.contactResult.principalId.eq(principalId)
//                .and(QContactResult.contactResult.level.gt(2)))));
//        model.setPhoneType(IterableUtils.toList(contactResultRepository.findAll(QContactResult.contactResult.principalId.eq(principalId)
//                .and(QContactResult.contactResult.level.gt(2)))));
//        model.setRelations(IterableUtils.toList(contactResultRepository.findAll(QContactResult.contactResult.principalId.eq(principalId)
//                .and(QContactResult.contactResult.level.gt(2)))));
        Sort sort = new Sort("sort");
        model.setCollStates(IterableUtils.toList(custConfigRepository.findAll(QCustConfig.custConfig.principalId.eq(principalId)
                .and(QCustConfig.custConfig.custConfigType.eq(CustConfigType.MANUAL)), sort)));
        return ResponseEntity.ok().body(model);
    }

    @GetMapping("/getAllColors")
    @ApiOperation(value = "查询所有颜色", notes = "查询所有颜色")
    public ResponseEntity<List<Color>> getAllColors() {
        List<Color> colors = colorRepository.findAll();
        return ResponseEntity.ok().body(colors);
    }


    @GetMapping("/getCaseStateByPrin")
    @ApiOperation(value = "查询指定委托方的案件状态", notes = "查询指定委托方的案件状态")
    public ResponseEntity<List<CustConfig>> getCaseStateByPrin(@RequestParam(required = false) String principalId) {
        Sort sort = new Sort("sort");
        List<CustConfig> custConfigs = null;
        if(ZWStringUtils.isNotEmpty(principalId)) {
            custConfigs = IterableUtils.toList(custConfigRepository.findAll(QCustConfig.custConfig.principalId.eq(principalId)
                    .and(QCustConfig.custConfig.custConfigType.eq(CustConfigType.MANUAL)), sort));
        }else{
            custConfigs = IterableUtils.toList(custConfigRepository.findAll(QCustConfig.custConfig.custConfigType.eq(CustConfigType.MANUAL),sort));
        }
        return ResponseEntity.ok().body(custConfigs);
    }

    @GetMapping("/getContactResultsTree")
    @ApiOperation(value = "获取结果树", notes = "获取结果树")
    public ResponseEntity<Page<ContactResultResponse>> getContactResultsTree(ContactResultSearchRequest request) {
        Sort sort = new Sort("principalId", "sort");
        List<ContactResult> contactResultList = IterableUtils.toList(contactResultRepository.findAll(request.generateQueryBuilder()));
        Page page = new PageImpl<>(contactResultList);
        Page<ContactResultResponse> response = modelMapper.map(page, new TypeToken<Page<ContactResultResponse>>() {
        }.getType());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/getFollowMarchRecord")
    @ApiOperation(value = "获取跟进记录字段匹配项", notes = "获取跟进记录字段匹配项")
    public ResponseEntity<List<CaseFollowRecordMatchModel>> getFollowMarchRecord() {
        List<Class<?>> objClassList = new ArrayList<>();
        objClassList.add(CaseFollowMatchModel.class);
        List<CaseFollowRecordMatchModel> matchModels = custConfigService.getFollowRecordModel(objClassList);
        return ResponseEntity.ok().body(matchModels);
    }

    @GetMapping("/getFollowRecordFields")
    @ApiOperation(value = "获取跟进记录字段匹配字段", notes = "获取跟进记录字段匹配字段")
    public ResponseEntity<List<CaseFollowRecordMatchResponse>> getFollowRecordFields(@RequestParam String principalId,
                                                                                     @RequestParam(required = false) String type) {
        List<CaseFollowRecordMatchResponse> responses = new ArrayList<>();
        Iterable<ContactResult> all = contactResultRepository.findAll(QContactResult.contactResult.principalId.eq(principalId)
                .and(QContactResult.contactResult.configState.eq(ConfigState.ENABLED)));
        responses = custConfigService.getFollowRecordFields(all,principalId,type);
        responses.sort(Comparator.comparing(CaseFollowRecordMatchResponse::getId));
        return ResponseEntity.ok().body(responses);
    }

    @GetMapping("/generalFollowRecordFields")
    @ApiOperation(value = "自动生成催记字段", notes = "自动生成催记字段")
    public ResponseEntity generalFollowRecordFields(@RequestParam String principalId) {
        List<ContactResult> listNew = new ArrayList<>();
        Iterable<ContactResult> all = contactResultRepository.findAll(QContactResult.contactResult.level.eq(2)
                .and(QContactResult.contactResult.principalId.eq(principalId)));
        if (all.iterator().hasNext()) {
            List<ContactResult> list = IteratorUtils.toList(all.iterator());
            list.forEach(contactResult -> {
                ContactResult contactResult1 = new ContactResult();
                BeanUtils.copyProperties(contactResult, contactResult1);
                contactResult1.setId(null);
                contactResult1.setConfigState(ConfigState.DISABLED);
                contactResult1.setPrincipalName("");
                contactResult1.setPrincipalId("");
                listNew.add(contactResult1);
            });
            contactResultRepository.saveAll(listNew);
        }
        return ResponseEntity.ok().body(listNew);
    }

}

