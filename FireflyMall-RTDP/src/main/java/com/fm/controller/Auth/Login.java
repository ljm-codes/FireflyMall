package com.fm.controller.Auth;

import POJO.Login.AdminLoginRequest;
import POJO.Result;
import Tools.JWT.JwtTool;
import com.fm.service.AdminAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("auth/login")
@RequiredArgsConstructor
public class Login {

    private final AdminAuthService adminAuthService;
    private final JwtTool jwtTool;

    @PostMapping
    public Result login(@RequestBody AdminLoginRequest adminLoginRequest) {
        log.info("登录");
        log.info("adminLoginRequest {}", adminLoginRequest);
        AdminLoginRequest loginSuccess = adminAuthService.login(adminLoginRequest);
        if (loginSuccess == null) {
            return Result.error(HttpStatus.BAD_REQUEST, "登录失败");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("id", loginSuccess.getId());
        map.put("name", loginSuccess.getAdminName());
        map.put("permission", loginSuccess.getPermission());
        map.put("type", "admin");
        String token = jwtTool.createToken(map);
        map.put("token", "Bearer " + token);
        return Result.success(map);
    }

    // 未来添加对token的登录检验

}
