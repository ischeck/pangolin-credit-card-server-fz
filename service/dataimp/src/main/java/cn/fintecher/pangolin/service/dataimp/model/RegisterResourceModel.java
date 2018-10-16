package cn.fintecher.pangolin.service.dataimp.model;

import cn.fintecher.pangolin.common.annotation.ExcelAnno;
import cn.fintecher.pangolin.common.model.AssistManagementModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author:huyanmin
 * @Desc: 户籍资料导出model
 * @Date:Create in 2018/8/31
 */
@Data
public class RegisterResourceModel extends AssistManagementModel{

    @ApiModelProperty(notes = "姓名")
    @ExcelAnno(cellName = "姓名", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String personalName;

    @ApiModelProperty(notes = "身份证号")
    @ExcelAnno(cellName = "身份证号", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String certificateNo;

    @ApiModelProperty("银行")
    @ExcelAnno(cellName = "银行", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String principalName;

    @ApiModelProperty("申调地区")
    @ExcelAnno(cellName = "申调地区", fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private String applyFileDepartId;

    @ApiModelProperty(notes = "户籍")
    @ExcelAnno(cellName = "户籍", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String address;

    @ApiModelProperty(notes = "户籍地区")
    @ExcelAnno(cellName = "户籍地区", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String addressCity;

    @ApiModelProperty(notes = "申调时间")
    @ExcelAnno(cellName = "申调时间", fieldDataType = ExcelAnno.FieldDataType.DATE)
    private Date applyDate;

    @ApiModelProperty(notes = "备注")
    @ExcelAnno(cellName = "备注", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String applyRemark;

    @ApiModelProperty(notes = "服务处所")
    @ExcelAnno(cellName = "服务处所", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String serviceHouse;

    @ApiModelProperty(notes = "联系方式")
    @ExcelAnno(cellName = "联系方式", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String contactWay;

    @ApiModelProperty(notes = "新地址")
    @ExcelAnno(cellName = "新地址", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private  String newAddress;

    @ApiModelProperty(notes = "曾用名")
    @ExcelAnno(cellName = "曾用名", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String hasUsedName;

}
