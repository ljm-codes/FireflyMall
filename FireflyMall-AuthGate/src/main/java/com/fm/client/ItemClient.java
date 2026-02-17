package com.fm.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("FireflyMall-AuthGate")
public interface ItemClient {
}
