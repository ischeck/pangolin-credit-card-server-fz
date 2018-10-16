package cn.fintecher.pangolin.common.model;

import cn.fintecher.pangolin.common.enums.RoleState;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * Created by ChenChang on 2018/6/7
 */
@Data
public class RoleModel {

    private String id;

    @ApiModelProperty(notes = "角色名称")
    private String name;

    @ApiModelProperty(notes = "角色状态 0：启用 1：停用")
    private RoleState state;

    @ApiModelProperty(notes = "描述")
    private String description;

}
