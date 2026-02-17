package POJO.DW;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class cartTopNByDoris {

    private Long goodsId;
    private String userId;
    private Integer topN;
    private String category;
    private String brand;
    private String type;
    private String keyword;
    private String message;
    private Integer sold;
    private String AD;
    private Integer count;
    private String windowStart;
    private String windowEnd;
    private LocalDateTime createTime;

}
