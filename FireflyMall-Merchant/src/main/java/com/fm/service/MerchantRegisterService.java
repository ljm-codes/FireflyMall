package com.fm.service;

import com.fm.POJO.MerchantDetailedInfo;

import java.util.Map;

public interface MerchantRegisterService {

    Map<String, String> register(MerchantDetailedInfo merchantDetailedInfo);

}
