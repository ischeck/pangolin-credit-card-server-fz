package cn.fintecher.pangolin.service.dataimp.model.response;

import cn.fintecher.pangolin.common.enums.CaseIssuedFlag;
import cn.fintecher.pangolin.common.enums.DistributeWay;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 17:03 2018/8/9
 */
@Data
public class GroupBatchDistributeResponse {
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

    @ApiModelProperty("批次号")
    private String batchNumber;

    @ApiModelProperty("案件状态")
    private CaseIssuedFlag issuedFlag;

    @ApiModelProperty("城市数据")
    private Set<String> citys=new HashSet<>();

    @ApiModelProperty("案件所属部门")
    private Set<String> departs=new HashSet<>();
}
