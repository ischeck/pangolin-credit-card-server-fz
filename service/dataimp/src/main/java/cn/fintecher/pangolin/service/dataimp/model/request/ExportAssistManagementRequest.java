package cn.fintecher.pangolin.service.dataimp.model.request;

import cn.fintecher.pangolin.common.enums.ApplyType;
import cn.fintecher.pangolin.common.enums.ApprovalStatus;
import cn.fintecher.pangolin.common.enums.ExportState;
import cn.fintecher.pangolin.common.enums.ImportDataExcelStatus;
import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.List;
import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * @Author:peishouwen
 * @Desc: 案件导入结果查询
 * @Date:Create in 11:37 2018/7/29
 */
@Data
public class ExportAssistManagementRequest extends SearchRequest {

    @ApiModelProperty(notes = "申请类型")
    private ApplyType applyType;

    @ApiModelProperty(notes = "委托方id")
    private String principalId;

    @ApiModelProperty(notes = "批次号")
    private String batchNumber;

    @ApiModelProperty("需要导出的id")
    private List<String> applyIds;


    @Override
    public BoolQueryBuilder generateQueryBuilder() {
        BoolQueryBuilder queryBuilder= QueryBuilders.boolQuery();

        if(Objects.nonNull(this.batchNumber)){
            queryBuilder.must(matchPhraseQuery("batchNumber",this.batchNumber));
        }

        if(Objects.nonNull(this.principalId)){
            queryBuilder.must(matchPhraseQuery("principal.id",this.principalId));
        }

        if(Objects.nonNull(this.applyType)){
            queryBuilder.must(matchPhraseQuery("applyType",this.applyType.name()));
            if(!this.applyType.equals(ApplyType.SUPPLEMENT_APPLY) && !this.applyType.equals(ApplyType.CHECK_MATERIAL_APPLY)){
                queryBuilder.must(matchPhraseQuery("exportState", ExportState.WAIT_EXPORT.toString()));
                queryBuilder.must(matchPhraseQuery("approvalStatus", ApprovalStatus.WAIT_APPROVAL.toString()));
            }else {
                queryBuilder.must(matchPhraseQuery("approvalStatus", ApprovalStatus.APPROVED_COMPLETED.toString()));
            }
        }

        if(Objects.nonNull(this.applyIds)){
            queryBuilder.must(termsQuery("id.keyword", this.applyIds));
        }

        return queryBuilder;
    }
}
