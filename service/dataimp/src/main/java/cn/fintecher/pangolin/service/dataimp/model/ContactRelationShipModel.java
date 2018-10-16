package cn.fintecher.pangolin.service.dataimp.model;

import cn.fintecher.pangolin.common.annotation.ExcelAnno;
import cn.fintecher.pangolin.common.model.AssistManagementModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author:huyanmin
 * @Desc: 关联关系导出model
 * @Date:Create in 2018/8/31
 */
@Data
public class ContactRelationShipModel extends AssistManagementModel{

    @ApiModelProperty(notes = "姓名")
    @ExcelAnno(cellName = "姓名", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String personalName;

    @ApiModelProperty(notes = "身份证号")
    @ExcelAnno(cellName = "身份证号", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String certificateNo;

    @ApiModelProperty("关系")
    @ExcelAnno(cellName = "关系", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String relationShip;

    @ApiModelProperty("关系人姓名")
    @ExcelAnno(cellName = "关系人姓名", fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private String relationShipName;

    @ApiModelProperty(notes = "关系人身份证号")
    @ExcelAnno(cellName = "关系人身份证号", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String relationShipCertificateNo;

}
