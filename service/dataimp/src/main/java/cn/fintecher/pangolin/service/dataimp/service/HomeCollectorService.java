package cn.fintecher.pangolin.service.dataimp.service;

import cn.fintecher.pangolin.common.enums.CaseDataStatus;
import cn.fintecher.pangolin.common.enums.CommentType;
import cn.fintecher.pangolin.common.enums.ManagementType;
import cn.fintecher.pangolin.common.enums.PaymentStatus;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.model.PrincipalModel;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.domain.Comment;
import cn.fintecher.pangolin.entity.domain.PaymentRecord;
import cn.fintecher.pangolin.entity.managentment.CustConfig;
import cn.fintecher.pangolin.entity.managentment.Operator;
import cn.fintecher.pangolin.service.dataimp.client.OrganizationClient;
import cn.fintecher.pangolin.service.dataimp.model.CollStateModel;
import cn.fintecher.pangolin.service.dataimp.model.PaymentRecordModel;
import cn.fintecher.pangolin.service.dataimp.model.response.*;
import cn.fintecher.pangolin.service.dataimp.repository.*;
import org.apache.commons.collections4.IterableUtils;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.InternalValueCount;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountAggregationBuilder;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

@Service
public class HomeCollectorService {

    Logger log = LoggerFactory.getLogger(HomeCollectorService.class);

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    ImportBaseCaseRepository importBaseCaseRepository;
    @Autowired
    CaseFollowupRecordRepository caseFollowupRecordRepository;
    @Autowired
    ImpCommentRepository impCommentRepository;
    @Autowired
    ImpPaymentRecordModelRepository impPaymentRecordModelRepository;
    @Autowired
    OrganizationClient organizationClient;
    @Autowired
    BalancePayRecordRepository balancePayRecordRepository;

    public MonthScheduleResponse getMonthSchedule(BoolQueryBuilder qb) {
        MonthScheduleResponse monthScheduleResponse = new MonthScheduleResponse();
        qb.must(rangeQuery("followInTime").gte(ZWDateUtil.getMonthFirstDay(0).getTime()));
        List<String> caseIds = new ArrayList<>();
        importBaseCaseRepository.search(qb).forEach(baseCase -> {
            monthScheduleResponse.setOverdueAmt(monthScheduleResponse.getOverdueAmt() + baseCase.getOverdueAmtTotal());
            caseIds.add(baseCase.getId());
        });
        Set<String> payCaseSet = new HashSet<>();
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        builder.must(rangeQuery("payDate").gte(ZWDateUtil.getMonthFirstDay(0).getTime()));
        builder.must(termsQuery("caseId", caseIds));
        balancePayRecordRepository.search(builder).forEach(balancePayRecord -> {
            monthScheduleResponse.setReturnAmt(monthScheduleResponse.getReturnAmt() + balancePayRecord.getPayAmt());
            payCaseSet.add(balancePayRecord.getCaseId());
        });
        if (monthScheduleResponse.getOverdueAmt() == 0) {
            monthScheduleResponse.setAmtRate(0.0);
        } else {
            monthScheduleResponse.setAmtRate(monthScheduleResponse.getReturnAmt() / monthScheduleResponse.getOverdueAmt());
        }
        monthScheduleResponse.setOverdueNum(caseIds.size());
        monthScheduleResponse.setReturnNum(payCaseSet.size());
        if (monthScheduleResponse.getOverdueNum() == 0) {
            monthScheduleResponse.setNumRate(0.0);
        } else {
            monthScheduleResponse.setNumRate(monthScheduleResponse.getReturnNum() / (monthScheduleResponse.getOverdueNum() + 0.0));
        }
        monthScheduleResponse.setOverdueAmt(monthScheduleResponse.getOverdueAmt());
        monthScheduleResponse.setReturnAmt(monthScheduleResponse.getReturnAmt());
        return monthScheduleResponse;
    }

    public List<CommentResponse> getComment(OperatorModel operator) {
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        qb.must(matchPhraseQuery("operator.keyword", operator.getId()));
        qb.must(matchPhraseQuery("commentType", CommentType.REMARKS.name()));
        qb.must(rangeQuery("reminderTime").gte(ZWDateUtil.getNightTime(-1).getTime()));
        List<Comment> comments = IterableUtils.toList(impCommentRepository.search(qb));
        Type type = new TypeToken<List<CommentResponse>>() {
        }.getType();
        ModelMapper modelMapper = new ModelMapper();
        List<CommentResponse> responses = modelMapper.map(comments, type);
        return responses;
    }

    public PaymentRecordModel getPTPRecord(OperatorModel operator) {
        PaymentRecordModel model = new PaymentRecordModel();
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        if (Objects.equals(operator.getIsManager(), ManagementType.YES)) {
            qb.must(termsQuery("departments.keyword", operator.getOrganization()));
        } else {
            qb.must(matchPhraseQuery("currentCollector.id.keyword", operator.getId()));
        }
        List<String> caseIds = new ArrayList<>();
        importBaseCaseRepository.search(qb).forEach(baseCase -> {
            caseIds.add(baseCase.getId());
        });
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        builder.must(termsQuery("caseId.keyword", caseIds));
        builder.must(matchPhraseQuery("paymentStatus", PaymentStatus.WAIT_CONFIRMED.name()));
        List<PaymentRecord> paymentRecords = IterableUtils.toList(impPaymentRecordModelRepository.search(builder));
        Type type = new TypeToken<List<PaymentRecordResponse>>() {
        }.getType();
        ModelMapper modelMapper = new ModelMapper();
        List<PaymentRecordResponse> paymentRecordResponses = modelMapper.map(paymentRecords, type);
        model.setTotalNum(caseIds.size());
        model.setPtpNum(paymentRecordResponses.size());
        model.setPaymentRecordResponses(paymentRecordResponses);
        return model;
    }

