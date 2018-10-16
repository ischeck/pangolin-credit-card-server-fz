package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.enums.ManagementType;
import cn.fintecher.pangolin.common.enums.OtherTemplateType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ModifyTemplateRequest {
    @NotNull(message = "id.is.required")
    private String id;

    @ApiModelProperty("模板类别")
    @NotNull(message = "templateType.is.required")
    private OtherTemplateType type;

    @NotNull(message = "templateName.is.required")
    @ApiModelProperty("模板名称")
    private String templateName;

    @NotNull(message = "principal.is.required")
    @ApiModelProperty("委托方ID")
    private String principalId;

    @ApiModelProperty("委托方名称")
    private String principalName;

    @ApiModelProperty("是否默认")
    private ManagementType isDefault;

    @ApiModelProperty("是否启用")
    private ManagementType isEnabled;

    @NotNull(message = "content.is.required")
    @ApiModelProperty("内容")
    private String content;

}
