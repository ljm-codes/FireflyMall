package com.fm;

import Tools.Config.openFeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableFeignClients(basePackages = {"com.fm"},defaultConfiguration = openFeignConfig.class)
@ComponentScan(basePackages = {"com.fm", "Tools.JWT", "Tools.JWT.JwtMapper","Tools"})
@MapperScans({
        @MapperScan("Tools.JWT.JwtMapper"),
        @MapperScan("com.fm.mapper")
})
public class AuthGateApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthGateApplication.class, args);
    }
}
