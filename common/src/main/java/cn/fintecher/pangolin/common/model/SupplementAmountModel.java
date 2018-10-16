package cn.fintecher.pangolin.common.model;

import cn.fintecher.pangolin.common.annotation.ExcelAnno;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Author:huyanmin
 * @Desc: 补款导出model
 * @Date:Create in 2018/8/31
 */
@Data
public class SupplementAmountModel {

    @ApiModelProperty(notes = "银行")
    @ExcelAnno(cellName = "银行", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String principalName;

    @ApiModelProperty(notes = "地区")
    @ExcelAnno(cellName = "地区", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String city;

    @ApiModelProperty(notes = "委案日期")
    @ExcelAnno(cellName = "委案日期", fieldDataType = ExcelAnno.FieldDataType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date delegationDate;

    @ApiModelProperty(notes = "手次")
    @ExcelAnno(cellName = "手次", fieldDataType = ExcelAnno.FieldDataType.INTEGER)
    private String handsNumber;

    @ApiModelProperty(notes = "客户姓名")
    @ExcelAnno(cellName = "姓名", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String personalName;

    @ApiModelProperty("委案金额")
    @ExcelAnno(cellName = "委外金额", fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private Double overdueAmtTotal;

    @ApiModelProperty(notes = "卡号")
    @ExcelAnno(cellName = "卡号", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String cardNo;

    @ApiModelProperty(notes = "证件号码")
    @ExcelAnno(cellName = "证件号码", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String certificateNo;

    @ApiModelProperty(notes = "公司补款")
    @ExcelAnno(cellName = "公司补款", fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private Double supplementRealAmount;

    @ApiModelProperty(notes = "还款总金额")
    @ExcelAnno(cellName = "还款总金额", fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private Double totalPaymentAmount;

    @ApiModelProperty(notes = "小组承担")
    @ExcelAnno(cellName = "小组承担", fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private Double groupSupplement;

    @ApiModelProperty(notes = "不列入计算激励奖金的回款金额")
    @ExcelAnno(cellName = "不列入计算激励奖金的回款金额", fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private Double paymentBack;

    @ApiModelProperty(notes = "补款日期")
    @ExcelAnno(cellName = "补款日期", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private Date supplementRealTime;

}
