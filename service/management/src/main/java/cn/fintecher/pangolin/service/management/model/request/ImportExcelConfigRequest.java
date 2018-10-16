package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.enums.TemplateType;
import cn.fintecher.pangolin.entity.managentment.ImportExcelConfigItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: huyanminn
 * @Description: 模板配置请求模板
 * @Date 2018/7/3
 */
@Data
@ApiModel(value = "ImportExcelConfigRequest", description = "模板配置请求模板")
public class ImportExcelConfigRequest implements Serializable {

    @ApiModelProperty("模板名称")
    @NotNull(message = "{importExcelConfigName.is.required}")
    public String name;

    @ApiModelProperty("委托方ID")
    @NotNull(message = "{principalId.is.required}")
    public String principalId;

    @ApiModelProperty("委托方名称")
    @NotNull(message = "{principalName.is.required}")
    public String principalName;

    @ApiModelProperty("模板类型")
    private TemplateType templateType;

    @ApiModelProperty("表头开始行")
    @NotNull(message = "{titleStartRow.is.required}")
    private String  titleStartRow;

    @ApiModelProperty("表头开始列")
    @NotNull(message = "{titleStartCol.is.required}")
    private String  titleStartCol;

    @ApiModelProperty("数据开始行")
    @NotNull(message = "{dataStartRow.is.required}")
    private String  dataStartRow;

    @ApiModelProperty("数据开始列")
    @NotNull(message = "{dataStartCol.is.required}")
    private String  dataStartCol;

    @ApiModelProperty("sheet页总数")
    @NotNull(message = "{sheetTotals.is.required}")
    private Integer sheetTotals;

    @ApiModelProperty("配置项")
    @NotNull(message = "{importExcelConfigItem.is.required}")
    private List<ImportExcelConfigItem> items;
}