    public CollStateModel getCollState(OperatorModel operator) {
        CollStateModel model = new CollStateModel();
        List<CustConfig> custConfigs = organizationClient.getAllCustConfig().getBody();
        Set<String> configs = new HashSet<>();
        custConfigs.forEach(custConfig -> {
            configs.add(custConfig.getName());
        });
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        if (Objects.equals(operator.getIsManager(), ManagementType.YES)) {
            qb.must(termsQuery("departments.keyword", operator.getOrganization()));
        } else {
            qb.must(matchPhraseQuery("currentCollector.id.keyword", operator.getId()));
        }
        List<String> caseIds = new ArrayList<>();
        importBaseCaseRepository.search(qb).forEach(baseCase -> {
            caseIds.add(baseCase.getId());
            if (Objects.nonNull(baseCase.getCollectionStatus())
                    && !baseCase.getCollectionStatus().isEmpty()) {
                model.setSignNum(model.getSignNum() + 1);
            }
        });
        List<CollStateResponse> collStateResponses = new ArrayList<>();
        configs.forEach(name -> {
            CollStateResponse collStateResponse = new CollStateResponse();
            BoolQueryBuilder builder = QueryBuilders.boolQuery();
            builder.must(termsQuery("id.keyword", caseIds));
            builder.must(termsQuery("collectionStatus.keyword", name));
            ValueCountAggregationBuilder field = AggregationBuilders.count("count").field("id.keyword");
            SumAggregationBuilder sumBuilder = AggregationBuilders.sum("sum").field("leftAmt");
            SearchQuery searchQuery = new NativeSearchQueryBuilder()
                    .withIndices("base_case")
                    .withTypes("base_case")
                    .withSearchType(SearchType.DEFAULT)
                    .withQuery(builder)
                    .addAggregation(field)
                    .addAggregation(sumBuilder).build();
            Aggregations aggregations = elasticsearchTemplate.query(searchQuery, response -> response.getAggregations());
            InternalSum sum = aggregations.get("sum");
            InternalValueCount count = aggregations.get("count");
            collStateResponse.setState(name);
            collStateResponse.setAmt(sum.getValue());
            collStateResponse.setNum(count.getValue());
            collStateResponses.add(collStateResponse);
        });
        model.setTotalNum(caseIds.size());
        model.setCollStateResponses(collStateResponses);
        return model;
    }

    public StockModel getStock(OperatorModel operator) {
        StockModel model = new StockModel();
        List<StockResponse> stockResponses = new ArrayList<>();
        List<PrincipalModel> principals = organizationClient.findAllPrincipal().getBody();
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        if (Objects.equals(operator.getIsManager(), ManagementType.YES)) {
            qb.must(termsQuery("departments.keyword", operator.getOrganization()));
        } else {
            qb.must(matchPhraseQuery("currentCollector.id.keyword", operator.getId()));
        }
        qb.must(matchPhraseQuery("caseDataStatus", CaseDataStatus.IN_POOL.name()));
        principals.forEach(principal -> {
            StockResponse stockResponse = new StockResponse();
            List<String> caseIds = new ArrayList<>();
            Set<Operator> collector = new HashSet<>();
            importBaseCaseRepository.search(QueryBuilders.boolQuery()
                    .must(matchPhraseQuery("principal.id.keyword", principal.getId()))
                    .must(qb)).forEach(baseCase -> {
                caseIds.add(baseCase.getId());
                stockResponse.setOverdueAmt(stockResponse.getOverdueAmt() + baseCase.getOverdueAmtTotal());
                if(Objects.nonNull(baseCase.getCurrentCollector())) {
                    collector.add(baseCase.getCurrentCollector());
                }
            });
            BoolQueryBuilder builder = QueryBuilders.boolQuery();
            builder.must(termsQuery("caseId.keyword", caseIds));
            balancePayRecordRepository.search(builder).forEach(balancePayRecord -> {
                stockResponse.setReturnAmt(stockResponse.getReturnAmt() + balancePayRecord.getPayAmt());
            });
            stockResponse.setPrincipalName(principal.getPrincipalName());
            stockResponse.setTotalNum(caseIds.size());
            if (stockResponse.getOverdueAmt() == 0) {
                stockResponse.setRate(0.0);
            } else {
                stockResponse.setRate(stockResponse.getReturnAmt() / stockResponse.getOverdueAmt());
            }
            stockResponse.setCollectorNum(collector.size());
            stockResponse.setOverdueAmt(stockResponse.getOverdueAmt() / 10000);
            stockResponse.setReturnAmt(stockResponse.getReturnAmt() / 10000);
            stockResponses.add(stockResponse);
        });
        Double bigAmt = 0.0;
        long bigNum = 0;
        for(StockResponse response : stockResponses){
            if(response.getOverdueAmt()>bigAmt){
                bigAmt = response.getOverdueAmt();
            }
            if(response.getTotalNum() > bigNum){
                bigNum = response.getTotalNum();
            }
        }
        model.setBigAmt(Math.ceil(bigAmt/100)*100);
        model.setBigNum((long)Math.ceil(bigNum/100.0)*100);
        model.setResponseList(stockResponses);
        return model;
    }

}
