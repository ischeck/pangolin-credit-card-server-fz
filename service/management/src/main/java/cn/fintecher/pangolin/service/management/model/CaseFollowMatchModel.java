package cn.fintecher.pangolin.service.management.model;

import cn.fintecher.pangolin.common.annotation.ExcelAnno;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @Author:huyanmin
 * @Desc:
 * @Date:Create in 2018/8/21
 */
@Data
public class CaseFollowMatchModel {

    @Id
    @ApiModelProperty(notes = "特定字段做Md5生成")
    private String id;

    @ApiModelProperty(notes = "联络对象姓名")
    @ExcelAnno(cellName = "联络对象姓名", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.INPUT, fieldInput = ExcelAnno.FieldInput.YES)
    private String targetName;

    @ApiModelProperty(notes = "联络对象")
    @ExcelAnno(cellName = "联络对象", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.SELECT, fieldInput = ExcelAnno.FieldInput.YES)
    private String target;

    @ApiModelProperty(notes = "催收日期")
    @ExcelAnno(cellName = "催收日期", fieldDataType = ExcelAnno.FieldDataType.DATE, fieldType = ExcelAnno.FieldType.DATEPICKER, fieldInput = ExcelAnno.FieldInput.YES)
    @Field(type = FieldType.Date)
    private Date followTime;

    @ApiModelProperty(notes = "行动代码/联络结果/电催摘要/催收代码")
    @ExcelAnno(cellName = "行动代码/联络结果", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.SELECT, fieldInput = ExcelAnno.FieldInput.YES)
    private String contactResult;

    @ApiModelProperty(notes = "外访摘要")
    @ExcelAnno(cellName = "外访摘要", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.SELECT, fieldInput = ExcelAnno.FieldInput.YES)
    private String collectionOutResult;

    @ApiModelProperty(notes = "催收状态")
    @ExcelAnno(cellName = "催收状态", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.SELECT, fieldInput = ExcelAnno.FieldInput.YES)
    private String collectionStatus;

    @ApiModelProperty(notes = "催收方式/催收措施")
    @ExcelAnno(cellName = "催收方式/催收措施", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.SELECT, fieldInput = ExcelAnno.FieldInput.YES)
    private String collectionType;

    @ApiModelProperty(notes = "电话类型/联络类型/联系类型")
    @ExcelAnno(cellName = "电话类型/联络类型", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.SELECT, fieldInput = ExcelAnno.FieldInput.YES)
    private String phoneType;

    @ApiModelProperty(notes = "电话号码/(号码/地址)/(电话/地址)")
    @ExcelAnno(cellName = "电话号码(电话/地址)", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.INPUT, fieldInput = ExcelAnno.FieldInput.YES)
    private String contactPhone;

    @ApiModelProperty(notes = "号码状态")
    @ExcelAnno(cellName = "号码状态", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.SELECT, fieldInput = ExcelAnno.FieldInput.YES)
    private String contactState;

    @ApiModelProperty(notes = "催收记录")
    @ExcelAnno(cellName = "催收记录", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.TEXTAREA)
    private String content;

    @ApiModelProperty(notes = "快捷录入")
    @ExcelAnno(cellName = "快捷录入", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.SELECT)
    private String quickRecord;

    @ApiModelProperty(notes = "承诺还款金额")
    @ExcelAnno(cellName = "承诺还款金额", fieldDataType = ExcelAnno.FieldDataType.DOUBLE, fieldType = ExcelAnno.FieldType.INPUT, fieldInput = ExcelAnno.FieldInput.YES)
    private Double promiseAmt;

    @ApiModelProperty(notes = "承诺还款日期")
    @ExcelAnno(cellName = "承诺还款日期", fieldDataType = ExcelAnno.FieldDataType.DATE, fieldType = ExcelAnno.FieldType.DATEPICKER, fieldInput = ExcelAnno.FieldInput.YES)
    @Field(type = FieldType.Date)
    private Date promiseDate;

    @ApiModelProperty(notes = "已还款金额")
    @ExcelAnno(cellName = "已还款金额", fieldDataType = ExcelAnno.FieldDataType.DOUBLE, fieldType = ExcelAnno.FieldType.INPUT, fieldInput = ExcelAnno.FieldInput.YES)
    private Double hasPaymentAmt;

    @ApiModelProperty(notes = "已还款日期")
    @ExcelAnno(cellName = "已还款日期", fieldDataType = ExcelAnno.FieldDataType.DATE, fieldType = ExcelAnno.FieldType.DATEPICKER, fieldInput = ExcelAnno.FieldInput.YES)
    @Field(type = FieldType.Date)
    private Date hasPaymentDate;

    @ApiModelProperty(notes = "是否提醒")
    @ExcelAnno(cellName = "是否提醒", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.RADIO)
    private String follNextFlag;

    @ApiModelProperty(notes = "提醒时间")
    @ExcelAnno(cellName = "提醒时间", fieldDataType = ExcelAnno.FieldDataType.DATE, fieldType = ExcelAnno.FieldType.DATEPICKER)
    @Field(type = FieldType.Date)
    private Date follNextDate;

    @ApiModelProperty(notes = "标红处理")
    @ExcelAnno(cellName = "标红处理", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.RADIO)
    private String redRemark;

    @ApiModelProperty(notes = "跟进备注")
    @ExcelAnno(cellName = "跟进备注", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.TEXTAREA)
    private String remark;

    @ApiModelProperty(notes = "要点标记")
    @ExcelAnno(cellName = "标红处理", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.SELECT)
    private String importRemark;

    @ApiModelProperty(notes = "信息更新")
    @ExcelAnno(cellName = "信息更新", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.INPUT)
    private String informationUpdate;

    @ApiModelProperty(notes = "地址类型")
    @ExcelAnno(cellName = "地址类型", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.SELECT, fieldInput = ExcelAnno.FieldInput.YES)
    private String addrType;

    @ApiModelProperty(notes = "地址状态")
    @ExcelAnno(cellName = "地址状态", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.SELECT, fieldInput = ExcelAnno.FieldInput.YES)
    private String addrStatus;

    @ApiModelProperty(notes = "详细地址")
    @ExcelAnno(cellName = "详细地址", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.TEXTAREA)
    private String detail;

}
