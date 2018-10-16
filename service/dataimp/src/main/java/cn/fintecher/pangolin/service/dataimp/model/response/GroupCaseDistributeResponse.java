package cn.fintecher.pangolin.service.dataimp.model.response;

import cn.fintecher.pangolin.common.enums.DistributeWay;
import cn.fintecher.pangolin.service.dataimp.model.request.DistributeConfigModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 17:03 2018/8/9
 */
@Data
public class GroupCaseDistributeResponse {
    @ApiModelProperty(notes = "案件总金额")
    private Double caseAmtTotal=new Double(0);

    @ApiModelProperty(notes = "催收员总数")
    private Long collectorTotal=new Long(0);

    @ApiModelProperty(notes = "案件总数")
    private Long caseNumTotal=new Long(0);

    @ApiModelProperty(notes = "分配规则")
    private List<DistributeConfigResModel> distributeConfigModels;

    @ApiModelProperty(notes = "分配方式")
    private DistributeWay distributeWay;

    @ApiModelProperty(notes = "批次号")
    private String batchNumber;
}
