package com.fm.mapper;

import com.fm.POJO.MerchantDetailedInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MerchantRegisterServiceMapper {
    int selectByMerchantName(String merchantName);

    void insert(MerchantDetailedInfo merchantDetailedInfo);
}
