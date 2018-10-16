package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.enums.ConfigState;
import cn.fintecher.pangolin.common.enums.ManagementType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by BBG on 2018/8/1.
 */
@Data
public class CreateContactResultRequest {

    @ApiModelProperty("PID")
    private String pid;

    @ApiModelProperty("CODE")
    private String code;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("是否可扩展")
    private ManagementType isExtension;

    @ApiModelProperty("委托方ID")
    private String principalId;

    @ApiModelProperty("委托方名称")
    private String principalName;

    @ApiModelProperty("属性名")
    private String attribute;

    @ApiModelProperty("属性类型")
    private String propertyType;

    @ApiModelProperty("是否必输")
    private String isMustInput;

    @ApiModelProperty("状态")
    private ConfigState configState;
}
