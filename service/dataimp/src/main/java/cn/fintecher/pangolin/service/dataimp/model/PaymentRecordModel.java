package cn.fintecher.pangolin.service.dataimp.model;

import cn.fintecher.pangolin.service.dataimp.model.response.PaymentRecordResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class PaymentRecordModel {
    @ApiModelProperty("总户数")
    private long totalNum;

    @ApiModelProperty("承诺户数")
    private long ptpNum;

    @ApiModelProperty("承诺记录")
    List<PaymentRecordResponse> paymentRecordResponses;
}
