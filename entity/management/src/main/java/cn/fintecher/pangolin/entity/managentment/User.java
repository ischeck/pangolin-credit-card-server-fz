package cn.fintecher.pangolin.entity.managentment;

import cn.fintecher.pangolin.common.enums.Marital;

import cn.fintecher.pangolin.common.enums.Sex;
import cn.fintecher.pangolin.common.enums.UserState;
import cn.fintecher.pangolin.common.model.FileModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 人员花名册
 * Created by huyanmin on 2018/8/29.
 */
@Data
@Document
public class User implements Serializable {
    @Id
    private String id;

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

    @ApiModelProperty("离职时间")
    private Date leaveOfficeTime;

    @ApiModelProperty("合同签订日")
    private Date contractTime;

    @ApiModelProperty("合同到期日")
    private Date contractEndTime;

    @ApiModelProperty("姓名")
    private String fullName;

    @ApiModelProperty("所属机构")
    private String organization;

    @ApiModelProperty("工号")
    private String employeeNumber;

    @ApiModelProperty("电话号码")
    private String cellPhone;

    @ApiModelProperty("文件路径")
    private List<FileModel> fileContent = new ArrayList<>();

    @ApiModelProperty("状态")
    private UserState state;

    @ApiModelProperty("操作者")
    private String operator;

    @ApiModelProperty("操作时间")
    private Date operatorTime;

}
