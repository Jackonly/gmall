package com.jmdf.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.jmdf.gmall.bean.PmsBaseAttrInfo;
import com.jmdf.gmall.bean.PmsBaseAttrValue;
import com.jmdf.gmall.bean.PmsBaseSaleAttr;
import com.jmdf.gmall.bean.PmsProductSaleAttr;
import com.jmdf.gmall.service.AttrService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@CrossOrigin
public class AttrController {

    @Reference
    AttrService attrService;

    @RequestMapping("baseSaleAttrList")
    @ResponseBody
    public List<PmsBaseSaleAttr> baseSaleAttrList(){
        List<PmsBaseSaleAttr> pmsBaseSaleAttrs = attrService.baseSaleAttrList();
        return pmsBaseSaleAttrs;
    }


    @RequestMapping("attrInfoList")
    @ResponseBody
    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id){
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = attrService.attrInfoList(catalog3Id);
        return pmsBaseAttrInfos;
    }

    @RequestMapping("saveAttrInfo")
    @ResponseBody
    public String saveAttrInfo(@RequestBody PmsBaseAttrInfo pmsBaseAttrInfo){
        String msg="";
      boolean flag = attrService.saveAttrInfo(pmsBaseAttrInfo);
      if (flag){
          msg = "sucess";
      }else {
          msg = "fail";
      }
        return msg;
    }
    @RequestMapping("getAttrValueList")
    @ResponseBody
    public List<PmsBaseAttrValue> getAttrValueList(String attrId){
        List<PmsBaseAttrValue> pmsBaseAttrValues= attrService.getAttrValueList(attrId);
        return pmsBaseAttrValues;
    }
}
