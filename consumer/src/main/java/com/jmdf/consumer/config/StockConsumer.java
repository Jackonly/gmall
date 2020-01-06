package com.jmdf.consumer.config;

import com.alibaba.fastjson.JSONObject;
import com.jmdf.consumer.entity.OrderEntity;
import com.jmdf.consumer.entity.SeckillEntity;
import com.jmdf.consumer.mapper.SeckillMapper;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

/**
 * 消费者
 */
@Component
public class StockConsumer {
    @Autowired
    private SeckillMapper seckillMapper;

    @RabbitListener(queues = {"modify_inventory_queue"})
    @SuppressWarnings("all")
    public void process(Message message, Channel channel) throws UnsupportedEncodingException {
        String messageId = message.getMessageProperties().getMessageId();
        String msg = new String(message.getBody(), "UTF-8");
        System.out.println("收到的消息"+msg);
        JSONObject jsonObject = JSONObject.parseObject(msg);
        // 1.获取秒杀id
        Long seckillId = jsonObject.getLong("seckillId");
        SeckillEntity seckillEntity = seckillMapper.findBySeckillId(seckillId);
        if (seckillEntity == null) {
            return;
        }
        Long version = seckillEntity.getVersion();
        int inventoryDeduction = seckillMapper.optimisticVersionSeckill(seckillId, version);
        if (!toDaoResult(inventoryDeduction)) {
            return;
        }
        // 2.添加秒杀订单
        OrderEntity orderEntity = new OrderEntity();
        String phone = jsonObject.getString("phone");
        orderEntity.setUserPhone(phone);
        orderEntity.setSeckillId(seckillId);
        orderEntity.setState((int) 1l);
        int insertOrder = seckillMapper.insertOrder(orderEntity);
        if (!toDaoResult(insertOrder)) {
            return;
        }
    }

    // 调用数据库层判断
    public Boolean toDaoResult(int result) {
        return result > 0 ? true : false;
    }

}
