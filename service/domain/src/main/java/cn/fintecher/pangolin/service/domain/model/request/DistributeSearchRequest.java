package cn.fintecher.pangolin.service.domain.model.request;

import cn.fintecher.pangolin.common.enums.CaseDataStatus;
import cn.fintecher.pangolin.common.enums.CaseIssuedFlag;
import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Date;
import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.existsQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

@Data
public class DistributeSearchRequest extends SearchRequest {

    @ApiModelProperty(notes = "案件分池")
    private CaseIssuedFlag casePool;

    @ApiModelProperty(notes = "委托方Id")
    private String principalId;

    @ApiModelProperty(notes = "批次号")
    private String batchNumber;

    @ApiModelProperty(notes = "姓名")
    private String name;

    @ApiModelProperty(notes = "委案日期")
    private Date delegationDate;

    @ApiModelProperty(notes = "结案日期")
    private Date endCaseDate;

    @ApiModelProperty(notes = "身份证号码")
    private String cardNo;

    @ApiModelProperty(notes = "城市")
    private String city;

    @ApiModelProperty(notes = "手数")
    private String handsNumber;

    @ApiModelProperty(notes = "账号")
    private String account;

    @ApiModelProperty(notes = "案件编号")
    private String caseNumber;

    @ApiModelProperty(notes = "当前部门ID")
    private String detaptId;

    @ApiModelProperty("催收员")
    private String currentCollectorName;

    @Override
    public BoolQueryBuilder generateQueryBuilder() {
        BoolQueryBuilder qb = QueryBuilders
                .boolQuery();
        if (Objects.nonNull(this.casePool)) {
            qb.must(matchPhraseQuery("issuedFlag", this.casePool.name()));
        }
        if (Objects.nonNull(this.name)) {
            qb.must(matchPhraseQuery("personal.personalName", this.name));
        }

        if (Objects.nonNull(this.batchNumber)) {
            qb.must(matchPhraseQuery("batchNumber", this.batchNumber));
        }

        if (Objects.nonNull(this.principalId)) {
            qb.must(matchPhraseQuery("principal.id", this.principalId));
        }

        if (Objects.nonNull(this.cardNo)) {
            qb.must(matchPhraseQuery("personal.certificateNo", this.cardNo));
        }

        if (Objects.nonNull(this.city)) {
            qb.must(matchPhraseQuery("city", this.city));
        }

        if (Objects.nonNull(this.handsNumber)) {
            qb.must(matchPhraseQuery("handsNumber", this.handsNumber));
        }

        if (Objects.nonNull(this.account)) {
            qb.must(matchPhraseQuery("account", this.account));
        }

        if (Objects.nonNull(this.caseNumber)) {
            qb.must(matchPhraseQuery("caseNumber", this.caseNumber));
        }

        if (Objects.nonNull(this.detaptId)) {
            qb.must(matchPhraseQuery("detaptId", this.detaptId));
        }

        if (Objects.nonNull(this.currentCollectorName)) {
            qb.must(matchPhraseQuery("currentCollector.fullName", this.currentCollectorName));
        }

        qb.must(matchPhraseQuery("caseDataStatus", CaseDataStatus.IN_POOL.name()));
        return qb;
    }
}
