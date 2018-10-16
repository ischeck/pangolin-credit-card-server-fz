package cn.fintecher.pangolin.service.domain.model.request;

import cn.fintecher.pangolin.common.enums.ApplyFileContent;
import cn.fintecher.pangolin.common.enums.ApplyType;
import cn.fintecher.pangolin.common.enums.ApprovalStatus;
import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

/**
 * @Author : huyanmin
 * @Description : 补款/减免/公共案件申请请求
 * @Date : 2018/7/18.
 */
@Data
@ApiModel(value = "ApplyCaseRequest", description = "补款/减免/公共案件申请请求")
public class ApplyCaseRequest extends SearchRequest {

    @ApiModelProperty("案件的ID")
    private String caseId;

    @ApiModelProperty("申请金额")
    private String applyAmount;

    @ApiModelProperty(notes = "客户姓名")
    private String personalName;

    @ApiModelProperty(notes = "身份证号")
    private String certificateNo;

    @ApiModelProperty("申请说明")
    private String applyRemark;

    @ApiModelProperty("审批部门Id")
    private String approvedDeptId;

    @ApiModelProperty("申请类型")
    private ApplyType applyType;

    @ApiModelProperty("调取材料的部门Id")
    private String getFileDeptId;

    @ApiModelProperty("申调资料名称")
    private String applyFileDepartName;

    @ApiModelProperty("调取材料的内容")
    private ApplyFileContent applyContent;

    @ApiModelProperty("公共案件id")
    private String publicCaseId;

    @ApiModelProperty("上传文件Id")
    private String fileId;

    @ApiModelProperty("上传文件名字")
    private String fileName;

    @ApiModelProperty("还款记录Id")
    private String paymentRecordId;

    @Override
    public BoolQueryBuilder generateQueryBuilder(){
        BoolQueryBuilder qb = QueryBuilders
                .boolQuery();
        if(Objects.equals(ApplyType.REPORT_CASE_APPLY.toString(), this.applyType.toString())){
            if (Objects.nonNull(this.caseId)) {
                qb.must(matchPhraseQuery("caseId", this.caseId));
            }
        }else {
            if (Objects.nonNull(this.caseId)) {
                qb.must(matchPhraseQuery("caseId", this.caseId))
                        .mustNot(matchPhraseQuery("approvalStatus", ApprovalStatus.APPROVED_COMPLETED.toString()));
            }
        }
        if(Objects.nonNull(this.applyType)){
            qb.must(matchPhraseQuery("applyType", this.applyType.toString()));
        }
        return qb;
    }
}
