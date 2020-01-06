package com.jmdf.gmall.service;

import com.jmdf.gmall.bean.PmsBaseAttrInfo;
import com.jmdf.gmall.bean.PmsBaseAttrValue;
import com.jmdf.gmall.bean.PmsBaseSaleAttr;

import java.util.List;

public interface AttrService {
    List<PmsBaseAttrInfo> attrInfoList(String catalog3Id);

    boolean saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo);

    List<PmsBaseAttrValue> getAttrValueList(String attrId);

    List<PmsBaseSaleAttr> baseSaleAttrList();
}
