package cn.fintecher.pangolin.entity.domain;

import cn.fintecher.pangolin.common.enums.*;
import cn.fintecher.pangolin.common.model.ApproveFlowConfigModel;
import cn.fintecher.pangolin.entity.managentment.Operator;
import cn.fintecher.pangolin.entity.managentment.Principal;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author : huyanmin
 * @Description : 申请实体
 * @Date : 2018/7/18.
 */
@Data
@Document(indexName = "basic_case_apply", type = "basic_case_apply", shards = 1, replicas = 0)
@ApiModel(value = "BasicCaseApply", description = "申请实体")
public class BasicCaseApply {

    @Id
    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty("案件ID")
    private String caseId;

    @ApiModelProperty("批次号")
    private String batchNumber;

    @ApiModelProperty("客户姓名")
    private String personalName;

    @ApiModelProperty("委案金额")
    private Double overdueAmtTotal;

    @ApiModelProperty("欠款")
    private Double leftAmt;

    @ApiModelProperty(notes = "账户号")
    private String account;

    @ApiModelProperty(notes = "案件编号")
    private String caseNumber;

    @ApiModelProperty("地区")
    private String city;

    @ApiModelProperty("手次")
    private String handsNumber;

    @ApiModelProperty("身份证号码")
    private String certificateNo;

    @ApiModelProperty(notes = "委案日期")
    private Date delegationDate;

    @ApiModelProperty("卡号")
    private Set<CardInformation> cardInformationSet;

    @ApiModelProperty("委托方名称")
    private String principalName;

    @ApiModelProperty("委托方")
    private Principal principal;

    @ApiModelProperty("当前审批级别")
    private Integer currentApprovalLevel;

    @ApiModelProperty("流程配置审批级别")
    private Integer configFlowApprovalLevel;

    @ApiModelProperty("当前审批角色的roleID")
    private Set<String> roles = new LinkedHashSet<>();

    @ApiModelProperty("历史审批角色的roleID")
    private Set<String> roleHistory = new LinkedHashSet<>();

    @ApiModelProperty("审批状态")
    private ApprovalStatus approvalStatus;

    @ApiModelProperty("发起人/申请人姓名")
    private String applyName;

    @ApiModelProperty("发起人/申请人")
    private Operator apply;

    @ApiModelProperty("发起人/申请人部门")
    private String applyDepart;

    @ApiModelProperty("申请类型")
    private ApplyType applyType;

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

    @ApiModelProperty("审批意见")
    private String approvedMemo;

    @ApiModelProperty("公共案件id")
    private String publicCaseId;

    @ApiModelProperty("审批组织机构")
    private Set<String> organizationList;

    @ApiModelProperty("流程配置")
    private ApproveFlowConfigModel configModel;

    @ApiModelProperty("最新欠款")
    private Double latestOverdueAmount;

    @ApiModelProperty("还款金额")
    private Double hasPayAmount;

    @ApiModelProperty("还款卡号")
    private  String backCardNo;

    @ApiModelProperty("还款类型")
    private PaymentType paymentType;

    @ApiModelProperty("还款记录Id")
    private String paymentRecordId;

    @ApiModelProperty("材料Id")
    private String fileId;

    @ApiModelProperty("材料名称")
    private String fileName;

    @ApiModelProperty("批注")
    private List<Comment> comments;

    @ApiModelProperty("补款金额")
    private Double supplementAmount;

    @ApiModelProperty("实际补款金额")
    private Double supplementRealAmount;

    @ApiModelProperty("实际补款时间")
    private Date supplementRealTime;

    @ApiModelProperty("减免金额")
    private Double derateAmount;

    @ApiModelProperty("实际减免金额")
    private Double derateRealAmount;

    @ApiModelProperty("申调资料类型")
    private ApplyFileContent applyContent;

    @ApiModelProperty("申调资料部门")
    private String applyFileDepartId;

    @ApiModelProperty("申调地区名称")
    private String applyFileDepartName;

    @ApiModelProperty("导出状态")
    private ExportState exportState;

    @ApiModelProperty("操作人")
    private String operator;

    @ApiModelProperty("操作时间")
    private Date operatorDate;

}
