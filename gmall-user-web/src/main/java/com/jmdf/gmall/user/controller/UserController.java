package com.jmdf.gmall.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.jmdf.gmall.bean.UmsMember;
import com.jmdf.gmall.bean.UmsMemberReceiveAddress;
import com.jmdf.gmall.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class UserController {

    @Reference
    UserService userService;

    @RequestMapping("index")
    @ResponseBody
    public String index(){
        return "hello user";
    }

    /**
     * 全部用户
     * @return
     */
    @RequestMapping("getAllUser")
    @ResponseBody
    public List<UmsMember> getAllUser(){
        List<UmsMember>  list    = userService.getAllUser();
        return list;
    }

    @RequestMapping("getReceiveAddressByMemberId")
    @ResponseBody
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId){
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = userService.getReceiveAddressByMemberId(memberId);
        return umsMemberReceiveAddresses;
    }
}
