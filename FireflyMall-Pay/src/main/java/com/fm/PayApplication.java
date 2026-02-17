package com.fm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.fm", "Tools.KafkaTool", "Tools.JWT.interceptors", "Tools.JWT"})
@MapperScan("com.fm.Mapper")
@MapperScan("Tools.JWT.JwtMapper")
public class PayApplication {
    public static void main(String[] args) {
        SpringApplication.run(PayApplication.class, args);
    }
}
