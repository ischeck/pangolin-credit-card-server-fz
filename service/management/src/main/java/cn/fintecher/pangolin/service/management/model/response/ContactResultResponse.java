package cn.fintecher.pangolin.service.management.model.response;

import cn.fintecher.pangolin.common.enums.CollConfigType;
import cn.fintecher.pangolin.common.enums.CustConfigType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by BBG on 2018/8/1.
 */
@Data
public class ContactResultResponse implements Serializable{
    private String id;

    @ApiModelProperty("PID")
    private String pid;

    @ApiModelProperty("类型")
    private CollConfigType type;

    @ApiModelProperty("CODE")
    private String code;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("级别")
    private Integer level;

    @ApiModelProperty("委托方ID")
    private String principalId;

    @ApiModelProperty("委托方名称")
    private String principalName;
}
