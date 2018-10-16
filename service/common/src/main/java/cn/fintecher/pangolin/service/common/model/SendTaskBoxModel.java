package cn.fintecher.pangolin.service.common.model;

import cn.fintecher.pangolin.common.enums.MessageDataType;
import cn.fintecher.pangolin.common.model.TaskBoxModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author:huyanmin
 * @Desc:
 * @Date:Create2018/9/19
 */
@Data
public class SendTaskBoxModel {

    @ApiModelProperty(notes = "消息类型")
    private MessageDataType messageType;

    private TaskBoxModel data;
}
