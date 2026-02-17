package com.fm.service;

import POJO.Login.UserInfo;

import java.util.Map;

public interface LoginService {

    UserInfo login(UserInfo userInfo);

    Map<String, Object> refreshToken(UserInfo userInfo);
}
