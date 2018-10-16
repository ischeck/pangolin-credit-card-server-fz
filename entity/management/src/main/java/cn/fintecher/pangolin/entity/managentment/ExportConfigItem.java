package cn.fintecher.pangolin.entity.managentment;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ExportConfigItem {

    @ApiModelProperty("属性名")
    private String attribute;
    @ApiModelProperty("Excel模板title名字")
    private String titleName;
    @ApiModelProperty("系统内置字段中文名")
    private String name;
    @ApiModelProperty("来源(实体)")
    private String source;
}
