package cn.fintecher.pangolin.entity.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * Created by huyanmin on 2017/7/12.
 */

@Data
@ApiModel(value = "cardInformation", description = "卡信息")
public class CardInformation {

    @ApiModelProperty(notes = "卡号")
    private String cardNo;

    @ApiModelProperty(notes = "卡类型")
    private String cardNoType;

    @ApiModelProperty(notes = "开户日期")
    @Field(type = FieldType.Date)
    private Date openAccountDate;

    @ApiModelProperty(notes = "账单日")
    private String billDay;

    @ApiModelProperty(notes = "逾期期数")
    private Integer overduePeriods;

    @ApiModelProperty(notes = "逾期天数")
    private Integer overdueDays;

    @ApiModelProperty(notes = "最后还款日期")
    @Field(type = FieldType.Date)
    private Date latestPayDate;

    @ApiModelProperty(notes = "本金(人民币)")
    private Double capitalAmt = 0.0;

    @ApiModelProperty(notes = "本金(美元)")
    private Double capitalAmtDollar = 0.0;

    @ApiModelProperty(notes = "欠款(人民币)")
    private Double leftAmt = 0.0;

    @ApiModelProperty(notes = "欠款(美元)")
    private Double leftAmtDollar = 0.0;

    @ApiModelProperty(notes = "利息(人民币)")
    private Double interestAmt = 0.0;

    @ApiModelProperty(notes = "利息(美元)")
    private Double interestAmtDollar = 0.0;

    @ApiModelProperty(notes = "滞纳金(人民币)")
    private Double lateFee = 0.0;

    @ApiModelProperty(notes = "滞纳金(美元)")
    private Double lateFeeDollar = 0.0;

    @ApiModelProperty(notes = "服务费(人民币)")
    private Double serviceFee = 0.0;

    @ApiModelProperty(notes = "服务费(美元)")
    private Double serviceFeeDollar = 0.0;

    @ApiModelProperty(notes = "超限费(人民币)")
    private Double overLimitFee = 0.0;

    @ApiModelProperty(notes = "超限费(美元)")
    private Double overLimitFeeDollar = 0.0;

    @ApiModelProperty(notes = "违约金(人民币)")
    private Double fineFee = 0.0;

    @ApiModelProperty(notes = "违约金(美元)")
    private Double fineFeeDollar = 0.0;

    @ApiModelProperty(notes = "最低还款金额(人民币)")
    private Double minPayAmt = 0.0;

    @ApiModelProperty(notes = "最低还款金额(美元)")
    private Double minPayAmtDollar = 0.0;

    @ApiModelProperty(notes = "最后还款金额(人民币)")
    private Double latestPayAmt = 0.0;

    @ApiModelProperty(notes = "最后还款金额(美元)")
    private Double latestPayAmtDollar = 0.0;

    @ApiModelProperty(notes = "还款总金额(人民币)")
    private Double payAmountTotal;

    @ApiModelProperty(notes = "还款总金额(美元)")
    private Double payAmountTotalDollar;

    @ApiModelProperty(notes = "停卡日期")
    @Field(type = FieldType.Date)
    private Date stopAccountDate;

    @ApiModelProperty(notes = "最后消费日")
    @Field(type = FieldType.Date)
    private Date lastConsumptionDate;

    @ApiModelProperty(notes = "最后提现日期")
    @Field(type = FieldType.Date)
    private Date lastPresentationDate;

    @ApiModelProperty(notes = "最后拖欠日期")
    @Field(type = FieldType.Date)
    private Date lastDefaultDate;

    @ApiModelProperty(notes = "冻结日期")
    @Field(type = FieldType.Date)
    private Date freeDate;

    @ApiModelProperty(notes = "卡额度")
    private Double limitAmt;

    @ApiModelProperty(notes = "更新日期(余额/对账)")
    @Field(type = FieldType.Date)
    private Date latelyUpdateDate;
}
