package cn.fintecher.pangolin.service.domain.model.response;

import cn.fintecher.pangolin.common.enums.*;
import cn.fintecher.pangolin.entity.domain.CardInformation;
import cn.fintecher.pangolin.entity.domain.Comment;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @Author : huyanmin
 * @Description : 案件协催申请
 * @Date : 2018/7/12.
 */
@Data
public class BasicCaseApplyResponse {

    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty("批次号")
    private String batchNumber;

    @ApiModelProperty("客户姓名")
    private String personalName;

    @ApiModelProperty("委托方")
    private String principalName;

    @ApiModelProperty("委托方Id")
    private String principalId;

    @ApiModelProperty("身份证号码")
    private String idCard;

    @ApiModelProperty("申请类型")
    private ApplyType applyType;

    @ApiModelProperty("申调资料类型")
    private ApplyFileContent applyContent;

    @ApiModelProperty("卡号")
    private Set<CardInformation> cardInformationSet;

    @ApiModelProperty("补款金额")
    private Double supplementAmount;

    @ApiModelProperty("减免金额")
    private Double derateAmount;

    @ApiModelProperty("实际减免金额")
    private Double derateRealAmount;

    @ApiModelProperty("实际补款金额")
    private Double supplementRealAmount;

    @ApiModelProperty("申请人姓名")
    private String applyName;

    @ApiModelProperty("申请时间")
    private Date applyDate;

    @ApiModelProperty("申请说明")
    private String applyRemark;

    @ApiModelProperty("审批人员")
    private String approvedName;

    @ApiModelProperty("审批时间")
    private Date approvedTime;

    @ApiModelProperty("审批结果")
    private ApprovalResult approvedResult;

    @ApiModelProperty("审批状态")
    private ApprovalStatus approvalStatus;

    @ApiModelProperty("审批意见")
    private String approvedMemo;

    @ApiModelProperty("还款卡号")
    private String backCardNo;

    @ApiModelProperty("附件Id")
    private String fileId;

    @ApiModelProperty("附件名称")
    private String fileName;

    @ApiModelProperty("最新欠款")
    private Double latestOverdueAmount;

    @ApiModelProperty("还款金额")
    private Double hasPayAmount;

    @ApiModelProperty("导出状态")
    private ExportState exportState;

    @ApiModelProperty("备注")
    private List<Comment> comments;

    @ApiModelProperty("申调资料名称")
    private String applyFileDepartName;

}
