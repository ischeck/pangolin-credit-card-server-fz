package cn.fintecher.pangolin.service.dataimp.model.response;

import cn.fintecher.pangolin.common.annotation.ExcelAnno;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 10:50 2018/7/31
 */
@Data
public class BaseCaseAllImportExcelTempResponse implements Serializable {

    @ApiModelProperty(notes = "批次")
    private String batchNumber;

    @ApiModelProperty(notes = "案件编号")
    private String caseNumber;

    @ApiModelProperty(notes = "委托方")
    private String principalName;

    @ApiModelProperty(notes = "接收方")
    private String receiveName;

    @ApiModelProperty(notes = "委案日期")
    private Date delegationDate;

    @ApiModelProperty(notes = "结案日期")
    private Date endCaseDate;

    @ApiModelProperty(notes = "委案城市")
    private String city;

    @ApiModelProperty(notes = "客户姓名")
    private String personalName;

    @ApiModelProperty(notes = "证件类型")
    private String  certificateType;

    @ApiModelProperty(notes = "证件号")
    private String certificateNo;

    @ApiModelProperty(notes = "帐号")
    private String account;

    @ApiModelProperty(notes = "卡号")
    private String cardNo1;

    @ApiModelProperty(notes = "逾期阶段")
    private String handsNumber;

    @ApiModelProperty(notes = "币种")
    private String currency;

    @ApiModelProperty(notes = "委案金额(人民币)")
    private Double overdueAmtTotal;

    @ApiModelProperty(notes = "委案金额(美元)")
    private Double overdueAmtTotalDollar;

    @ApiModelProperty(notes = "本金(人民币)")
    private Double capitalAmt ;

    @ApiModelProperty(notes = "本金(美元)")
    private Double capitalAmtDollar ;

    @ApiModelProperty(notes = "欠款(人民币)")
    private Double leftAmt ;

    @ApiModelProperty(notes = "欠款(美元)")
    private Double leftAmtDollar = 0.0;

    @ApiModelProperty(notes = "利息")
    private Double interestAmt ;

    @ApiModelProperty(notes = "滞纳金")
    private Double lateFee ;

    @ApiModelProperty(notes = "服务费")
    private Double serviceFee ;

    @ApiModelProperty(notes = "超限费")
    private Double overLimitFee ;

    @ApiModelProperty(notes = "违约金")
    private Double fineFee;

    @ApiModelProperty(notes = "逾期期数")
    private Integer overduePeriods;

    @ApiModelProperty(notes = "逾期天数")
    private Integer overdueDays;

}
