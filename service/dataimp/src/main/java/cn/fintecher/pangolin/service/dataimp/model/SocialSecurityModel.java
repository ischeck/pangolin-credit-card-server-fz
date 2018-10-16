package cn.fintecher.pangolin.service.dataimp.model;

import cn.fintecher.pangolin.common.annotation.ExcelAnno;
import cn.fintecher.pangolin.common.model.AssistManagementModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author:huyanmin
 * @Desc: 社保资料导出model
 * @Date:Create in 2018/8/31
 */
@Data
public class SocialSecurityModel extends AssistManagementModel{

    @ApiModelProperty(notes = "身份证号")
    @ExcelAnno(cellName = "身份证号", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String certificateNo;

    @ApiModelProperty("银行")
    @ExcelAnno(cellName = "银行", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String principalName;

    @ApiModelProperty("申调地区")
    @ExcelAnno(cellName = "申调地区", fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private String applyFileDepartId;

    @ApiModelProperty(notes = "户籍地址")
    @ExcelAnno(cellName = "户籍地址", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String address;

    @ApiModelProperty(notes = "申调时间")
    @ExcelAnno(cellName = "申调时间", fieldDataType = ExcelAnno.FieldDataType.DATE)
    private Date applyDate;

    @ApiModelProperty(notes = "社保号")
    @ExcelAnno(cellName = "社保号", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String applyRemark;

    @ApiModelProperty(notes = "参保时间")
    @ExcelAnno(cellName = "参保时间", fieldDataType = ExcelAnno.FieldDataType.DATE)
    private Date socialSecurityTime;

    @ApiModelProperty(notes = "参保状态")
    @ExcelAnno(cellName = "参保状态", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String socialSecurityState;

    @ApiModelProperty(notes = "最近缴纳时间")
    @ExcelAnno(cellName = "最近缴纳时间", fieldDataType = ExcelAnno.FieldDataType.DATE)
    private Date latestPayTime;

    @ApiModelProperty(notes = "最近缴费基数")
    @ExcelAnno(cellName = "最近缴费基数", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String latestPayCount;

    @ApiModelProperty(notes = "工作单位")
    @ExcelAnno(cellName = "工作单位", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String workCompany;

    @ApiModelProperty(notes = "公司地址")
    @ExcelAnno(cellName = "公司地址", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String workAddress;

    @ApiModelProperty(notes = "公司电话")
    @ExcelAnno(cellName = "公司电话", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String workPhone;

    @ApiModelProperty(notes = "备注")
    @ExcelAnno(cellName = "备注", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String remark;
}
