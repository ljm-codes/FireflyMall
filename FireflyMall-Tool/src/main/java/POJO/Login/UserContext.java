package POJO.Login;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserContext {

    private static final ThreadLocal<UserInfo> userContext = new ThreadLocal<>();

    public static UserInfo getUserInfo() {
        return userContext.get();
    }

    public static void setUserInfo(UserInfo userInfo) {
        userContext.set(userInfo);
    }

     public static void removeUserInfo() {
        userContext.remove();
    }

}
