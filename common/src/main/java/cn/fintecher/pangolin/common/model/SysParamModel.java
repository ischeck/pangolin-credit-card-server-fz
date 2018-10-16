package cn.fintecher.pangolin.common.model;

import cn.fintecher.pangolin.common.enums.ManagementType;
import cn.fintecher.pangolin.common.enums.SysParamState;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author : huyanmin
 * @Description : 系统参数返回model
 * @Date : 2018/6/27.
 */
@Data
public class SysParamModel {

    @ApiModelProperty(notes = "参数名称")
    private String code;

    @ApiModelProperty(notes = "参数值")
    private String value;

    @ApiModelProperty(notes = "参数是否启用")
    private SysParamState state;

    @ApiModelProperty(notes = "修改标识")
    private ManagementType modifyFlag;

    @ApiModelProperty(notes = "参数说明")
    private String remark;

}
