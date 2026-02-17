package com.fm.configuration;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlipayAutoConfiguration {
    @Bean
    public AlipayClient alipayClient(AlipayConfig alipayConfig) {
        return new DefaultAlipayClient(
                alipayConfig.getGATEWAY(),
                alipayConfig.getAPPID(),
                alipayConfig.getAPP_PRIVATE_KEY(),
                alipayConfig.getFORMAT(),
                alipayConfig.getCHARSET(),
                alipayConfig.getALIPAY_PUBLIC_KEY(),
                alipayConfig.getSIGN_TYPE()
        );
    }
}
