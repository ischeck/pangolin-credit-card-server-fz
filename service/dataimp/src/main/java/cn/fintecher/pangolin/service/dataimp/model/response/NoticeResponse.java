package cn.fintecher.pangolin.service.dataimp.model.response;

import cn.fintecher.pangolin.common.enums.NoticeType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class NoticeResponse {
    private String id;

    @ApiModelProperty("类型")
    private NoticeType type;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("发布时间")
    private Date operatorTime;

    @ApiModelProperty("发布人")
    private String operatorName;
}
