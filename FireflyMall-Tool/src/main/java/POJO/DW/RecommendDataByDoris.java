package POJO.DW;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendDataByDoris {

    private String userId;
    private Long goodsId;
    private String startTime;
    private String endTime;
    private LocalDateTime createTime;

}
