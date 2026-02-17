package Tools.JWT;

import Tools.JWT.JwtMapper.keyMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTool {

    private final ObjectMapper objectMapper;
    private final keyMapper keyMapper;

    // 密钥存储
    private static final Map<String, SecretKey> KeyStore = new ConcurrentHashMap<>();
    // 当前密钥版本
    private String KeyVersion = "v1"; // 最新密钥版本

    public static final long TOKEN_EXPIRATION_MS = 35 * 24 * 60 * 60 * 1000L; // 35天

    public static final long KEY_ROTATION_MS = 30 * 24 * 60 * 60 * 1000L; // 30天

    public static final long TRANSITION_MS = 5 * 24 * 60 * 60 * 1000L; // 5天

    /**
     * 初始化密钥
     */
    @PostConstruct
    public void init() {
        initKeyVersion();
        String KeyValue = keyMapper.selectKeyByVersion(KeyVersion);
        if (KeyValue == null) {
            throw new RuntimeException("密钥不存在");
        }
        KeyStore.put(KeyVersion, Keys.hmacShaKeyFor(Base64.getDecoder().decode(KeyValue)));
    }

    private void initKeyVersion() {
        KeyVersion = keyMapper.selectKeyVersionByCreateTime();
        if (KeyVersion == null) {
            throw new RuntimeException("密钥版本不存在");
        }
    }

    /**
     * 创建JWT令牌
     *
     * @param claims 令牌负载
     * @return JWT令牌
     */
    public String createToken(Map<String, Object> claims) {
        SecretKey Key = KeyStore.get(KeyVersion);
        log.info("使用的密钥版本:{}", KeyVersion);
        log.info("使用的密钥:{}", Base64.getEncoder().encodeToString(Key.getEncoded()));
        KeyStore.forEach((key, value) -> log.info("{}:{}", key, Base64.getEncoder().encodeToString(value.getEncoded())));
        return Jwts.builder()
                .header().add("KeyVersion", KeyVersion).and()
                .json(new JacksonSerializer<>(objectMapper))
                .claims(claims)
                .signWith(Key)
                .expiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_MS))
                .compact();
    }

    /**
     * 解析JWT令牌
     *
     * @param token JWT令牌
     * @return 令牌负载
     */
    public Claims parseToken(String token) {
        String KeyVersion = parseKeyVersionFromToken(token);
        SecretKey Key = KeyStore.get(KeyVersion);
        if (Key == null) {
            throw new RuntimeException("密钥不存在");
        }
        log.info("KeyVersion:{}", KeyVersion);
        log.info("Key:{}", Base64.getEncoder().encodeToString(Key.getEncoded()));
        try {
            return Jwts.parser()
                    .verifyWith(Key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.error("JWT令牌已过期:", e);
            throw new RuntimeException("JWT令牌已过期", e);
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            log.error("JWT令牌格式错误:", e);
            throw new RuntimeException("JWT令牌格式错误", e);
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            log.error("JWT令牌不支持:", e);
            throw new RuntimeException("JWT令牌不支持", e);
        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.error("JWT令牌签名错误:", e);
            throw new RuntimeException("JWT令牌签名错误", e);
        }
    }

    /**
     * 从令牌中解析密钥版本
     *
     * @param token JWT令牌
     * @return 密钥版本
     */
    public String parseKeyVersionFromToken(String token) {
        String[] parts = token.split("\\.");
        String header = new String(Base64.getUrlDecoder().decode(parts[0]));
        // 解析JWT头
        try {
            JsonNode headerJson = objectMapper.readTree(header);
            return headerJson.get("KeyVersion").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("解析JWT头失败", e);
        }
    }

    /**
     * 生成新的密钥,将密钥存放在MySQL中
     *
     */
    public void generateNewKey() {
        SecretKey NewKey = Jwts.SIG.HS256.key().build();
        String NewKeyVersion = "v" + (Integer.parseInt(KeyVersion.substring(1)) + 1);
        keyMapper.insertKeyByVersion(NewKeyVersion, Base64.getEncoder().encodeToString(NewKey.getEncoded()));

        log.info("密钥：{}", Base64.getEncoder().encodeToString(NewKey.getEncoded()));
        log.info("密钥版本：{}", NewKeyVersion);
    }

    /**
     * 密钥轮换
     */
    public void rotateKey() {
        String OldKeyVersion = KeyVersion;
        String NewKeyVersion = "v" + (Integer.parseInt(KeyVersion.substring(1)) + 1);

        String NewKey = keyMapper.selectKeyByVersion(NewKeyVersion);
        KeyStore.put(NewKeyVersion, Keys.hmacShaKeyFor(Base64.getDecoder().decode(NewKey)));

        KeyVersion = NewKeyVersion;
        log.info("密钥版本更新为：{}，新密钥为：{}", KeyVersion, Base64.getEncoder().encodeToString(Keys.hmacShaKeyFor(Base64.getDecoder().decode(NewKey)).getEncoded()));
        removeOldKeys(OldKeyVersion);
    }

    /**
     * 定期删除过期密钥
     *
     * @param OldKeyVersion 过期密钥版本
     */
    public void removeOldKeys(String OldKeyVersion) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> {
            KeyStore.remove(OldKeyVersion);
            log.info("删除过期密钥：{}", OldKeyVersion);
            keyMapper.deleteKeyByVersion(OldKeyVersion);
            executorService.shutdown();
        }, TRANSITION_MS, TimeUnit.MILLISECONDS);
    }
}