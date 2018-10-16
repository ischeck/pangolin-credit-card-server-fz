package cn.fintecher.pangolin.service.domain.web;

import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.utils.BeanUtils;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.domain.*;
import cn.fintecher.pangolin.service.domain.model.MarkPhoneStatusModel;
import cn.fintecher.pangolin.service.domain.model.PersonalAddressSearchModel;
import cn.fintecher.pangolin.service.domain.model.PersonalContactSearchModel;
import cn.fintecher.pangolin.service.domain.model.request.*;
import cn.fintecher.pangolin.service.domain.respository.BaseCaseRepository;
import cn.fintecher.pangolin.service.domain.respository.PersonalAddressRepository;
import cn.fintecher.pangolin.service.domain.respository.PersonalContactRepository;
import cn.fintecher.pangolin.service.domain.respository.PersonalRepository;
import cn.fintecher.pangolin.service.domain.service.CollectionCaseService;
import cn.fintecher.pangolin.service.domain.service.DomainBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.IteratorUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.spanOrQuery;

/**
 * Created by ChenChang on 2017/8/4.
 */
@RestController
@RequestMapping("/api/personal")
@Api(value = "个人信息", description = "个人信息")
public class PersonController {
    private final Logger log = LoggerFactory.getLogger(PersonController.class);

    @Autowired
    private PersonalContactRepository personalContactRepository;
    @Autowired
    private DomainBaseService domainBaseService;
    @Autowired
    private BaseCaseRepository baseCaseRepository;
    @Autowired
    private PersonalAddressRepository personalAddressRepository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private PersonalRepository personalRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CollectionCaseService collectionCaseService;

    @GetMapping("/search")
    @ApiOperation(value = "查询客户本人", notes = "查询客户本人")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<Personal>> search(Pageable pageable, PersonalSearchRequest request) {

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(request.generateQueryBuilder()).build();
        log.debug("search Activity : {} query :{}", request.toString(), searchQuery.getQuery().toString());
        Page<Personal> searchPageResults = personalRepository.search(searchQuery);
        return new ResponseEntity<>(searchPageResults, HttpStatus.OK);
    }

    @GetMapping("/searchPersonalContact")
    @ApiOperation(value = "联系人电话查询", notes = "联系人电话查询")
    public ResponseEntity<Set<PersonalContactSearchModel>> searchPersonalContact(String personalId) throws Exception {

        BoolQueryBuilder qb = QueryBuilders
                .boolQuery();
        Set<PersonalContactSearchModel> set = new HashSet<>();
        Iterable<PersonalContact> personalContacts = personalContactRepository.search(qb.must(matchPhraseQuery("personalId", personalId)));
        if (personalContacts.iterator().hasNext()) {
            List<PersonalContact> personalContactsList = IteratorUtils.toList(personalContacts.iterator());
            for (PersonalContact personalContact: personalContactsList){
                Set<PersonalPerCall> personalPerCalls = personalContact.getPersonalPerCalls();
                if(personalPerCalls.size()>0){
                   personalPerCalls.forEach(personalPerCall -> {
                     if(Objects.nonNull(personalPerCall.getPhoneNo())){
                         PersonalContactSearchModel personalPerCellModel = new PersonalContactSearchModel();
                         BeanUtils.copyProperties(personalPerCall, personalPerCellModel);
                         BeanUtils.copyProperties(personalContact, personalPerCellModel);
                         personalPerCellModel.setPersonalPerId(personalPerCall.getId());
                         set.add(personalPerCellModel);
                     }
                   });
                }else {
                    PersonalContactSearchModel personalPerCellModel = new PersonalContactSearchModel();
                    BeanUtils.copyProperties(personalContact, personalPerCellModel);
                    set.add(personalPerCellModel);
                }
            }
        }
        return new ResponseEntity<>(set, HttpStatus.OK);
    }

