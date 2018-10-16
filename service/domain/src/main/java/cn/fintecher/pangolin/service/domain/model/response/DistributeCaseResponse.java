package cn.fintecher.pangolin.service.domain.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Created by BBG 2018/08/07
 */

@Data
public class DistributeCaseResponse {

    @ApiModelProperty(notes = "批次号")
    private String batchNumber;

    @ApiModelProperty(notes = "委托方名称")
    private String principalName;

    @ApiModelProperty(notes = "案件个数")
    private Long caseCount;

    @ApiModelProperty(notes = "案件金额(￥)")
    private Double leftAmt;

    @ApiModelProperty(notes = "案件金额($)")
    private Double leftAmtDollar;

    @ApiModelProperty(notes = "委案日期")
    private Date delegationDate;

    @ApiModelProperty(notes = "结案日期")
    private Date endCaseDate;

}
