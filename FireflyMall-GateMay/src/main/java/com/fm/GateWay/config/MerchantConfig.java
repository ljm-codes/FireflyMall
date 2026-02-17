package com.fm.GateWay.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <h1>商户白名单路径</h1>
 * 只允许商户访问指定路径，其他路径均会拒绝
 * */
@Data
@Profile("merchant")
@Component
@RefreshScope
@ConfigurationProperties(prefix = "merchant")
public class MerchantConfig {

    private MerchantWhiteList merchantWhiteList;

    @Data
    public static class MerchantWhiteList {
        private List<String> merchantWhitePaths;
    }

}
