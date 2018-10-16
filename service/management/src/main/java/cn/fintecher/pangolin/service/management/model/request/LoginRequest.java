package cn.fintecher.pangolin.service.management.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by ChenChang on 2018/6/14.
 */
@Data
public class LoginRequest {
    @ApiModelProperty("用户名")
    @NotNull(message = "{username.is.required}")
    private String username;
    @ApiModelProperty("密码")
    @NotNull(message = "{password.is.required}")
    private String password;
}
