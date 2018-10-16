package cn.fintecher.pangolin.service.domain.model.request;

import cn.fintecher.pangolin.common.utils.ZWStringUtils;
import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

@Data
public class HisCaseSearchRequest extends SearchRequest {
    @ApiModelProperty("客户姓名")
    private String personalName;

    @ApiModelProperty("账号")
    private String account;

    @ApiModelProperty("城市")
    private String city;

    @ApiModelProperty("手数")
    private String handsNumber;

    @ApiModelProperty("委托方id")
    private String principalId;

    @ApiModelProperty("催收员")
    private String currentCollectorName;

    @Override
    public QueryBuilder generateQueryBuilder() {
        BoolQueryBuilder qb = QueryBuilders
                .boolQuery();
        if (ZWStringUtils.isNotEmpty(this.personalName)) {
            qb.must(matchPhraseQuery("personal.personalName", this.personalName));
        }
        if (ZWStringUtils.isNotEmpty(this.account)) {
            qb.must(matchPhraseQuery("account", this.account));
        }
        if (ZWStringUtils.isNotEmpty(this.city)) {
            qb.must(matchPhraseQuery("city", this.city));
        }
        if (ZWStringUtils.isNotEmpty(this.handsNumber)) {
            qb.must(matchPhraseQuery("handsNumber", this.handsNumber));
        }
        if (ZWStringUtils.isNotEmpty(this.principalId)) {
            qb.must(matchPhraseQuery("principal.id", this.principalId));
        }
        if (ZWStringUtils.isNotEmpty(this.currentCollectorName)) {
            qb.must(matchPhraseQuery("currentCollector.fullName", this.currentCollectorName));
        }
        return qb;
    }
}
