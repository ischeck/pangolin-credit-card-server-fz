package cn.fintecher.pangolin.entity.domain;

import cn.fintecher.pangolin.common.annotation.ExcelAnno;
import cn.fintecher.pangolin.common.enums.CaseDataStatus;
import cn.fintecher.pangolin.common.model.RemarkModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.Set;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 15:08 2018/9/27
 */
@Data
@Document(indexName = "merge_data_model", type = "merge_data_model", shards = 1, replicas = 0)
@ApiModel(value = "MergeDataModel", description = "数据合并信息记录")
public class MergeDataModel {

    @Id
    @ApiModelProperty(notes = "ID")
    private String id;

    @ApiModelProperty(notes = "导入记录ID")
    private String recordId;

    @ApiModelProperty(notes = "用于后续排序使用")
    private Long sequenceNo;

    @ApiModelProperty(notes = "主键")
    private String primaryKey;

    @ApiModelProperty(notes = "客户信息关联字段做Md5生成")
    private String relationPersonalId;

    @ApiModelProperty(notes = "批次号")
    private String batchNumber;

    @ApiModelProperty(notes = "批次")
    private String batchNo;

    @ApiModelProperty(notes = "委托方")
    private String principalName;

    @ApiModelProperty(notes = "接收方")
    private String receiveName;

    @ApiModelProperty(notes = "委托类型")
    private String principalType;

    @ApiModelProperty(notes = "委案日期")
    @Field(type = FieldType.Date)
    private Date delegationDate;

    @ApiModelProperty(notes = "结案日期")
    @Field(type = FieldType.Date)
    private Date endCaseDate;

    @ApiModelProperty(notes = "委案城市")
    private String city;

    @ApiModelProperty(notes = "案件编号")
    private String caseNumber;

    @ApiModelProperty(notes = "姓名")
    private String personalName;

    @ApiModelProperty(notes = "证件类型")
    private String  certificateType;

    @ApiModelProperty(notes = "证件号")
    private String certificateNo;

    @ApiModelProperty(notes = "帐号")
    private String account;

    @ApiModelProperty(notes = "逾期阶段")
    private String handsNumber;

    @ApiModelProperty(notes = "账单日")
    private String billDay;

    @ApiModelProperty(notes = "进入催收日期")
    @Field(type = FieldType.Date)
    private Date remindersDate;

    @ApiModelProperty(notes = "币种")
    private String currency;

    @ApiModelProperty(notes = "委案金额(人民币)")
    private Double overdueAmtTotal = 0.0;

    @ApiModelProperty(notes = "委案金额(美元)")
    private Double overdueAmtTotalDollar = 0.0;

    @ApiModelProperty(notes = "本金(人民币)")
    private Double capitalAmt = 0.0;

    @ApiModelProperty(notes = "本金(美元)")
    private Double capitalAmtDollar = 0.0;

    @ApiModelProperty(notes = "欠款(人民币)")
    private Double leftAmt = 0.0;

    @ApiModelProperty(notes = "欠款(美元)")
    private Double leftAmtDollar = 0.0;

    @ApiModelProperty(notes = "利息(人民币)")
    private Double interestAmt = 0.0;

    @ApiModelProperty(notes = "利息(美元)")
    private Double interestAmtDollar = 0.0;

    @ApiModelProperty(notes = "滞纳金(人民币)")
    private Double lateFee = 0.0;

    @ApiModelProperty(notes = "滞纳金(美元)")
    private Double lateFeeDollar = 0.0;

    @ApiModelProperty(notes = "服务费(人民币)")
    private Double serviceFee = 0.0;

    @ApiModelProperty(notes = "服务费(美元)")
    private Double serviceFeeDollar = 0.0;

    @ApiModelProperty(notes = "超限费(人民币)")
    private Double overLimitFee = 0.0;

    @ApiModelProperty(notes = "超限费(美元)")
    private Double overLimitFeeDollar = 0.0;

    @ApiModelProperty(notes = "违约金(人民币)")
    private Double fineFee = 0.0;

    @ApiModelProperty(notes = "违约金(美元)")
    private Double fineFeeDollar = 0.0;

    @ApiModelProperty(notes = "逾期期数")
    private Integer overduePeriods;

    @ApiModelProperty(notes = "逾期天数")
    private Integer overdueDays;

    @ApiModelProperty(notes = "最后还款日期")
    @Field(type = FieldType.Date)
    private Date latestPayDate;

    @ApiModelProperty(notes = "最后还款金额")
    private Double latestPayAmt = 0.0;

    @ApiModelProperty(notes = "最低还款金额(人民币)")
    private Double minPayAmt = 0.0;

    @ApiModelProperty(notes = "最低还款金额(美元)")
    private Double minPayAmtDollar = 0.0;

    @ApiModelProperty(notes = "委托阶段")
    private String overdueSection;

    @ApiModelProperty(notes = "卡号")
    private String cardNo1;

    @ApiModelProperty(notes = "卡类型")
    private String cardNo1Type1;

    @ApiModelProperty(notes = "开户日期")
    @Field(type = FieldType.Date)
    private Date openAccountDate1;

    @ApiModelProperty(notes = "还款日")
    private String repayDay1;

    @ApiModelProperty(notes = "停卡日期")
    @Field(type = FieldType.Date)
    private Date stopAccountDate1;

