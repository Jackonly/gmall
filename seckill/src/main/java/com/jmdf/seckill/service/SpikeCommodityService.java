package com.jmdf.seckill.service;

import com.alibaba.fastjson.JSONObject;
import com.jmdf.seckill.entity.OrderEntity;
import com.jmdf.seckill.entity.SeckillEntity;
import com.jmdf.seckill.mapper.SeckillMapper;
import com.jmdf.seckill.product.SpikeCommodity;
import com.jmdf.seckill.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 库存超卖
 */
@Service
public class SpikeCommodityService {

    @Autowired
    private SeckillMapper seckillMapper;


    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    SpikeCommodity spikeCommodity;

    @Transactional
    public JSONObject spike(String phone, Long seckillId) {
        JSONObject jsonObject = new JSONObject();
        // 1.验证参数
        if (StringUtils.isEmpty(phone)) {
            jsonObject.put("error","手机号码不能为空!");
            return jsonObject;
        }
        if (seckillId == null) {
            jsonObject.put("error","库存id不能为空!");
            return jsonObject;
        }
        // >>>限制用户访问频率 比如10秒中只能访问一次
        Boolean resultNx = redisUtil.setNx(phone, seckillId + "", 10l);
        if (!resultNx) {
            jsonObject.put("error","该用户操作过于频繁,请稍后重试!");
            return jsonObject;
        }
        jsonObject = spikeCommodity.getOrder(phone,seckillId);
        return jsonObject;
    }

}
