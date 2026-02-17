package POJO.ADS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopNData {

    private Long goodsId = 0L;
    private String userId = "";
    private String keyword = "";
    private String message = "";
    private Integer sold = 0;
    private String category = "";
    private String brand = "";
    private String AD = "";
    private Integer count = 1;
    private Integer topN = 0;

}
