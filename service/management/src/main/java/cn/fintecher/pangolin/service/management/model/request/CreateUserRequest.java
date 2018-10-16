package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.enums.Marital;
import cn.fintecher.pangolin.common.enums.OperatorState;
import cn.fintecher.pangolin.common.enums.Sex;
import cn.fintecher.pangolin.common.enums.UserState;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * Created by huyanmin on 2018/8/29
 */
@Data
public class CreateUserRequest {

    @ApiModelProperty("性别")
    private Sex sex;

    @ApiModelProperty("籍贯")
    private String register;

    @ApiModelProperty("婚姻状况")
    private Marital marital;

    @ApiModelProperty("学历")
    private String education;

    @ApiModelProperty("户籍地址")
    private String registerAddress;

    @ApiModelProperty("家庭地址")
    private String homeAddress;

    @ApiModelProperty("毕业学校")
    private String graduation;

    @ApiModelProperty("专业")
    private String major;

    @ApiModelProperty("毕业时间")
    private Date graduationTime;

    @ApiModelProperty("催收经验")
    private String experience;

    @ApiModelProperty("年龄")
    private Integer age;

    @ApiModelProperty("入职时间")
    private Date createTime;

    @ApiModelProperty("合同签订日")
    private Date contractTime;

    @ApiModelProperty("合同到期日")
    private Date contractEndTime;

    @ApiModelProperty("姓名")
    @NotNull(message = "{fullName.is.required}")
    private String fullName;

    @ApiModelProperty("所属机构")
    @NotNull(message = "{org.is.required}")
    private String organization;

    @ApiModelProperty("所属组织")
    @NotNull(message = "{employeeNumber.is.required}")
    private String employeeNumber;

    @ApiModelProperty("电话号码")
    private String cellPhone;

    @ApiModelProperty("状态")
    private UserState state;
}
