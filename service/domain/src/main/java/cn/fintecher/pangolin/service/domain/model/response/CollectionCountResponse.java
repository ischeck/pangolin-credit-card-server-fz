package cn.fintecher.pangolin.service.domain.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CollectionCountResponse {

    @ApiModelProperty("今日跟催")
    private long todayFollow;

    @ApiModelProperty("明日跟催")
    private long tomorrowFollow;

    @ApiModelProperty("PTP")
    private long ptpNumber;

    @ApiModelProperty("重点跟进")
    private long majorFollow;

    @ApiModelProperty("1-3天未跟")
    private long oneToThreeNoFollow;

    @ApiModelProperty("3-6天未跟")
    private long fourToSixNoFollow;

    @ApiModelProperty("3天内退案")
    private long threeDaysLeft;

    @ApiModelProperty("7天内退案")
    private long sevenDaysLeft;
}
