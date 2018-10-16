package cn.fintecher.pangolin.entity.managentment;

import cn.fintecher.pangolin.common.enums.ManagementType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 案件导入配置项目
 * Created by ChenChang on 2017/12/23.
 */
@Data
public class ImportExcelConfigItem {

    @ApiModelProperty("属性名")
    private String attribute;
    @ApiModelProperty("Excel模板title名字")
    private String titleName;
    @ApiModelProperty("系统内置字段中文名")
    private String name;
    @ApiModelProperty("对应列")
    private String col;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("sheet编号")
    private Integer sheetNum;
    @ApiModelProperty("主键标识")
    private boolean isKeyFlag;
    @ApiModelProperty("sheet页关联属性")
    private boolean isRelationFlag;
    @ApiModelProperty("属性类型")
    private String propertyType;

    @ApiModelProperty("是否隐藏")
    private ManagementType hideFlag=ManagementType.NO;

    @ApiModelProperty("允许为空")
    private ManagementType blankFlag=ManagementType.YES;

    @ApiModelProperty("字段排序")
    private Integer sort=new Integer(0);
}
