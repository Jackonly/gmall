package com.jmdf.seckill.controller;

import com.alibaba.fastjson.JSONObject;
import com.jmdf.seckill.entity.SeckillEntity;
import com.jmdf.seckill.product.SpikeCommodity;
import com.jmdf.seckill.product.SpikeCommodityProducer;
import com.jmdf.seckill.service.SpikeCommodityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpikeCommodityController {

    @Autowired
    private SpikeCommodityService spikeCommodityService;

    @Autowired
    SpikeCommodity spikeCommodity;

    @Autowired
    SpikeCommodityProducer send;

    @RequestMapping("/spike")
    public JSONObject spike(String phone, Long seckillId){
        JSONObject jsonObject = spikeCommodityService.spike(phone,seckillId);
        return jsonObject;
    }

    @RequestMapping("/addSpikeToken")
    public String addSpikeToken(Long seckillId, Long tokenQuantity){
        String str = spikeCommodity.addSpikeToken(seckillId,tokenQuantity);
        return str;
    }
    @RequestMapping("/getOrder")
    public JSONObject getOrder(String phone, Long seckillId){
        JSONObject str = spikeCommodity.getOrder(phone,seckillId);
        return str;
    }
    @RequestMapping("/send")
    public String send(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name","我是晁文奇！");
        send.send(jsonObject);
        return "sucess";
    }

}
