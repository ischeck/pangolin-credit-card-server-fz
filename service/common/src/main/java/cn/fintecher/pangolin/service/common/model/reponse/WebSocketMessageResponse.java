package cn.fintecher.pangolin.service.common.model.reponse;

import cn.fintecher.pangolin.common.enums.MessageMode;
import cn.fintecher.pangolin.common.enums.MessageReadStatus;
import cn.fintecher.pangolin.common.enums.MessageType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 10:43 2018/9/13
 */
@Data
public class WebSocketMessageResponse {
    private String id;

    @ApiModelProperty(notes = "消息类型")
    private MessageType messageType;

    @ApiModelProperty(notes = "消息标题")
    private String title;

    @ApiModelProperty(notes = "消息日期")
    private Date msgDate;

    @ApiModelProperty(notes = "消息内容")
    private String content;

    @ApiModelProperty(notes = "消息标识")
    private MessageReadStatus readStatus;

    @ApiModelProperty(notes = "消息模式")
    private MessageMode messageMode;
}
