package com.fm.POJO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantDetailedInfo {

    private Long id;
    @NotBlank(message = "商户名称不能为空")
    private String merchantName;
    @NotBlank(message = "商户密码不能为空")
    private String merchantPassword;
    @NotBlank(message = "省份不能为空")
    private String province;
    @NotBlank(message = "城市不能为空")
    private String city;
    @NotBlank(message = "区县不能为空")
    private String district;
    @NotBlank(message = "详细地址不能为空")
    private String address;
    @NotBlank(message = "手机号不能为空")
    private String phone;
    @NotBlank(message = "邮箱不能为空")
    private String email;
    private String remark;
    private Integer confidenceLevel;

}
