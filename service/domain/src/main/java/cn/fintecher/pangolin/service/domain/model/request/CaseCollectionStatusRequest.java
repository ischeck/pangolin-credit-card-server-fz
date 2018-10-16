package cn.fintecher.pangolin.service.domain.model.request;

import cn.fintecher.pangolin.common.enums.AssistFlag;
import cn.fintecher.pangolin.common.enums.CaseDataStatus;
import cn.fintecher.pangolin.common.enums.CaseLeaveFlag;
import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/***
 *
 * created by huyanmin 2018/7/31
 *
 */
@Data
public class CaseCollectionStatusRequest extends SearchRequest {


    @ApiModelProperty(notes = "姓名")
    private String personalName;

    @ApiModelProperty(notes = "委托方Id")
    private String principalId;

    @ApiModelProperty(notes = "协助标识")
    private String assistFlag;


    @Override
    public BoolQueryBuilder generateQueryBuilder() {
        BoolQueryBuilder qb = QueryBuilders
                .boolQuery();

        if (Objects.nonNull(this.personalName)) {
            qb.must(matchPhraseQuery("personal.personalName", this.personalName));
        }

        if (Objects.nonNull(this.principalId)) {
            qb.must(matchPhraseQuery("principal.id", this.principalId));
        }
        qb.mustNot(matchPhraseQuery("leaveFlag", CaseLeaveFlag.HAS_LEAVE.toString()))
                .mustNot(matchPhraseQuery("caseDataStatus", CaseDataStatus.OUT_POOL.toString()))
                .mustNot(matchPhraseQuery("caseDataStatus", CaseDataStatus.PAUSE.toString()));
        return qb;
    }
}
