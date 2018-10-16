package cn.fintecher.pangolin.service.domain.model.request;

import cn.fintecher.pangolin.common.enums.GroupType;
import cn.fintecher.pangolin.common.utils.ZWStringUtils;
import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

/**
 * Created by huyanmin on 2018/8/7.
 */
@Data
public class SearchCollectionCaseRequest extends SearchRequest {

    @ApiModelProperty(notes = "委托方Id")
    private String principalId;

    @ApiModelProperty(notes = "批次号")
    private String batchNumber;

    @ApiModelProperty(notes = "委托方名称")
    private String principalNumber;

    @ApiModelProperty(notes = "委案日期开始")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date delegationDateStart;

    @ApiModelProperty(notes = "委案日期结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date delegationDateEnd;

    @ApiModelProperty(notes = "退案日期开始")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endCaseDateStart;

    @ApiModelProperty(notes = "退案日期结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endCaseDateEnd;

    @ApiModelProperty(notes = "小组案件标识")
    private GroupType groupType;


    @Override
    public BoolQueryBuilder generateQueryBuilder() {
        BoolQueryBuilder qb = QueryBuilders
                .boolQuery();

        if (ZWStringUtils.isNotEmpty(this.principalId)) {
            qb.must(matchPhraseQuery("principal.id", this.principalId));
        }

        if (ZWStringUtils.isNotEmpty(this.principalNumber)) {
            qb.must(matchPhraseQuery("principal.principalName", this.principalNumber));
        }

        if (ZWStringUtils.isNotEmpty(this.batchNumber)) {
            qb.must(matchPhraseQuery("batchNumber", this.batchNumber));
        }
        if (Objects.nonNull(this.delegationDateStart)) {
            qb.must(rangeQuery("delegationDate").gte(this.delegationDateStart.getTime()));
        }
        if (Objects.nonNull(this.delegationDateEnd)) {
            qb.must(rangeQuery("delegationDate").lte(this.delegationDateEnd.getTime()+86400000));
        }
        if (Objects.nonNull(this.endCaseDateStart)) {
            qb.must(rangeQuery("endCaseDate").gte(this.endCaseDateStart.getTime()));
        }
        if (Objects.nonNull(this.endCaseDateEnd)) {
            qb.must(rangeQuery("endCaseDate").lte(this.endCaseDateEnd.getTime()+86400000));
        }
        return qb;
    }

}
