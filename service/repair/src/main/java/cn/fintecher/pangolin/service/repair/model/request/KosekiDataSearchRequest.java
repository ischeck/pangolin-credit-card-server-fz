package cn.fintecher.pangolin.service.repair.model.request;

import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

@Data
public class KosekiDataSearchRequest extends SearchRequest {
    @ApiModelProperty(notes = "姓名")
    private String name;

    @ApiModelProperty("身份证号")
    private String idNo;

    @ApiModelProperty("户籍地区")
    private String kosekiArea;

    @ApiModelProperty("户籍")
    private String koseki;


    @Override
    public QueryBuilder generateQueryBuilder() {
        BoolQueryBuilder qb = QueryBuilders
                .boolQuery();
        if (Objects.nonNull(this.name)) {
            qb.must(matchPhraseQuery("name", this.name));
        }
        if (Objects.nonNull(this.idNo)) {
            qb.must(matchPhraseQuery("idNo", this.idNo));
        }
        if (Objects.nonNull(this.kosekiArea)) {
            qb.must(matchPhraseQuery("kosekiArea", this.kosekiArea));
        }
        if (Objects.nonNull(this.koseki)) {
            qb.must(matchPhraseQuery("koseki", this.koseki));
        }
        return qb;
    }
}
