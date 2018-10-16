package cn.fintecher.pangolin.service.domain.model.request;

import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

@Data
public class CollectionDebtRequest extends SearchRequest {

    @ApiModelProperty(notes = "客户身份证")
    private String idCard;

    @ApiModelProperty(notes = "案件id")
    private String collectionId;

    @ApiModelProperty(notes = "起始页码")
    private Integer page;

    @ApiModelProperty(notes = "每页的大小")
    private Integer size;


    @Override
    public BoolQueryBuilder generateQueryBuilder() {
        BoolQueryBuilder qb = QueryBuilders
                .boolQuery();
        if (Objects.nonNull(this.idCard)) {
            qb.must(matchPhraseQuery("personal.certificateNo", this.idCard));
        }
        return qb;
    }
}
