package cn.fintecher.pangolin.service.dataimp.model.response;

import cn.fintecher.pangolin.common.enums.StrategyState;
import cn.fintecher.pangolin.common.enums.StrategyType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Created by ChenChang on 2018/8/8.
 */
@Data
public class CollectionCaseStrategyConfigListResponse {
    private String id;
    @ApiModelProperty("策略名称")
    private String name;
    @ApiModelProperty("创建日期")
    private Date createTime;
    @ApiModelProperty("对应分配机构Id")
    private String organization;
    @ApiModelProperty("机构名称")
    private String organizationName;
    @ApiModelProperty("创建人")
    private String operatorName;
    @ApiModelProperty("公式JSON")
    private String formulaJson;
    @ApiModelProperty("策略类型")
    private StrategyType strategyType;
    @ApiModelProperty("优先级")
    private Integer priority;
    @ApiModelProperty("状态")
    private StrategyState strategyState;
}
