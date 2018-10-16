package cn.fintecher.pangolin.service.repair.model.request;

import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;
import static org.elasticsearch.index.query.QueryBuilders.wildcardQuery;

@Data
public class VillageCommitteeDataSearchRequest extends SearchRequest {

    @ApiModelProperty(notes = "地区")
    private String area;

    @ApiModelProperty("联系人")
    private String linkman;

    @ApiModelProperty("手机")
    private String mobile;

    @Override
    public QueryBuilder generateQueryBuilder() {
        BoolQueryBuilder qb = QueryBuilders
                .boolQuery();
        if (Objects.nonNull(this.area)) {
            qb.must(matchPhraseQuery("area", this.area));
        }
        if (Objects.nonNull(this.linkman)) {
            qb.must(matchPhraseQuery("linkman", this.linkman));
        }
        if (Objects.nonNull(this.mobile)) {
            qb.must(matchPhraseQuery("mobile", this.mobile));
        }

        return qb;
    }
}
