package cn.fintecher.pangolin.common.model;

import cn.fintecher.pangolin.common.annotation.ExcelAnno;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author:huyanmin
 * @Desc: 申请资料导出model
 * @Date:Create in 2018/8/31
 */
@Data
public class ApplyMaterialModel {

    @ApiModelProperty(notes = "客户姓名")
    @ExcelAnno(cellName = "客户姓名", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String personalName;

    @ApiModelProperty(notes = "证件号码")
    @ExcelAnno(cellName = "证件号码", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String certificateNo;

    @ApiModelProperty(notes = "申调资料类型")
    @ExcelAnno(cellName = "申调资料类型", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String applyContent;

    @ApiModelProperty(notes = "申调地区")
    @ExcelAnno(cellName = "申调地区", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String applyFileDepartName;

}
