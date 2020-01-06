package com.jmdf.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.jmdf.gmall.bean.PmsProductImage;
import com.jmdf.gmall.bean.PmsProductInfo;
import com.jmdf.gmall.bean.PmsProductSaleAttr;
import com.jmdf.gmall.bean.PmsProductSaleAttrValue;
import com.jmdf.gmall.manage.mapper.PmsProductImageMapper;
import com.jmdf.gmall.manage.mapper.PmsProductSaleAttrMapper;
import com.jmdf.gmall.manage.mapper.PmsProductSaleAttrValueMapper;
import com.jmdf.gmall.manage.mapper.SpuMapper;
import com.jmdf.gmall.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    SpuMapper spuMapper;

    @Autowired
    PmsProductSaleAttrMapper pmsProductSaleAttrMapper;

    @Autowired
    PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;

    @Autowired
    PmsProductImageMapper pmsProductImageMapper;

    @Override
    public List<PmsProductInfo> spuList(String catalog3Id) {
        PmsProductInfo pmsProductInfo = new PmsProductInfo();
        pmsProductInfo.setCatalog3Id(catalog3Id);
        List<PmsProductInfo> productInfos = spuMapper.select(pmsProductInfo);
        return productInfos;
    }

    @Override
    public boolean saveSpuInfo(PmsProductInfo pmsProductInfo) {
            //保存PmsProductInfo表
            spuMapper.insertSelective(pmsProductInfo);

        for (PmsProductSaleAttr attr:pmsProductInfo.getSpuSaleAttrList()){
            //保存PmsProductSaleAttr表
            attr.setProductId(pmsProductInfo.getId());
            pmsProductSaleAttrMapper.insertSelective(attr);
            //保存PmsProductSaleAttrValue表
            for (PmsProductSaleAttrValue value :attr.getSpuSaleAttrValueList()){
                value.setProductId(pmsProductInfo.getId());
                pmsProductSaleAttrValueMapper.insertSelective(value);
            }
        }
        //保存PmsProductImage表
        for (PmsProductImage image :pmsProductInfo.getSpuImageList()){
            image.setProductId(pmsProductInfo.getId());
            pmsProductImageMapper.insertSelective(image);
        }

        return false;
    }

    @Override
    public List<PmsProductImage> spuImageList(String spuId) {
        PmsProductImage pmsProductImage = new PmsProductImage();
        pmsProductImage.setProductId(spuId);
        List<PmsProductImage> pmsProductImages = pmsProductImageMapper.select(pmsProductImage);

        return pmsProductImages;
    }

    @Override
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId) {
        PmsProductSaleAttr pmsProductSaleAttr = new PmsProductSaleAttr();
        pmsProductSaleAttr.setProductId(spuId);
        List<PmsProductSaleAttr> PmsProductSaleAttrs = pmsProductSaleAttrMapper.select(pmsProductSaleAttr);

        for (PmsProductSaleAttr productSaleAttr : PmsProductSaleAttrs) {
            PmsProductSaleAttrValue pmsProductSaleAttrValue = new PmsProductSaleAttrValue();
            pmsProductSaleAttrValue.setProductId(spuId);
            pmsProductSaleAttrValue.setSaleAttrId(productSaleAttr.getSaleAttrId());// 销售属性id用的是系统的字典表中id，不是销售属性表的主键
            List<PmsProductSaleAttrValue> pmsProductSaleAttrValues = pmsProductSaleAttrValueMapper.select(pmsProductSaleAttrValue);
            productSaleAttr.setSpuSaleAttrValueList(pmsProductSaleAttrValues);
        }

        return PmsProductSaleAttrs;
    }

    @Override
    public List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String productId, String skuId) {
        List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductSaleAttrMapper.selectSpuSaleAttrListCheckBySku(productId,skuId);
        return pmsProductSaleAttrs;
    }

}
