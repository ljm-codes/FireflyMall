package POJO.DW;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserIpRegionTopNByDoris {

    private String userIP;
    private String username;
    private Integer topN;
    private Integer count;
    private String windowStart;
    private String windowEnd;
    private LocalDateTime createTime;

}
