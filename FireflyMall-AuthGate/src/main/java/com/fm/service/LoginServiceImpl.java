package com.fm.service;

import POJO.Login.UserInfo;
import Tools.JWT.JwtTool;
import com.fm.mapper.LoginMapper;
import com.fm.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginServiceImpl implements LoginService {

    private final BCryptPasswordEncoder passwordEncoder;

    private final LoginMapper loginMapper;

    private final JwtTool jwtTool;

    @Override
    public UserInfo login(UserInfo userInfo) {
        if (userInfo.getUsername() == null || userInfo.getPassword() == null) {
            return null;
        }
        UserInfo userInfoByUsernameAndPassword = loginMapper.selectUserInfoByUsernameAndPassword(userInfo);
        if (userInfoByUsernameAndPassword == null) {
            return null;
        }
        if (!passwordEncoder.matches(userInfo.getPassword(), userInfoByUsernameAndPassword.getPassword())) {
            return null;
        }
        return userInfoByUsernameAndPassword;
    }

    @Override
    public Map<String, Object> refreshToken(UserInfo userInfo) {
        Map<String, Object> Infomap = new HashMap<>();
        String token = userInfo.getToken();
        String version = jwtTool.parseKeyVersionFromToken(token);
        Integer isOldKey = loginMapper.selectIsOldKeyByVersion(version);
        String newToken = null;
        if(isOldKey == 1){
            Map<String,Object> map = new HashMap<>();
            map.put("username", userInfo.getUsername());
            map.put("password", userInfo.getPassword());
            map.put("id", userInfo.getId());
            map.put("type", "user");
            newToken = "Bearer " +  jwtTool.createToken(map);
        }
        Infomap.put("username", userInfo.getUsername());
        Infomap.put("password", userInfo.getPassword());
        Infomap.put("id", userInfo.getId());
        Infomap.put("type", "user");
        Infomap.put("token", newToken);
        return Infomap;
    }
}
