package cn.fintecher.pangolin.entity.managentment;

import cn.fintecher.pangolin.common.enums.ClockType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ClockConfigDetail {
    @ApiModelProperty("打卡类型")
    private ClockType clockType;

    @ApiModelProperty("签到时间")
    private String signTime;

    @ApiModelProperty("允许时间开始")
    private String allowTimeMin;

    @ApiModelProperty("允许时间结束")
    private String allowTimeMax;
}
