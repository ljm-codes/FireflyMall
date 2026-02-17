package com.fm.service;

import com.fm.POJO.MerchantInfo;
import com.fm.mapper.MerchantProcessServiceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantProcessServiceImpl implements MerchantProcessService {

    private final MerchantProcessServiceMapper merchantProcessServiceMapper;

    @Override
    public boolean verifyMerchantId(Long id) {
        MerchantInfo info = merchantProcessServiceMapper.selectMerchantInfo(id);
        return info != null;
    }

}
