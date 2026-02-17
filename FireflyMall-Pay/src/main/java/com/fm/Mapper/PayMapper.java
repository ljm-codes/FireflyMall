package com.fm.Mapper;

import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

@Mapper
public interface PayMapper {

    void updateOrderStatus(String orderId, int orderStatus, LocalDateTime payTime);

}
