package cn.fintecher.pangolin.service.management.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description:
 * @Author: peishouwen
 * @Date Created in 2018/7/24 23:34
 */
@Data
public class ImportExcelConfigParseResponse implements Serializable {

    @ApiModelProperty("标题名称")
    private String titleName;

    @ApiModelProperty("sheet编码")
    private Integer sheetNum;

    @ApiModelProperty("对应列")
    private String col;

    @ApiModelProperty("备注")
    private String remark;

}
