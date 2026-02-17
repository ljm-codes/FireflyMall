package com.fm.mapper;

import POJO.Login.AdminLoginRequest;
import POJO.Login.UserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminAuthMapper {

    AdminLoginRequest login(String name);

    AdminLoginRequest auth(String adminName, Integer permission);

    UserInfo userAuth(Long id, String name);
}
