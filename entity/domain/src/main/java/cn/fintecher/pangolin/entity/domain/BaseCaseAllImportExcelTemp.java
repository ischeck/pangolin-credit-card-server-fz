package cn.fintecher.pangolin.entity.domain;

import cn.fintecher.pangolin.common.enums.CaseDataStatus;
import cn.fintecher.pangolin.common.annotation.ExcelAnno;
import cn.fintecher.pangolin.common.model.RemarkModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @Author:peishouwen
 * @Desc: 接收Excel导入数据临时数据表
 * @Date:Create in 17:28 2018/7/25
 */
@Data
@Document(indexName = "base_case_all_import_excel_temp", type = "base_case_all_import_excel_temp", shards = 1, replicas = 0)
@ApiModel(value = "BaseCaseAllImportExcelTemp", description = "接收Excel导入数据临时数据表(全)")
public class BaseCaseAllImportExcelTemp  {

    @Id
    private String id;

    @ApiModelProperty(notes = "用于后续排序使用")
    private Long sequenceNo;

    @ApiModelProperty(notes = "主键")
    private String primaryKey;

    @ApiModelProperty(notes = "客户信息关联字段做Md5生成")
    private String relationPersonalId;

    @ApiModelProperty(notes = "批次号")
    private String batchNumber;

    @ApiModelProperty(notes = "批次")
    @ExcelAnno(cellName = "批次",fieldDataType = ExcelAnno.FieldDataType.STRING,fieldType = ExcelAnno.FieldType.CASE)
    private String batchNo;

    @ApiModelProperty(notes = "委托方")
    @ExcelAnno(cellName = "委托方",fieldDataType = ExcelAnno.FieldDataType.STRING,fieldType = ExcelAnno.FieldType.CASE)
    private String principalName;

    @ApiModelProperty(notes = "接收方")
    @ExcelAnno(cellName = "接收方",fieldDataType = ExcelAnno.FieldDataType.STRING,fieldType = ExcelAnno.FieldType.CASE)
    private String receiveName;

    @ApiModelProperty(notes = "委托类型")
    @ExcelAnno(cellName = "委托类型",fieldDataType = ExcelAnno.FieldDataType.STRING,fieldType = ExcelAnno.FieldType.CASE)
    private String principalType;

    @ApiModelProperty(notes = "委案日期")
    @ExcelAnno(cellName = "委案日期",fieldDataType = ExcelAnno.FieldDataType.DATE,fieldType = ExcelAnno.FieldType.CASE)
    @Field(type = FieldType.Date)
    private Date delegationDate;

    @ApiModelProperty(notes = "结案日期")
    @ExcelAnno(cellName = "结案日期",fieldDataType = ExcelAnno.FieldDataType.DATE,fieldType = ExcelAnno.FieldType.CASE)
    @Field(type = FieldType.Date)
    private Date endCaseDate;

    @ApiModelProperty(notes = "委案城市")
    @ExcelAnno(cellName = "委案城市",fieldDataType = ExcelAnno.FieldDataType.STRING,fieldType = ExcelAnno.FieldType.CASE)
    private String city;

    @ApiModelProperty(notes = "案件编号")
    @ExcelAnno(cellName = "案件编号",fieldDataType = ExcelAnno.FieldDataType.STRING,fieldType = ExcelAnno.FieldType.CASE)
    private String caseNumber;

    @ApiModelProperty(notes = "姓名")
    @ExcelAnno(cellName = "姓名",fieldDataType = ExcelAnno.FieldDataType.STRING,fieldType = ExcelAnno.FieldType.CASE)
    private String personalName;

    @ApiModelProperty(notes = "证件类型")
    @ExcelAnno(cellName = "证件类型",fieldDataType = ExcelAnno.FieldDataType.STRING,fieldType = ExcelAnno.FieldType.CASE)
    private String  certificateType;

    @ApiModelProperty(notes = "证件号")
    @ExcelAnno(cellName = "证件号",fieldDataType = ExcelAnno.FieldDataType.STRING,fieldType = ExcelAnno.FieldType.CASE)
    private String certificateNo;

    @ApiModelProperty(notes = "帐号")
    @ExcelAnno(cellName = "帐号",fieldDataType = ExcelAnno.FieldDataType.STRING,fieldType = ExcelAnno.FieldType.CASE)
    private String account;

    @ApiModelProperty(notes = "逾期阶段")
    @ExcelAnno(cellName = "逾期阶段",fieldDataType = ExcelAnno.FieldDataType.STRING,fieldType = ExcelAnno.FieldType.CASE)
    private String handsNumber;

    @ApiModelProperty(notes = "进入催收日期")
    @ExcelAnno(cellName = "进入催收日期",fieldDataType = ExcelAnno.FieldDataType.DATE,fieldType = ExcelAnno.FieldType.CASE)
    @Field(type = FieldType.Date)
    private Date remindersDate;

    @ApiModelProperty(notes = "币种")
    @ExcelAnno(cellName = "币种",fieldDataType = ExcelAnno.FieldDataType.STRING,fieldType = ExcelAnno.FieldType.CASE)
    private String currency;

