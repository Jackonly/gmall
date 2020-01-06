package com.jmdf.seckill.mapper;

import com.jmdf.seckill.entity.OrderEntity;
import com.jmdf.seckill.entity.SeckillEntity;
import org.apache.ibatis.annotations.*;


@Mapper
public interface SeckillMapper {

    /**
     * 基于版本号形式实现乐观锁
     *
     * @param seckillId
     * @return
     */
    @Update("update meite_seckill set inventory=inventory-1 ,version=version+1 where  seckill_id=#{seckillId} and version=#{version} and inventory>0;")
    int optimisticVersionSeckill(@Param("seckillId") Long seckillId, @Param("version") Long version);


    /**
     * 查询秒杀订单
     * @param seckillId
     * @return
     */
    @Select("SELECT seckill_id AS seckillId,name as name,inventory as inventory,start_time as startTime,end_time as endTime,create_time as createTime,version as version from meite_seckill where seckill_id=#{seckillId}")
    SeckillEntity findBySeckillId(Long seckillId);

    /**
     * 插入秒杀订单
     * @param orderEntity
     * @return
     */
    @Insert("INSERT INTO `meite_order` VALUES (#{seckillId},#{userPhone}, '1', now());")
    int insertOrder(OrderEntity orderEntity);
}
