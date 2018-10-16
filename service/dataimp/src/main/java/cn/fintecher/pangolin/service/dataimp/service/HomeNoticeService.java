package cn.fintecher.pangolin.service.dataimp.service;

import cn.fintecher.pangolin.common.enums.AssistStatus;
import cn.fintecher.pangolin.common.enums.CommentType;
import cn.fintecher.pangolin.common.enums.ManagementType;
import cn.fintecher.pangolin.common.enums.PaymentStatus;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.domain.AssistCollectionCase;
import cn.fintecher.pangolin.entity.domain.Comment;
import cn.fintecher.pangolin.entity.domain.PaymentRecord;
import cn.fintecher.pangolin.service.dataimp.model.WaitHandleListModel;
import cn.fintecher.pangolin.service.dataimp.model.response.*;
import cn.fintecher.pangolin.service.dataimp.repository.*;
import org.apache.commons.collections4.IterableUtils;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
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


@Service("homeNoticeService")
public class HomeNoticeService {

    Logger log = LoggerFactory.getLogger(HomeNoticeService.class);

    @Autowired
    BalancePayRecordRepository balancePayRecordRepository;
    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    ImpPaymentRecordModelRepository impPaymentRecordModelRepository;
    @Autowired
    ImportBaseCaseRepository importBaseCaseRepository;
    @Autowired
    ImpAssistCaseRepository impAssistCaseRepository;
    @Autowired
    ImpCommentRepository impCommentRepository;

