package POJO.Order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfo {

    private Long orderId; // 订单ID
    private Long userId; // 用户ID
    private String receiptAddress; // 收货地址
    private String remark; // 订单备注
    private Double totalAmount; // 订单总金额
    private Integer status = 0; // 订单状态
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime payTime; // 支付时间
    private LocalDateTime deliveryTime; // 发货时间
    private LocalDateTime receiveTime; // 收货时间

}
