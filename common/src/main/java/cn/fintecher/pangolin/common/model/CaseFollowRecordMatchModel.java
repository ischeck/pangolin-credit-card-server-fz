package cn.fintecher.pangolin.common.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author:huyanmin
 * @Desc: 催记字段字段属性
 * @Date:Create 2018/8/22
 */
@Data
public class CaseFollowRecordMatchModel implements Serializable{

    @ApiModelProperty("属性名")
    private String attribute;

    @ApiModelProperty("中文名")
    private String name;

    @ApiModelProperty("属性类型")
    private String propertyType;

    @ApiModelProperty("是否必输")
    private String isMustInput;
}
