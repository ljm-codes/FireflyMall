package POJO.DW;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchTopNByDoris {

    private String keyword;
    private Long id;
    private String category;
    private String brand;
    private Integer topN;
    private Integer quantity;
    private Integer price;
    private String image;
    private String spec;
    private Integer sold;
    private Integer isAD;
    private Integer count;
    private String windowStart;
    private String windowEnd;
    private LocalDateTime createTime;

}
