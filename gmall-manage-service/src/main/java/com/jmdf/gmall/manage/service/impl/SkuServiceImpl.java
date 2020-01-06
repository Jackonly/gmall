package com.jmdf.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.jmdf.gmall.bean.PmsSkuAttrValue;
import com.jmdf.gmall.bean.PmsSkuImage;
import com.jmdf.gmall.bean.PmsSkuInfo;
import com.jmdf.gmall.bean.PmsSkuSaleAttrValue;
import com.jmdf.gmall.manage.mapper.PmsSkuAttrValueMapper;
import com.jmdf.gmall.manage.mapper.PmsSkuImageMapper;
import com.jmdf.gmall.manage.mapper.PmsSkuInfoMapper;
import com.jmdf.gmall.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.jmdf.gmall.service.SkuService;
import com.jmdf.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    PmsSkuInfoMapper pmsSkuInfoMapper;

    @Autowired
    PmsSkuAttrValueMapper pmsSkuAttrValueMapper;

    @Autowired
    PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;

    @Autowired
    PmsSkuImageMapper pmsSkuImageMapper;

    @Autowired
    RedisUtil redisUtil;


    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {

        // 插入skuInfo
        int i = pmsSkuInfoMapper.insertSelective(pmsSkuInfo);
        String skuId = pmsSkuInfo.getId();

        // 插入平台属性关联
        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
            pmsSkuAttrValue.setSkuId(skuId);
            pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);
        }

        // 插入销售属性关联
        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
            pmsSkuSaleAttrValue.setSkuId(skuId);
            pmsSkuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
        }

        // 插入图片信息
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
        for (PmsSkuImage pmsSkuImage : skuImageList) {
            pmsSkuImage.setSkuId(skuId);
            pmsSkuImageMapper.insertSelective(pmsSkuImage);
        }


    }
    @SuppressWarnings("all")
    public PmsSkuInfo getSkuFromDbById(String skuId) {
        // sku商品对象
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        PmsSkuInfo skuInfo = pmsSkuInfoMapper.selectOne(pmsSkuInfo);

        // sku的图片集合
        PmsSkuImage pmsSkuImage = new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        List<PmsSkuImage> pmsSkuImages = pmsSkuImageMapper.select(pmsSkuImage);
        skuInfo.setSkuImageList(pmsSkuImages);
        return skuInfo;
    }

    /**
     * redis 缓存查询
     * 缓存击穿   高并发环境下redis某个热点数据失效，请求打到mysql数据库上              解决方案: 利用mysql的分布式锁和队列
     * 缓存穿透：高并发访问mysql不存在的值，导致mysql崩溃。                            解决方案：如果数据库不存在该sku，设置空值，有效期未10分钟，给redis
     * 缓存雪崩：高并发环境下，热点数据在同一时间失效。导致数据库压力过大，崩溃。      解决方案：设置热点数据不同的失效时间。
     * @param skuId
     * @return
     */
    @Override
    @SuppressWarnings("all")
    public PmsSkuInfo getSkuById(String skuId) {
        // sku商品对象
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        Jedis jedis =null;
        try {
            //链接缓存
             jedis = redisUtil.getJedis();
            //查询缓存
            String skuKey = "sku:"+skuId+":info";
            //如果缓存没有命中，查询mysql
            String skuJson = jedis.get(skuKey);
            if (StringUtils.isNotBlank(skuJson)) {
                pmsSkuInfo = JSON.parseObject(skuJson, PmsSkuInfo.class);
            }else {
                //如果缓存未命中，查询mysql

                //设置分布式锁
                String token = UUID.randomUUID().toString();

               String ok  = jedis.set("sku"+skuId+":lock",token,"nx","px",10*1000);//拿到锁的线程10s过期时间。
                if (StringUtils.isNotBlank(ok)&&ok.equals("ok")){
                    pmsSkuInfo  = getSkuFromDbById(skuId);
                    //mysql查询结果存入redis
                    if (pmsSkuInfo!=null){
                        jedis.set("sku:"+skuId+":info",JSON.toJSONString(pmsSkuInfo));

                    }else {
                        //缓存穿透：高并发访问mysql不存在的值，导致mysql崩溃。
                        //如果数据库不存在该sku，设置空值，有效期未10分钟，给redis
                        jedis.setex("sku:"+skuId+":info",60*10,"");
                    }
                    //Lua脚本
                    String script ="if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                    jedis.eval(script, Collections.singletonList(jedis.get("sku" + skuId + ":lock")),Collections.singletonList(token));

                    /*
                    //访问数据库后，存入redis后，删除分布式锁
                    String lockToken = jedis.get("sku" + skuId + ":lock");
                    if (StringUtils.isNotBlank(lockToken)&&lockToken.equals(token)){
                        //如果在此过期了，下一个请求获取锁，第一个人还是删除了别人的锁。
                        jedis.del("sku"+skuId+":lock");
                    }
                    */
                }else {
                    //分布式锁设置失败，自旋（在该线程睡眠几秒后，重新尝试获取分布式锁）

                    try {
                        Thread.sleep(3000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    /*
                     * finally {
                     *  return getSkuById(skuId);
                     * }
                     *
                     * */
                    return getSkuById(skuId);
                }




                    }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return pmsSkuInfo;
    }

    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId) {
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();

        pmsSkuInfo.setProductId(productId);

        List<PmsSkuInfo> select = pmsSkuInfoMapper.select(pmsSkuInfo);

        for (PmsSkuInfo sku :select){
            PmsSkuSaleAttrValue pmsSkuSaleAttrValue = new PmsSkuSaleAttrValue();
            pmsSkuSaleAttrValue.setSkuId(sku.getId());
            List<PmsSkuSaleAttrValue> select1 = pmsSkuSaleAttrValueMapper.select(pmsSkuSaleAttrValue);
            sku.setSkuSaleAttrValueList(select1);
        }


       /* PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        // 链接缓存
        Jedis jedis = redisUtil.getJedis();
        // 查询缓存
        String skuKey = "sku:"+skuId+":info";
        String skuJson = jedis.get(skuKey);

        if(StringUtils.isNotBlank(skuJson)){//if(skuJson!=null&&!skuJson.equals(""))
            pmsSkuInfo = JSON.parseObject(skuJson, PmsSkuInfo.class);
        }else{
            // 如果缓存中没有，查询mysql

            // 设置分布式锁
            String OK = jedis.set("sku:" + skuId + ":lock", "1", "nx", "px", 10);
            if(StringUtils.isNotBlank(OK)&&OK.equals("OK")){
                // 设置成功，有权在10秒的过期时间内访问数据库
                pmsSkuInfo =  getSkuByIdFromDb(skuId);
                if(pmsSkuInfo!=null){
                    // mysql查询结果存入redis
                    jedis.set("sku:"+skuId+":info",JSON.toJSONString(pmsSkuInfo));
                }else{
                    // 数据库中不存在该sku
                    // 为了防止缓存穿透将，null或者空字符串值设置给redis
                    jedis.setex("sku:"+skuId+":info",60*3,JSON.toJSONString(""));
                }
            }else{
                // 设置失败，自旋（该线程在睡眠几秒后，重新尝试访问本方法）
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return getSkuById(skuId);
            }
        }
        jedis.close();
        return pmsSkuInfo;*/
       return select;
    }
}
