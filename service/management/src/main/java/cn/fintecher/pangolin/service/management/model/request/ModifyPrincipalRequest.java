package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.enums.PrincipalState;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 *
 * @Author huyanmin
 * @Date 2018/06/26
 * @Dessciption 委托方修改参数
 */
@Data
public class ModifyPrincipalRequest extends CreatePrincipalRequest {

    @ApiModelProperty("委托方ID")
    @NotNull(message = "{id.is.required}")
    private String id;

    @ApiModelProperty(notes = "状态")
    private PrincipalState state;

}
