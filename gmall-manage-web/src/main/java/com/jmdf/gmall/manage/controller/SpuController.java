package com.jmdf.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.jmdf.gmall.bean.PmsProductImage;
import com.jmdf.gmall.bean.PmsProductInfo;
import com.jmdf.gmall.bean.PmsProductSaleAttr;
import com.jmdf.gmall.service.SpuService;
import com.jmdf.gmall.util.PmsUploadUtil;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Random;

@Controller
@CrossOrigin
public class SpuController {

    @Reference
    SpuService spuService;

    @ResponseBody
    @RequestMapping("spuList")
    public List<PmsProductInfo> spuList(String catalog3Id){
        List<PmsProductInfo> pmsProductInfos =spuService.spuList(catalog3Id);
    return pmsProductInfos;
    }

    @ResponseBody
    @RequestMapping("saveSpuInfo")
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo){
        String msg="";
        boolean flag = spuService.saveSpuInfo(pmsProductInfo);
        if (flag){
            msg = "sucess";
        }else {
            msg = "fail";
        }
        return msg;
    }

    @ResponseBody
    @RequestMapping("fileUpload")
    public String fileUpload(@RequestParam("file")MultipartFile multipartFile){
        //将图片或者音频视频上传到分布式存储系统
        //将图片的存储路径返回。
       // String imageUrl= PmsUploadUtil.uploadImage(multipartFile);
        String imageUrl = "1.jpg";
        return imageUrl;
    }
    @RequestMapping("spuImageList")
    @ResponseBody
    public List<PmsProductImage> spuImageList(String spuId){

        List<PmsProductImage> pmsProductImages = spuService.spuImageList(spuId);
        return pmsProductImages;
    }


    @RequestMapping("spuSaleAttrList")
    @ResponseBody
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId){

        List<PmsProductSaleAttr> pmsProductSaleAttrs = spuService.spuSaleAttrList(spuId);
        return pmsProductSaleAttrs;
    }
}
