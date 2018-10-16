package cn.fintecher.pangolin.service.domain.model.response;

import cn.fintecher.pangolin.common.enums.AssistFlag;
import cn.fintecher.pangolin.common.enums.CaseDataStatus;
import cn.fintecher.pangolin.common.enums.CaseLeaveFlag;
import cn.fintecher.pangolin.entity.domain.CardInformation;
import cn.fintecher.pangolin.entity.domain.Personal;
import cn.fintecher.pangolin.entity.managentment.Principal;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.*;

/**
 *
 * Created by huyanmin 2018/07/13
 *
 * */

@Data
public class CaseInfoSearchResponse {

    private String id;

    @ApiModelProperty(notes = "余额")
    private Double leftAmt;

    @ApiModelProperty(notes = "美元本金")
    private Double capitalAmtDollar;

    @ApiModelProperty(notes = "本金")
    private Double capitalAmt;

    @ApiModelProperty(notes = "利息")
    private Double interestAmt;

    @ApiModelProperty(notes = "美元余额")
    private Double leftAmtDollar;

    @ApiModelProperty(notes = "美元利息")
    private Double interestAmtDollar;

    @ApiModelProperty(notes = "更新日期")
    private Date latelyUpdateDate;

    @ApiModelProperty(notes = "还款总金额(人民币)")
    private Double payAmountTotal;

    @ApiModelProperty(notes = "还款总金额(美元)")
    private Double payAmountTotalDollar;

    @ApiModelProperty(notes = "委托方ID")
    private Principal principal;

    @ApiModelProperty(notes = "客户年龄")
    private Integer age;

    @ApiModelProperty(notes = "客户信息")
    private Personal personal;

    @ApiModelProperty(notes = "帐号")
    private String account;

    @ApiModelProperty(notes = "委案日期")
    private Date delegationDate;

    @ApiModelProperty(notes = "结案日期")
    private Date endCaseDate;

    @ApiModelProperty(notes = "协助标识")
    private AssistFlag assistFlag;

    @ApiModelProperty(notes = "距离退案天数")
    private Integer returnDays;

    @ApiModelProperty(notes = "逾期阶段")
    private String handsNumber;

    @ApiModelProperty(notes = "地区")
    private String city;

    @ApiModelProperty(notes = "最后还款日期")
    private Date latestPayDate;

    @ApiModelProperty(notes = "批次号")
    private String batchNumber;

    @ApiModelProperty(notes = "案件编号")
    private String caseNumber;

    @ApiModelProperty(notes = "入催日期")
    private Date remindersDate;

    @ApiModelProperty(notes = "卡信息")
    private Set<CardInformation> cardInformationSet;

    @ApiModelProperty(notes = "数据状态")
    private CaseDataStatus caseDataStatus;

    @ApiModelProperty(notes = "系统状态")
    private Set<String> caseStatus;

    @ApiModelProperty(notes = "逾期状态:M+")
    private String overdueStatus;

    @ApiModelProperty(notes = "手动催收")
    private Set<String> collectionStatus;

    @ApiModelProperty(notes = "留案标识")
    private CaseLeaveFlag leaveFlag=CaseLeaveFlag.NO_LEAVE;

    @ApiModelProperty(notes = "催计数")
    private Long collectionRecordCount = 0L;

    @ApiModelProperty(notes = "催计数总数")
    private Long collectionTotalRecordCount = 0L;

    @ApiModelProperty(notes = "导入时不匹配字段")
    private Map<String, String> remarkMap;
}
