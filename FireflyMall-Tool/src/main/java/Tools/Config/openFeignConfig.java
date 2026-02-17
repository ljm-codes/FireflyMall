package Tools.Config;

import POJO.Login.UserContext;
import POJO.Login.UserInfo;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

public class openFeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            UserInfo userInfo = UserContext.getUserInfo();
            if (userInfo != null) {
                String userInfoStr = userInfo.getUsername() +
                                "," + userInfo.getPassword() +
                                "," + userInfo.getId() +
                                "," + userInfo.getCreateTime().toString() +
                                "," + userInfo.getToken();
                requestTemplate.header("UserInfo", userInfoStr);
            }
        };
    }
}
