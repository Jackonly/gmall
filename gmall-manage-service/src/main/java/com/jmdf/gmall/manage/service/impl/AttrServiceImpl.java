package com.jmdf.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.jmdf.gmall.bean.PmsBaseAttrInfo;
import com.jmdf.gmall.bean.PmsBaseAttrValue;
import com.jmdf.gmall.bean.PmsBaseSaleAttr;
import com.jmdf.gmall.bean.PmsProductSaleAttr;
import com.jmdf.gmall.manage.mapper.PmsBaseAttrInfoMapper;
import com.jmdf.gmall.manage.mapper.PmsBaseAttrValueMapper;
import com.jmdf.gmall.manage.mapper.PmsBaseSaleAttrMapper;
import com.jmdf.gmall.service.AttrService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

@Service
public class AttrServiceImpl implements AttrService {

    @Autowired
    PmsBaseAttrInfoMapper pmsBaseAttrInfoMapper;

    @Autowired
    PmsBaseAttrValueMapper pmsBaseAttrValueMapper;

    @Autowired
    PmsBaseSaleAttrMapper pmsBaseSaleAttrMapper;

    @Override
    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id) {
        PmsBaseAttrInfo pmsBaseAttrInfo = new PmsBaseAttrInfo();
        pmsBaseAttrInfo.setCatalog3Id(catalog3Id);
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = pmsBaseAttrInfoMapper.select(pmsBaseAttrInfo);
        for (PmsBaseAttrInfo baseAttrInfo : pmsBaseAttrInfos) {

            List<PmsBaseAttrValue> pmsBaseAttrValues = new ArrayList<PmsBaseAttrValue>();
            PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
            pmsBaseAttrValue.setAttrId(baseAttrInfo.getId());
            pmsBaseAttrValues = pmsBaseAttrValueMapper.select(pmsBaseAttrValue);
            baseAttrInfo.setAttrValueList(pmsBaseAttrValues);
        }
        return pmsBaseAttrInfos;
    }

    @Override
    public boolean saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {
        if (StringUtils.isBlank(pmsBaseAttrInfo.getId())){
        try {
            //属性新增保存
            pmsBaseAttrInfoMapper.insertSelective(pmsBaseAttrInfo);
            //属性值新增保存
            for (PmsBaseAttrValue value: pmsBaseAttrInfo.getAttrValueList()){
                value.setAttrId(pmsBaseAttrInfo.getId());
                pmsBaseAttrValueMapper.insert(value);
            }
        }catch (Exception e){
            // 手动硬编码开启spring事务管理
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return false;
        } }else{
            try {
                //属性修改
            Example example = new Example(PmsBaseAttrInfo.class);
            example.createCriteria().andEqualTo("id",pmsBaseAttrInfo.getId());
            pmsBaseAttrInfoMapper.updateByExampleSelective(pmsBaseAttrInfo,example);

                //属性值修改（先删除旧的，插入新的带旧的属性值ID）
            PmsBaseAttrValue pmsBaseAttrValueDel = new PmsBaseAttrValue();
                pmsBaseAttrValueDel.setAttrId(pmsBaseAttrInfo.getId());
                pmsBaseAttrValueMapper.delete(pmsBaseAttrValueDel);
                //删除后将新的属性值插入
                List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
                for (PmsBaseAttrValue pmsBaseAttrValue1:attrValueList){
                    pmsBaseAttrValue1.setAttrId(pmsBaseAttrInfo.getId());
                pmsBaseAttrValueMapper.insertSelective(pmsBaseAttrValue1);
            }
        }catch (Exception e){
                // 手动硬编码开启spring事务管理
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    @Override
    public List<PmsBaseAttrValue> getAttrValueList(String attrId) {
        PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
        pmsBaseAttrValue.setAttrId(attrId);
        List<PmsBaseAttrValue> pmsBaseAttrValues = pmsBaseAttrValueMapper.select(pmsBaseAttrValue);
        return pmsBaseAttrValues;
    }

    @Override
    public List<PmsBaseSaleAttr> baseSaleAttrList() {
        List<PmsBaseSaleAttr> pmsBaseSaleAttrs = pmsBaseSaleAttrMapper.selectAll();
        return pmsBaseSaleAttrs;
    }
}
