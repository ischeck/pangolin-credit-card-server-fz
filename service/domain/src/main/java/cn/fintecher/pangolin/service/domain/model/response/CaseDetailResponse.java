package cn.fintecher.pangolin.service.domain.model.response;

import cn.fintecher.pangolin.common.enums.CaseLeaveFlag;
import cn.fintecher.pangolin.entity.domain.CardInformation;
import cn.fintecher.pangolin.entity.domain.Personal;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 *
 * Created by huyanmin 2018/07/13
 *
 * */

@Data
public class CaseDetailResponse {

    private String id;

    @ApiModelProperty(notes = "批次号")
    private String batchNumber;

    @ApiModelProperty(notes = "案件编号")
    private String caseNumber;

    @ApiModelProperty(notes = "手数")
    private String handsNumber;

    @ApiModelProperty(notes = "客户信息")
    private Personal personal;

    @ApiModelProperty(notes = "账单日")
    private Integer billDay;

    @ApiModelProperty(notes = "入催日期")
    @Field(type = FieldType.Date)
    private Date remindersDate;

    @ApiModelProperty(notes = "人民币清算层帐号")
    private String clearAccountRMB;

    @ApiModelProperty(notes = "美元清算层帐号")
    private String clearAccountDollar;

    @ApiModelProperty(notes = "案件相关的卡信息")
    private Set<CardInformation> cardInformationSet;

    @ApiModelProperty(notes = "币种")
    private String currency;

    @ApiModelProperty(notes = "地区")
    private String city;

    @ApiModelProperty(notes = "还款状态")
    private String payStatus;

    @ApiModelProperty(notes = "委案总金额")
    @Field(type = FieldType.Double)
    private Double overdueAmtTotal;

    @ApiModelProperty(notes = "美元委案总金额")
    private Double overdueAmtTotalDollar;

    @ApiModelProperty(notes = "本金")
    @Field(type = FieldType.Double)
    private Double capitalAmt;

    @ApiModelProperty(notes = "本金美元")
    private Double capitalAmtDollar;

    @ApiModelProperty(notes = "余额")
    private Double leftAmt;

    @ApiModelProperty(notes = "美元余额")
    private Double leftAmtDollar;

    @ApiModelProperty(notes = "最低还款金额")
    private Double minPayAmt;

    @ApiModelProperty(notes = "美元最低还款金额")
    private Double minPayAmtDollar;

    @ApiModelProperty(notes = "最近还款金额")
    private Double latestPayAmt;

    @ApiModelProperty(notes = "最近还款金额(美元)")
    private Double latestPayAmtDollar;

    @ApiModelProperty(notes = "最后一次还款日（还款金额和余额）")
    @Field(type = FieldType.Date)
    private Date latestPayDate;

    @ApiModelProperty(notes = "更新日期(余额)")
    @Field(type = FieldType.Date)
    private Date latelyUpdateDate;

    @ApiModelProperty(notes = "表内利息")
    private Double interestAmt;

    @ApiModelProperty(notes = "表外利息")
    private Double outInterestAmt;

    @ApiModelProperty(notes = "滞纳金")
    private Double lateFee;

    @ApiModelProperty(notes = "服务费")
    private Double serviceFee;

    @ApiModelProperty(notes = "超限费")
    private Double overLimitFee;

    @ApiModelProperty(notes = "罚息")
    private Double fineFee;

    @ApiModelProperty(notes = "逾期期数")
    private Integer overduePeriods;

    @ApiModelProperty(notes = "逾期天数")
    private Integer overdueDays;

    @ApiModelProperty(notes = "逾期期限段")
    private String overdueSection;

    @ApiModelProperty(notes = "逾期状态:M+")
    private String overdueStatus;

    @ApiModelProperty(notes = "委案日期")
    @Field(type = FieldType.Date)
    private Date delegationDate;

    @ApiModelProperty(notes = "结案日期")
    @Field(type = FieldType.Date)
    private Date endCaseDate;

    @ApiModelProperty(notes = "系统自动设置状态")
    private Set<String> caseStatus;

    @ApiModelProperty(notes = "手动状态")
    private Set<String> collectionStatus;

    @ApiModelProperty(notes = "留案标识")
    private CaseLeaveFlag leaveFlag;

    @ApiModelProperty(notes = "留案原因")
    private String leaveReason;

    @ApiModelProperty(notes = "联络结果")
    private String contactResult;

    @ApiModelProperty(notes = "导入时不匹配字段")
    private Map<String, String> remarkMap;
}
