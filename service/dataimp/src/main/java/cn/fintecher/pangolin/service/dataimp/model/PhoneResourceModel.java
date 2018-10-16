package cn.fintecher.pangolin.service.dataimp.model;

import cn.fintecher.pangolin.common.annotation.ExcelAnno;
import cn.fintecher.pangolin.common.model.AssistManagementModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author:huyanmin
 * @Desc: 通讯资料导出model
 * @Date:Create in 2018/8/31
 */
@Data
public class PhoneResourceModel extends AssistManagementModel{

    @ApiModelProperty(notes = "客户姓名")
    @ExcelAnno(cellName = "客户姓名", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String personalName;

    @ApiModelProperty(notes = "证件号码")
    @ExcelAnno(cellName = "证件号码", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String certificateNo;

    @ApiModelProperty("银行")
    @ExcelAnno(cellName = "银行", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String principalName;

    @ApiModelProperty("申调地区")
    @ExcelAnno(cellName = "申调地区", fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private String applyFileDepartId;

    @ApiModelProperty("座机")
    @ExcelAnno(cellName = "座机", fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private String phone;

    @ApiModelProperty("手机")
    @ExcelAnno(cellName = "手机", fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private String mobile;

    @ApiModelProperty("地址")
    @ExcelAnno(cellName = "地址", fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private String address;

    @ApiModelProperty(notes = "申调时间")
    @ExcelAnno(cellName = "申调时间", fieldDataType = ExcelAnno.FieldDataType.DATE)
    private Date applyDate;

    @ApiModelProperty(notes = "备注")
    @ExcelAnno(cellName = "备注", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String applyRemark;

    @ApiModelProperty(notes = "电话类型")
    @ExcelAnno(cellName = "电话类型", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String phoneType;
}
