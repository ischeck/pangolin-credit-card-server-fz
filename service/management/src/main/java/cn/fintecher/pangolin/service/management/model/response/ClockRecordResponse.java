package cn.fintecher.pangolin.service.management.model.response;

import cn.fintecher.pangolin.common.enums.ClockStatus;
import cn.fintecher.pangolin.common.enums.ClockType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@Data
public class ClockRecordResponse {

    @ApiModelProperty("用户名称")
    private String operatorName;

    @ApiModelProperty("机构名称")
    private String organizationName;

    @ApiModelProperty("日期")
    private String date;

    @ApiModelProperty("上班打卡时间")
    private Date morningClockTime;

    @ApiModelProperty("上班标准时间")
    private Date morningSignTime;

    @ApiModelProperty("上班打卡地点")
    private String morningClockAddr;

    @ApiModelProperty("上班打卡状态")
    private ClockStatus morningStatus = ClockStatus.NOCLOCK;

    @ApiModelProperty("午间打卡时间")
    private Date noonClockTime;

    @ApiModelProperty("午间标准时间")
    private Date noonSignTime;

    @ApiModelProperty("午间打卡地点")
    private String noonClockAddr;

    @ApiModelProperty("午间打卡状态")
    private ClockStatus noonStatus = ClockStatus.NOCLOCK;

    @ApiModelProperty("下班打卡时间")
    private Date afterClockTime;

    @ApiModelProperty("下班标准时间")
    private Date afterSignTime;

    @ApiModelProperty("下班打卡地点")
    private String afterClockAddr;

    @ApiModelProperty("下班打卡状态")
    private ClockStatus afterStatus = ClockStatus.NOCLOCK;

    @ApiModelProperty("打卡类型")
    private ClockType clockType;
}
