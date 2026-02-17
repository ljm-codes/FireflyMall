package POJO.Cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class shoppingCart {

    private Long id; // 商品ID
    private Integer quantity; // 商品数量

}
