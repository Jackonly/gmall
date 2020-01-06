package com.jmdf.consumer.entity;

import java.util.Date;

public class SeckillEntity {
    private long seckillId;
    //商品名称
    private String name;
    //库存数量
    private Integer inventory;
    //秒杀开启时间
    private Date startTime;
    //秒杀结束时间
    private Date endTime;
    //创建时间
    private Date createTime;
    //版本号
    private Long version;

    public SeckillEntity() {
    }

    public SeckillEntity(long seckillId, String name, Integer inventory, Date startTime, Date endTime, Date createTime, Long version) {
        this.seckillId = seckillId;
        this.name = name;
        this.inventory = inventory;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createTime = createTime;
        this.version = version;
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getInventory() {
        return inventory;
    }

    public void setInventory(Integer inventory) {
        this.inventory = inventory;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "SeckillEntity{" +
                "seckillId=" + seckillId +
                ", name='" + name + '\'' +
                ", inventory=" + inventory +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", createTime=" + createTime +
                ", version=" + version +
                '}';
    }
}
