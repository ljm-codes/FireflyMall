package com.fm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.fm", "Tools.KafkaTool",  "Tools.JWT"})
@MapperScan("Tools.JWT.JwtMapper")
@MapperScan("com.fm.mapper")
public class ProductShowApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductShowApplication.class, args);
    }
}
