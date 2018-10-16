package cn.fintecher.pangolin.common.model;

import cn.fintecher.pangolin.common.enums.ManagementType;
import cn.fintecher.pangolin.common.enums.OperatorState;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by ChenChang on 2017/12/20.
 */
@Data
public class OperatorModel implements Serializable {

    private String id;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("所属角色")
    private List<String> role;

    @ApiModelProperty("最后一次登录时间")
    private Date lastLoginTime;

    @ApiModelProperty("头像")
    private String headPic;

    @ApiModelProperty("姓名")
    private String fullName;

    @ApiModelProperty("所属机构")
    private String organization;

    @ApiModelProperty(notes = "部门名称")
    private String detaptName;

    @ApiModelProperty("工号")
    private String employeeNumber;

    @ApiModelProperty("电话号码")
    private String cellPhone;

    @ApiModelProperty("创建时间")
    private Date createDateTime;

    @ApiModelProperty("绑定主叫号码")
    private String callPhone;

    @ApiModelProperty(notes = "是否是管理员 NO-否，YES-是")
    private ManagementType isManager;

    @ApiModelProperty("状态 DISABLED(\"禁用\"),  ENABLED(\"启用\");")
    private OperatorState state;

    private List<ResourceModel> menu;

    private Set<String> resource ;

    private List<PrincipalSearchModel> principalSearchModels;
}
