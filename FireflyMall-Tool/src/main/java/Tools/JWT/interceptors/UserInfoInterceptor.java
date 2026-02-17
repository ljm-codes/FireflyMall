package Tools.JWT.interceptors;

import POJO.Login.UserContext;
import POJO.Login.UserInfo;
import Tools.JWT.JwtTool;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 用户信息拦截器
 */
@Slf4j
@Component
@ConditionalOnClass(HandlerInterceptor.class)
@RequiredArgsConstructor
public class UserInfoInterceptor implements HandlerInterceptor {

    private final JwtTool jwtTool;
    private final RedisTemplate<String, String> redisTemplate;

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * preHandle方法 在Controller处理请求之前调用
     */
    @Override
    public boolean preHandle(HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) {
        // 从请求头中获取UserInfo
        String userInfo = request.getHeader("UserInfo");
        String identityType = request.getHeader("type");
        String key = request.getHeader("UUIDKey");
        String userIP = getUserIp(request, key);
        log.info("请求头中UserInfo:{}", userInfo);
        log.info("请求头中UserIP:{}", userIP);

        if (identityType.equals("admin")) {
            log.info("管理员操作，无需验证");
            return true;
        }

        String username = null;
        String password = null;
        String id;
        String createTime;
        String token = null;

        if (userInfo != null) {
            log.info("UserInfo:{}", userInfo);
            List<String> userInfoList = Arrays.asList(userInfo.split(","));
            // 从userInfoList中获取用户ID
            username = userInfoList.getFirst();
            password = userInfoList.get(1);
            id = userInfoList.get(2);
            createTime = userInfoList.get(3);
            String type = userInfoList.get(4);
            token = userInfoList.get(5);
            log.info("name:{},password:{},id:{},createTime:{},type:{},token:{}", username, password, id, createTime, type, token);
            // 存储用户信息到ThreadLocal
            try {
                UserContext.setUserInfo(new UserInfo(type, Long.valueOf(id), username, password, userIP, LocalDateTime.parse(createTime, DATE_TIME_FORMATTER), token));
            } catch (Exception e) {
                throw new RuntimeException("UserInfo格式错误");
            }
        } else {
            String authorization = request.getHeader("Authorization");
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                UserContext.setUserInfo(new UserInfo("unverified", null, username, password, userIP, null, token));
                return true;
            }
            token = authorization.substring(7);

            try {

                Claims claims = jwtTool.parseToken(token);
                username = claims.get("name").toString();
                password = claims.get("password").toString();
                id = claims.get("id").toString();
                createTime = claims.get("createTime").toString();

            } catch (Exception e) {
                throw new RuntimeException("token格式错误");
            }

            UserContext.setUserInfo(new UserInfo("unverified",Long.valueOf(id), username, password, userIP, LocalDateTime.parse(createTime, DATE_TIME_FORMATTER), token));
        }

        return true;
    }

    private String getUserIp(HttpServletRequest request, String key) {
        String sessionId = "";
        if (key != null) {
            String uuid = Objects.requireNonNull(redisTemplate.opsForValue().get(key));
            sessionId = "_" + uuid;
        }
        if (request == null) {
            return "unknown" + sessionId;
        }
        String ip = request.getHeader("X-Forwarded-For");
        if (ipCheck(ip)) {
            return ip + sessionId;
        }
        ip = request.getHeader("X-Real-IP");
        if (ipCheck(ip)) {
            return ip + sessionId;
        }
        ip = request.getRemoteAddr();
        log.info("ip:{}", ip);
        if (ipCheck(ip)) {
            return ip + sessionId;
        }
        return null;
    }

    private boolean ipCheck(String ip) {
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            return false;
        }
        String ipv4regex = "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$";
        return ip.matches(ipv4regex);
    }

    /**
     * afterCompletion方法 在Controller处理请求完成后调用
     */
    @Override
    public void afterCompletion(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler, Exception ex) {
        // 移除用户信息
        UserContext.removeUserInfo();
    }

}
