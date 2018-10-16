package cn.fintecher.pangolin.common.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author:peishouwen
 * @Desc: Excel 单元格数据
 * @Date:Create in 13:41 2018/7/25
 */
@Data
public class CellDataModel {
    @ApiModelProperty(notes = "单元所在列")
    private String cellKey;
    @ApiModelProperty(notes = "单元所在列")
    private String cellValue;
}
