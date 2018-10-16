package cn.fintecher.pangolin.service.domain.model.request;

import cn.fintecher.pangolin.common.enums.*;
import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * @Author : huyanmin
 * @Description : 申请审批搜索
 * @Date : 2018/7/16.
 */
@Data
public class CaseApplySearchRequest extends SearchRequest {

    @ApiModelProperty("申请类型")
    private ApplyType applyType;

    @ApiModelProperty("审批状态")
    private ApprovalStatus approvalStatus;

    @ApiModelProperty("审批结果")
    private ApprovalResult approvalResult;

    @ApiModelProperty("导出状态")
    private ExportState exportState;

    @ApiModelProperty("批次号")
    private String batchNumber;

    @ApiModelProperty("客户姓名")
    private String personalName;

    @ApiModelProperty("身份证号码")
    private String idCard;

    @ApiModelProperty("委托方Id")
    private String principalId;

    @ApiModelProperty(notes = "申请日期开始")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date applyDateStart;

    @ApiModelProperty(notes = "申请日期结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date applyDateEnd;


    @Override
    public BoolQueryBuilder generateQueryBuilder() {
        BoolQueryBuilder qb = QueryBuilders
                .boolQuery();

        if (Objects.nonNull(this.applyType)) {
            if(this.applyType.equals(ApplyType.PUBLIC_CASE_APPLY)){
                qb.must(QueryBuilders.boolQuery().should(matchPhraseQuery("applyType", ApplyType.PUBLIC_DISTRIBUTE_CASE_APPLY.toString()))
                        .should(matchPhraseQuery("applyType", ApplyType.PUBLIC_CASE_APPLY.toString())));
            }else {
                qb.must(matchPhraseQuery("applyType", this.applyType.toString()));
            }
        }
        if(Objects.nonNull(this.approvalStatus)){
            qb.must(matchPhraseQuery("approvalStatus", this.approvalStatus.toString()));
        }

        if(Objects.nonNull(this.approvalResult)) {
            qb.must(matchPhraseQuery("approvedResult", this.approvalResult.toString()));
        }

        if(Objects.nonNull(this.exportState)){
            qb.must(matchPhraseQuery("exportState", this.exportState.toString()));
        }
        if(Objects.nonNull(this.principalId)){
            qb.must(matchPhraseQuery("principal.id", this.principalId));
        }
        if(Objects.nonNull(this.personalName)){
            qb.must(matchPhraseQuery("personalName", this.personalName));
        }
        if(Objects.nonNull(this.idCard)){
            qb.must(matchPhraseQuery("idCard", this.idCard));
        }
        if(Objects.nonNull(this.batchNumber)){
            qb.must(matchPhraseQuery("batchNumber", this.batchNumber));
        }
        if(Objects.nonNull(this.applyDateStart)) {
            qb.must(rangeQuery("applyDate").gte(this.applyDateStart.getTime()));
        }
        if(Objects.nonNull(this.applyDateEnd)){
            qb.must(rangeQuery("applyDate").lte(this.applyDateEnd.getTime()+86400000));
        }
        return qb;
    }

}
