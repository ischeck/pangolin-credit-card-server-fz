package cn.fintecher.pangolin.service.common.web;

import cn.fintecher.pangolin.common.enums.MessageMode;
import cn.fintecher.pangolin.common.enums.MessageReadStatus;
import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.WebSocketMessageModel;
import cn.fintecher.pangolin.common.model.response.LoginResponse;
import cn.fintecher.pangolin.service.common.client.OperatorCommonClient;
import cn.fintecher.pangolin.service.common.model.QWebSocketMessage;
import cn.fintecher.pangolin.service.common.model.TaskBox;
import cn.fintecher.pangolin.service.common.model.WebSocketMessage;
import cn.fintecher.pangolin.service.common.model.reponse.WebSocketMessageResponse;
import cn.fintecher.pangolin.service.common.model.request.MessageDeletedRequest;
import cn.fintecher.pangolin.service.common.model.request.UpdateMsgRequest;
import cn.fintecher.pangolin.service.common.respository.WebSocketMessageRepository;
import cn.fintecher.pangolin.service.common.service.WebSocketService;
import com.querydsl.core.BooleanBuilder;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 10:29 2018/9/4
 */
@RestController
@RequestMapping("/api/webSocketMsgController")
@Api(value = "消息服务", description = "消息服务")
public class WebSocketMsgController {
    Logger logger= LoggerFactory.getLogger(WebSocketMsgController.class);

    @Autowired
    OperatorCommonClient operatorCommonClient;

    @Autowired
    WebSocketService webSocketService;

    @Autowired
    WebSocketMessageRepository webSocketMessageRepository;

