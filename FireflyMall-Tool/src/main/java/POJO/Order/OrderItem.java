package POJO.Order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

    private Long id;
    private Long orderId;
    private Long goodsId;
    private Integer quantity;

}
