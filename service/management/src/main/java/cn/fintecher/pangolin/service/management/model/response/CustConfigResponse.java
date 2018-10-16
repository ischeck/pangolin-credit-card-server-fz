package cn.fintecher.pangolin.service.management.model.response;

import cn.fintecher.pangolin.common.enums.CustConfigType;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by BBG on 2018/8/1.
 */
@Data
public class CustConfigResponse implements Serializable{
    private String id;

    @ApiModelProperty("CODE")
    private String code;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("类型")
    private CustConfigType custConfigType;

    @ApiModelProperty("颜色")
    private String color;

    @ApiModelProperty("委托方ID")
    private String principalId;

    @ApiModelProperty("委托方名称")
    private String principalName;
}
