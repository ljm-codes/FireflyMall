package com.fm;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.fm", "Tools.JWT", "Tools.JWT.JwtMapper"})
@MapperScans({
        @MapperScan("Tools.JWT.JwtMapper"),
        @MapperScan("com.fm.mapper")
})
public class FireflyMallMerchantApplication {

    public static void main(String[] args) {
        SpringApplication.run(FireflyMallMerchantApplication.class, args);
    }

}
