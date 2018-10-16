package cn.fintecher.pangolin.service.dataimp.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StarInfoResponse {

    @ApiModelProperty("名字")
    private String name;

    @ApiModelProperty("金额")
    private Double amt = 0.0;

    @ApiModelProperty("月份")
    private String month;
}
