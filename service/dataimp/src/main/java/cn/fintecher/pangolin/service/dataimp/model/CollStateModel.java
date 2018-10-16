package cn.fintecher.pangolin.service.dataimp.model;

import cn.fintecher.pangolin.service.dataimp.model.response.CollStateResponse;
import cn.fintecher.pangolin.service.dataimp.model.response.PaymentRecordResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CollStateModel {
    @ApiModelProperty("总户数")
    private long totalNum;

    @ApiModelProperty("标记户数")
    private long signNum;

    @ApiModelProperty("承诺记录")
    List<CollStateResponse> collStateResponses;
}
