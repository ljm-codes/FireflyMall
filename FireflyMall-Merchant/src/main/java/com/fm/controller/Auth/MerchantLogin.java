package com.fm.controller.Auth;

import POJO.Result;
import Tools.JWT.JwtTool;
import com.fm.POJO.MerchantDetailedInfo;
import com.fm.service.MerchantLoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/merchant/login")
@RequiredArgsConstructor
@Slf4j
public class MerchantLogin {

    private final MerchantLoginService merchantLoginService;
    private final JwtTool jwtTool;

    @PostMapping
    public Result login(@RequestBody MerchantDetailedInfo merchantDetailedInfo) {
        log.info("商户登录:{}", merchantDetailedInfo);
        boolean loginSuccess = merchantLoginService.getMerchantInfo(merchantDetailedInfo);
        if (!loginSuccess) {
            return Result.error(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("name",  merchantDetailedInfo.getMerchantName());
        map.put("id", merchantDetailedInfo.getId());
        map.put("type", "merchant");
        String token = "Bearer " + jwtTool.createToken(map);
        return  Result.success(token);
    }

    @GetMapping("/token")
    public Result loginByToken(@RequestParam("token") String token) {
        if (token == null || token.isEmpty()) {
            return Result.error(HttpStatus.UNAUTHORIZED, "token不能为空");
        }
        Map<String, Object> tokenReplaceInfoMap = merchantLoginService.refreshToken(token);
        if  (tokenReplaceInfoMap == null) {
            return Result.error(HttpStatus.UNAUTHORIZED, "token无效");
        }
        return Result.success(tokenReplaceInfoMap);
    }

}
