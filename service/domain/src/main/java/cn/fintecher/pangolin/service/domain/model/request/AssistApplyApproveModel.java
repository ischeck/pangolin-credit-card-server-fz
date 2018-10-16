package cn.fintecher.pangolin.service.domain.model.request;

import cn.fintecher.pangolin.common.enums.ApprovalResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author : sunyanping
 * @Description : 协催申请审批对象
 * @Date : 2017/7/17.
 */
@Data
@ApiModel(value = "AssistApplyApproveModel", description = "协催申请审批对象")
public class AssistApplyApproveModel {

    @ApiModelProperty("协催案件的ID")
    private List<String> idList;
    @ApiModelProperty("审批结果")
    private ApprovalResult approveResult;
    @ApiModelProperty("审批意见")
    private String approveMemo;
}
