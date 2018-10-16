package cn.fintecher.pangolin.service.dataimp.service;

import cn.fintecher.pangolin.common.enums.AssistFlag;
import cn.fintecher.pangolin.common.enums.AssistStatus;
import cn.fintecher.pangolin.entity.domain.AssistCollectionCase;
import cn.fintecher.pangolin.service.dataimp.model.response.AppHomePageResponse;
import cn.fintecher.pangolin.service.dataimp.repository.CaseFollowupRecordRepository;
import cn.fintecher.pangolin.service.dataimp.repository.ImpAssistCaseRepository;
import org.apache.commons.collections4.IterableUtils;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;


@Service("appHomeService")
public class AppHomeService {

    @Autowired
    CaseFollowupRecordRepository caseFollowupRecordRepository;
    @Autowired
    ImpAssistCaseRepository impAssistCaseRepository;
    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;


    public AppHomePageResponse getHomePage(String operatorId){
        AppHomePageResponse response = new AppHomePageResponse();
        response.setWaitAssistNum(getWaitAssistNum(operatorId));
        response.setHasAssistNum(getHasAssistNum(operatorId));
        response.setWaitVisitNum(getWaitVisitNum(operatorId));
        response.setHasVisitNum(getHasVisitNum(operatorId));
        response.setAssistNum(response.getWaitAssistNum()+response.getHasAssistNum());
        response.setVisitNum(response.getWaitVisitNum()+response.getHasVisitNum());
        response.setWaitCollAmt(getWaitCollAmt(operatorId));
        return response;
    }

    private Integer getWaitAssistNum(String operatorId){
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        qb.must(matchPhraseQuery("assistStatus", AssistStatus.ASSIST_COLLECTING.name()));
        qb.must(matchPhraseQuery("assistFlag", AssistFlag.OFFSITE_OUT_ASSIST.name()));
        qb.must(rangeQuery("collectionRecordCount").gt(0));
        qb.must(matchPhraseQuery("currentCollector.id",operatorId));
        return IterableUtils.size(impAssistCaseRepository.search(qb));
    }

    private Integer getHasAssistNum(String operatorId){
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        qb.must(matchPhraseQuery("assistStatus", AssistStatus.ASSIST_COLLECTING.name()));
        qb.must(matchPhraseQuery("assistFlag", AssistFlag.OFFSITE_OUT_ASSIST.name()));
        qb.must(rangeQuery("collectionRecordCount").lte(0));
        qb.must(matchPhraseQuery("currentCollector.id",operatorId));
        return IterableUtils.size(impAssistCaseRepository.search(qb));
    }

    private Integer getWaitVisitNum(String operatorId){
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        qb.must(matchPhraseQuery("assistStatus", AssistStatus.ASSIST_COLLECTING.name()));
        qb.must(matchPhraseQuery("assistFlag", AssistFlag.LOCAL_OUT_ASSIST.name()));
        qb.must(rangeQuery("collectionRecordCount").lte(0));
        qb.must(matchPhraseQuery("currentCollector.id",operatorId));
        return IterableUtils.size(impAssistCaseRepository.search(qb));
    }

    private Integer getHasVisitNum(String operatorId){
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        qb.must(matchPhraseQuery("assistStatus", AssistStatus.ASSIST_COLLECTING.name()));
        qb.must(matchPhraseQuery("assistFlag", AssistFlag.LOCAL_OUT_ASSIST.name()));
        qb.must(rangeQuery("collectionRecordCount").gt(0));
        qb.must(matchPhraseQuery("currentCollector.id",operatorId));
        return IterableUtils.size(impAssistCaseRepository.search(qb));
    }

    private Double getWaitCollAmt(String operatorId){
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        qb.must(matchPhraseQuery("assistStatus", AssistStatus.ASSIST_COLLECTING.name()));
        qb.must(rangeQuery("collectionRecordCount").gt(0));
        qb.must(matchPhraseQuery("currentCollector.id",operatorId));
        Set<String> caseIds = new HashSet<>();
        List<AssistCollectionCase> assistCollectionCaseList = IterableUtils.toList(impAssistCaseRepository.search(qb));
        assistCollectionCaseList.forEach(assistCollectionCase -> {
            caseIds.add(assistCollectionCase.getCaseId());
        });
        BoolQueryBuilder qb1 = new BoolQueryBuilder();
        qb1.must(termsQuery("id.keyword",caseIds));
        SumAggregationBuilder sumBuilder = AggregationBuilders.sum("sum").field("leftAmt");
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices("base_case")
                .withTypes("base_case")
                .withSearchType(SearchType.DEFAULT)
                .withQuery(qb1)
                .addAggregation(sumBuilder).build();
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, response -> response.getAggregations());
        InternalSum sum = aggregations.get("sum");
        return sum.getValue();
    }
}
