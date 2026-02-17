package com.fm.controller;

import POJO.Login.UserInfo;
import POJO.Result;
import com.fm.service.RegisterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/register")
@Slf4j
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    @PostMapping
    public Result register(@RequestBody UserInfo userInfo) {
        log.info("用户注册:{}", userInfo);
        boolean registerMsg = registerService.register(userInfo);
        if (registerMsg) {
            return Result.success(HttpStatus.OK, "注册成功");
        } else {
            return Result.error(HttpStatus.BAD_REQUEST, "注册失败，用户名已存在");
        }
    }

}
