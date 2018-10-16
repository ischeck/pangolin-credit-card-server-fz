
package cn.fintecher.pangolin.entity.domain;

import cn.fintecher.pangolin.common.enums.*;
import cn.fintecher.pangolin.common.model.RemarkModel;
import cn.fintecher.pangolin.entity.managentment.Operator;
import cn.fintecher.pangolin.entity.managentment.Principal;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @Author:peishouwen
 * @Desc: 案件基本信息
 * @Date:Create in 15:36 2018/7/20
 */
@Data
@Document(indexName = "base_case", type = "base_case", shards = 1, replicas = 0)
@ApiModel(value = "BaseCase", description = "案件基本信息")
public class BaseCase {
    @Id
    @ApiModelProperty(notes = "特定字段做Md5生成")
    private String id;

    @ApiModelProperty(notes = "主键")
    private String primaryKey;

    @ApiModelProperty(notes = "批次号")
    private String batchNumber;

    @ApiModelProperty(notes = "批次")
    private String batchNo;

    @ApiModelProperty(notes = "委托方信息")
    private Principal principal;

    @ApiModelProperty(notes = "委托类型")
    private String principalType;

    @ApiModelProperty(notes = "委案日期")
    @Field(type = FieldType.Date)
    private Date delegationDate;

    @ApiModelProperty(notes = "结案日期")
    @Field(type = FieldType.Date)
    private Date endCaseDate;

    @ApiModelProperty(notes = "委案城市")
    private String city;

    @ApiModelProperty(notes = "案件编号")
    private String caseNumber;

    @ApiModelProperty(notes = "客户信息")
    private Personal personal;

    @ApiModelProperty(notes = "帐号")
    private String account;

    @ApiModelProperty(notes = "逾期阶段")
    private String handsNumber;

    @ApiModelProperty(notes = "进入催收日期")
    @Field(type = FieldType.Date)
    private Date remindersDate;

    @ApiModelProperty(notes = "流入日期")
    @Field(type = FieldType.Date)
    private Date followInTime;

    @ApiModelProperty(notes = "币种")
    private String currency;

    /****金额字段都是各个银行卡的金额合并****/
    @ApiModelProperty(notes = "委案金额(人民币)")
    @Field(type = FieldType.Double)
    private Double overdueAmtTotal=0.0;

    @ApiModelProperty(notes = "委案总金额(美元)")
    private Double overdueAmtTotalDollar=0.0;

    @ApiModelProperty(notes = "本金(人民币)")
    @Field(type = FieldType.Double)
    private Double capitalAmt=0.0;

    @ApiModelProperty(notes = "本金(美元)")
    private Double capitalAmtDollar=0.0;

    @ApiModelProperty(notes = "欠款(人民币)")
    private Double leftAmt=0.0;

    @ApiModelProperty(notes = "欠款(美元)")
    private Double leftAmtDollar=0.0;

    @ApiModelProperty(notes = "利息(人民币)")
    private Double interestAmt=0.0;

    @ApiModelProperty(notes = "利息(美元)")
    private Double interestAmtDollar = 0.0;

    @ApiModelProperty(notes = "滞纳金(人民币)")
    private Double lateFee=0.0;

    @ApiModelProperty(notes = "滞纳金(美元)")
    private Double lateFeeDollar = 0.0;

    @ApiModelProperty(notes = "服务费(人民币)")
    private Double serviceFee=0.0;

    @ApiModelProperty(notes = "服务费(美元)")
    private Double serviceFeeDollar = 0.0;

    @ApiModelProperty(notes = "超限费(人民币)")
    private Double overLimitFee=0.0;

    @ApiModelProperty(notes = "超限费(美元)")
    private Double overLimitFeeDollar = 0.0;

    @ApiModelProperty(notes = "违约金(人民币)")
    private Double fineFee=0.0;

    @ApiModelProperty(notes = "违约金(美元)")
    private Double fineFeeDollar = 0.0;

    @ApiModelProperty(notes = "委托阶段")
    private String overdueSection;

    @ApiModelProperty(notes = "案件相关的卡信息")
    private Set<CardInformation> cardInformationSet;

    @ApiModelProperty(notes = "更新日期(余额/对账)")
    @Field(type = FieldType.Date)
    private Date latelyUpdateDate;

    @ApiModelProperty(notes = "还款总金额(人民币)")
    private Double payAmountTotal;

    @ApiModelProperty(notes = "还款总金额(美元)")
    private Double payAmountTotalDollar;

    @ApiModelProperty(notes = "导入时不匹配字段")
    @Field(type = FieldType.Object)
    private Set<RemarkModel> remarkMap;

    @ApiModelProperty(notes = "系统自动设置状态")
    private Set<String> caseStatus;

    @ApiModelProperty(notes = "手动状态")
    private Set<String> collectionStatus;

    @ApiModelProperty(notes = "颜色")
    private String color;

    @ApiModelProperty(notes = "催计数")
    @Field(type = FieldType.Integer)
    private Integer collectionRecordCount=0;

    @ApiModelProperty(notes = "催计数总数")
    @Field(type = FieldType.Integer)
    private Integer collectionTotalRecordCount=0;

    @ApiModelProperty(notes = "跟进日期")
    @Field(type = FieldType.Date)
    private Date followTime;

    @ApiModelProperty(notes = "留案标识")
    private CaseLeaveFlag leaveFlag=CaseLeaveFlag.NO_LEAVE;

    @ApiModelProperty(notes = "协催标识")
    private AssistFlag assistFlag = AssistFlag.NO_ASSIST;

    @ApiModelProperty(notes = "数据状态")
    private CaseDataStatus caseDataStatus = CaseDataStatus.IN_POOL;

    @ApiModelProperty(notes = "下发标识")
    private CaseIssuedFlag issuedFlag;

    @ApiModelProperty(notes = "联络结果")
    private String contactResult;

    @ApiModelProperty(notes = "停催时间")
    @Field(type = FieldType.Date)
    private Date stopTime;

    @ApiModelProperty(notes = "催收员")
    private Operator currentCollector;

    @ApiModelProperty(notes = "上一个催收员")
    private Operator latelyCollector;

    @ApiModelProperty(notes = "部门ID用于权限判断")
    private Set<String> departments;

    @ApiModelProperty(notes = "当前部门ID")
    private String detaptId;

    @ApiModelProperty(notes = "当前部门名称")
    private String detaptName;

    @ApiModelProperty(notes = "操作员")
    private String operator;

    @ApiModelProperty(notes = "操作时间")
    @Field(type = FieldType.Date)
    private Date operatorTime;

    @ApiModelProperty("删案日期")
    @Field(type = FieldType.Date)
    private Date deleteCaseDateEnd;

    @ApiModelProperty("流转日期")
    @Field(type = FieldType.Date)
    private Date transferDate;

    @ApiModelProperty("是否重点跟进")
    private ManagementType isMajor = ManagementType.NO;
}
