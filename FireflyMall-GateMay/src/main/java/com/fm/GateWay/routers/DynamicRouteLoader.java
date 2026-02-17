package com.fm.GateWay.routers;

import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicRouteLoader {

    private final NacosConfigManager nacosConfigManager;
    private final RouteDefinitionWriter routeDefinitionWriter;

    private final String dataId = "gateway-routes.json";
    private final String group = "DEFAULT_GROUP";

    private final Set<String> routeIds = new HashSet<>();

    @PostConstruct
    private void initRouteLoaderListener() throws NacosException {
        String configInfo = nacosConfigManager.getConfigService()
                .getConfigAndSignListener(dataId, group, 5000, new Listener() {
                    @Override
                    public Executor getExecutor() {
                        return null;
                    }

                    /**
                     * 配置变更时调用
                     * @param s 配置内容
                     */
                    @Override
                    public void receiveConfigInfo(String s) {
                        updateRouteConfig(s);
                    }
                });
        updateRouteConfig(configInfo);
    }

    private void updateRouteConfig(String configInfo) {
        try {
            log.info("更新路由配置:{}", configInfo);
            List<RouteDefinition> routeDefinitions = JSONUtil.toList(configInfo, RouteDefinition.class);
            if (!routeIds.isEmpty()) {
                routeIds.forEach(routeId ->
                        routeDefinitionWriter.delete(Mono.just(routeId)).subscribe()
                );
                routeIds.clear();
            }
            for (RouteDefinition routeDefinition : routeDefinitions) {
                routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe();
                routeIds.add(routeDefinition.getId());
            }
        } catch (Exception e) {
            log.error("解析路由配置失败", e);
        }
    }

}
