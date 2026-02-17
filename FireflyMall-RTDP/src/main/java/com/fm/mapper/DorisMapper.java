package com.fm.mapper;

import POJO.ADS.*;
import POJO.DW.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DorisMapper {

    List<IdTopNByDoris> getAllIdTopNData();

    List<SearchTopNByDoris> getAllSearchTopNData();

    List<cartTopNByDoris> getAllCartTopNData();

    List<CreateOrderTopNByDoris> getAllCreateOrderTopNData();

    List<CategoryOrBrandTopNByDoris> getAllCategoryOrBrandTopNData();

    List<UserIpRegionTopNByDoris> getAllUserIpRegionTopNData();

    List<FunnelResult> getAllConvertCount();

    List<IntervalResult> getAllIntervalTime();

    List<SuccessRateWindowResult> getAllTypeRate();

    List<Amount> getAllOrderAmountTopNData();

    List<PayStatusByDoris> getAllPayStatus();

    List<RecommendDataByDoris> getAllClarifyRecommendedData(String userId);

    List<RecommendDataByDoris> getAllUnclearRecommendedData(String userId);

}
