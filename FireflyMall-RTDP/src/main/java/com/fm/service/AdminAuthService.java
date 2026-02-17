package com.fm.service;

import POJO.Login.AdminLoginRequest;
import POJO.Login.UserInfo;

public interface AdminAuthService {
    AdminLoginRequest login(AdminLoginRequest adminLoginRequest);

    AdminLoginRequest auth(String adminName, Integer permission);

    UserInfo userAuth(Long id, String name);
}
