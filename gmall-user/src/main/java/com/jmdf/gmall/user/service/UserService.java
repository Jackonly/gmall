package com.jmdf.gmall.user.service;

import com.jmdf.gmall.user.bean.UmsMember;
import com.jmdf.gmall.user.bean.UmsMemberReceiveAddress;

import java.util.List;

public interface UserService {
    List<UmsMember> getAllUser();

    List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId);
}
