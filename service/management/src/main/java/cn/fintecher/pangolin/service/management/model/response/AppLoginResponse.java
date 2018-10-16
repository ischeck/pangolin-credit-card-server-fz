package cn.fintecher.pangolin.service.management.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AppLoginResponse {
    @ApiModelProperty("TOKEN")
    private String token;

    @ApiModelProperty("用户名称")
    private String fullName;

    @ApiModelProperty("用户ID")
    private String operatorId;

    @ApiModelProperty("头像")
    private String headPic;
}