    /**
     * 明星催收员
     *
     * @param builder
     * @return
     */
    public StarInfoResponse getStarCollector(BoolQueryBuilder builder) {
        StarInfoResponse starInfoResponse = new StarInfoResponse();
        TermsAggregationBuilder field = AggregationBuilders.terms("collector").field("collectorId.keyword")
                .order(Terms.Order.compound(Terms.Order.aggregation("sum", false)));
//        TermsAggregationBuilder field = AggregationBuilders.terms("collector").field("collectorId.keyword");
        TermsAggregationBuilder nameBuilder = AggregationBuilders.terms("name").field("collectorName.keyword");
        TermsAggregationBuilder employeeNumberBuilder = AggregationBuilders.terms("number").field("employeeNumber.keyword");
        SumAggregationBuilder sumBuilder = AggregationBuilders.sum("sum").field("payAmt");
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices("balance_pay_record")
                .withTypes("balance_pay_record")
                .withSearchType(SearchType.DEFAULT)
                .withQuery(builder)
                .addAggregation(field.subAggregation(nameBuilder)
                        .subAggregation(employeeNumberBuilder)
                        .subAggregation(sumBuilder)).build();
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, response -> response.getAggregations());
        Map<String, Aggregation> map = aggregations.asMap();
        if (map.get("collector") instanceof StringTerms) {
            StringTerms count = (StringTerms) map.get("collector");
            for (StringTerms.Bucket bucket : count.getBuckets()) {
                InternalSum sum = bucket.getAggregations().get("sum");
                starInfoResponse.setAmt(sum.getValue() / 10000);
                StringTerms collectorName = bucket.getAggregations().get("name");
                List<StringTerms.Bucket> collectorNameBuckets = collectorName.getBuckets();
                StringTerms employeeNumber = bucket.getAggregations().get("number");
                List<StringTerms.Bucket> employeeNumberBuckets = employeeNumber.getBuckets();
                starInfoResponse.setName(collectorNameBuckets.get(0).getKeyAsString() + "(" + employeeNumberBuckets.get(0).getKeyAsString() + ")");
                break;
            }

        }
        return starInfoResponse;
    }


    /**
     * 明星催收部门
     *
     * @param builder
     * @return
     */
    public StarInfoResponse getStarOrganization(BoolQueryBuilder builder) {
        StarInfoResponse starInfoResponse = new StarInfoResponse();
        TermsAggregationBuilder field = AggregationBuilders.terms("organization").field("organizationName.keyword")
                .order(Terms.Order.compound(Terms.Order.aggregation("sum", false)));
        SumAggregationBuilder sumBuilder = AggregationBuilders.sum("sum").field("payAmt");
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices("balance_pay_record")
                .withTypes("balance_pay_record")
                .withSearchType(SearchType.DEFAULT)
                .withQuery(builder)
                .addAggregation(field.subAggregation(sumBuilder)).build();
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, response -> response.getAggregations());
        Map<String, Aggregation> map = aggregations.asMap();
        if (map.get("organization") instanceof StringTerms) {
            StringTerms count = (StringTerms) map.get("organization");
            for (StringTerms.Bucket bucket : count.getBuckets()) {
                starInfoResponse.setName(bucket.getKeyAsString());
                InternalSum sum = bucket.getAggregations().get("sum");
                starInfoResponse.setAmt(sum.getValue() / 10000);
                break;
            }
        }
        return starInfoResponse;
    }

    /**
     * 待处理事项
     *
     * @param operator
     * @return
     */
    public WaitHandleListModel getWaitHandleList(OperatorModel operator) {
        WaitHandleListModel model = new WaitHandleListModel();
        ModelMapper modelMapper = new ModelMapper();
        List<String> caseIds = new ArrayList<>();
        BoolQueryBuilder caseBuilder = QueryBuilders.boolQuery();
        if (Objects.equals(operator.getIsManager(), ManagementType.YES)) {
            caseBuilder.must(termsQuery("departments.keyword", operator.getOrganization()));
        } else {
            caseBuilder.must(matchPhraseQuery("currentCollector.id.keyword", operator.getId()));
        }
        importBaseCaseRepository.search(caseBuilder).forEach(baseCase -> {
            caseIds.add(baseCase.getId());
        });
        //PTP记录
        BoolQueryBuilder ptpBuilder = QueryBuilders.boolQuery();
        ptpBuilder.must(termsQuery("caseId", caseIds));
        ptpBuilder.must(matchPhraseQuery("paymentStatus", PaymentStatus.WAIT_CONFIRMED.name()));
        List<PaymentRecord> ptpRecords = IterableUtils.toList(impPaymentRecordModelRepository.search(ptpBuilder));
        Type type = new TypeToken<List<PaymentRecordResponse>>() {
        }.getType();
        model.setPtpRcords(modelMapper.map(ptpRecords, type));
        //FP记录
        BoolQueryBuilder cpBuilder = QueryBuilders.boolQuery();
        cpBuilder.must(termsQuery("caseId", caseIds));
        cpBuilder.must(matchPhraseQuery("paymentStatus", PaymentStatus.CONFIRMING.name()));
        List<PaymentRecord> cpRecords = IterableUtils.toList(impPaymentRecordModelRepository.search(cpBuilder));
        model.setCpRcords(modelMapper.map(cpRecords, type));
        //外访案件
        List<AssistCollectionCase> assistCollectionCases = IterableUtils.toList(impAssistCaseRepository.search(QueryBuilders.boolQuery()
                .must(caseBuilder)
                .must(matchPhraseQuery("assistStatus", AssistStatus.ASSIST_COLLECTING.name()))));
        Type assistType = new TypeToken<List<AssistCaseResponse>>() {
        }.getType();
        model.setVisitRecords(modelMapper.map(assistCollectionCases, assistType));
        return model;
    }

    public List<CommentModel> getComment(OperatorModel operator) {
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        ModelMapper modelMapper = new ModelMapper();
        qb.must(matchPhraseQuery("operator.keyword", operator.getId()));
        qb.must(matchPhraseQuery("commentType", CommentType.REMARKS.name()));
        qb.must(rangeQuery("reminderTime").gte(ZWDateUtil.getNowDate().getTime()));
        List<Comment> comments = IterableUtils.toList(impCommentRepository.search(qb));
        Map<Date, CommentModel> map = new HashMap<>();
        comments.forEach(comment -> {
            Date reminderDate = ZWDateUtil.getLocalTime(comment.getReminderTime());
            CommentModel model = null;
            if (map.containsKey(reminderDate)) {
                model = map.get(reminderDate);
            } else {
                model = new CommentModel();
                model.setReminderTime(reminderDate);
            }
            CommentResponse response = modelMapper.map(comment, CommentResponse.class);
            model.getResponses().add(response);
            map.put(reminderDate, model);
        });
        return new ArrayList(map.values());
    }
}
