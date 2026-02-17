package com.fm.POJO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AfterSalesExchange {

    private Long id;
    private Long applyId;

    @NotBlank(message = "新商品名称不能为空")
    private String new_sku_name;
    @NotNull(message = "新商品规格不能为空")
    private Map<String, Object> new_spec_values;
    @NotNull(message = "兑换数量不能为空")
    private Integer exchange_num;
    @NotBlank(message = "物流单号不能为空")
    private String delivery_waybill_no;
    @NotBlank(message = "快递公司不能为空")
    private String delivery_express_code;
    @NotBlank(message = "快递公司名称不能为空")
    private String delivery_express_name;
    @NotNull(message = "发货时间不能为空")
    private LocalDateTime delivery_time;
    @NotBlank(message = "收货人姓名不能为空")
    private String receiver_name;
    @NotBlank(message = "收货人手机号不能为空")
    private String receiver_phone;
    @NotBlank(message = "收货人地址不能为空")
    private String receiver_address;
    @NotBlank(message = "签收时间不能为空")
    private String sign_time;
    @NotNull(message = "签收状态不能为空")
    private Integer sign_status;
    @NotNull(message = "价格差不能为空")
    private Float price_diff;

    private LocalDateTime create_time;
    private LocalDateTime update_time;

}
