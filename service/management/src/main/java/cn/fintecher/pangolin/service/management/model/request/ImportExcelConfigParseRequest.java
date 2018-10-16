package cn.fintecher.pangolin.service.management.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Description:
 * @Author: peishouwen
 * @Date Created in 2018/7/24 23:44
 */
@Data
@ApiModel(value = "解析配置模板头信息", description = "解析配置模板头信息")
public class ImportExcelConfigParseRequest implements Serializable {

    @ApiModelProperty("表头开始行")
    @NotNull(message = "{titleStartRow.is.required}")
    private Integer titleStartRow;

    @ApiModelProperty("表头开始列")
    @NotNull(message = "{titleStartCol.is.required}")
    private Integer titleStartCol;

    @ApiModelProperty("sheet页总数")
    @NotNull(message = "{sheetTotals.is.required}")
    private Integer sheetTotals;

    @ApiModelProperty("模板文件")
    @NotNull(message = "{file.is.required}")
    private String fileId;
}
