package cn.fintecher.pangolin.common.model;

import cn.fintecher.pangolin.common.enums.MessageDataType;
import cn.fintecher.pangolin.common.enums.MessageMode;
import cn.fintecher.pangolin.common.enums.MessageReadStatus;
import cn.fintecher.pangolin.common.enums.MessageType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Created by ChenChang on 2017/4/5.
 */
@Data
public class WebSocketMessageModel {

    @ApiModelProperty(notes = "消息数据类型")
    private MessageDataType messageDataType=MessageDataType.Reminder;

    @ApiModelProperty(notes = "消息类型")
    private MessageType messageType;

    @ApiModelProperty(notes = "消息标题")
    private String title;

    @ApiModelProperty(notes = "消息日期")
    private Date msgDate;

    @ApiModelProperty(notes = "消息内容")
    private String content;

    @ApiModelProperty(notes = "消息标识")
    private MessageReadStatus readStatus=MessageReadStatus.UNREAD;

    @ApiModelProperty(notes = "消息模式")
    private MessageMode messageMode=MessageMode.COMMON;

}
