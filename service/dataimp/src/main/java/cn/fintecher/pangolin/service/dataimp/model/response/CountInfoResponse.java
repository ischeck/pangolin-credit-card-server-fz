package cn.fintecher.pangolin.service.dataimp.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CountInfoResponse {
    @ApiModelProperty("委案金额")
    private Double totalOverdueAmt = 0.0;

    @ApiModelProperty("回款金额")
    private Double totalReturnAmt = 0.0;

    @ApiModelProperty("总户数")
    private long totalCaseNum;

    @ApiModelProperty("催收员数")
    private long collectorNum;

    @ApiModelProperty("人均回款")
    private Double perReturnAmt = 0.0;

    @ApiModelProperty("人均金额")
    private Double perOverdueAmt = 0.0;

    @ApiModelProperty("人均户数")
    private long perCaseNum;
}
