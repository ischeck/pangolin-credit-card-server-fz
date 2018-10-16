package cn.fintecher.pangolin.common.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author:peishouwen
 * @Desc: 案件信息字段属性
 * @Date:Create in 9:29 2018/7/26
 */
@Data
public class CaseInfoPropertyResponse implements Serializable{

    @ApiModelProperty("属性名")
    private String attribute;

    @ApiModelProperty("中文名")
    private String name;

    @ApiModelProperty("属性类型")
    private String propertyType;
}
