package com.fm.service.Impl;

import Tools.JWT.JwtTool;
import com.fm.POJO.MerchantDetailedInfo;
import com.fm.mapper.MerchantLoginServiceMapper;
import com.fm.service.MerchantLoginService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MerchantLoginServiceImpl implements MerchantLoginService {

    private final MerchantLoginServiceMapper merchantLoginServiceMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTool jwtTool;

    @Override
    public boolean getMerchantInfo(MerchantDetailedInfo merchantDetailedInfo) {
        if (merchantDetailedInfo.getMerchantName() == null || merchantDetailedInfo.getMerchantPassword() == null) {
            return false;
        }
        MerchantDetailedInfo merchantDetailedInfoFromDB = merchantLoginServiceMapper.selectMerchantInfo(merchantDetailedInfo);
        if (merchantDetailedInfoFromDB == null){
            return false;
        }
        return passwordEncoder.matches(merchantDetailedInfo.getMerchantPassword(), merchantDetailedInfoFromDB.getMerchantPassword());
    }

    @Override
    public Map<String, Object> refreshToken(String token) {
        Map<String, Object> merchantInfoMap = new HashMap<>();
        Integer isOldKey;
        try {
            String version = jwtTool.parseKeyVersionFromToken(token);
            isOldKey = merchantLoginServiceMapper.selectIsOldKeyByVersion(version);
        }catch (Exception e){
            return null;
        }
        if(isOldKey == 1){
            Claims merchantClaims = jwtTool.parseToken(token);
            Map<String,Object> map = new HashMap<>();
            map.put("username", merchantClaims.get("name"));
            map.put("id", merchantClaims.get("id"));
            map.put("type", "merchant");
            String newToken = "Bearer " +  jwtTool.createToken(map);
            merchantInfoMap.put("token", newToken);
            merchantInfoMap.put("replace", true);
            return merchantInfoMap;
        }
        merchantInfoMap.put("replace", false);
        merchantInfoMap.put("token", null);
        return merchantInfoMap;
    }

}
