package cn.fintecher.pangolin.service.management.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 *
 * @Author huyanmin
 * @Date 2018/06/26
 * @Dessciption 委托方创建参数
 */
@Data
public class CreatePrincipalRequest {

    @ApiModelProperty("委托方名称")
    @NotNull(message = "{name.is.required}")
    private String principalName;

    @ApiModelProperty(notes = "手机号")
    private String phone;

    @ApiModelProperty(notes = "备注")
    private String remark;

}
