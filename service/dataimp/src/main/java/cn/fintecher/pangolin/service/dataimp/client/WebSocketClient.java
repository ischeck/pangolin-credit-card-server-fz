package cn.fintecher.pangolin.service.dataimp.client;

import cn.fintecher.pangolin.common.model.TaskBoxModel;
import cn.fintecher.pangolin.common.model.WebSocketMessageModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 10:46 2018/9/4
 */
@FeignClient("common-service")
public interface WebSocketClient {

    @PostMapping("/api/webSocketMsgController/sendMsgByUserId")
    @ApiOperation(value = "发送消息", notes = "发送消息")
    ResponseEntity sendMsgByUserId(@RequestBody WebSocketMessageModel message, @RequestParam("userName") String userName);

    @PostMapping("/api/taskBoxController/sendTaskByUserId")
    @ApiOperation(value = "消息盒子", notes = "消息盒子")
    ResponseEntity sendTaskByUserId(@RequestBody TaskBoxModel model, @RequestParam("userName") String userName);
}
