package cn.fintecher.pangolin.service.dataimp.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class StockModel {

    @ApiModelProperty("存量列表")
    private List<StockResponse> responseList;

    @ApiModelProperty("最大金额")
    private Double bigAmt;

    @ApiModelProperty("最大户数")
    private long bigNum;
}
