package cn.fintecher.pangolin.service.dataimp.model;

import cn.fintecher.pangolin.common.annotation.ExcelAnno;
import cn.fintecher.pangolin.common.model.AssistManagementModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author:huyanmin
 * @Desc: 户籍备注导出model
 * @Date:Create in 2018/8/31
 */
@Data
public class RegisterRemarkModel extends AssistManagementModel {

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

    @ApiModelProperty(notes = "申调时间")
    @ExcelAnno(cellName = "申调时间", fieldDataType = ExcelAnno.FieldDataType.DATE)
    private Date applyDate;

    @ApiModelProperty(notes = "户籍地址")
    @ExcelAnno(cellName = "户籍地址", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String address;

    @ApiModelProperty(notes = "备注")
    @ExcelAnno(cellName = "备注", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String applyRemark;
}
