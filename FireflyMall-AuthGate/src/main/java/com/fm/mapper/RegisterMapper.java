package com.fm.mapper;

import POJO.Login.UserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RegisterMapper {
    Integer selectByUsername(String username);

    void insertByUsernameAndPassword(UserInfo userInfo);
}
