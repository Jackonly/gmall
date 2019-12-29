package com.jmdf.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.jmdf.gmall.bean.UmsMember;
import com.jmdf.gmall.bean.UmsMemberReceiveAddress;
import com.jmdf.gmall.service.UserService;
import com.jmdf.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import com.jmdf.gmall.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    /**
     * 通用mapper
     */
    @Autowired
    UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;

    @Override
    public List<UmsMember> getAllUser() {
        return userMapper.selectAllUser();
    }


    //通用mapper根据传入对象不为空的字段去条件查询，返回对象的集合。
    @Override
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId) {
        /**
         * 方式一：Example
         */
        Example example = new Example(UmsMemberReceiveAddress.class);
        example.createCriteria().andEqualTo("memberId",memberId);
        List<UmsMemberReceiveAddress> umsMemberReceiveAddressesList1 = umsMemberReceiveAddressMapper.selectByExample(example);
        /**
         * 方式二：UmsMemberReceiveAddress
         */
        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setMemberId(memberId);

        List<UmsMemberReceiveAddress> umsMemberReceiveAddressesList2 = umsMemberReceiveAddressMapper.select(umsMemberReceiveAddress);
        return umsMemberReceiveAddressesList1;
    }
}
