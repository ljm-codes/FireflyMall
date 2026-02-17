package com.fm.mapper;

import com.fm.POJO.MerchantInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MerchantProcessServiceMapper {

    @Select("select id, merchantName ,confidenceLevel from merchant where id = #{id}")
    MerchantInfo selectMerchantInfo(@Param("id") Long id);

}
