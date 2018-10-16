package cn.fintecher.pangolin.service.dataimp.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CollStateResponse {
    @ApiModelProperty("状态")
    private String state;

    @ApiModelProperty("金额")
    private Double amt;

    @ApiModelProperty("数量")
    private long num;
}
