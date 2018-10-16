package cn.fintecher.pangolin.service.domain.service;


import cn.fintecher.pangolin.common.enums.*;
import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.entity.domain.*;
import cn.fintecher.pangolin.service.domain.model.request.AssistCaseSearchRequest;
import cn.fintecher.pangolin.service.domain.model.request.RetractAssistCaseRequest;
import cn.fintecher.pangolin.service.domain.model.response.AssistTelCaseSearchResponse;
import cn.fintecher.pangolin.service.domain.respository.*;
import org.apache.commons.collections4.IteratorUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * Created by huyanmin on 2018/08/08.
 */
@Service("assistCollectionCaseService")
public class AssistCollectionCaseService {

    final Logger log = LoggerFactory.getLogger(AssistCollectionCaseService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private DomainBaseService domainBaseService;

    @Autowired
    private AssistCaseRepository assistCaseRepository;

    @Autowired
    private BaseCaseRepository baseCaseRepository;

    @Autowired
    private AssistCaseApplyRepository assistCaseApplyRepository;

    @Autowired
    private PersonalContactRepository personalContactRepository;

    @Autowired
    private PersonalAddressRepository personalAddressRepository;

    @Autowired
    private CaseApplyService applyService;

    /**
     * 获取协催案件
     * @param request
     * @param token
     * @return
     */
    public List<AssistTelCaseSearchResponse> searchAssistCase(AssistCaseSearchRequest request, String token){

        BoolQueryBuilder qb = request.generateQueryBuilder();
        OperatorModel operator = domainBaseService.getOperator(token);
        if(operator.getIsManager().equals(ManagementType.YES)){
            qb.must(termQuery("departments", operator.getOrganization()));
        }else {
            qb.must(matchPhraseQuery("currentCollector.id", operator.getId()));
        }
        Iterable<AssistCollectionCase> search = assistCaseRepository.search(qb);
        List<String> caseList = new ArrayList<>();
        List<AssistTelCaseSearchResponse> listContent = new ArrayList<>();
        List<AssistTelCaseSearchResponse> returnList = new ArrayList<>();
        //协催案件中未记录案件相关的金额，故此处循环获取案件ID
        if(search.iterator().hasNext()){
            List<AssistCollectionCase> list = IteratorUtils.toList(search.iterator());
            list.forEach(assistCase->{
                AssistTelCaseSearchResponse response = new AssistTelCaseSearchResponse();
                BeanUtils.copyProperties(assistCase, response);
                response.setAssistCollector(Objects.nonNull(assistCase.getCurrentCollector())?assistCase.getCurrentCollector().getFullName():null);
                response.setAssistOutType(assistCase.getAssistFlag());
                response.setAssistId(assistCase.getId());
                listContent.add(response);
                caseList.add(assistCase.getCaseId());
            });
        }
        //获取案件金额
        if(caseList.size()>0){
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            boolQuery.must(matchQuery("id", caseList));
            Iterable<BaseCase> baseCases = baseCaseRepository.search(boolQuery);
            if(baseCases.iterator().hasNext()){
                List<BaseCase> baseCases1 = IteratorUtils.toList(baseCases.iterator());
                baseCases1.forEach(baseCase -> {
                    listContent.forEach(response->{
                        if(response.getCaseId().equals(baseCase.getId())){
                            BeanUtils.copyProperties(baseCase, response);
                            response.setPersonalId(Objects.nonNull(baseCase.getPersonal())?baseCase.getPersonal().getId():null);
                            response.setCertificateNo(Objects.nonNull(baseCase.getPersonal())?baseCase.getPersonal().getCertificateNo():null);
                            response.setPersonalName(Objects.nonNull(baseCase.getPersonal())?baseCase.getPersonal().getPersonalName():null);
                            response.setPrincipalId(Objects.nonNull(baseCase.getPrincipal())?baseCase.getPrincipal().getId():null);
                            response.setPrincipalName(Objects.nonNull(baseCase.getPrincipal())?baseCase.getPrincipal().getPrincipalName():null);
                            response.setCurrentCollector(Objects.nonNull(baseCase.getCurrentCollector())?baseCase.getCurrentCollector().getFullName():null);
                            returnList.add(response);
                        }
                    });
                });
            }
        }
        return returnList;
    }

    /***
     * 撤回协催案件
     * @param request
     */
    public void retractAssistCase(RetractAssistCaseRequest request){

        log.info("撤回协催案件开始: "+request);
        //撤回地址案件
        if(Objects.nonNull(request.getPersonalAddressId())){
            applyService.setPersonalPerAddressState(request.getPersonalContactId(), request.getPersonalAddressId(),AssistFlag.NO_ASSIST);
        }
        //撤回电话案件
        if(Objects.nonNull(request.getCaseId())){
            Optional<BaseCase> byId = baseCaseRepository.findById(request.getCaseId());
            byId.orElseThrow(() -> new BadRequestException(null, "collectionCase", "collectionCase.is.not.exist"));
            BaseCase baseCase = byId.get();
            baseCase.setAssistFlag(AssistFlag.NO_ASSIST);
            baseCaseRepository.save(baseCase);
        }
        //撤回协催案件
        domainBaseService.retractAssistCollectionCase(request, MessageType.ASSIST_CALL_BACK,null);
        //撤回协催申请
        domainBaseService.retractAssistCaseApply(request, MessageType.ASSIST_CALL_BACK,null);
    }

}