    @GetMapping("/searchPersonalAddress")
    @ApiOperation(value = "联系人地址查询", notes = "联系人地址查询")
    public ResponseEntity<Set<PersonalAddressSearchModel>> searchPersonalAddress(String personalId) throws Exception {

        BoolQueryBuilder qb = QueryBuilders
                .boolQuery();
        Set<PersonalAddressSearchModel> set = new HashSet<>();
        Iterable<PersonalContact> personalContacts = personalContactRepository.search(qb.must(matchPhraseQuery("personalId", personalId)));
        if (personalContacts.iterator().hasNext()) {
            List<PersonalContact> personalContacts1 = IteratorUtils.toList(personalContacts.iterator());
            for (PersonalContact personalContact: personalContacts1){
                Set<PersonalPerAddr> personalPerAddrs = personalContact.getPersonalPerAddrs();
                if(personalPerAddrs.size()>0){
                    personalPerAddrs.forEach(personalPerAddr -> {
                        if(Objects.nonNull(personalPerAddr.getAddressDetail())){
                            PersonalAddressSearchModel personalAddressSearchModel = new PersonalAddressSearchModel();
                            BeanUtils.copyProperties(personalPerAddr, personalAddressSearchModel);
                            personalAddressSearchModel.setPersonalAddressId(personalPerAddr.getId());
                            BeanUtils.copyProperties(personalContact, personalAddressSearchModel);
                            set.add(personalAddressSearchModel);
                        }
                    });
                }else {
                    PersonalAddressSearchModel personalAddressSearchModel = new PersonalAddressSearchModel();
                    BeanUtils.copyProperties(personalContact, personalAddressSearchModel);
                    set.add(personalAddressSearchModel);
                }
            }
        }
        return new ResponseEntity<>(set, HttpStatus.OK);
    }

    @PostMapping("/inertPersonalContact")
    @ApiOperation(value = "创建联系人", notes = "创建联系人")
    public ResponseEntity inertPersonalContact(@RequestBody CreatePersonalContactRequest personalContactCreate,
                                               @RequestHeader(value = "X-UserToken") String token) throws Exception {
        log.debug("Create personal contact {}", personalContactCreate);
        OperatorModel operator = domainBaseService.getOperator(token);
        PersonalContact personalContact = new PersonalContact();
        BeanUtils.copyProperties(personalContactCreate, personalContact);
        Set<PersonalPerCall> personalPerCalls = new HashSet<>();
        PersonalPerCall personalPerCall = collectionCaseService.setPersonalPerCell(personalContactCreate);
        personalPerCalls.add(personalPerCall);
        personalContact.setPersonalPerCalls(personalPerCalls);
        personalContact.setSort(0);
        personalContact.setOperator(operator.getId());
        personalContact.setOperatorTime(ZWDateUtil.getNowDateTime());
        personalContactRepository.save(personalContact);
        return ResponseEntity.ok().body(personalContact);
    }


    @PostMapping("/inertPersonalAddress")
    @ApiOperation(value = "创建联系人地址", notes = "创建联系人地址")
    public ResponseEntity inertPersonalAddress(@RequestBody CreatePersonalAddressRequest personalAddressCreate,
                                               @RequestHeader(value = "X-UserToken") String token) throws BadRequestException {
        log.debug("Create personal address {}", personalAddressCreate);
        OperatorModel operator = domainBaseService.getOperator(token);
        PersonalContact personalContact = new PersonalContact();
        BeanUtils.copyProperties(personalAddressCreate, personalContact);
        Set<PersonalPerAddr> personalPerAddrs = new HashSet<>();
        PersonalPerAddr personalPerAddr = collectionCaseService.setPersonalPerAddress(personalAddressCreate);
        personalPerAddrs.add(personalPerAddr);
        personalContact.setPersonalPerAddrs(personalPerAddrs);
        personalContact.setOperator(operator.getId());
        personalContact.setOperatorTime(ZWDateUtil.getNowDateTime());
        personalContactRepository.save(personalContact);
        return ResponseEntity.ok().body(personalContact);
    }

    @PostMapping("/modifyPersonalContact")
    @ApiOperation(value = "修改联系人电话", notes = "修改联系人电话")
    public ResponseEntity modifyPersonalContact(@RequestBody ModifyPersonalContactRequest request,
                                               @RequestHeader(value = "X-UserToken") String token) throws BadRequestException {
        log.info("modify personal address {}", request);
        OperatorModel operator = domainBaseService.getOperator(token);
        PersonalContact personalContact = collectionCaseService.validPersonalContact(request.getId());
        if(operator.getId().equals(personalContact.getOperator())){
            personalContact.setName(request.getName());
            personalContact.setRelation(request.getRelation());
            personalContact.setCertificateNo(request.getCertificateNo());
            Set<PersonalPerCall> personalPerCalls = personalContact.getPersonalPerCalls();
            if(personalPerCalls.size()>0){
                personalPerCalls.forEach(personalPerCall -> {
                    if(request.getPersonalPerId().equals(personalPerCall.getId())){
                        personalPerCall.setPhoneNo(request.getPhoneNo());
                        personalPerCall.setPhoneType(request.getPhoneType());
                        personalPerCall.setRemark(request.getRemark());
                    }
                });
            }
            personalContact.setOperatorTime(ZWDateUtil.getNowDateTime());
            personalContactRepository.save(personalContact);
        }else {
            throw new BadRequestException(null, "deletedPersonalContact","only.operate.your.owner.record");
        }
        return ResponseEntity.ok().body(personalContact);
    }

