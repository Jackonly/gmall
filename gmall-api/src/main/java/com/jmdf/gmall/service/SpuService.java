package com.jmdf.gmall.service;

import com.jmdf.gmall.bean.PmsProductImage;
import com.jmdf.gmall.bean.PmsProductInfo;
import com.jmdf.gmall.bean.PmsProductSaleAttr;

import java.util.List;

public interface SpuService {
    List<PmsProductInfo> spuList(String catalog3Id);

    boolean saveSpuInfo(PmsProductInfo pmsProductInfo);


    List<PmsProductImage> spuImageList(String spuId);

    List<PmsProductSaleAttr> spuSaleAttrList(String spuId);

    List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String productId, String skuId);
}
