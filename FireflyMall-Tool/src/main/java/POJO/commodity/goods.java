package POJO.commodity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class goods {
    private Long id; // 商品ID
    private String name; // 商品名称
    private Integer price; // 商品价格(单位：分)
    private Integer stock; // 商品库存
    private String image; // 商品图片URL
    private String category; // 商品分类
    private String brand; // 商品品牌
    private Map<String, Object> spec; // 商品规格
    private Integer sold; // 商品的销售量
    private Integer comment_count; // 商品的评论数量
    private Integer isAD; // 是否为广告商品(0：不是广告，1：是广告)
    private Integer status; // 商品状态(1：正常，2：下架，3：删除)

    private Integer quantity; // 购买的商品数量
}
