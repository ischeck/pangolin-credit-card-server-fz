package cn.fintecher.pangolin.service.dataimp.model;

import cn.fintecher.pangolin.common.annotation.ExcelAnno;
import cn.fintecher.pangolin.common.model.RemarkModel;
import cn.fintecher.pangolin.entity.domain.CardInformation;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.Map;
import java.util.Set;

@Data
public class ExportCaseModel {

    @ApiModelProperty(notes = "委托方")
    @ExcelAnno(cellName = "委托方",fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String principalName;

    @ApiModelProperty(notes = "接收方")
    @ExcelAnno(cellName = "接收方",fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String receiveName;

    @ApiModelProperty(notes = "委托类型")
    @ExcelAnno(cellName = "委托类型",fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String principalType;

    @ApiModelProperty(notes = "委案日期")
    @ExcelAnno(cellName = "委案日期",fieldDataType = ExcelAnno.FieldDataType.DATE)
    private Date delegationDate;

    @ApiModelProperty(notes = "结案日期")
    @ExcelAnno(cellName = "结案日期",fieldDataType = ExcelAnno.FieldDataType.DATE)
    private Date endCaseDate;

    @ApiModelProperty(notes = "委案城市")
    @ExcelAnno(cellName = "委案城市",fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String city;

    @ApiModelProperty(notes = "案件编号")
    @ExcelAnno(cellName = "案件编号",fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String caseNumber;

    @ApiModelProperty(notes = "帐号")
    @ExcelAnno(cellName = "帐号",fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String account;

    @ApiModelProperty(notes = "逾期阶段")
    @ExcelAnno(cellName = "逾期阶段",fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String handsNumber;

    @ApiModelProperty(notes = "进入催收日期")
    @ExcelAnno(cellName = "进入催收日期",fieldDataType = ExcelAnno.FieldDataType.DATE)
    private Date remindersDate;

    @ApiModelProperty(notes = "币种")
    @ExcelAnno(cellName = "币种",fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String currency;

    @ApiModelProperty(notes = "委案金额(人民币)")
    @ExcelAnno(cellName = "委案金额(人民币)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private Double overdueAmtTotal = 0.0;

    @ApiModelProperty(notes = "委案金额(美元)")
    @ExcelAnno(cellName = "委案金额(美元)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private Double overdueAmtTotalDollar = 0.0;


    @ApiModelProperty(notes = "委托阶段")
    @ExcelAnno(cellName = "委托阶段",fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String overdueSection;

    @ApiModelProperty(notes = "卡号")
    @ExcelAnno(cellName = "卡号",fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String cardNo;

    @ApiModelProperty(notes = "卡类型")
    @ExcelAnno(cellName = "卡类型",fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String cardNo1Type;

    @ApiModelProperty(notes = "开户日期")
    @ExcelAnno(cellName = "开户日期",fieldDataType = ExcelAnno.FieldDataType.DATE)
    @Field(type = FieldType.Date)
    private Date openAccountDate;

    @ApiModelProperty(notes = "账单日")
    @ExcelAnno(cellName = "账单日",fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String billDay;

    @ApiModelProperty(notes = "逾期期数")
    @ExcelAnno(cellName = "账单日",fieldDataType = ExcelAnno.FieldDataType.STRING)
    private Integer overduePeriods;

    @ApiModelProperty(notes = "逾期天数")
    @ExcelAnno(cellName = "账单日",fieldDataType = ExcelAnno.FieldDataType.STRING)
    private Integer overdueDays;

    @ApiModelProperty(notes = "最后还款日期")
    @Field(type = FieldType.Date)
    @ExcelAnno(cellName = "最后还款日期",fieldDataType = ExcelAnno.FieldDataType.DATE)
    private Date latestPayDate;

    @ApiModelProperty(notes = "本金(人民币)")
    @ExcelAnno(cellName = "本金(人民币)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private Double capitalAmt = 0.0;

    @ApiModelProperty(notes = "本金(美元)")
    @ExcelAnno(cellName = "本金(美元)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private Double capitalAmtDollar = 0.0;

    @ApiModelProperty(notes = "欠款(人民币)")
    @ExcelAnno(cellName = "欠款(人民币)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private Double leftAmt = 0.0;

    @ApiModelProperty(notes = "欠款(美元)")
    @ExcelAnno(cellName = "欠款(美元)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private Double leftAmtDollar = 0.0;

    @ApiModelProperty(notes = "利息(人民币)")
    @ExcelAnno(cellName = "利息(人民币)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private Double interestAmt = 0.0;

    @ApiModelProperty(notes = "利息(美元)")
    @ExcelAnno(cellName = "利息(美元)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private Double interestAmtDollar = 0.0;

    @ApiModelProperty(notes = "滞纳金(人民币)")
    @ExcelAnno(cellName = "滞纳金(人民币)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private Double lateFee = 0.0;

    @ApiModelProperty(notes = "滞纳金(美元)")
    @ExcelAnno(cellName = "滞纳金(美元)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private Double lateFeeDollar = 0.0;

    @ApiModelProperty(notes = "服务费(人民币)")
    @ExcelAnno(cellName = "服务费(人民币)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private Double serviceFee = 0.0;

    @ApiModelProperty(notes = "服务费(美元)")
    @ExcelAnno(cellName = "服务费(美元)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private Double serviceFeeDollar = 0.0;

    @ApiModelProperty(notes = "超限费(人民币)")
    @ExcelAnno(cellName = "超限费(人民币)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private Double overLimitFee = 0.0;

    @ApiModelProperty(notes = "超限费(美元)")
    @ExcelAnno(cellName = "违约金(人民币)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private Double overLimitFeeDollar = 0.0;

    @ApiModelProperty(notes = "违约金(人民币)")
    @ExcelAnno(cellName = "违约金(人民币)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private Double fineFee = 0.0;

    @ApiModelProperty(notes = "违约金(美元)")
    @ExcelAnno(cellName = "违约金(美元)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private Double fineFeeDollar = 0.0;

    @ApiModelProperty(notes = "最低还款金额(人民币)")
    @ExcelAnno(cellName = "最低还款金额(人民币)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private Double minPayAmt = 0.0;

    @ApiModelProperty(notes = "最低还款金额(美元)")
    @ExcelAnno(cellName = "最低还款金额(美元)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private Double minPayAmtDollar = 0.0;

    @ApiModelProperty(notes = "最后还款金额(人民币)")
    @ExcelAnno(cellName = "最后还款金额(人民币)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private Double latestPayAmt = 0.0;

    @ApiModelProperty(notes = "最后还款金额(美元)")
    @ExcelAnno(cellName = "最后还款金额(美元)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private Double latestPayAmtDollar = 0.0;

    @ApiModelProperty(notes = "还款总金额(人民币)")
    @ExcelAnno(cellName = "还款总金额(人民币)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private Double payAmountTotal;

    @ApiModelProperty(notes = "还款总金额(美元)")
    @ExcelAnno(cellName = "还款总金额(美元)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private Double payAmountTotalDollar;

    @ApiModelProperty(notes = "停卡日期")
    @Field(type = FieldType.Date)
    @ExcelAnno(cellName = "停卡日期",fieldDataType = ExcelAnno.FieldDataType.DATE)
    private Date stopAccountDate;

    @ApiModelProperty(notes = "最后消费日")
    @Field(type = FieldType.Date)
    @ExcelAnno(cellName = "最后消费日",fieldDataType = ExcelAnno.FieldDataType.DATE)
    private Date lastConsumptionDate;

    @ApiModelProperty(notes = "最后提现日期")
    @Field(type = FieldType.Date)
    @ExcelAnno(cellName = "最后提现日期",fieldDataType = ExcelAnno.FieldDataType.DATE)

    private Date lastPresentationDate;

    @ApiModelProperty(notes = "最后拖欠日期")
    @Field(type = FieldType.Date)
    @ExcelAnno(cellName = "最后拖欠日期",fieldDataType = ExcelAnno.FieldDataType.DATE)
    private Date lastDefaultDate;

    @ApiModelProperty(notes = "冻结日期")
    @Field(type = FieldType.Date)
    @ExcelAnno(cellName = "冻结日期",fieldDataType = ExcelAnno.FieldDataType.DATE)
    private Date freeDate;

    @ApiModelProperty(notes = "卡额度")
    @ExcelAnno(cellName = "卡额度",fieldDataType = ExcelAnno.FieldDataType.DOUBLE)
    private Double limitAmt;

    @ApiModelProperty(notes = "更新日期(余额/对账)")
    @Field(type = FieldType.Date)
    @ExcelAnno(cellName = "更新日期(余额/对账)",fieldDataType = ExcelAnno.FieldDataType.DATE)
    private Date latelyUpdateDate;

    @ApiModelProperty(notes = "导入时不匹配字段")
//    @ExcelAnno(cellName = "导入时不匹配字段")
    private Set<RemarkModel> remarkMap;

    @ApiModelProperty("删案日期")
    @Field(type = FieldType.Date)
    private Date deleteCaseDateEnd;

    private Set<CardInformation> cardInformationSet;

    //客户信息
    @ApiModelProperty(notes = "姓名")
    @ExcelAnno(cellName = "姓名",fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String personalName;

    @ApiModelProperty(notes = "证件类型")
    @ExcelAnno(cellName = "证件类型",fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String  certificateType;

    @ApiModelProperty(notes = "证件号")
    @ExcelAnno(cellName = "证件号",fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String certificateNo;

    @ApiModelProperty(notes = "性别")
    @ExcelAnno(cellName = "性别", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String sex;

    @ApiModelProperty(notes = "出生年月")
    @ExcelAnno(cellName = "出生年月", fieldDataType = ExcelAnno.FieldDataType.DATE)
    private Date birthday;

    @ApiModelProperty(notes = "移动电话")
    @ExcelAnno(cellName = "移动电话", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String selfPhoneNo;

    @ApiModelProperty(notes = "住宅电话")
    @ExcelAnno(cellName = "住宅电话", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String homePhoneNo;

    @ApiModelProperty(notes = "单位电话")
    @ExcelAnno(cellName = "单位电话", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String employerPhoneNo;

    @ApiModelProperty(notes = "单位名称")
    @ExcelAnno(cellName = "单位名称", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String employerName;

    @ApiModelProperty(notes = "单位地址")
    @ExcelAnno(cellName = "单位地址", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String employerAddr;

    @ApiModelProperty(notes = "户籍地址")
    @ExcelAnno(cellName = "户籍地址", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String residenceAddr;

    @ApiModelProperty(notes = "住宅地址")
    @ExcelAnno(cellName = "住宅地址", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String homeAddr;

    @ApiModelProperty(notes = "邮件地址")
    @ExcelAnno(cellName = "邮件地址", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String emailAddr;

    @ApiModelProperty(notes = "邮寄地址")
    @ExcelAnno(cellName = "邮寄地址", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String billAddr;

    //联系人信息
    @ApiModelProperty(notes =  "姓名1")
    @ExcelAnno(cellName = "姓名1", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String name1;

    @ApiModelProperty(notes = "关系1")
    @ExcelAnno(cellName = "关系1", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String relation1;

    @ApiModelProperty(notes = "证件号1")
    @ExcelAnno(cellName = "证件号1", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String certificateNo1;

    @ApiModelProperty(notes = "单位名称1")
    @ExcelAnno(cellName = "单位名称1", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String employerName1;

    @ApiModelProperty(notes = "电话1")
    @ExcelAnno(cellName = "电话1", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String PersonalPerCall1;

    @ApiModelProperty(notes = "地址1")
    @ExcelAnno(cellName = "地址1", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String PersonalPerAddr1;

    @ApiModelProperty(notes = "备注1")
    @ExcelAnno(cellName = "备注1", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String remark1;

    @ApiModelProperty(notes =  "姓名2")
    @ExcelAnno(cellName = "姓名2", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String name2;

    @ApiModelProperty(notes = "关系2")
    @ExcelAnno(cellName = "关系2", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String relation2;

    @ApiModelProperty(notes = "证件号2")
    @ExcelAnno(cellName = "证件号2", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String certificateNo2;

    @ApiModelProperty(notes = "单位名称2")
    @ExcelAnno(cellName = "单位名称2", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String employerName2;

    @ApiModelProperty(notes = "电话2")
    @ExcelAnno(cellName = "电话2", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String PersonalPerCall2;

    @ApiModelProperty(notes = "地址2")
    @ExcelAnno(cellName = "地址2", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String PersonalPerAddr2;

    @ApiModelProperty(notes = "备注2")
    @ExcelAnno(cellName = "备注2", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String remark2;

    @ApiModelProperty(notes =  "姓名3")
    @ExcelAnno(cellName = "姓名3", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String name3;

    @ApiModelProperty(notes = "关系3")
    @ExcelAnno(cellName = "关系3", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String relation3;

    @ApiModelProperty(notes = "证件号3")
    @ExcelAnno(cellName = "证件号3", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String certificateNo3;

    @ApiModelProperty(notes = "单位名称3")
    @ExcelAnno(cellName = "单位名称3", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String employerName3;

    @ApiModelProperty(notes = "电话3")
    @ExcelAnno(cellName = "电话3", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String PersonalPerCall3;

    @ApiModelProperty(notes = "地址3")
    @ExcelAnno(cellName = "地址3", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String PersonalPerAddr3;

    @ApiModelProperty(notes = "备注3")
    @ExcelAnno(cellName = "备注3", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String remark3;

    @ApiModelProperty(notes =  "姓名4")
    @ExcelAnno(cellName = "姓名4", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String name4;

    @ApiModelProperty(notes = "关系4")
    @ExcelAnno(cellName = "关系4", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String relation4;

    @ApiModelProperty(notes = "证件号4")
    @ExcelAnno(cellName = "证件号4", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String certificateNo4;

    @ApiModelProperty(notes = "单位名称4")
    @ExcelAnno(cellName = "单位名称4", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String employerName4;

    @ApiModelProperty(notes = "电话4")
    @ExcelAnno(cellName = "电话4", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String PersonalPerCall4;

    @ApiModelProperty(notes = "地址4")
    @ExcelAnno(cellName = "地址4", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String PersonalPerAddr4;

    @ApiModelProperty(notes = "备注4")
    @ExcelAnno(cellName = "备注4", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String remark4;

    @ApiModelProperty(notes =  "姓名5")
    @ExcelAnno(cellName = "姓名5", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String name5;

    @ApiModelProperty(notes = "关系5")
    @ExcelAnno(cellName = "关系5", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String relation5;

    @ApiModelProperty(notes = "证件号5")
    @ExcelAnno(cellName = "证件号5", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String certificateNo5;

    @ApiModelProperty(notes = "单位名称5")
    @ExcelAnno(cellName = "单位名称5", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String employerName5;

    @ApiModelProperty(notes = "电话5")
    @ExcelAnno(cellName = "电话5", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String PersonalPerCall5;

    @ApiModelProperty(notes = "地址5")
    @ExcelAnno(cellName = "地址5", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String PersonalPerAddr5;

    @ApiModelProperty(notes = "备注5")
    @ExcelAnno(cellName = "备注5", fieldDataType = ExcelAnno.FieldDataType.STRING)
    private String remark5;
}
