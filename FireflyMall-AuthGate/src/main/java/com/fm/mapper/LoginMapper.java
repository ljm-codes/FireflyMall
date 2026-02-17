package com.fm.mapper;

import POJO.Login.UserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginMapper {
    UserInfo selectUserInfoByUsernameAndPassword(UserInfo userInfo);

    Integer selectIsOldKeyByVersion(String version);
}
