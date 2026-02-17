package POJO.Login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminLoginRequest {
    private Integer id;
    private String adminName;
    private String adminPassword;
    private Integer permission;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private Integer modifyPermission;
}
