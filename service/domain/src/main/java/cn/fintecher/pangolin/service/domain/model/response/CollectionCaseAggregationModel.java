package cn.fintecher.pangolin.service.domain.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Created by huyanmin 2018/07/31
 */

@Data
public class  CollectionCaseAggregationModel {

    @ApiModelProperty(notes = "批次号")
    private String batchNumber;

    @ApiModelProperty(notes = "委托方名称")
    private String principalName;

    @ApiModelProperty(notes = "案件个数")
    private Long caseCount;

    @ApiModelProperty(notes = "总金额")
    private Double totalAmount;

    @ApiModelProperty(notes = "委案日期")
    private Date delegationDate;

    @ApiModelProperty(notes = "结案日期")
    private Date endCaseDate;

    @ApiModelProperty(notes = "入催日期")
    private Date remindersDate;
}
