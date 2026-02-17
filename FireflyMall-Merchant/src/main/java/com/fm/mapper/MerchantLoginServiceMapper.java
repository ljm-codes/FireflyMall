package com.fm.mapper;

import com.fm.POJO.MerchantDetailedInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MerchantLoginServiceMapper {

    MerchantDetailedInfo selectMerchantInfo(MerchantDetailedInfo merchantDetailedInfo);

    Integer selectIsOldKeyByVersion(String version);
}
