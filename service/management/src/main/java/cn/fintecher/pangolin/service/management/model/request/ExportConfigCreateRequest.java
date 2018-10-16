package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.enums.ExportType;
import cn.fintecher.pangolin.entity.managentment.ExportConfigItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @Description:
 * @Author: BBG
 * @Date Created in 2018/7/24 23:44
 */
@Data
@ApiModel(value = "新建导出模板", description = "新建导出模板")
public class ExportConfigCreateRequest implements Serializable {

    @ApiModelProperty("模板名称")
    @NotNull(message = "{ExportConfigName.is.required}")
    public String name;

    @ApiModelProperty("委托方ID")
    @NotNull(message = "{principalId.is.required}")
    public String principalId;

    @ApiModelProperty("类型")
    private ExportType exportType;

    @ApiModelProperty("委托方名称")
    @NotNull(message = "{principalName.is.required}")
    public String principalName;

    @ApiModelProperty("配置项")
    @NotNull(message = "{ExportConfigName.is.required}")
    private List<ExportConfigItem> items;
}
