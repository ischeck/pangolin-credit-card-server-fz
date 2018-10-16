package cn.fintecher.pangolin.entity.managentment;

import cn.fintecher.pangolin.common.enums.ClockStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

@Data
@Document
@ApiModel("打卡记录")
public class ClockRecord {
    @Id
    private String id;

    @ApiModelProperty("用户")
    private String operator;

    @ApiModelProperty("用户名称")
    private String operatorName;

    @ApiModelProperty("日期")
    private String date;

    @ApiModelProperty("机构名称")
    private String organizationName;

    @ApiModelProperty("机构集合")
    private Set<String> organizations;

    @ApiModelProperty("上班打卡时间")
    private Date morningClockTime;

    @ApiModelProperty("上班标准时间")
    private Date morningSignTime;

    @ApiModelProperty("早上允许时间开始")
    private Date morningAllowTimeMin;

    @ApiModelProperty("中午允许时间结束")
    private Date morningAllowTimeMax;

    @ApiModelProperty("上班打卡地点")
    private String morningClockAddr;

    @ApiModelProperty("上班打卡状态")
    private ClockStatus morningStatus = ClockStatus.NOCLOCK;

    @ApiModelProperty("午间打卡时间")
    private Date noonClockTime;

    @ApiModelProperty("午间标准时间")
    private Date noonSignTime;

    @ApiModelProperty("早上允许时间开始")
    private Date noonAllowTimeMin;

    @ApiModelProperty("中午允许时间结束")
    private Date noonAllowTimeMax;

    @ApiModelProperty("午间打卡地点")
    private String noonClockAddr;

    @ApiModelProperty("午间打卡状态")
    private ClockStatus noonStatus = ClockStatus.NOCLOCK;

    @ApiModelProperty("下班打卡时间")
    private Date afterClockTime;

    @ApiModelProperty("下班标准时间")
    private Date afterSignTime;

    @ApiModelProperty("早上允许时间开始")
    private Date afterAllowTimeMin;

    @ApiModelProperty("中午允许时间结束")
    private Date afterAllowTimeMax;

    @ApiModelProperty("下班打卡地点")
    private String afterClockAddr;

    @ApiModelProperty("下班打卡状态")
    private ClockStatus afterStatus = ClockStatus.NOCLOCK;

}
