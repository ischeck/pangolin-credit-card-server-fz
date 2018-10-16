package cn.fintecher.pangolin.service.dataimp.service;

import cn.fintecher.pangolin.common.enums.*;
import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.response.LoginResponse;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.domain.BaseCase;
import cn.fintecher.pangolin.entity.domain.CaseTransferLog;
import cn.fintecher.pangolin.entity.domain.CollectionCaseStrategyConfig;
import cn.fintecher.pangolin.entity.managentment.Organization;
import cn.fintecher.pangolin.service.dataimp.client.OperatorClient;
import cn.fintecher.pangolin.service.dataimp.client.OrganizationClient;
import cn.fintecher.pangolin.service.dataimp.repository.CaseTransferLogRepository;
import cn.fintecher.pangolin.service.dataimp.repository.CollectionCaseStrategyConfigRepository;
import cn.fintecher.pangolin.service.dataimp.repository.ImportBaseCaseRepository;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.core.RuleBuilder;
import org.jeasy.rules.mvel.MVELRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

/**
 * 策略分配service
 * Created by ChenChang on 2018/8/8.
 */
@Service
public class CaseStrategyDistributionService {
    @Autowired
    private CollectionCaseStrategyConfigRepository collectionCaseStrategyConfigRepository;

    @Autowired
    ImportBaseCaseRepository importBaseCaseRepository;

    @Autowired
    private OrganizationClient organizationClient;

    @Autowired
    private DataimpBaseService dataimpBaseService;

    @Autowired
    private CaseTransferLogRepository caseTransferLogRepository;

    private Logger logger = LoggerFactory.getLogger(CaseStrategyDistributionService.class);

    public void test() {

        CollectionCaseStrategyConfig collectionCaseStrategyConfig = new CollectionCaseStrategyConfig();
        collectionCaseStrategyConfig.setOrganization(new Organization());
        collectionCaseStrategyConfig.getOrganization().setId("守文");
        RulesEngine rulesEngine = new DefaultRulesEngine();
        BaseCase baseCase = new BaseCase();
        baseCase.setCity("西安");
        Multimap<String, BaseCase> resultMap = ArrayListMultimap.create();
        Rules rules = new Rules();
        Rule caseStrategyDistributionRule = new MVELRule()
                .name("caseStrategyDistributionRule")
                .description("Strategy Distribution Case Rule")
                .when("baseCase.city == \"西安\"")
                .then("baseCase.detaptId = '" + collectionCaseStrategyConfig.getOrganization().getId() + "'");
        rules.register(caseStrategyDistributionRule);
        Facts facts = new Facts();
        facts.put("baseCase", baseCase);
        facts.put("collectionCaseStrategyConfig", collectionCaseStrategyConfig);
//        facts.put("resultMap", resultMap);
        StopWatch stopWatch = new StopWatch("10000次规则调用");
        stopWatch.start("脚本执行");
        for (int i = 0; i < 10000; i++) {
            rulesEngine.fire(rules, facts);
        }

        stopWatch.stop();

        Rule javaRule = new RuleBuilder()
                .name("weather rule")
                .description("if it rains then take an umbrella")
                .when(f -> ((BaseCase) f.get("baseCase")).getCity().equals("西安"))
                .then(f -> ((Multimap<Organization, BaseCase>) f.get("resultMap")).put(((CollectionCaseStrategyConfig) f.get("collectionCaseStrategyConfig")).getOrganization(), ((BaseCase) f.get("baseCase"))))
                .build();
        rules = new Rules();
        rules.register(javaRule);
        stopWatch.start("代码执行");
        for (int i = 0; i < 10000; i++) {
            rulesEngine.fire(rules, facts);
        }
        stopWatch.stop();
        System.err.println(stopWatch.prettyPrint());

    }

    /**
     * 策略分配
     * @param baseCaseList
     * @return
     */
    @Async
    public void distribution(List<BaseCase> baseCaseList, LoginResponse loginResponse) {
        logger.info("执行策略分配开始..........");
        StopWatch stopWatch=new StopWatch();
        stopWatch.start();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must().add(matchPhraseQuery("strategyState", StrategyState.ENABLED.name()));
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder).withSort(SortBuilders.fieldSort("priority").order(SortOrder.ASC)).build();
        Multimap<String, BaseCase> resultMap = ArrayListMultimap.create();
        RulesEngine rulesEngine = new DefaultRulesEngine();

