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
public class RTDPApplication {
    public static void main(String[] args) {
        SpringApplication.run(RTDPApplication.class, args);
    }
}
