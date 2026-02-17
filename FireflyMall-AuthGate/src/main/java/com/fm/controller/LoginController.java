package com.fm.controller;

import POJO.Login.UserContext;
import POJO.Login.UserInfo;
import POJO.Result;
import Tools.JWT.JwtTool;
import com.fm.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final LoginService loginService;

    private final JwtTool jwtTool;

    @PostMapping
    public Result login(@RequestBody UserInfo userInfo) {
        log.info("用户登录:{}", userInfo);
        UserInfo loginMsg = loginService.login(userInfo);
        if (loginMsg == null) {
            return Result.error(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("name", loginMsg.getUsername());
        map.put("password", loginMsg.getPassword());
        map.put("id", loginMsg.getId());
        map.put("createTime", loginMsg.getCreateTime());
        map.put("type", "user");
        String token = "Bearer " + jwtTool.createToken(map);
        UserInfo getUserMsg = new UserInfo("user", loginMsg.getId(), loginMsg.getUsername(), loginMsg.getPassword(), "", loginMsg.getCreateTime(), token);
        return Result.success(getUserMsg);
    }

    /**
     * 从UserInfo中获取token与基本信息，并返回新的token（若UserInfo中的token的将要过期时）
     * */
    @GetMapping("/token")
    public Result token() {
        UserInfo userInfo = UserContext.getUserInfo();
        if (userInfo == null) {
            return Result.error(HttpStatus.UNAUTHORIZED, "用户未登录");
        }
        Map<String, Object> newToken = loginService.refreshToken(userInfo);
        return Result.success(newToken);
    }

}
