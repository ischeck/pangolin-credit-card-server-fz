package cn.fintecher.pangolin.service.domain.model.response;

import cn.fintecher.pangolin.entity.managentment.Operator;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.Set;

/**
 * @Author : huyanmin
 * @Description : 案件查询结果
 * @Date : 2018/7/30.
 */
@Data
public class CollectionCaseByBatchNumber {

    @ApiModelProperty("批次号")
    private String batchNumber;

    @ApiModelProperty("委托方名称")
    private String principalName;

    @ApiModelProperty("委案日期")
    private Date delegationDate;

    @ApiModelProperty(notes = "结案日期")
    private Date endCaseDate;

    @ApiModelProperty(notes = "案件数量")
    private Integer caseCount;

    @ApiModelProperty(notes = "委案总金额")
    private Double overdueAmtTotal ;
}
