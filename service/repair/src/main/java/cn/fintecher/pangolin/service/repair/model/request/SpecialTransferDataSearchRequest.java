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
public class SpecialTransferDataSearchRequest extends SearchRequest {

    @ApiModelProperty(notes = "姓名")
    private String name;

    @ApiModelProperty("身份证号")
    private String idNo;

    @ApiModelProperty("申调地区")
    private String applyTransferArea;

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
        if (Objects.nonNull(this.applyTransferArea)) {
            qb.must(matchPhraseQuery("applyTransferArea", this.applyTransferArea));
        }
        return qb;
    }
}
