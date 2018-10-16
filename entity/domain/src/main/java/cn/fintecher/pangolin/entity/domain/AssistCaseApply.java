package cn.fintecher.pangolin.entity.domain;

import cn.fintecher.pangolin.common.enums.AssistApprovedResult;
import cn.fintecher.pangolin.common.enums.AssistApprovedStatus;
import cn.fintecher.pangolin.common.enums.AssistFlag;
import cn.fintecher.pangolin.common.model.ApproveFlowConfigModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @Author : huyanmin
 * @Description : 案件协催申请
 * @Date : 2018/7/10.
 */
@Data
@Document(indexName = "assist_case_apply", type = "assist_case_apply", shards = 1, replicas = 0)
@ApiModel(value = "AssistCaseApply", description = "案件协催申请")
public class AssistCaseApply {

    @Id
    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty("案件ID")
    private String caseId;

    @ApiModelProperty("客户姓名")
    private String personalName;

    @ApiModelProperty("催收对象名称")
    private String targetName;

    @ApiModelProperty("批次号")
    private String batchNumber;

    @ApiModelProperty("身份证号码")
    private String idCard;

    @ApiModelProperty("协催地址")
    private String addressDetail;

    @ApiModelProperty("地址类型")
    private String addressType;

    @ApiModelProperty("关系")
    private String relation;

    @ApiModelProperty("地址Id")
    private String personalAddressId;

    @ApiModelProperty("联系电话Id")
    private String personalContactId;

    @ApiModelProperty("协催方式(电话、外访、信函)")
    private AssistFlag assistFlag;

    @ApiModelProperty("当前审批级别")
    private Integer currentApprovalLevel;

    @ApiModelProperty("流程配置")
    private ApproveFlowConfigModel configFlowApproval;

    @ApiModelProperty("委托方名称")
    private String principalName;

    @ApiModelProperty("委托方名称")
    private String principalId;

    @ApiModelProperty("信函模板")
    private String letterTemp;

    @ApiModelProperty("申请人姓名")
    private String applyRealName;

    @ApiModelProperty("申请人")
    private String applyUserName;

    @ApiModelProperty("申请原因")
    private String applyReason;

    @ApiModelProperty("申请时间")
    private Date applyDate;

    @ApiModelProperty("申请部门Ids")
    private Set<String> applyDeptIds;

    @ApiModelProperty("审批部门Ids")
    private Set<String> approvedDeptIds;

    @ApiModelProperty("电话号码")
    private String assistMobile;

    @ApiModelProperty("协催审批角色Ids")
    private Set<String> roleIds = new LinkedHashSet<>();

    @ApiModelProperty("历史角色角色Ids")
    private Set<String> roleHistoryIds = new LinkedHashSet<>();

    @ApiModelProperty("协助审批部门Ids")
    private Set<String> assistDeptIds;

    @ApiModelProperty("审批状态")
    private AssistApprovedStatus approveStatus;

    @ApiModelProperty("审批结果")
    private AssistApprovedResult approveResult;

    @ApiModelProperty("审批人姓名")
    private String approveName;

    @ApiModelProperty("审批人")
    private String approveID;

    @ApiModelProperty("审批批时间")
    @Field(type = FieldType.Date)
    private Date approveTime;

    @ApiModelProperty("本地城市审批意见")
    private String approveMemo;

    @ApiModelProperty("操作人")
    private String operator;

    @ApiModelProperty("操作人")
    private String operatorName;

    @ApiModelProperty("操作时间")
    @Field(type = FieldType.Date)
    private Date operatorDate;

}
