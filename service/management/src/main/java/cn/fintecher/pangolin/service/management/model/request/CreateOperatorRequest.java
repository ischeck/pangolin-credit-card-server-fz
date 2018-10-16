package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.enums.ManagementType;
import cn.fintecher.pangolin.common.enums.OperatorState;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by ChenChang on 2018/6/8
 */
@Data
public class CreateOperatorRequest {
    @ApiModelProperty("用户名")
    @NotNull(message = "{username.is.required}")
    private String username;
    @ApiModelProperty("所属组织")
    @NotNull(message = "{org.is.required}")
    private String organization;
    @ApiModelProperty("姓名")
    @NotNull(message = "{fullName.is.required}")
    private String fullName;
    @ApiModelProperty("工号")
    private String employeeNumber;
    @ApiModelProperty("电话号码")
    private String cellPhone;
    @ApiModelProperty(notes = "是否是管理员 NO-否，YES-是")
    private ManagementType isManager;
    @ApiModelProperty("状态         DISABLED(\"禁用\"),  ENABLED(\"启用\");")
    private OperatorState state;

}