    @ApiModelProperty(notes = "委案金额(人民币)")
    @ExcelAnno(cellName = "委案金额(人民币)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE,fieldType = ExcelAnno.FieldType.CASE)
    private Double overdueAmtTotal = 0.0;

    @ApiModelProperty(notes = "委案金额(美元)")
    @ExcelAnno(cellName = "委案金额(美元)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE,fieldType = ExcelAnno.FieldType.CASE)
    private Double overdueAmtTotalDollar = 0.0;

    @ApiModelProperty(notes = "本金(人民币)")
    @ExcelAnno(cellName = "本金",fieldDataType = ExcelAnno.FieldDataType.DOUBLE,fieldType = ExcelAnno.FieldType.CASE)
    private Double capitalAmt = 0.0;

    @ApiModelProperty(notes = "本金(美元)")
    @ExcelAnno(cellName = "本金美元",fieldDataType = ExcelAnno.FieldDataType.DOUBLE,fieldType = ExcelAnno.FieldType.CASE)
    private Double capitalAmtDollar = 0.0;

    @ApiModelProperty(notes = "欠款(人民币)")
    @ExcelAnno(cellName = "欠款人民币",fieldDataType = ExcelAnno.FieldDataType.DOUBLE,fieldType = ExcelAnno.FieldType.CASE)
    private Double leftAmt = 0.0;

    @ApiModelProperty(notes = "欠款(美元)")
    @ExcelAnno(cellName = "欠款美元",fieldDataType = ExcelAnno.FieldDataType.DOUBLE,fieldType = ExcelAnno.FieldType.CASE)
    private Double leftAmtDollar = 0.0;

    @ApiModelProperty(notes = "利息(人民币)")
    @ExcelAnno(cellName = "利息(人民币)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE,fieldType = ExcelAnno.FieldType.CASE)
    private Double interestAmt = 0.0;

    @ApiModelProperty(notes = "利息(美元)")
    @ExcelAnno(cellName = "利息(美元)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE,fieldType = ExcelAnno.FieldType.CASE)
    private Double interestAmtDollar = 0.0;

    @ApiModelProperty(notes = "滞纳金(人民币)")
    @ExcelAnno(cellName = "滞纳金(人民币)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE,fieldType = ExcelAnno.FieldType.CASE)
    private Double lateFee = 0.0;

    @ApiModelProperty(notes = "滞纳金(美元)")
    @ExcelAnno(cellName = "滞纳金(美元)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE,fieldType = ExcelAnno.FieldType.CASE)
    private Double lateFeeDollar = 0.0;

    @ApiModelProperty(notes = "服务费(人民币)")
    @ExcelAnno(cellName = "服务费(人民币)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE,fieldType = ExcelAnno.FieldType.CASE)
    private Double serviceFee = 0.0;

    @ApiModelProperty(notes = "服务费(美元)")
    @ExcelAnno(cellName = "服务费(美元)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE,fieldType = ExcelAnno.FieldType.CASE)
    private Double serviceFeeDollar = 0.0;

    @ApiModelProperty(notes = "超限费(人民币)")
    @ExcelAnno(cellName = "超限费(人民币)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE,fieldType = ExcelAnno.FieldType.CASE)
    private Double overLimitFee = 0.0;

    @ApiModelProperty(notes = "超限费(美元)")
    @ExcelAnno(cellName = "超限费(美元)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE,fieldType = ExcelAnno.FieldType.CASE)
    private Double overLimitFeeDollar = 0.0;

    @ApiModelProperty(notes = "违约金(人民币)")
    @ExcelAnno(cellName = "违约金(人民币)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE,fieldType = ExcelAnno.FieldType.CASE)
    private Double fineFee = 0.0;

    @ApiModelProperty(notes = "违约金(美元)")
    @ExcelAnno(cellName = "违约金(美元)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE,fieldType = ExcelAnno.FieldType.CASE)
    private Double fineFeeDollar = 0.0;

    @ApiModelProperty(notes = "逾期期数")
    @ExcelAnno(cellName = "逾期期数",fieldDataType = ExcelAnno.FieldDataType.INTEGER,fieldType = ExcelAnno.FieldType.CASE)
    private Integer overduePeriods;

    @ApiModelProperty(notes = "逾期天数")
    @ExcelAnno(cellName = "逾期天数",fieldDataType = ExcelAnno.FieldDataType.INTEGER,fieldType = ExcelAnno.FieldType.CASE)
    private Integer overdueDays;

    @ApiModelProperty(notes = "最后还款日期")
    @ExcelAnno(cellName = "最后还款日期",fieldDataType = ExcelAnno.FieldDataType.DATE,fieldType = ExcelAnno.FieldType.CASE)
    @Field(type = FieldType.Date)
    private Date latestPayDate;

    @ApiModelProperty(notes = "最后还款金额(人民币)")
    @ExcelAnno(cellName = "最后还款金额(人民币)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE,fieldType = ExcelAnno.FieldType.CASE)
    private Double latestPayAmt = 0.0;

    @ApiModelProperty(notes = "最后还款金额(美元)")
    @ExcelAnno(cellName = "最后还款金额(美元)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE,fieldType = ExcelAnno.FieldType.CASE)
    private Double latestPayAmtDollar = 0.0;

    @ApiModelProperty(notes = "最低还款金额(人民币)")
    @ExcelAnno(cellName = "最低还款金额(人民币)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE,fieldType = ExcelAnno.FieldType.CASE)
    private Double minPayAmt = 0.0;

    @ApiModelProperty(notes = "最低还款金额(美元)")
    @ExcelAnno(cellName = "最低还款金额(美元)",fieldDataType = ExcelAnno.FieldDataType.DOUBLE,fieldType = ExcelAnno.FieldType.CASE)
    private Double minPayAmtDollar = 0.0;

    @ApiModelProperty(notes = "委托阶段")
    @ExcelAnno(cellName = "委托阶段",fieldDataType = ExcelAnno.FieldDataType.STRING,fieldType = ExcelAnno.FieldType.CASE)
    private String overdueSection;

    @ApiModelProperty(notes = "卡号")
    @ExcelAnno(cellName = "卡号",fieldDataType = ExcelAnno.FieldDataType.STRING,fieldType = ExcelAnno.FieldType.CASE)
    private String cardNo;

    @ApiModelProperty(notes = "卡类型")
    @ExcelAnno(cellName = "卡类型",fieldDataType = ExcelAnno.FieldDataType.STRING,fieldType = ExcelAnno.FieldType.CASE)
    private String cardNoType;

    @ApiModelProperty(notes = "开户日期")
    @ExcelAnno(cellName = "开户日期",fieldDataType = ExcelAnno.FieldDataType.DATE,fieldType = ExcelAnno.FieldType.CASE)
    @Field(type = FieldType.Date)
    private Date openAccountDate;

    @ApiModelProperty(notes = "账单日")
    @ExcelAnno(cellName = "账单日",fieldDataType = ExcelAnno.FieldDataType.STRING,fieldType = ExcelAnno.FieldType.CASE)
    private String billDay;

    @ApiModelProperty(notes = "停卡日期")
    @ExcelAnno(cellName = "停卡日期",fieldDataType = ExcelAnno.FieldDataType.DATE,fieldType = ExcelAnno.FieldType.CASE)
    @Field(type = FieldType.Date)
    private Date stopAccountDate;

    @ApiModelProperty(notes = "最后消费日")
    @ExcelAnno(cellName = "最后消费日",fieldDataType = ExcelAnno.FieldDataType.DATE,fieldType = ExcelAnno.FieldType.CASE)
    @Field(type = FieldType.Date)
    private Date lastConsumptionDate;

    @ApiModelProperty(notes = "最后提现日期")
    @ExcelAnno(cellName = "最后提现日期",fieldDataType = ExcelAnno.FieldDataType.DATE,fieldType = ExcelAnno.FieldType.CASE)
    @Field(type = FieldType.Date)
    private Date lastPresentationDate;

    @ApiModelProperty(notes = "最后拖欠日期")
    @ExcelAnno(cellName = "最后拖欠日期",fieldDataType = ExcelAnno.FieldDataType.DATE,fieldType = ExcelAnno.FieldType.CASE)
    @Field(type = FieldType.Date)
    private Date lastDefaultDate;

    @ApiModelProperty(notes = "冻结日期")
    @ExcelAnno(cellName = "冻结日期",fieldDataType = ExcelAnno.FieldDataType.DATE,fieldType = ExcelAnno.FieldType.CASE)
    @Field(type = FieldType.Date)
    private Date freeDate;

    @ApiModelProperty(notes = "卡额度")
    @ExcelAnno(cellName = "卡额度",fieldDataType = ExcelAnno.FieldDataType.DOUBLE,fieldType = ExcelAnno.FieldType.CASE)
    private Double limitAmt = 0.0;

    @ApiModelProperty(notes = "导入时不匹配字段")
    @Field(type = FieldType.Object)
    private Set<RemarkModel> remarkMap;

    @ApiModelProperty(notes = "案件相关的卡信息")
    private Set<CardInformation> cardInformationSet;

    @ApiModelProperty(notes = "逾期阶段合并信息")
    private Set<String> handsNumberSet;

    @ApiModelProperty(notes = "sheet页总数")
    private Integer sheetTotals;

    @ApiModelProperty(notes = "委托方id标识后台记录用")
    private String principalId;


    @ApiModelProperty(notes = "数据状态")
    private CaseDataStatus caseDataStatus=CaseDataStatus.IN_POOL;

    @ApiModelProperty(notes = "停催时间")
    @Field(type = FieldType.Date)
    private Date stopTime;

    @ApiModelProperty(notes = "操作员")
    private String operator;

    @ApiModelProperty(notes = "操作时间")
    @Field(type = FieldType.Date)
    private Date operatorTime;

    @ApiModelProperty("删案日期")
    @Field(type = FieldType.Date)
    private Date deleteCaseDateEnd;

}
