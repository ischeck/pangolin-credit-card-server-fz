package cn.fintecher.pangolin.service.domain.model.request;

import cn.fintecher.pangolin.common.enums.ApplyType;
import cn.fintecher.pangolin.common.enums.ApprovalResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author : huyanmin
 * @Description : 补款/减免审批请求
 * @Date : 2018/7/18.
 */
@Data
@ApiModel(value = "ApplyCaseApproveRequest", description = "补款/减免审批请求")
public class ApplyCaseApproveRequest {

    @ApiModelProperty("审批的ID")
    private List<String> idList;

    @ApiModelProperty("审批结果")
    private ApprovalResult approvalResult;

    @ApiModelProperty("审批说明")
    private String approveRemark;

    @ApiModelProperty("申请类型")
    private ApplyType applyType;

    @ApiModelProperty("最新欠款")
    private Double latestOverdueAmount;

    @ApiModelProperty("还款金额")
    private String hasPayAmount;
}
