package cn.fintecher.pangolin.service.dataimp.model.response;

import cn.fintecher.pangolin.common.enums.DistributeWay;
import cn.fintecher.pangolin.service.dataimp.model.request.DistributeConfigModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author:peishouwen
 * @Desc: 区域案件分配
 * @Date:Create in 14:42 2018/8/9
 */
@Data
public class AreaCaseDistributeResponse {

    @ApiModelProperty(notes = "案件总数")
    private Long caseNumTotal=new Long(0);

    @ApiModelProperty(notes = "案件总金额")
    private Double caseAmtTotal=new Double(0);

    @ApiModelProperty(notes = "区域总数")
    private Long collectorTotal=new Long(0);

    @ApiModelProperty(notes = "分配规则")
    private List<DistributeConfigResModel> distributeConfigModels;

    @ApiModelProperty(notes = "分配方式")
    private DistributeWay distributeWay;

}
