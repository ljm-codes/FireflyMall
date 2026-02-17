package com.fm.service;

import com.fm.POJO.MerchantDetailedInfo;

import java.util.Map;

public interface MerchantLoginService {

    boolean getMerchantInfo(MerchantDetailedInfo merchantDetailedInfo);

    Map<String, Object> refreshToken(String token);
}
