package cn.fintecher.pangolin.service.dataimp.model;

import cn.fintecher.pangolin.common.annotation.ExcelAnno;
import cn.fintecher.pangolin.common.model.AssistManagementModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author:huyanmin
 * @Desc: 村委资料导出model
 * @Date:Create in 2018/8/31
 */
@Data
public class TownResourceModel extends AssistManagementModel{

    @ApiModelProperty(notes = "证件号码")
    @ExcelAnno(cellName = "证件号码", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String certificateNo;

    @ApiModelProperty(notes = "省份")
    @ExcelAnno(cellName = "省份", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String province;

    @ApiModelProperty("城市")
    @ExcelAnno(cellName = "城市", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String city;

    @ApiModelProperty("区/县")
    @ExcelAnno(cellName = "区/县", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String district;

    @ApiModelProperty(notes = "镇/乡")
    @ExcelAnno(cellName = "镇/乡", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String town;

    @ApiModelProperty(notes = "村/居委会")
    @ExcelAnno(cellName = "村/居委会", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String village;

    @ApiModelProperty(notes = "地区码")
    @ExcelAnno(cellName = "地区码", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String areaCode;

    @ApiModelProperty(notes = "查询人")
    @ExcelAnno(cellName = "查询人", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String searchPeople;

    @ApiModelProperty(notes = "联系人")
    @ExcelAnno(cellName = "联系人", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String contactName;

    @ApiModelProperty(notes = "职务")
    @ExcelAnno(cellName = "职务", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String duties;

    @ApiModelProperty(notes = "办公电话")
    @ExcelAnno(cellName = "办公电话", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private  String officePhone;

    @ApiModelProperty(notes = "手机")
    @ExcelAnno(cellName = "手机", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String mobile;

    @ApiModelProperty(notes = "家庭电话")
    @ExcelAnno(cellName = "家庭电话", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String homePhone;

    @ApiModelProperty(notes = "备注")
    @ExcelAnno(cellName = "备注", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String remark;
}
