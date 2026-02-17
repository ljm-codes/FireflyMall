package POJO.Login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    private String type;
    private Long id;
    private String username;
    private String password;
    private String userIP;
    private LocalDateTime createTime;

    private String token;
}