    @PostMapping("/modifyPersonalAddress")
    @ApiOperation(value = "修改联系人地址", notes = "修改联系人地址")
    public ResponseEntity modifyPersonalAddress(@RequestBody ModifyPersonalAddressRequest request,
                                                @RequestHeader(value = "X-UserToken") String token) throws BadRequestException {
        log.info("modify personal address {}", request);
        OperatorModel operator = domainBaseService.getOperator(token);
        PersonalContact personalContact = collectionCaseService.validPersonalContact(request.getId());
        if(operator.getId().equals(personalContact.getOperator())){
            personalContact.setName(request.getName());
            personalContact.setRelation(request.getRelation());
            personalContact.setCertificateNo(request.getCertificateNo());
            Set<PersonalPerAddr> personalPerAddrs = personalContact.getPersonalPerAddrs();
            if(personalPerAddrs.size()>0){
                personalPerAddrs.forEach(personalPerAddr -> {
                    if(request.getPersonalAddressId().equals(personalPerAddr.getId())){
                        personalPerAddr.setAddressType(request.getAddressType());
                        personalPerAddr.setAddressDetail(request.getAddressDetail());
                        personalPerAddr.setRemark(request.getRemark());
                    }
                });
            }
            personalContact.setOperatorTime(ZWDateUtil.getNowDateTime());
            personalContactRepository.save(personalContact);
        }else {
            throw new BadRequestException(null, "deletedPersonalContact","only.operate.your.owner.record");
        }
        return ResponseEntity.ok().body(personalContact);
    }

    @GetMapping("/deletePersonalContact")
    @ApiOperation(value = "删除电话记录", notes = "删除电话记录")
    public ResponseEntity deletePersonalContact(@RequestParam String id,
                                                @RequestHeader(value = "X-UserToken") String token) throws BadRequestException{
        OperatorModel operator = domainBaseService.getOperator(token);
        PersonalContact personalContact = collectionCaseService.validPersonalContact(id);
        if(operator.getId().equals(personalContact.getOperator())){
            personalContactRepository.deleteById(id);
        }else {
            throw new BadRequestException(null, "deletedPersonalContact","only.operate.your.owner.record");
        }
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/deleteAddressContact")
    @ApiOperation(value = "删除地址记录", notes = "删除地址记录")
    public ResponseEntity deleteAddressContact(@RequestParam String id,
                                                @RequestHeader(value = "X-UserToken") String token) throws BadRequestException{
        OperatorModel operator = domainBaseService.getOperator(token);
        PersonalContact personalContact = collectionCaseService.validPersonalContact(id);
        if(operator.getId().equals(personalContact.getOperator())){
            personalContactRepository.deleteById(id);
        }else {
            throw new BadRequestException(null, "deletedPersonalContact","only.operate.your.owner.record");
        }
        return ResponseEntity.ok().body(null);
    }

    /**
     * 标记电话/地址状态
     *
     * @return
     */
    @PostMapping("/markPhoneOrAddressStatus")
    @ApiOperation(value = "标记电话/地址状态", notes = "标记电话/地址状态")
    public ResponseEntity markPhoneOrAddressStatus(@RequestBody MarkPhoneStatusModel model) {
        log.debug("标记电话/地址状态"+model);
        Optional<PersonalContact> byId = personalContactRepository.findById(model.getPersonalContactId());
        byId.orElseThrow(()->new BadRequestException(null, "personalContact","personalContact.is.not.exist"));
        PersonalContact personalContact = byId.get();
        //改变电话状态
        if(Objects.nonNull(model.getPhoneNo())){
            Set<PersonalPerCall> personalPerCalls = personalContact.getPersonalPerCalls();
            if(personalPerCalls.size()>0){
                personalPerCalls.forEach(personalPerCall -> {
                    if(model.getPhoneNo().equals(personalPerCall.getPhoneNo())){
                        personalPerCall.setPhoneState(model.getPhoneState());
                    }
                });
            }
        }else {
            Set<PersonalPerAddr> personalPerAddrs = personalContact.getPersonalPerAddrs();
            if(personalPerAddrs.size()>0){
                personalPerAddrs.forEach(personalPerAddr -> {
                    if(model.getAddressDetail().equals(personalPerAddr.getAddressDetail())){
                        personalPerAddr.setAddressState(model.getAddressState());
                    }
                });
            }
        }
        personalContactRepository.save(personalContact);
        return ResponseEntity.ok().body(null);
    }
}
