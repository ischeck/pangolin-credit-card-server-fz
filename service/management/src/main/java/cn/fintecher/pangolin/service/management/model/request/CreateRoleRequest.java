package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.enums.RoleState;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Created by ChenChang on 2018/6/15.
 */
@Data
public class CreateRoleRequest {
    @ApiModelProperty(notes = "角色名称")
    @NotNull(message = "{name.is.required}")
    private String name;

    @ApiModelProperty(notes = "角色状态 0：启用 1：停用")
    private RoleState state;

    @ApiModelProperty(notes = "描述")
    private String description;

    @ApiModelProperty(notes = "包含资源")
    private Set<String> resources;
}
