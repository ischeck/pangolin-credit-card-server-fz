package cn.fintecher.pangolin.service.domain.model.request;

import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.prefixQuery;

/**
 * Created by ChenChang on 2018/7/17.
 */
@Data
public class ChinaFiveLevelAreaSearchRequest extends SearchRequest {
    @ApiModelProperty(notes = "编码")
    private String code;
    @ApiModelProperty(notes = "名称")
    private String name;

    @Override
    public QueryBuilder generateQueryBuilder() {
        BoolQueryBuilder qb = QueryBuilders
                .boolQuery();
        if (Objects.nonNull(this.name)) {
            qb.must(matchPhraseQuery("name", this.name));
        }
        if (Objects.nonNull(this.code)) {
            qb.must(prefixQuery("code", this.code));
        }
        return qb;
    }
}
