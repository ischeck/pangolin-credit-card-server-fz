package cn.fintecher.pangolin.service.dataimp.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MonthScheduleResponse {

    @ApiModelProperty("委案金额")
    private Double overdueAmt = 0.0;

    @ApiModelProperty("回收金额")
    private Double returnAmt = 0.0;

    @ApiModelProperty("金额比例")
    private Double amtRate;

    @ApiModelProperty("委案数量")
    private Integer overdueNum = 0;

    @ApiModelProperty("回款数量")
    private Integer returnNum = 0;

    @ApiModelProperty("数量比例")
    private Double numRate;
}
