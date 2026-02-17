package com.fm.service;

import POJO.Login.AdminLoginRequest;
import POJO.Login.UserInfo;
import com.fm.mapper.AdminAuthMapper;
import com.fm.service.AdminAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements AdminAuthService {

    private final AdminAuthMapper adminAuthMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public AdminLoginRequest login(AdminLoginRequest adminLoginRequest) {

        if(adminLoginRequest.getAdminPassword() == null || adminLoginRequest.getAdminName() == null){
            log.error("AdminLoginRequest is null");
            return null;
        }
        String name = adminLoginRequest.getAdminName();

        AdminLoginRequest login = adminAuthMapper.login(name);

        if(login == null){
            log.error("AdminLoginRequest is null");
            return null;
        }

        if(!bCryptPasswordEncoder.matches(adminLoginRequest.getAdminPassword(), login.getAdminPassword())){
            log.error("AdminLoginRequest password is not match");
            return null;
        }

        return login;

    }

    @Override
    public AdminLoginRequest auth(String adminName, Integer permission) {
        return adminAuthMapper.auth(adminName, permission);
    }

    @Override
    public UserInfo userAuth(Long id, String name) {
        return adminAuthMapper.userAuth(id, name);
    }

}
