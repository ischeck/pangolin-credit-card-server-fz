package cn.fintecher.pangolin.service.dataimp.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AppHomePageResponse {

    @ApiModelProperty("待催收金额")
    private Double waitCollAmt;

    @ApiModelProperty("外访数")
    private Integer visitNum;

    @ApiModelProperty("协催数")
    private Integer assistNum;

    @ApiModelProperty("待外访数")
    private Integer waitVisitNum;

    @ApiModelProperty("已外访数")
    private Integer hasVisitNum;

    @ApiModelProperty("待协催数")
    private Integer waitAssistNum;

    @ApiModelProperty("已协催数")
    private Integer hasAssistNum;
}
