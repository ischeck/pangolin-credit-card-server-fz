package cn.fintecher.pangolin.service.repair.model.request;

import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Date;
import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

@Data
public class ExamineStatusSearchRequest extends SearchRequest {

    @ApiModelProperty(notes = "姓名")
    private String name;

    @ApiModelProperty("证件号")
    private String idNo;

    @ApiModelProperty("申请人")
    private String applyPerson;

    @ApiModelProperty("是否回复")
    private String replyStatus;

    @ApiModelProperty("申请情况")
    private String applyStatus;

    @ApiModelProperty("案件地区")
    private String caseArea;

    @ApiModelProperty("申调地区")
    private String applyTransferArea;

    @ApiModelProperty("申调日期")
    private Date applyTransferDate;

    @ApiModelProperty("回复日期")
    private Date replyDate;

    @ApiModelProperty("最后一次调取结果")
    private String latelyTransferResult;

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
        if (Objects.nonNull(this.applyPerson)) {
            qb.must(matchPhraseQuery("applyPerson", this.applyPerson));
        }
        if (Objects.nonNull(this.replyStatus)) {
            qb.must(matchPhraseQuery("replyStatus", this.replyStatus));
        }
        if (Objects.nonNull(this.applyStatus)) {
            qb.must(matchPhraseQuery("applyStatus", this.applyStatus));
        }
        if (Objects.nonNull(this.caseArea)) {
            qb.must(matchPhraseQuery("caseArea", this.caseArea));
        }
        if (Objects.nonNull(this.applyTransferArea)) {
            qb.must(matchPhraseQuery("applyTransferArea", this.applyTransferArea));
        }
        if (Objects.nonNull(this.applyTransferDate)) {
            qb.must(matchPhraseQuery("applyTransferDate", this.applyTransferDate));
        }
        if (Objects.nonNull(this.replyDate)) {
            qb.must(matchPhraseQuery("replyDate", this.replyDate));
        }
        if (Objects.nonNull(this.latelyTransferResult)) {
            qb.must(matchPhraseQuery("latelyTransferResult", this.latelyTransferResult));
        }
        return qb;
    }
}
