package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.enums.ClockType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CreateClockRecordRequest {

    @ApiModelProperty("打卡类型")
    private ClockType clockType;

    @ApiModelProperty("打卡地点")
    private String clockAddr;
}
