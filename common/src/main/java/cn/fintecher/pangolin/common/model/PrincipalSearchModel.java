package cn.fintecher.pangolin.common.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author : huyanmin
 * @Description : 委托方查询查询结果
 * @Date : 2018/8/2.
 */
@Data
public class PrincipalSearchModel {

    @ApiModelProperty("委托方Id")
    private String id;

    @ApiModelProperty("委托方Name")
    private String name;
}
