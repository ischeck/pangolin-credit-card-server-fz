package cn.fintecher.pangolin.service.dataimp.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StockResponse {

    @ApiModelProperty("委托方")
    private String principalName;

    @ApiModelProperty("委案金额")
    private Double overdueAmt = 0.0;

    @ApiModelProperty("回收金额")
    private Double returnAmt = 0.0;

    @ApiModelProperty("比例")
    private Double rate;

    @ApiModelProperty("总户数")
    private long totalNum = 0;

    @ApiModelProperty("在催人员")
    private long collectorNum = 0;
}
