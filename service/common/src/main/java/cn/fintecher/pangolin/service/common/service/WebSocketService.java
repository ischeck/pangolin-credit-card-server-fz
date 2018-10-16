package cn.fintecher.pangolin.service.common.service;

import cn.fintecher.pangolin.common.enums.MessageDataType;
import cn.fintecher.pangolin.common.model.TaskBoxModel;
import cn.fintecher.pangolin.common.model.WebSocketMessageModel;
import cn.fintecher.pangolin.service.common.model.SendMsgModel;
import cn.fintecher.pangolin.service.common.model.SendTaskBoxModel;
import cn.fintecher.pangolin.service.common.model.TaskBox;
import cn.fintecher.pangolin.service.common.model.WebSocketMessage;
import cn.fintecher.pangolin.service.common.model.reponse.WebSocketMessageResponse;
import cn.fintecher.pangolin.service.common.respository.TaskBoxRepository;
import cn.fintecher.pangolin.service.common.respository.WebSocketMessageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 13:16 2018/7/28
 */
@Service("webSocketService")
public class WebSocketService {
    @Autowired
    private TopicExchange webMessageExchange;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private WebSocketMessageRepository webSocketMessageRepository;

    @Autowired
    TaskBoxRepository taskBoxRepository;

    /**
     * 发送消息
     *
     * @param userName
     * @param message
     */
    public void sendMessage(String userName, WebSocketMessageModel message) {
        ObjectMapper mapper = new ObjectMapper();
        WebSocketMessageResponse response = new WebSocketMessageResponse();
        BeanUtils.copyProperties(message, response);
        SendMsgModel sendMsgModel = new SendMsgModel();
        sendMsgModel.setMessageType(message.getMessageDataType());
        sendMsgModel.setData(response);
        String messageString = null;
        try {
            messageString = mapper.writeValueAsString(sendMsgModel);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        rabbitTemplate.convertAndSend(webMessageExchange.getName(), userName, messageString);
        //提醒类消息
        WebSocketMessage webSocketMessage = new WebSocketMessage();
        webSocketMessage.setUserName(userName);
        webSocketMessage.setWebSocketMessageModel(message);
        webSocketMessageRepository.save(webSocketMessage);

    }

    /**
     * 发送任务盒子的消息
     *
     * @param userName
     * @param taskBoxModel
     */
    public void sendTaskBoxMessage(String userName, TaskBoxModel taskBoxModel) {
        ObjectMapper mapper = new ObjectMapper();
        SendTaskBoxModel sendMsgModel = new SendTaskBoxModel();
        sendMsgModel.setMessageType(MessageDataType.TaskBox);
        sendMsgModel.setData(taskBoxModel);
        String messageString = null;
        try {
            messageString = mapper.writeValueAsString(sendMsgModel);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        rabbitTemplate.convertAndSend(webMessageExchange.getName(), userName, messageString);
        //保存任务盒子
        if(Objects.nonNull(taskBoxModel.getId())){
            Optional<TaskBox> optional = taskBoxRepository.findById(taskBoxModel.getId());
            if(optional.isPresent()){
                TaskBox taskBox = optional.get();
                BeanUtils.copyProperties(taskBoxModel,taskBox);
                taskBoxRepository.save(taskBox);
            }else {
                TaskBox taskBox = new TaskBox();
                BeanUtils.copyProperties(taskBoxModel,taskBox);
                taskBoxRepository.save(taskBox);
            }
        }
    }

}
