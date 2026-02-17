package com.fm.Config.Jwt;

import Tools.JWT.JwtTool;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static Tools.JWT.JwtTool.KEY_ROTATION_MS;

@Component
@RequiredArgsConstructor
public class TokenKey {

    private final JwtTool jwtTool;

    @Scheduled(initialDelay = KEY_ROTATION_MS, fixedRate = KEY_ROTATION_MS)
    public void refreshToken() throws InterruptedException {
        Thread.sleep(100);
        jwtTool.rotateKey();
    }
}
