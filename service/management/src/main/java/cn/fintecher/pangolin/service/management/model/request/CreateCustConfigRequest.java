package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.enums.CustConfigType;
import cn.fintecher.pangolin.common.enums.ManagementType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by BBG on 2018/8/1.
 */
@Data
public class CreateCustConfigRequest {
    private String id;

    @ApiModelProperty("CODE")
    private String code;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("类型 手工 MANUAL, 系统 SYS")
    private CustConfigType custConfigType;

    @ApiModelProperty("颜色")
    private String color;

    @ApiModelProperty("委托方ID")
    private String principalId;

    @ApiModelProperty("委托方名称")
    private String principalName;

    @ApiModelProperty("是否重点")
    private ManagementType isMajor;
}
