package cn.fintecher.pangolin.service.domain.model.request;

import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import java.util.Objects;
import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;


/**
 * Created by BBG on 2018/8/10.
 */
@Data
public class CaseFindQueryRequest extends SearchRequest {

    @ApiModelProperty("客户姓名")
    private String personalName;

    @ApiModelProperty("身份证号码")
    private String certificateNo;

    @ApiModelProperty("案件号")
    private String caseNumber;

    @ApiModelProperty("电话号码")
    private String phone;

    @Override
    public BoolQueryBuilder generateQueryBuilder() {
        BoolQueryBuilder qb = QueryBuilders
                .boolQuery();
        if (Objects.nonNull(this.personalName)) {
            qb.must(matchPhraseQuery("personal.personalName", this.personalName));
        }
        if (Objects.nonNull(this.caseNumber)) {
            qb.must(matchPhraseQuery("caseNumber", this.caseNumber));
        }
        if (Objects.nonNull(this.certificateNo)) {
            qb.must(matchPhraseQuery("personal.certificateNo", this.certificateNo));
        }
        return qb;
    }
}
