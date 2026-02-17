package com.fm.service.Impl;

import com.fm.POJO.MerchantDetailedInfo;
import com.fm.mapper.MerchantRegisterServiceMapper;
import com.fm.service.MerchantRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MerchantRegisterServiceImpl implements MerchantRegisterService {

    private final MerchantRegisterServiceMapper merchantRegisterServiceMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Map<String, String> register(MerchantDetailedInfo merchantDetailedInfo) {
        Map<String, String> registerResultMap = new HashMap<>();
        String merchantName = merchantDetailedInfo.getMerchantName();
        String merchantPassword = merchantDetailedInfo.getMerchantPassword();
        try {
            if (merchantRegisterServiceMapper.selectByMerchantName(merchantName) != 0) {
                registerResultMap.put("msg", "商户名称已存在");
                registerResultMap.put("status", "error");
                return registerResultMap;
            }
            String encodedPassword = passwordEncoder.encode(merchantPassword);
            merchantDetailedInfo.setMerchantPassword(encodedPassword);
            if (merchantDetailedInfo.getRemark() == null || merchantDetailedInfo.getRemark().isEmpty()) {
                merchantDetailedInfo.setRemark("该商户未填写备注");
            }
            merchantDetailedInfo.setConfidenceLevel(0);
            merchantRegisterServiceMapper.insert(merchantDetailedInfo);
            registerResultMap.put("msg", "注册成功");
            registerResultMap.put("status", "ok");
            return registerResultMap;
        }catch (Exception e){
            registerResultMap.put("status", "error");
            registerResultMap.put("msg", "注册失败, 原因为：" + e.getMessage());
            return registerResultMap;
        }
    }

}
