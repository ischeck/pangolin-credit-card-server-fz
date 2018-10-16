package cn.fintecher.pangolin.service.management.model.response;

import cn.fintecher.pangolin.common.enums.TemplateType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 16:08 2018/7/31
 */
@Data
public class TemplateDataSelect implements Serializable {
    @ApiModelProperty("Excel 模板ID")
    private String id;

    @ApiModelProperty("模板名称")
    public String name;

    @ApiModelProperty("模板类型")
    private TemplateType templateType;
}
