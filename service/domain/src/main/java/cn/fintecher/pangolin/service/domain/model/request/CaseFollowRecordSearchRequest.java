package cn.fintecher.pangolin.service.domain.model.request;

import cn.fintecher.pangolin.common.enums.FollowType;
import cn.fintecher.pangolin.common.utils.ZWStringUtils;
import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * Created by BBG on 2018/8/2.
 */
@Data
public class CaseFollowRecordSearchRequest extends SearchRequest {
    @ApiModelProperty(notes = "案件信息ID")
    private String caseId;

    @ApiModelProperty(notes = "催记方式 0-自动 1-手动")
    private Integer collectionWay;

    @ApiModelProperty(notes = "跟进方式")
    private FollowType type;

    @ApiModelProperty(notes = "跟进时间开始")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Field(type = FieldType.Date)
    private Date followTimeStatrt;

    @ApiModelProperty(notes = "跟进时间结束")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Field(type = FieldType.Date)
    private Date followTimeEnd;

    @ApiModelProperty("协催ID")
    private String assistId;

    @Override
    public QueryBuilder generateQueryBuilder() {
        BoolQueryBuilder qb = QueryBuilders
                .boolQuery();

        if(ZWStringUtils.isNotEmpty(this.caseId)){
            qb.must(matchPhraseQuery("caseId",this.caseId));
        }
        if(ZWStringUtils.isNotEmpty(this.assistId)){
            qb.must(matchPhraseQuery("assistId",this.assistId));
        }
        if(Objects.nonNull(type)){
            qb.must(matchPhraseQuery("type",this.type.toString()));
        }
        if(Objects.nonNull(collectionWay)){
            qb.must(termQuery("collectionWay",this.collectionWay));
        }
        if(Objects.nonNull(this.followTimeStatrt)) {
            qb.must(rangeQuery("operatorTime").gte(this.followTimeStatrt.getTime()));
        }
        if(Objects.nonNull(this.followTimeEnd)){
            qb.must(rangeQuery("operatorTime").lte(this.followTimeEnd.getTime()+86400000));
        }
        return qb;
    }
}
