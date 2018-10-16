package cn.fintecher.pangolin.common.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @Author:huyanmin
 * @Desc:
 * @Date:Create in 2018/9/3
 */
@Data
public class ConfigFlowModel {

    @ApiModelProperty(notes = "角色ids")
    private Set<String> roleIds;

    @ApiModelProperty(notes = "部门Id")
    private String organization;

    @ApiModelProperty(notes = "机构数组")
    private List<String> organizationList;

}
