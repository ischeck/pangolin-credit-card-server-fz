package cn.fintecher.pangolin.service.domain.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class AppCaseInfoResponse {
    @ApiModelProperty(notes = "欠款金额")
    private Double leftAmt = 0.0;

    @ApiModelProperty(notes = "欠款金额(美元)")
    private Double leftAmtDollar = 0.0;

    @ApiModelProperty(notes = "本金")
    private Double capitalAmt = 0.0;

    @ApiModelProperty(notes = "本金(美元)")
    private Double capitalAmtDollar = 0.0;

    @ApiModelProperty(notes = "表内利息")
    private Double interestAmt = 0.0;

    @ApiModelProperty(notes = "表外利息")
    private Double outInterestAmt = 0.0;

    @ApiModelProperty(notes = "最后还款")
    private Double latestPayAmt = 0.0;

    @ApiModelProperty(notes = "最后还款(美元)")
    private Double latestPayAmtDollar = 0.0;

    @ApiModelProperty(notes = "还款金额")
    private Double payAmt = 0.0;

    @ApiModelProperty(notes = "更新日期")
    private Date latelyUpdateDate;
}
