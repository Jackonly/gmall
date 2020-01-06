package com.jmdf.seckill.product;

import com.alibaba.fastjson.JSONObject;
import com.jmdf.seckill.entity.SeckillEntity;
import com.jmdf.seckill.mapper.SeckillMapper;
import com.jmdf.seckill.util.GenerateToken;
import com.jmdf.seckill.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 基于mq实现库存
 */
@Component
public class SpikeCommodity {
    @Autowired
    private SeckillMapper seckillMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private GenerateToken generateToken;
    @Autowired
    private SpikeCommodityProducer spikeCommodityProducer;

    @Transactional
    public JSONObject getOrder(String phone, Long seckillId) {
        JSONObject jsonObject = new JSONObject();
        // 1.验证参数
        if (StringUtils.isEmpty(phone)) {
            jsonObject.put("error", "手机号码不能为空!");
            return jsonObject;
        }
        if (seckillId == null) {
            jsonObject.put("error", "库存id不能为空!");
            return jsonObject;
        }
        // 2.从redis从获取对应的秒杀token
        String seckillToken = generateToken.getListKeyToken(seckillId + "");
        if (StringUtils.isEmpty(seckillToken)) {
            return null;
        }
        // 3.获取到秒杀token之后，异步放入mq中实现修改商品的库存
        try {
            sendSeckillMsg(seckillId, phone);
            jsonObject.put("success","恭喜你,秒杀成功!");
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("error","秒杀失败");
            return jsonObject;
        }

        return jsonObject;
    }

    @Async
    public void sendSeckillMsg(Long seckillId, String phone) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("seckillId", seckillId);
        jsonObject.put("phone", phone);
        spikeCommodityProducer.send(jsonObject);
    }
    // 采用redis数据库类型为 list类型 key为 商品库存id list 多个秒杀token

    public String addSpikeToken(Long seckillId, Long tokenQuantity) {
        // 1.验证参数
        if (seckillId == null) {
            return "商品库存id不能为空!";
        }
        if (tokenQuantity == null) {
            return "token数量不能为空!";
        }
        SeckillEntity seckillEntity = seckillMapper.findBySeckillId(seckillId);
        if (seckillEntity == null) {
            return "商品信息不存在!";
        }
        // 2.使用多线程异步生产令牌
        createSeckillToken(seckillId, tokenQuantity);
        return "令牌正在生成中.....";
    }

    @Async
    public void createSeckillToken(Long seckillId, Long tokenQuantity) {
        generateToken.createListToken("seckill_", seckillId + "", tokenQuantity);
    }
}