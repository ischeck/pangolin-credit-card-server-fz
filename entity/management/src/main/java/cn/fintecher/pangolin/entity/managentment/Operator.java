package cn.fintecher.pangolin.entity.managentment;

import cn.fintecher.pangolin.common.enums.ManagementType;
import cn.fintecher.pangolin.common.enums.OperatorState;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 系统账号
 * Created by ChenChang on 2017/10/8.
 */
@Data
@Document
public class Operator implements Serializable {
    @Id
    private String id;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("所属角色")
    private List<String> role;

    @ApiModelProperty("最后一次登录时间")
    private Date lastLoginTime;

    @ApiModelProperty("创建时间")
    private Date createDateTime;

    @ApiModelProperty("头像")
    private String headPic;

    @ApiModelProperty("姓名")
    private String fullName;

    @ApiModelProperty("所属机构")
    private String organization;

    @ApiModelProperty("工号")
    private String employeeNumber;

    @ApiModelProperty("电话号码")
    private String cellPhone;

    @ApiModelProperty("绑定主叫号码")
    private String callPhone;

    @ApiModelProperty(notes = "密码的定时修改（比如3个月后提醒修改密码）")
    private Date passwordInvalidTime;

    @ApiModelProperty(notes = "是否是管理员 NO-否，YES-是")
    private ManagementType isManager;

    @ApiModelProperty("状态         DISABLED(\"禁用\"),  ENABLED(\"启用\");")
    private OperatorState state;

}