    @ApiModelProperty(notes = "最后消费日")
    @Field(type = FieldType.Date)
    private Date lastConsumptionDate1;

    @ApiModelProperty(notes = "最后提现日期")
    @Field(type = FieldType.Date)
    private Date lastPresentationDate1;

    @ApiModelProperty(notes = "最后拖欠日期")
    @Field(type = FieldType.Date)
    private Date lastDefaultDate1;

    @ApiModelProperty(notes = "冻结日期")
    @Field(type = FieldType.Date)
    private Date freeDate1;

    @ApiModelProperty(notes = "卡额度")
    private Double limitAmt1 = 0.0;

    @ApiModelProperty(notes = "备注")
    private String cardRemark1;

    @ApiModelProperty(notes = "卡号2")
    private String cardNo2;

    @ApiModelProperty(notes = "卡类型2")
    private String cardNo1Type2;

    @ApiModelProperty(notes = "开户日期2")
    @Field(type = FieldType.Date)
    private Date openAccountDate2;

    @ApiModelProperty(notes = "还款日2")
    private String repayDay2;

    @ApiModelProperty(notes = "停卡日期2")
    @Field(type = FieldType.Date)
    private Date stopAccountDate2;

    @ApiModelProperty(notes = "最后消费日2")
    @Field(type = FieldType.Date)
    private Date lastConsumptionDate2;

    @ApiModelProperty(notes = "最后提现日期2")
    @Field(type = FieldType.Date)
    private Date lastPresentationDate2;

    @ApiModelProperty(notes = "最后拖欠日期2")
    @Field(type = FieldType.Date)
    private Date lastDefaultDate2;

    @ApiModelProperty(notes = "冻结日期2")
    @Field(type = FieldType.Date)
    private Date freeDate2;

    @ApiModelProperty(notes = "卡额度2")
    private Double limitAmt2 = 0.0;

    @ApiModelProperty(notes = "备注2")
    private String cardRemark2;

    @ApiModelProperty(notes = "卡号3")
    private String cardNo3;

    @ApiModelProperty(notes = "卡类型3")
    private String cardNo1Type3;

    @ApiModelProperty(notes = "开户日期3")
    @Field(type = FieldType.Date)
    private Date openAccountDate3;

    @ApiModelProperty(notes = "还款日3")
    private String repayDay3;

    @ApiModelProperty(notes = "停卡日期3")
    @Field(type = FieldType.Date)
    private Date stopAccountDate3;

    @ApiModelProperty(notes = "最后消费日3")
    @Field(type = FieldType.Date)
    private Date lastConsumptionDate3;

    @ApiModelProperty(notes = "最后提现日期3")
    @Field(type = FieldType.Date)
    private Date lastPresentationDate3;

    @ApiModelProperty(notes = "最后拖欠日期3")
    @Field(type = FieldType.Date)
    private Date lastDefaultDate3;

    @ApiModelProperty(notes = "冻结日期3")
    @Field(type = FieldType.Date)
    private Date freeDate3;

    @ApiModelProperty(notes = "卡额度3")
    private Double limitAmt3 = 0.0;

    @ApiModelProperty(notes = "备注3")
    private String cardRemark3;

    @ApiModelProperty(notes = "卡号4")
    private String cardNo4;

    @ApiModelProperty(notes = "卡类型4")
    private String cardNo1Type4;

    @ApiModelProperty(notes = "开户日期4")
    @Field(type = FieldType.Date)
    private Date openAccountDate4;

    @ApiModelProperty(notes = "还款日4")
    private String repayDay4;

    @ApiModelProperty(notes = "停卡日期4")
    @Field(type = FieldType.Date)
    private Date stopAccountDate4;

    @ApiModelProperty(notes = "最后消费日4")
    @Field(type = FieldType.Date)
    private Date lastConsumptionDate4;

    @ApiModelProperty(notes = "最后提现日期4")
    @Field(type = FieldType.Date)
    private Date lastPresentationDate4;

    @ApiModelProperty(notes = "最后拖欠日期4")
    @Field(type = FieldType.Date)
    private Date lastDefaultDate4;

    @ApiModelProperty(notes = "冻结日期4")
    @Field(type = FieldType.Date)
    private Date freeDate4;

    @ApiModelProperty(notes = "卡额度4")
    private Double limitAmt4 = 0.0;

    @ApiModelProperty(notes = "备注4")
    private String cardRemark4;

    @ApiModelProperty(notes = "sheet页总数")
    private Integer sheetTotals;

    @ApiModelProperty(notes = "委托方id标识后台记录用")
    private String principalId;


    @ApiModelProperty(notes = "数据状态")
    private CaseDataStatus caseDataStatus=CaseDataStatus.IN_POOL;

    @ApiModelProperty(notes = "停催时间")
    @Field(type = FieldType.Date)
    private Date stopTime;

    @ApiModelProperty(notes = "导入时不匹配字段")
    private Set<RemarkModel> remarkMap;

    @ApiModelProperty("删案日期")
    @Field(type = FieldType.Date)
    private Date deleteCaseDateEnd;

    @ApiModelProperty(notes = "操作员")
    private String operator;

    @ApiModelProperty(notes = "操作时间")
    @Field(type = FieldType.Date)
    private Date operatorTime;

}
