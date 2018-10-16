package cn.fintecher.pangolin.service.domain.model.request;

import cn.fintecher.pangolin.common.enums.AssistApprovedStatus;
import cn.fintecher.pangolin.common.enums.AssistFlag;
import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

/**
 * @Author : huyanmin
 * @Description : 案件协催申请请求model
 * @Date : 2018/7/16.
 */
@Data
public class AssistCaseApplySearchRequest extends SearchRequest {

    @ApiModelProperty("协催审批状态")
    private AssistApprovedStatus approvalStatus;

    @ApiModelProperty("协助类型")
    private AssistFlag assistFlag;

    @ApiModelProperty("协助类型集合")
    private List<String> assistFlags;

    @ApiModelProperty("客户姓名")
    private String personalName;

    @ApiModelProperty("批次号")
    private String batchNumber;

    @ApiModelProperty("身份证号码")
    private String idCard;

    @ApiModelProperty("申请人姓名")
    private String applyRealName;

    @ApiModelProperty("委托方Id")
    private String principalId;


    @Override
    public BoolQueryBuilder generateQueryBuilder() {
        BoolQueryBuilder qb = QueryBuilders
                .boolQuery();

        if (Objects.nonNull(this.assistFlag)) {
            qb.must(matchPhraseQuery("approveStatus", this.approvalStatus.toString()))
                    .must(matchPhraseQuery("assistFlag",this.assistFlag.toString()));
        }
        if (Objects.nonNull(this.personalName)) {
            qb.must(matchPhraseQuery("personalName", this.personalName));
        }

        if (Objects.nonNull(this.batchNumber)) {
            qb.must(matchPhraseQuery("batchNumber", this.batchNumber));
        }

        if (Objects.nonNull(this.idCard)) {
            qb.must(matchPhraseQuery("idCard", this.idCard));
        }

        if (Objects.nonNull(this.principalId)) {
            qb.must(matchPhraseQuery("principalId", this.principalId));
        }

        if (Objects.nonNull(this.applyRealName)) {
            qb.must(matchPhraseQuery("applyRealName", this.applyRealName));
        }

        if(Objects.nonNull(this.assistFlags)){
            if(this.approvalStatus.equals(AssistApprovedStatus.ASSIST_WAIT_APPROVAL)){
                List<String> list = new ArrayList<>();
                list.add(AssistApprovedStatus.LOCAL_WAIT_APPROVAL.toString());
                list.add(AssistApprovedStatus.ASSIST_WAIT_APPROVAL.toString());
                qb.must(matchQuery("approveStatus", list))
                        .must(matchQuery("assistFlag",this.assistFlags));
            }else {
                List<String> list = new ArrayList<>();
                list.add(AssistApprovedStatus.LOCAL_COMPLETED.toString());
                list.add(AssistApprovedStatus.ASSIST_COMPLETED.toString());
                qb.must(matchQuery("approveStatus", list))
                        .must(matchQuery("assistFlag",this.assistFlags));
            }
        }

        return qb;
    }

}
