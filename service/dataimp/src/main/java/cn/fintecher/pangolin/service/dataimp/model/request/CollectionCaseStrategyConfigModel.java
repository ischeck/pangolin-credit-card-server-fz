package cn.fintecher.pangolin.service.dataimp.model.request;

import cn.fintecher.pangolin.common.enums.StrategyState;
import cn.fintecher.pangolin.common.enums.StrategyType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by ChenChang on 2018/8/8.
 */
@Data
public class CollectionCaseStrategyConfigModel {
    private String id;
    @ApiModelProperty("策略名称")
    private String name;
    @ApiModelProperty("公式JSON")
    private String formulaJson;
    @ApiModelProperty("对应分配机构 ID")
    private String organization;
    @ApiModelProperty("优先级")
    private Integer priority;
    @ApiModelProperty("策略类型")
    private StrategyType strategyType;
    @ApiModelProperty("创建人")
    private String operatorName;
    @ApiModelProperty("状态")
    private StrategyState strategyState;
}
