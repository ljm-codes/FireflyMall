package POJO.ADS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Amount {

    private String userId;
    private Double amount;
    private LocalDateTime createTime;
    private Integer topN;

    private String windowStart;
    private String windowEnd;

}
