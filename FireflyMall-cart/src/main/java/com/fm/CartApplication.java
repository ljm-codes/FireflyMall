package com.fm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.fm", "Tools.JWT.interceptors","Tools.KafkaTool", "Tools.JWT"})
@MapperScan("com.fm.Mapper")
@MapperScan("Tools.JWT.JwtMapper")
public class CartApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartApplication.class, args);
    }
}
