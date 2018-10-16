package cn.fintecher.pangolin.service.domain.model.response;

import cn.fintecher.pangolin.common.enums.AssistApprovedResult;
import cn.fintecher.pangolin.common.enums.AssistApprovedStatus;
import cn.fintecher.pangolin.common.enums.AssistFlag;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author : huyanmin
 * @Description : 案件协催申请
 * @Date : 2018/7/12.
 */
@Data
public class AssistCaseApplyResponse {

    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty("批次号")
    private String batchNumber;

    @ApiModelProperty("客户姓名")
    private String personalName;

    @ApiModelProperty("身份证号码")
    private String idCard;

    @ApiModelProperty("委托方名称")
    private String principalName;

    @ApiModelProperty("委外金额")
    private Double overDueAmount;

    @ApiModelProperty("协催方式(本地外访，异地外访，电话，信函)")
    private AssistFlag assistFlag;

    @ApiModelProperty("协催地址")
    private String addressDetail;

    @ApiModelProperty("地址类型")
    private String addressType;

    @ApiModelProperty("关系")
    private String relation;

    @ApiModelProperty("申请人姓名")
    private String applyRealName;

    @ApiModelProperty("申请原因")
    private String applyReason;

    @ApiModelProperty("申请时间")
    private Date applyDate;

    @ApiModelProperty("审批状态")
    private AssistApprovedStatus approveStatus;

    @ApiModelProperty("审批结果")
    private AssistApprovedResult approveResult;

    @ApiModelProperty("审批人姓名")
    private String approveName;

    @ApiModelProperty("审批人")
    private String approveID;

    @ApiModelProperty("审批批时间")
    private Date approveTime;

    @ApiModelProperty("本地城市审批意见")
    private String approveMemo;
}
