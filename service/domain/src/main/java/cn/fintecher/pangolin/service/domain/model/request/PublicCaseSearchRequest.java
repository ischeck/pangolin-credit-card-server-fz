package cn.fintecher.pangolin.service.domain.model.request;

import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Created by huyanmin on 2018/8/15.
 */
@Data
public class PublicCaseSearchRequest extends SearchRequest {

    @ApiModelProperty("客户")
    private String personalName;

    @ApiModelProperty("批次号")
    private String batchNumber;

    @ApiModelProperty("案件号")
    private String caseNumber;

    @ApiModelProperty(notes = "催收员")
    private String currentCollector;

    @ApiModelProperty(notes = "证件号")
    private String certificateNo;

    @ApiModelProperty(notes = "查询标记")
    private Integer flag;

    @Override
    public BoolQueryBuilder generateQueryBuilder() {
        BoolQueryBuilder qb = QueryBuilders
                .boolQuery();
        if (Objects.nonNull(this.personalName)) {
            qb.must(matchPhraseQuery("personal.personalName", this.personalName));
        }

        if (Objects.nonNull(this.certificateNo)) {
            qb.must(matchPhraseQuery("personal.certificateNo", this.certificateNo));
        }

        if (Objects.nonNull(this.batchNumber)) {
            qb.must(matchPhraseQuery("batchNumber", this.batchNumber));
        }

        if (Objects.nonNull(this.caseNumber)) {
            qb.must(matchPhraseQuery("caseNumber", this.caseNumber));
        }
        return qb;
    }
}
