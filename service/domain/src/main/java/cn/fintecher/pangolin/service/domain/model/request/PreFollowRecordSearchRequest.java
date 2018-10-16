package cn.fintecher.pangolin.service.domain.model.request;

import cn.fintecher.pangolin.common.utils.ZWStringUtils;
import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Date;
import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

/**
 * Created by BBG on 2018/8/6.
 */
@Data
public class PreFollowRecordSearchRequest extends SearchRequest {

    @ApiModelProperty(notes = "案件信息ID")
    private String caseId;

    @ApiModelProperty(notes = "跟进时间开始")
    private Date followTimeStatrt;

    @ApiModelProperty(notes = "跟进时间结束")
    private Date followTimeEnd;

    @Override
    public QueryBuilder generateQueryBuilder() {
        BoolQueryBuilder qb = QueryBuilders
                .boolQuery();

        if(ZWStringUtils.isNotEmpty(this.caseId)){
            qb.must(matchPhraseQuery("caseId",this.caseId));
        }
        if(Objects.nonNull(this.followTimeStatrt)) {
            qb.must(rangeQuery("followTime").gte(this.followTimeStatrt.getTime()));
        }
        if(Objects.nonNull(this.followTimeEnd)){
            qb.must(rangeQuery("followTime").lte(this.followTimeEnd.getTime()+86400000));
        }
        return qb;
    }
}
