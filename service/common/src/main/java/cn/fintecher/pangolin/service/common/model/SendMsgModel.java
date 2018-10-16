package cn.fintecher.pangolin.service.common.model;

import cn.fintecher.pangolin.common.enums.MessageDataType;
import cn.fintecher.pangolin.common.model.TaskBoxModel;
import cn.fintecher.pangolin.service.common.model.reponse.WebSocketMessageResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 18:39 2018/9/13
 */
@Data
public class SendMsgModel {

    @ApiModelProperty(notes = "消息类型")
    private MessageDataType messageType;

    private WebSocketMessageResponse data;
}
