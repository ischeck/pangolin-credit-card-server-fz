package cn.fintecher.pangolin.entity.domain;

import cn.fintecher.pangolin.common.enums.ManagementType;
import cn.fintecher.pangolin.common.enums.PaymentStatus;
import cn.fintecher.pangolin.common.enums.PaymentType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @Author : huyanmin
 * @Description : 还款记录
 * @Date : 2018/8/7.
 */
@Data
@Document(indexName = "payment_record", type = "payment_record", shards = 1, replicas = 0)
@ApiModel(value = "PaymentRecord", description = "还款申请")
public class PaymentRecord {

    @Id
    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty("案件ID")
    private String caseId;

    @ApiModelProperty(notes = "承诺还款金额")
    private Double promiseAmt;

    @ApiModelProperty(notes = "客户姓名")
    private String personalName;

    @ApiModelProperty(notes = "身份证号")
    private String certificateNo;

    @ApiModelProperty(notes = "承诺还款日期")
    @Field(type = FieldType.Date)
    private Date promiseDate;

    @ApiModelProperty(notes = "已还款金额")
    private Double hasPaymentAmt;

    @ApiModelProperty(notes = "已还款日期")
    @Field(type = FieldType.Date)
    private Date hasPaymentDate;

    @ApiModelProperty("回复结果")
    private Double fallBackAmount;

    @ApiModelProperty("审批状态")
    private PaymentStatus paymentStatus;

    @ApiModelProperty("是否跳票")
    private ManagementType isBouncedCheck;

    @ApiModelProperty("备注")
    private  String remark;

    @ApiModelProperty("操作人")
    private String operator;

    @ApiModelProperty("操作人")
    private String operatorName;

    @ApiModelProperty("操作时间")
    @Field(type = FieldType.Date)
    private Date operatorDate;

}
