package com.fm.POJO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketQueryInfo {

    private String token;

    // 人工服务需要的基本信息
    private Long id;
    // 可能是用户ID也有可能是商户ID
    private String text;

}
