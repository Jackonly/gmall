package com.jmdf.seckill.product;

import com.alibaba.fastjson.JSONObject;
import com.jmdf.seckill.config.RabbitMqConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageBuilderSupport;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 生产者发送消息
 */
@Component
public class SpikeCommodityProducer implements RabbitTemplate.ConfirmCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Transactional
    public void send(JSONObject jsonObject){
        String jsonString = jsonObject.toJSONString();
        String messAgeId = UUID.randomUUID().toString().replace("-", "");
        Message message = MessageBuilder.withBody(jsonString.getBytes())
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setContentEncoding("utf-8")
                .setMessageId(messAgeId).build();
        //构造参数
        this.rabbitTemplate.setMandatory(true);
        this.rabbitTemplate.setConfirmCallback(this);
        this.rabbitTemplate.convertAndSend("modify_exchange_name","modifyRoutingKey",message);

    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        //获取id
        String messageId = correlationData.getId();
        JSONObject jsonObject = JSONObject.parseObject(messageId);
        if (ack){
            System.out.println("消费成功");
        }else{
            System.out.println("消费失败重试");
            //重试机制调用
            send(jsonObject);
        }
    }
}
