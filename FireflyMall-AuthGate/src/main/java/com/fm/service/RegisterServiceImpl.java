package com.fm.service;

import POJO.Login.UserInfo;
import com.fm.mapper.RegisterMapper;
import com.fm.service.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final RegisterMapper registerMapper;

    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public boolean register(UserInfo userInfo) {
        String username = userInfo.getUsername();
        String password = userInfo.getPassword();
        // 检查用户名是否已存在
        if (registerMapper.selectByUsername(username) != 0) {
            return false;
        }
        // 注册用户
        String encodedPassword = passwordEncoder.encode(password);
        userInfo.setPassword(encodedPassword);
        LocalDateTime now = LocalDateTime.now();
        userInfo.setCreateTime(now);
        registerMapper.insertByUsernameAndPassword(userInfo);
        return true;
    }
}
