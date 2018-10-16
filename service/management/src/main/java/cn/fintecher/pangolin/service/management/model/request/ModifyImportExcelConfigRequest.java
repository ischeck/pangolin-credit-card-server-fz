package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.enums.TemplateType;
import cn.fintecher.pangolin.entity.managentment.ImportExcelConfigItem;
import cn.fintecher.pangolin.entity.managentment.Principal;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 16:40 2018/7/25
 */
@Data
public class ModifyImportExcelConfigRequest implements Serializable{

    private String id;
    @ApiModelProperty("配置名称")
    @NotNull(message = "{importExcelConfigName.is.required}")
    private String name;

    @ApiModelProperty("模板类型")
    @NotNull(message = "{templateType.is.required}")
    private TemplateType templateType;

    @ApiModelProperty("委托方ID")
    @NotNull(message = "{principalId.is.required}")
    public String principalId;

    @ApiModelProperty("委托方名称")
    @NotNull(message = "{principalId.is.required}")
    public String principalName;

    @ApiModelProperty("表头开始行")
    @NotNull(message = "{titleStartRow.is.required}")
    private Integer  titleStartRow;

    @ApiModelProperty("表头开始列")
    @NotNull(message = "{titleStartCol.is.required}")
    private Integer  titleStartCol;

    @ApiModelProperty("数据开始行")
    @NotNull(message = "{dataStartRow.is.required}")
    private Integer  dataStartRow;

    @ApiModelProperty("数据开始列")
    @NotNull(message = "{dataStartCol.is.required}")
    private Integer  dataStartCol;

    @ApiModelProperty("sheet页总数")
    @NotNull(message = "{sheetTotals.is.required}")
    private Integer sheetTotals;

    @ApiModelProperty("配置项")
    @NotNull(message = "{importExcelConfigItem.is.required}")
    private List<ImportExcelConfigItem> items;
}
