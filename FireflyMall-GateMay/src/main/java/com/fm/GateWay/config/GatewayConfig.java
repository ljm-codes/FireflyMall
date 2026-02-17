package com.fm.GateWay.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Profile("gateway")
@Component
@RefreshScope
@ConfigurationProperties(prefix = "gateway")
public class GatewayConfig {

    private Whitelist whitelist;

    @Data
    public static class Whitelist {
        private List<String> paths;
    }

}
