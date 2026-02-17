package com.fm.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "alipay")
public class AlipayConfig {

    private String GATEWAY = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";

    private String APPID;

    private String APP_PRIVATE_KEY;

    private String ALIPAY_PUBLIC_KEY;

    private String SIGN_TYPE;

    private String CHARSET = "UTF-8";

    private String FORMAT = "json";

    private String RETURN_URL = "http://localhost:8080/pay/Alipay/getStatus";

    private boolean SANDBOX = true;
}
