package POJO.ADS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FunnelResult {

    private String windowStart;
    private String windowEnd;
    private String step; // 步骤名称
    private Long count; // 该步骤的用户数

}
