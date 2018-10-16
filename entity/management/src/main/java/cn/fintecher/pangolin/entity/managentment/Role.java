package cn.fintecher.pangolin.entity.managentment;

import cn.fintecher.pangolin.common.enums.RoleState;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * Created by ChenChang on 2017/12/12.
 */
@Data
@Document
public class Role implements Serializable {
    @Id
    private String id;
    @ApiModelProperty(notes = "角色名称")
    private String name;

    @ApiModelProperty(notes = "角色状态 0：启用 1：停用")
    private RoleState state;

    @ApiModelProperty(notes = "描述")
    private String description;

    @ApiModelProperty(notes = "包含资源")
    private Set<String> resources;

    private Date createTime;

}
