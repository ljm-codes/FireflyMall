package com.fm.GateWay.filters;

import Tools.JWT.JwtTool;
import com.fm.GateWay.config.GatewayConfig;
import com.fm.GateWay.config.MerchantConfig;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthFilter implements GlobalFilter, Ordered {

    private final JwtTool jwtTool;
    private final GatewayConfig gatewayConfig;
    private final MerchantConfig merchantConfig;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String path = exchange.getRequest().getURI().getPath();

        // 路径无需验证
        if (isWhitelistPath(path)) {
            log.info("路径{}在白名单中，无需验证", path);
            return chain.filter(exchange);
        }

        String token;
        String authorization = headers.getFirst("Authorization");
        log.info("Authorization header:{}", authorization);
        if (authorization != null && authorization.startsWith("Bearer ")) {
            token = authorization.substring(7);
        } else {
            token = null;
        }
        log.info("提取的token:{}", token);
        Claims claims;
        try {
            claims = jwtTool.parseToken(token);
        } catch (Exception e) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        // 添加一个对商户的限制操作
        if (claims.get("type").equals("merchant") && !isMerchantWhitelistPath(path)) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }

        log.info("claims:{}", claims);
        ServerWebExchange build = exchange.mutate()
                .request(
                        request -> request.header(
                                "UserInfo", claims.get("name").toString() +
                                        "," + claims.get("password").toString() +
                                        "," + claims.get("id").toString() +
                                        "," + claims.get("createTime").toString() +
                                        "," + claims.get("type").toString() +
                                        "," + token
                        )
                ).request(
                        request -> request.header(
                                "type", claims.get("type").toString()
                        )
                )
                .build();

        // 令牌验证通过，继续处理请求
        return chain.filter(build);
    }

    private boolean isMerchantWhitelistPath(String path) {
        for(String merchantWhitePath : merchantConfig.getMerchantWhiteList().getMerchantWhitePaths()) {
            if (antPathMatcher.match(merchantWhitePath, path)) {
                return true;
            }
        }
        return false;
    }

    private boolean isWhitelistPath(String path) {
        for (String whitelistPath : gatewayConfig.getWhitelist().getPaths()) {
            if (antPathMatcher.match(whitelistPath, path)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
