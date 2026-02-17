package com.fm.controller.Register;

import POJO.Result;
import com.fm.POJO.MerchantDetailedInfo;
import com.fm.service.MerchantRegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/merchant/register")
@RequiredArgsConstructor
@Slf4j
public class MerchantRegister {

    private final MerchantRegisterService merchantRegisterService;

    @PostMapping
    public Result register(@Valid @RequestBody MerchantDetailedInfo merchantDetailedInfo) {
        log.info("商户注册:{}", merchantDetailedInfo);
        if (merchantDetailedInfo == null) {
            return Result.error(HttpStatus.BAD_REQUEST, "商户注册信息不能为空");
        }
        Map<String, String> registerMsg = merchantRegisterService.register(merchantDetailedInfo);
        if (registerMsg == null) {
            return Result.error(HttpStatus.BAD_REQUEST, "注册失败");
        }
        if (registerMsg.get("status").equals("error")) {
            return Result.error(HttpStatus.BAD_REQUEST, registerMsg.get("msg"));
        }
        return Result.success(registerMsg);

    }

}
