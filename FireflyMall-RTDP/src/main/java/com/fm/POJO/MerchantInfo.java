package com.fm.POJO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantInfo {

    private Long id;
    private String merchantName;
    private String merchantPassword;
    private String province;
    private String city;
    private String district;
    private String address;
    private String phone;
    private String email;
    private String remark;
    private Integer confidenceLevel;

}