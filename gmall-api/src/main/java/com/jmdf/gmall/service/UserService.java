package com.jmdf.gmall.service;

import com.jmdf.gmall.bean.UmsMember;
import com.jmdf.gmall.bean.UmsMemberReceiveAddress;

import java.util.List;

public interface UserService {
    List<UmsMember> getAllUser();

    List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId);
}
