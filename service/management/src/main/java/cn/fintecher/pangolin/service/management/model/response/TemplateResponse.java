package cn.fintecher.pangolin.service.management.model.response;

import cn.fintecher.pangolin.common.enums.ManagementType;
import cn.fintecher.pangolin.common.enums.OtherTemplateType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "模板查询结果", description = "模板查询结果")
public class TemplateResponse {
    private String id;

    @ApiModelProperty("模板类别")
    private OtherTemplateType type;

    @ApiModelProperty("模板名称")
    private String templateName;

    @ApiModelProperty("委托方ID")
    private String principalId;

    @ApiModelProperty("委托方名称")
    private String principalName;

    @ApiModelProperty("是否默认")
    private ManagementType isDefault;

    @ApiModelProperty("是否启用")
    private ManagementType isEnabled;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("操作人名字")
    private String operatorName;

    @ApiModelProperty("操作时间")
    private Date operatorTime;
}
