package cn.fintecher.pangolin.service.domain.service;


import cn.fintecher.pangolin.service.domain.model.response.DistributeCaseResponse;
import cn.fintecher.pangolin.service.domain.respository.CaseOperatorLogRepository;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.max.InternalMax;
import org.elasticsearch.search.aggregations.metrics.max.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.min.InternalMin;
import org.elasticsearch.search.aggregations.metrics.min.MinAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * Created by huyanmin on 2018/07/23.
 */
@Service("distributeCaseService")
public class DistributeCaseService {

    final Logger log = LoggerFactory.getLogger(DistributeCaseService.class);


    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    CaseOperatorLogRepository caseOperatorLogRepository;


    /***
     * 查询全部案件
     * @return
     */
    public List<DistributeCaseResponse> searchDisCase( BoolQueryBuilder qb) {
        TermsAggregationBuilder field = AggregationBuilders.terms("count").field("batchNumber.keyword").size(50);
        SumAggregationBuilder sumBuilder = AggregationBuilders.sum("sum").field("leftAmt");
        SumAggregationBuilder sumBuilderDollor = AggregationBuilders.sum("sumDollor").field("leftAmtDollar");
        MaxAggregationBuilder field1 = AggregationBuilders.max("delegationDate").field("delegationDate");
        MinAggregationBuilder field2 = AggregationBuilders.min("endCaseDate").field("endCaseDate");
        TermsAggregationBuilder principal = AggregationBuilders.terms("principal").field("principal.principalName.keyword");
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices("base_case")
                .withTypes("base_case")
                .withSearchType(SearchType.DEFAULT)
                .withQuery(qb)
                .addAggregation(field.subAggregation(sumBuilder)
                        .subAggregation(sumBuilderDollor)
                        .subAggregation(field1)
                        .subAggregation(field2)
                        .subAggregation(principal)).build();
        log.debug("search Activity : query :{}, agg: {}", searchQuery.getQuery().toString(), searchQuery.getAggregations());
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, response -> response.getAggregations());
        List<DistributeCaseResponse> buffList = new ArrayList<>();
        Map<String, Aggregation> map = aggregations.asMap();
        StringTerms count = (StringTerms) map.get("count");
        for (StringTerms.Bucket bucket : count.getBuckets()) {
            DistributeCaseResponse model = new DistributeCaseResponse();
            model.setCaseCount(bucket.getDocCount());
            model.setBatchNumber(bucket.getKeyAsString());
            InternalSum sum = bucket.getAggregations().get("sum");
            model.setLeftAmt(sum.getValue());
            InternalSum sumDoller = bucket.getAggregations().get("sumDollor");
            model.setLeftAmtDollar(sumDoller.getValue());
            InternalMax delegationDate = bucket.getAggregations().get("delegationDate");
            model.setDelegationDate(new Date(new Double(delegationDate.getValue()).longValue()));
            InternalMin endCaseDate = bucket.getAggregations().get("endCaseDate");
            model.setEndCaseDate(new Date(new Double(endCaseDate.getValue()).longValue()));
            StringTerms principalName  = bucket.getAggregations().get("principal");
            List<StringTerms.Bucket> buckets = principalName.getBuckets();
            model.setPrincipalName(buckets.get(0).getKeyAsString());
            buffList.add(model);
        }
        return buffList;
    }
}