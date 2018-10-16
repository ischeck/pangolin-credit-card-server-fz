package cn.fintecher.pangolin.service.common.model;

import cn.fintecher.pangolin.common.model.WebSocketMessageModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 14:45 2018/9/4
 */
@Data
@Document
public class WebSocketMessage {

    @Id
    private String id;

    @ApiModelProperty("消息接收人")
    private String userName;

    @ApiModelProperty("消息实体")
    private WebSocketMessageModel webSocketMessageModel;
}
