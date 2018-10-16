package cn.fintecher.pangolin.service.dataimp.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AssistCaseResponse {

    @ApiModelProperty("姓名")
    private String personalName;

    @ApiModelProperty("证件号")
    private String idCard;

    @ApiModelProperty("协催地址")
    private String addressDetail;
}
