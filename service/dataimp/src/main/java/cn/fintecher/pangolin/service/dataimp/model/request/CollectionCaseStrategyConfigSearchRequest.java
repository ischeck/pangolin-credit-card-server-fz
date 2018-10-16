package cn.fintecher.pangolin.service.dataimp.model.request;

import cn.fintecher.pangolin.common.enums.StrategyState;
import cn.fintecher.pangolin.common.enums.StrategyType;
import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.wildcardQuery;

/**
 * Created by ChenChang on 2018/8/8.
 */

@Data
public class CollectionCaseStrategyConfigSearchRequest extends SearchRequest {
    @ApiModelProperty(notes = "名称")
    private String name;

    @ApiModelProperty("策略类型")
    private StrategyType strategyType;

    @Override
    public QueryBuilder generateQueryBuilder() {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(name)) {
            //模糊查询
            queryBuilder.must().add(wildcardQuery("name.keyword", "*" + this.name + "*"));
        }
        if (Objects.nonNull(this.strategyType)) {
            queryBuilder.must().add(matchPhraseQuery("strategyType", this.strategyType.name()));
        }

        queryBuilder.must(matchPhraseQuery("strategyState", StrategyState.ENABLED.toString()));
        return queryBuilder;
    }
}