        List<CollectionCaseStrategyConfig> collectionCaseStrategyConfigs = collectionCaseStrategyConfigRepository.search(searchQuery).getContent();
        //按策略优先级分组
        if(!collectionCaseStrategyConfigs.isEmpty()){
          Map<Integer,List<CollectionCaseStrategyConfig>> integerListGroupMap=collectionCaseStrategyConfigs.stream().collect(
                  Collectors.groupingBy(CollectionCaseStrategyConfig::getPriority));
          Map<Integer,List<CollectionCaseStrategyConfig>> finalMap = new LinkedHashMap<>();
          //按优先级排序
          integerListGroupMap.entrySet().stream().sorted(Map.Entry.<Integer,List<CollectionCaseStrategyConfig>>comparingByKey().reversed()).
                    forEachOrdered(e -> finalMap.put(e.getKey(), e.getValue()));
          for(Map.Entry<Integer,List<CollectionCaseStrategyConfig>> listEntry:finalMap.entrySet()){
              logger.info("执行优先级为：{}的测试",listEntry.getKey());
              List<CollectionCaseStrategyConfig> caseStrategyConfigList=listEntry.getValue();
              Rules rules = new Rules();
              Facts facts = new Facts();
              //组装规则脚本
              for (CollectionCaseStrategyConfig collectionCaseStrategyConfig : caseStrategyConfigList) {
                  Rule caseStrategyDistributionRule = new MVELRule()
                          .name(collectionCaseStrategyConfig.getId())
                          .description(collectionCaseStrategyConfig.getName())
                          .when(collectionCaseStrategyConfig.getFormula())
                          .then("baseCase.detaptId = '" + collectionCaseStrategyConfig.getOrganization().getId() + "'");
                  rules.register(caseStrategyDistributionRule);

              }
              //规则匹配
              for (BaseCase baseCase : baseCaseList) {
                  facts.put("baseCase", baseCase);
                  rulesEngine.fire(rules, facts);
                  if (Objects.nonNull(baseCase.getDetaptId())) {
                      resultMap.put(baseCase.getDetaptId(), baseCase);
                      baseCaseList.remove(baseCase);
                  }
              }
          }

        }
       long total= distributeCase(resultMap,loginResponse);
        stopWatch.stop();
        logger.info("策略分配执行完成,耗时:{}",stopWatch.getTotalTimeMillis());
        dataimpBaseService.sendMessage("策略分配完成,分配数量:".concat(String.valueOf(total)),loginResponse.getUser().getUsername(),"策略分配完成", MessageType.DISTRIBUTE_CASE, MessageMode.POPUP);
    }

    private long distributeCase( Multimap<String, BaseCase> baseCaseMultimap, LoginResponse loginResponse){
        Set<String> keySet = baseCaseMultimap.keySet();
        //获取所需的机构数据
        List<Organization> organizationList= organizationClient.getByIds(keySet).getBody();
       Map<String,Organization> organizationMap= organizationList.stream().collect(Collectors.toMap(Organization::getId,organization->organization));
        //分配数量
        long total=0;
        for (String id : keySet) {
            Organization obj =organizationMap.get(id);
            List<BaseCase> baseCaseListPre = (List<BaseCase>) baseCaseMultimap.get(id);
            List<CaseTransferLog> caseTransferLogList=new ArrayList<>();
            total=total+baseCaseListPre.size();
            Set<String> departmentIds=new HashSet<>();
                ResponseEntity<List<Organization>> responseEntity=organizationClient.findOrgIdsByLevelLess(obj.getId());
                List<Organization> all=responseEntity.getBody();
                for(Organization org:all){
                    departmentIds.add(org.getId());
                }
            departmentIds.add(loginResponse.getUser().getOrganization());
            for(BaseCase baseCase:baseCaseListPre){
                baseCase.setDetaptId(obj.getId());
                baseCase.setDetaptName(obj.getName());
                baseCase.setIssuedFlag(CaseIssuedFlag.AREA_UN_ISSUED);
                Set<String> departments=baseCase.getDepartments();
                if(Objects.isNull(departments)){
                    departments=new HashSet<>();
                }
                departments.add(obj.getId());
                if(!departmentIds.isEmpty()){
                    departments.addAll(departmentIds);
                }
                baseCase.setDepartments(departments);
                baseCase.setFollowInTime(ZWDateUtil.getNowDateTime());
                caseTransferLogList.add(dataimpBaseService.createLog(baseCase.getId(),"案件流转:".concat(baseCase.getDetaptName()),loginResponse));
            }
            //保存案件信息
            List<List<BaseCase>> parts = Lists.partition(baseCaseListPre, 1000);
            parts.stream().forEach(list -> importBaseCaseRepository.saveAll(list));
            //保存流转信息
            List<List<CaseTransferLog>>  parts2=Lists.partition(caseTransferLogList,1000);
            parts2.stream().forEach(list-> caseTransferLogRepository.saveAll(list));
        }
        return total;
    }
}
