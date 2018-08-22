package com.busi.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Destination;

/**
 * 发送消息给MQ
 * author：SunTianJie
 * create time：2018/6/28 9:40
 */
@Component
public class MqProducer {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    /***
     * 发送消息给 MQ
     * @param destination
     * @param json 消息内容 格式如下：
     * {
     * 	"header": {
     * 		"interfaceType": 1
     *   },
     * 	"content": {
     * 		"email": "test@qq.com",
     * 		"phone": "15901213694",
     * 		"userId": "12345"
     *    }
     * }
     */
    public void sendMsg(Destination destination, String json) {
        jmsMessagingTemplate.convertAndSend(destination, json);
    }
}