    @PostMapping("/sendMsgByUserId")
    @ApiOperation(value = "发送消息", notes = "发送消息")
    public ResponseEntity sendMsgByUserId(@RequestBody WebSocketMessageModel message, @RequestParam("userName") String userName){
        webSocketService.sendMessage(userName,message);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/getMsgList")
    @ApiOperation(value = "查询消息列表", notes = "查询消息列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<WebSocketMessageResponse>> getMsgList(Pageable pageable,
                                                                     MessageReadStatus messageReadStatus,
                                                                     @RequestHeader(value = "X-UserToken") String token) throws BadRequestException{
        ResponseEntity<LoginResponse> responseEntity= operatorCommonClient.getUserByToken(token);
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QWebSocketMessage qWebSocketMessage=QWebSocketMessage.webSocketMessage;
        booleanBuilder.and(qWebSocketMessage.userName.eq(responseEntity.getBody().getUser().getUsername()));
        booleanBuilder.and(qWebSocketMessage.webSocketMessageModel.readStatus.eq(messageReadStatus));
        //Sort sort =new Sort(Sort.Direction.DESC, "webSocketMessageModel.msgDate");
        Page<WebSocketMessageResponse> allList=webSocketMessageRepository.findAll(booleanBuilder,pageable).map(webSocketMessage -> {
            WebSocketMessageResponse response = new WebSocketMessageResponse();
            response.setId(webSocketMessage.getId());
            response.setContent(webSocketMessage.getWebSocketMessageModel().getContent());
            response.setMessageMode(webSocketMessage.getWebSocketMessageModel().getMessageMode());
            response.setMessageType(webSocketMessage.getWebSocketMessageModel().getMessageType());
            response.setMsgDate(webSocketMessage.getWebSocketMessageModel().getMsgDate());
            response.setReadStatus(webSocketMessage.getWebSocketMessageModel().getReadStatus());
            response.setTitle(webSocketMessage.getWebSocketMessageModel().getTitle());
            return response;
        });
        return ResponseEntity.ok().body(allList);
    }

    @GetMapping("/getUnReadMsgCount")
    @ApiOperation(value = "获取未读消息总数", notes = "获取未读消息总数")
    public ResponseEntity<Long> getUnReadMsgCount(@RequestHeader(value = "X-UserToken") String token) throws BadRequestException{
        ResponseEntity<LoginResponse> responseEntity= operatorCommonClient.getUserByToken(token);
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QWebSocketMessage qWebSocketMessage=QWebSocketMessage.webSocketMessage;
        booleanBuilder.and(qWebSocketMessage.userName.eq(responseEntity.getBody().getUser().getUsername()));
        booleanBuilder.and(qWebSocketMessage.webSocketMessageModel.readStatus.eq(MessageReadStatus.UNREAD));
        Long reponse= webSocketMessageRepository.count(booleanBuilder);
        return ResponseEntity.ok().body(reponse);
    }

    @PostMapping("/updateMsgStatus")
    @ApiOperation(value = "更新消息状态", notes = "更新消息状态")
    public ResponseEntity updateMsgStatus(@RequestBody UpdateMsgRequest updateMsgRequest) throws BadRequestException{
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QWebSocketMessage qWebSocketMessage=QWebSocketMessage.webSocketMessage;
        booleanBuilder.and(qWebSocketMessage.id.in(updateMsgRequest.getIds()));
        Iterator<WebSocketMessage> webSocketMessages= webSocketMessageRepository.findAll(booleanBuilder).iterator();
        List<WebSocketMessage> list=new ArrayList<>();
        while (webSocketMessages.hasNext()){
            WebSocketMessage webSocketMessage=webSocketMessages.next();
            webSocketMessage.getWebSocketMessageModel().setReadStatus(MessageReadStatus.READ);
            list.add(webSocketMessage);
        }
        webSocketMessageRepository.saveAll(list);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/getMsgDetail")
    @ApiOperation(value = "获取消息详情", notes = "获取消息详情")
    public ResponseEntity<WebSocketMessageResponse> getMsgDetail(@RequestParam String id) throws BadRequestException{
        Optional<WebSocketMessage> webSocketMessage=   webSocketMessageRepository.findById(id);
        WebSocketMessageResponse response = new WebSocketMessageResponse();
        if(webSocketMessage.isPresent()){
            response.setId(webSocketMessage.get().getId());
            response.setContent(webSocketMessage.get().getWebSocketMessageModel().getContent());
            response.setMessageMode(webSocketMessage.get().getWebSocketMessageModel().getMessageMode());
            response.setMessageType(webSocketMessage.get().getWebSocketMessageModel().getMessageType());
            response.setMsgDate(webSocketMessage.get().getWebSocketMessageModel().getMsgDate());
            response.setReadStatus(webSocketMessage.get().getWebSocketMessageModel().getReadStatus());
            response.setTitle(webSocketMessage.get().getWebSocketMessageModel().getTitle());
        }
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/getWorkbenchReminder")
    @ApiOperation(value = "弹框消息", notes = "弹框消息")
    public ResponseEntity<Page<WebSocketMessageResponse>> getWorkbenchReminder(@RequestHeader(value = "X-UserToken") String token) {
        ResponseEntity<LoginResponse> responseEntity= operatorCommonClient.getUserByToken(token);
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QWebSocketMessage qWebSocketMessage=QWebSocketMessage.webSocketMessage;
        booleanBuilder.and(qWebSocketMessage.userName.eq(responseEntity.getBody().getUser().getUsername()));
        booleanBuilder.and(qWebSocketMessage.webSocketMessageModel.readStatus.eq(MessageReadStatus.UNREAD));
        booleanBuilder.and(qWebSocketMessage.webSocketMessageModel.messageMode.eq(MessageMode.POPUP));
        Sort sort =new Sort(Sort.Direction.DESC, "webSocketMessageModel.msgDate");
        Pageable pageable =PageRequest.of( 0,  3, sort);

        Page<WebSocketMessageResponse>  page=webSocketMessageRepository.findAll(booleanBuilder,pageable).map(webSocketMessage -> {
            WebSocketMessageResponse response = new WebSocketMessageResponse();
            response.setId(webSocketMessage.getId());
            response.setContent(webSocketMessage.getWebSocketMessageModel().getContent());
            response.setMessageMode(webSocketMessage.getWebSocketMessageModel().getMessageMode());
            response.setMessageType(webSocketMessage.getWebSocketMessageModel().getMessageType());
            response.setMsgDate(webSocketMessage.getWebSocketMessageModel().getMsgDate());
            response.setReadStatus(webSocketMessage.getWebSocketMessageModel().getReadStatus());
            response.setTitle(webSocketMessage.getWebSocketMessageModel().getTitle());
            return response;
        });
        return ResponseEntity.ok().body(page);
    }

    @PostMapping("/deletedReminderMessage")
    @ApiOperation(value = "删除提醒消息", notes = "删除提醒消息")
    public ResponseEntity<TaskBox> deletedReminderMessage(@RequestBody MessageDeletedRequest messageDeletedRequest) {
        logger.info("删除提醒消息开始"+messageDeletedRequest);
        Iterable<WebSocketMessage> allById = webSocketMessageRepository.findAllById(messageDeletedRequest.getIdList());
        webSocketMessageRepository.deleteAll(allById);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/testSendMsg")
    @ApiOperation(value = "消息测试", notes = "消息测试")
    public ResponseEntity testSendMsg(){
        WebSocketMessageModel message=new WebSocketMessageModel();
        message.setContent("这是一个测试消息");
        webSocketService.sendMessage("E42015120",message);
        return ResponseEntity.ok().body(null);
    }


}
