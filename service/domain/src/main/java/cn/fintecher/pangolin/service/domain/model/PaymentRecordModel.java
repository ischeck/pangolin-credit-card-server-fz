package cn.fintecher.pangolin.service.domain.model;

import cn.fintecher.pangolin.common.enums.ManagementType;
import cn.fintecher.pangolin.common.enums.PaymentStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * Created by huyanmin on 2018/8/7.
 */
@Data
public class PaymentRecordModel {

    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty(notes = "承诺还款金额")
    private Double promiseAmt;

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

    @ApiModelProperty("操作时间")
    private Date operatorDate;

    @ApiModelProperty("备注")
    private  String remark;

    @ApiModelProperty("操作人")
    private String operatorName;

}
