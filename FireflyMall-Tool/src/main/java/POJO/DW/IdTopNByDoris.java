package POJO.DW;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdTopNByDoris{

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
    private String windowStart;
    private String windowEnd;
    private LocalDateTime createTime;

}
