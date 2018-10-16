package cn.fintecher.pangolin.service.dataimp.model;

import cn.fintecher.pangolin.service.dataimp.model.response.AssistCaseResponse;
import cn.fintecher.pangolin.service.dataimp.model.response.PaymentRecordResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class WaitHandleListModel {

    @ApiModelProperty("承诺还款记录")
    List<PaymentRecordResponse> ptpRcords;

    @ApiModelProperty("已还款记录")
    List<PaymentRecordResponse> cpRcords;

    @ApiModelProperty("外访案件")
    List<AssistCaseResponse> visitRecords;
}
