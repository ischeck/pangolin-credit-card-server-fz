package cn.fintecher.pangolin.common.model;

import cn.fintecher.pangolin.common.annotation.ExcelAnno;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 用户导入
 * Created by huyanmin 2018/09/28
 */

@Data
public class UserImportModel {

    @ApiModelProperty("性别")
    @ExcelAnno(cellName = "性别", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String sex;

    @ApiModelProperty("籍贯")
    @ExcelAnno(cellName = "籍贯", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String register;

    @ApiModelProperty("婚姻状况")
    @ExcelAnno(cellName = "婚姻状况", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String marital;

    @ApiModelProperty("学历")
    @ExcelAnno(cellName = "学历", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String education;

    @ApiModelProperty("籍贯")
    @ExcelAnno(cellName = "籍贯", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String registerAddress;

    @ApiModelProperty("家庭地址")
    @ExcelAnno(cellName = "家庭地址", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String homeAddress;

    @ApiModelProperty("毕业学校")
    @ExcelAnno(cellName = "毕业学校", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String graduation;

    @ApiModelProperty("专业")
    @ExcelAnno(cellName = "专业", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String major;

    @ApiModelProperty("毕业时间")
    @ExcelAnno(cellName = "毕业时间", fieldDataType = ExcelAnno.FieldDataType.DATE)
    private Date graduationTime;

    @ApiModelProperty("催收经验")
    @ExcelAnno(cellName = "催收经验", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String experience;

    @ApiModelProperty("年龄")
    @ExcelAnno(cellName = "年龄", fieldDataType = ExcelAnno.FieldDataType.INTEGER)
    private Integer age;

    @ApiModelProperty("入职时间")
    @ExcelAnno(cellName = "入职时间", fieldDataType = ExcelAnno.FieldDataType.DATE)
    private Date createTime;

    @ApiModelProperty("离职时间")
    @ExcelAnno(cellName = "离职时间", fieldDataType = ExcelAnno.FieldDataType.DATE)
    private Date leaveOfficeTime;

    @ApiModelProperty("合同签订日")
    @ExcelAnno(cellName = "合同签订日", fieldDataType = ExcelAnno.FieldDataType.DATE)
    private Date contractTime;

    @ApiModelProperty("合同到期日")
    @ExcelAnno(cellName = "合同到期日", fieldDataType = ExcelAnno.FieldDataType.DATE)
    private Date contractEndTime;

    @ApiModelProperty("姓名")
    @ExcelAnno(cellName = "姓名", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String fullName;

    @ApiModelProperty("组织机构码/组织机构的ID")
    @ExcelAnno(cellName = "组织机构码", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String organizationId;

    @ApiModelProperty("员工工号")
    @ExcelAnno(cellName = "员工工号", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String employeeNumber;

    @ApiModelProperty("电话号码")
    @ExcelAnno(cellName = "电话号码", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String cellPhone;

    @ApiModelProperty("状态")
    @ExcelAnno(cellName = "状态", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String state;

}
