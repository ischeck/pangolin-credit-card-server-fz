package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.enums.SysParamState;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author huyanmin
 * @Date 2018/06/27
 * @Dessciption 系统参数修改请求
 */
@Data
public class ModifySystemParamRequest {

    @ApiModelProperty("参数表ID")
    @NotNull(message = "{id.is.required}")
    private String id;

    @ApiModelProperty("参数表")
    @NotNull(message = "{name.is.required}")
    private String code;

    @ApiModelProperty(notes = "参数值")
    private String value;

    @ApiModelProperty(notes = "参数是否启用")
    private SysParamState state;
}
