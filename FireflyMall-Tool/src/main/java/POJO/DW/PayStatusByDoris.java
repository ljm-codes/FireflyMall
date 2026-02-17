package POJO.DW;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayStatusByDoris {

    private String orderId;
    private String status;
    private LocalDateTime createTime;

}
