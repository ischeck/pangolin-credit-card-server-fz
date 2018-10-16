package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.enums.NoticeType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NoticeCteateRequest {
    @ApiModelProperty("类型")
    private NoticeType type;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("内容")
    private String content;
}
