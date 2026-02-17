package com.fm.GateWay.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;

@Slf4j
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsFilter() {
        // 1. 创建CORS配置对象
        CorsConfiguration config = new CorsConfiguration();
        // 允许所有来源访问
        config.addAllowedOriginPattern("*");
        // 允许所有请求头
        config.addAllowedHeader("*");
        // 允许所有HTTP方法（GET, POST, PUT, DELETE等）
        config.addAllowedMethod("*");
        // 允许携带cookie
        config.setAllowCredentials(true);
        // 设置预检请求的有效期（秒）
        config.setMaxAge(3600L);

        // 2. 创建路径匹配器
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有路径应用CORS配置
        source.registerCorsConfiguration("/**", config);
        log.info("CORS配置已注册，允许所有来源访问，所有请求头，所有HTTP方法，允许携带cookie，预检请求有效期3600秒");
        // 3. 返回CORS过滤器
        return new CorsWebFilter(source);
    }
}
