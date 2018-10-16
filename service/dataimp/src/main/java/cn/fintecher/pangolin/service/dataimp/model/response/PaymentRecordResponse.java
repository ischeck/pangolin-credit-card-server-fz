package cn.fintecher.pangolin.service.dataimp.model.response;

import cn.fintecher.pangolin.common.enums.ManagementType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class PaymentRecordResponse {

    @ApiModelProperty(notes = "承诺还款金额")
    private Double promiseAmt;

    @ApiModelProperty(notes = "客户姓名")
    private String personalName;

    @ApiModelProperty(notes = "身份证")
    private String certificateNo;

    @ApiModelProperty(notes = "承诺还款日期")
    private Date promiseDate;

    @ApiModelProperty("是否跳票")
    private ManagementType isBouncedCheck;
}
