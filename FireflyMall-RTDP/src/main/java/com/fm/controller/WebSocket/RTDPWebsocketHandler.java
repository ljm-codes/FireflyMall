package com.fm.controller.WebSocket;

import POJO.ADS.*;
import POJO.DW.*;
import POJO.Login.AdminLoginRequest;
import POJO.Login.UserInfo;
import POJO.UserWebSocket.UserWSInfo;
import Tools.JWT.JwtTool;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.fm.POJO.WebSocketQueryInfo;
import com.fm.WSTool.WebSocket.SendWebSocketMassage;
import com.fm.mapper.DorisMapper;
import com.fm.service.AdminAuthService;
import com.fm.service.MerchantProcessService;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class RTDPWebsocketHandler extends AbstractWebSocketHandler {

    private final JwtTool jwtTool;
    private final AdminAuthService adminAuthService;
    private final DorisMapper dorisMapper;
    private final MerchantProcessService merchantProcessService;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final Snowflake snowflake = IdUtil.getSnowflake(1, 1);

    private enum Identity {
        ADMIN,
        CLARIFY,
        UNKNOWN,
        MERCHANT
    }

    @Override
    public void afterConnectionEstablished(@Nonnull WebSocketSession session) throws IOException {
        log.info("连接中...");
        WebSocketQueryInfo webSocketQueryInfo = getInfoBySession(session);
        String token = null;
        Long merchantId = null;
        if (webSocketQueryInfo != null) {
            token = webSocketQueryInfo.getToken();
            merchantId = webSocketQueryInfo.getId();
            // 用户申请人工服务时，会传入merchantId
            if (merchantId != null && !merchantProcessService.verifyMerchantId(merchantId)) {
                log.warn("不存在{}的商户ID", merchantId);
                SendWebSocketMassage.sendTextMessage(session, HttpStatus.NOT_FOUND.value(), "不存在ID 为" + merchantId + "的商户", null);
                session.close(CloseStatus.BAD_DATA.withReason("不存在ID 为" + merchantId + "的商户"));
                return;
            }
        }
        if (token == null && merchantId != null) {
            log.info("不允许出现token与merchantId都为null的情况");
            SendWebSocketMassage.sendTextMessage(session, HttpStatus.BAD_REQUEST.value(), "不允许出现token与merchantId都为null的情况", null);
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("不允许出现token与merchantId都为null的情况"));
            return;
        }
        Long uuid = 0L;   // value
        String key = ""; // key
        Object oldKey;  // 查看是否有重复的key
        if (token == null) {
            //  token为空时，为未知用户，将其生成唯一UUID，并存在Redis
            uuid = snowflake.nextId();
            log.info("为未知用户生成UUID为：{}", uuid);
            do {

                key = IdUtil.simpleUUID();
                oldKey = redisTemplate.opsForValue().get(key);

            } while (oldKey != null);

            redisTemplate.opsForValue().set(key, uuid);
        }
        String url = Objects.requireNonNull(session.getUri()).getPath().substring(9);
        Identity identity = parseToken(token, session);

        if (identity == null) {
            log.error("连接失败,token解析失败");
            SendWebSocketMassage.sendTextMessage(session, HttpStatus.UNAUTHORIZED.value(), "token解析失败", null);
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("token解析失败"));
            return;
        } else if (identity == Identity.UNKNOWN) {
            UserWSInfo.sessionNameMap.put(session, uuid);
            UserWSInfo.sessionMapById.put(uuid, session);
            SendWebSocketMassage.sendTextMessage(session, HttpStatus.OK.value(), "未知用户", "WebSocketId_" + key);
        }
        if (identity != Identity.UNKNOWN)
            SendWebSocketMassage.sendTextMessage(session, HttpStatus.OK.value(), "认证成功", null);

        handleRealTimeAndRecommendationWebSocketRequest(session, identity, uuid, url, webSocketQueryInfo);

    }

    @Override
    protected void handleTextMessage(@Nonnull WebSocketSession session, TextMessage message) throws Exception {
        log.info("收到文本消息:{}", message.getPayload());
        SendWebSocketMassage.sendTextMessage(session, 200, "收到文本消息", message.getPayload());
    }

    @Override
    protected void handleBinaryMessage(@Nonnull WebSocketSession session, @Nonnull BinaryMessage message) {
        log.info("收到二进制消息:{}", message);
    }

    @Override
    public void afterConnectionClosed(@Nonnull WebSocketSession session, @Nonnull CloseStatus status) {
        log.info("连接关闭,状态:{}", status);

        Long id = UserWSInfo.onlineUserMap.remove(session);
        if (id != null) {
            UserWSInfo.onlineUser.remove(id);
        }

        Long sessionId = UserWSInfo.sessionNameMap.remove(session);
        if (sessionId != null) {
            UserWSInfo.sessionMapById.remove(sessionId);
        }

    }

    private void handleRealTimeAndRecommendationWebSocketRequest(WebSocketSession session, Identity identity, Long uuid, String url, WebSocketQueryInfo webSocketQueryInfo) throws IOException {
        //  不是未知用户
        if (identity == Identity.UNKNOWN && url.equals("/Recommendation/unclearUserRecommendData")) {
            List<RecommendDataByDoris> unclearRecommendData = dorisMapper.getAllUnclearRecommendedData(String.valueOf(uuid));
            SendWebSocketMassage.sendTextMessage(session, HttpStatus.OK.value(), "未知用户推荐数据", unclearRecommendData);
        }
        if (identity == Identity.ADMIN) {
            switch (url) {
                //  实时处理
                case "/realTimeTopN/idTopN" -> {
                    List<IdTopNByDoris> idTopNData = dorisMapper.getAllIdTopNData();
                    SendWebSocketMassage.sendTextMessage(session, HttpStatus.OK.value(), "idTopN数据", idTopNData);
                }
                case "/realTimeTopN/searchTopN" -> {
                    List<SearchTopNByDoris> searchTopNData = dorisMapper.getAllSearchTopNData();
                    SendWebSocketMassage.sendTextMessage(session, HttpStatus.OK.value(), "searchTopN数据", searchTopNData);
                }
                case "/realTimeTopN/cartAdd", "/realTimeTopN/cartDelete" -> {
                    List<cartTopNByDoris> cartTopNData = dorisMapper.getAllCartTopNData();
                    SendWebSocketMassage.sendTextMessage(session, HttpStatus.OK.value(), "cartTopN数据", cartTopNData);
                }
                case "/realTimeTopN/createOrderTopN" -> {
                    List<CreateOrderTopNByDoris> createOrderTopNData = dorisMapper.getAllCreateOrderTopNData();
                    SendWebSocketMassage.sendTextMessage(session, HttpStatus.OK.value(), "createOrderTopN数据", createOrderTopNData);
                }
                case "/realTimeTopN/categoryTopN", "/realTimeTopN/brandTopN" -> {
                    List<CategoryOrBrandTopNByDoris> categoryOrBrandTopNData = dorisMapper.getAllCategoryOrBrandTopNData();
                    SendWebSocketMassage.sendTextMessage(session, HttpStatus.OK.value(), "categoryOrBrandTopN数据", categoryOrBrandTopNData);
                }
                case "/realTimeTopN/userIpRegionTopN" -> {
                    List<UserIpRegionTopNByDoris> userIpRegionTopNData = dorisMapper.getAllUserIpRegionTopNData();
                    SendWebSocketMassage.sendTextMessage(session, HttpStatus.OK.value(), "userIpRegionTopN数据", userIpRegionTopNData);
                }
                case "/realTimeTopN/convertCount" -> {
                    List<FunnelResult> convertCount = dorisMapper.getAllConvertCount();
                    SendWebSocketMassage.sendTextMessage(session, HttpStatus.OK.value(), "convertCount数据", convertCount);
                }
                case "/realTimeTopN/intervalTime" -> {
                    List<IntervalResult> intervalTime = dorisMapper.getAllIntervalTime();
                    SendWebSocketMassage.sendTextMessage(session, HttpStatus.OK.value(), "intervalTime数据", intervalTime);
                }
                case "/realTimeTopN/createOrder", "/realTimeTopN/cartAbandon", "/realTimeTopN/adClick",
                     "/realTimeTopN/adChange" -> {
                    List<SuccessRateWindowResult> typeRate = dorisMapper.getAllTypeRate();
                    SendWebSocketMassage.sendTextMessage(session, HttpStatus.OK.value(), "typeRate数据", typeRate);
                }
                case "/realTimeTopN/orderAmountTopN" -> {
                    List<Amount> orderAmountTopNData = dorisMapper.getAllOrderAmountTopNData();
                    SendWebSocketMassage.sendTextMessage(session, HttpStatus.OK.value(), "orderAmountTopN数据", orderAmountTopNData);
                }
                case "/realTimeTopN/payStatus" -> {
                    List<PayStatusByDoris> payStatus = dorisMapper.getAllPayStatus();
                    SendWebSocketMassage.sendTextMessage(session, HttpStatus.OK.value(), "订单支付状态", payStatus);
                }
            }
        }
        if (identity == Identity.CLARIFY) {
            // 普通用户
            switch (url) {
                case "/Recommendation/clarifyUserRecommendData" -> {
                    Long userId = UserWSInfo.sessionNameMap.get(session);
                    List<RecommendDataByDoris> clarifyRecommendData = dorisMapper.getAllClarifyRecommendedData(String.valueOf(userId));
                    SendWebSocketMassage.sendTextMessage(session, HttpStatus.OK.value(), "明确用户推荐数据", clarifyRecommendData);
                }
                case "/human/customerService" -> {
                    // 用户 -> 商户
                    try {
                        if (webSocketQueryInfo.getId() == null) {
                            log.error("customerService接口需要id参数");
                            SendWebSocketMassage.sendTextMessage(session, HttpStatus.BAD_REQUEST.value(), "customerService接口需要id参数", null);
                            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("customerService接口需要id参数"));
                            return;
                        }
                        if (webSocketQueryInfo.getText() == null || webSocketQueryInfo.getText().isEmpty()) {
                            SendWebSocketMassage.sendTextMessage(session, HttpStatus.BAD_REQUEST.value(), "不允许出现发送文本为空的情况", null);
                            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("不允许出现发送文本为空的情况"));
                            return;
                        }
                        WebSocketSession merchantSession = UserWSInfo.merchantSessionMap.get(webSocketQueryInfo.getId());
                        Long userId = UserWSInfo.sessionNameMap.get(session);
                        if (merchantSession == null) {
                            SendWebSocketMassage.sendTextMessage(session, HttpStatus.NOT_FOUND.value(), "对应的商户还未开始", null);
                            session.close(CloseStatus.BAD_DATA.withReason("对应的商户还未开始"));
                            return;
                        }
                        Map<String, Object> dataMap = new HashMap<>();
                        dataMap.put("userId", userId);
                        dataMap.put("text", webSocketQueryInfo.getText());
                        SendWebSocketMassage.sendTextMessage(merchantSession, HttpStatus.OK.value(), "响应成功", dataMap);
                    }catch (Exception e) {
                        log.error("出现错误：{}", e.getMessage());
                        // 这里使用RabbitMQ对消息缓存，暂时不写
                    }

                }
            }
        }
        if (identity ==  Identity.MERCHANT && url.equals("/customerService/user")){
            // 商户 -> 用户
            try {
                if (webSocketQueryInfo.getId() == null) {
                    log.error("用户 -> 商户时 customerService接口需要id参数");
                    SendWebSocketMassage.sendTextMessage(session, HttpStatus.BAD_REQUEST.value(), "customerService接口需要id参数", null);
                    session.close(CloseStatus.NOT_ACCEPTABLE.withReason("customerService接口需要id参数"));
                    return;
                }
                if (webSocketQueryInfo.getText() == null || webSocketQueryInfo.getText().isEmpty()) {
                    SendWebSocketMassage.sendTextMessage(session, HttpStatus.BAD_REQUEST.value(), "不允许出现发送文本为空的情况", null);
                    session.close(CloseStatus.NOT_ACCEPTABLE.withReason("不允许出现发送文本为空的情况"));
                    return;
                }
                WebSocketSession userSession = UserWSInfo.sessionMapById.get(webSocketQueryInfo.getId());
                Long merchantId = UserWSInfo.merchantIdMap.get(session);
                if (userSession == null) {
                    SendWebSocketMassage.sendTextMessage(session, HttpStatus.NOT_FOUND.value(), "用户走了", null);
                    session.close(CloseStatus.BAD_DATA.withReason("用户走了"));
                    return;
                }
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("merchantId", merchantId);
                dataMap.put("text", webSocketQueryInfo.getText());
                SendWebSocketMassage.sendTextMessage(userSession, HttpStatus.OK.value(), "响应成功", dataMap);
            }catch (Exception e) {
                log.error("在从 商户 -> 用户时 出现错误：{}", e.getMessage());
            }
        }

    }

    private WebSocketQueryInfo getInfoBySession(WebSocketSession session) {
        String query = Objects.requireNonNull(session.getUri()).getQuery();
        if (query == null) {
            return null;
        }
        String[] queryParams = query.split("&");
        WebSocketQueryInfo webSocketQueryInfo = new WebSocketQueryInfo();
        for (String param : queryParams) {
            if (param.startsWith("token=")) {
                String token = param.substring("token=".length());
                if (token.startsWith("Bearer ")) {
                    webSocketQueryInfo.setToken(token.substring("Bearer ".length()));
                }
            }
            if (param.startsWith("merchantId=")) {
                String merchantId = param.substring("merchantId=".length());
                webSocketQueryInfo.setId(Long.valueOf(merchantId));
            }
            if (param.startsWith("text=")) {
                String text = param.substring("text=".length());
                webSocketQueryInfo.setText(text);
            }
        }
        return webSocketQueryInfo;
    }

    private Identity parseToken(String token, WebSocketSession session) {
        try {
            if (token == null) {
                return Identity.UNKNOWN;
            }
            Claims userInfoClaims = jwtTool.parseToken(token);
            Long id = (Long) userInfoClaims.get("id");
            Integer permission = (Integer) userInfoClaims.get("permission");
            String name = (String) userInfoClaims.get("name");
            String type = (String) userInfoClaims.get("type");
            //  admin
            if (permission != null) {
                AdminLoginRequest adminData = adminAuthService.auth(name, permission);
                if (adminData == null) {
                    return null;
                }
                UserWSInfo.onlineUser.add(id);
                UserWSInfo.onlineUserMap.put(session, id);
                UserWSInfo.sessionMapById.put(id, session);
                UserWSInfo.sessionNameMap.put(session, id);
                return Identity.ADMIN;
            }
            if (type.equals("merchant")) {
                // 商户验证
                if (!merchantProcessService.verifyMerchantId(id)) return null;

                UserWSInfo.onlineUser.add(id);
                UserWSInfo.merchantSessionMap.put(id, session);
                UserWSInfo.merchantIdMap.put(session, id);
                return Identity.MERCHANT;
            }
            //  user
            UserInfo userInfo = adminAuthService.userAuth(id, name);
            if (userInfo == null) {
                return null;
            }
            UserWSInfo.onlineUser.add(id);
            UserWSInfo.onlineUserMap.put(session, id);
            UserWSInfo.sessionMapById.put(id, session);
            UserWSInfo.sessionNameMap.put(session, id);
            return Identity.CLARIFY;
        } catch (Exception e) {
            log.info("在认证token时，出现了问题：{}", e.getMessage());
            return null;
        }
    }


    /*         WebSocket 公有方法          */

    /**
     * 向所有在线用户发送文本消息
     *
     * @param topNDataStr 要发送的消息内容
     */
    public void sendTextMessageToAll(Object topNDataStr, String msg, String url) {
        for (WebSocketSession userSession : UserWSInfo.onlineUserMap.keySet()) {
            try {
                String uri = Objects.requireNonNull(userSession.getUri()).getPath().substring(9);
//                log.info("用户 {} 的 URI 为: {}", UserWSInfo.onlineUserMap.get(userSession), uri);
                if (uri.equals(url) || url.isEmpty()) {
                    SendWebSocketMassage.sendTextMessage(userSession, HttpStatus.OK.value(), msg, topNDataStr);
                }
            } catch (IOException e) {
                log.error("广播：发送消息到用户 {} 失败: {}", UserWSInfo.onlineUserMap.get(userSession), e.getMessage());
            }
        }

    }

    /**
     * 向指定用户发送文本消息
     *
     * @param toUserSession 目标用户的WebSocket会话
     * @param topNDataStr   要发送的消息内容
     */
    public void sendTextMessageToUserBySession(WebSocketSession toUserSession, Object topNDataStr) {
        try {
            for (WebSocketSession userSession : UserWSInfo.onlineUserMap.keySet()) {
                if (userSession.equals(toUserSession)) {
                    SendWebSocketMassage.sendTextMessage(userSession, HttpStatus.OK.value(), "topNData", topNDataStr);
                }
            }
        } catch (IOException e) {
            log.error("发送消息到用户 {} 失败: {}", UserWSInfo.onlineUserMap.get(toUserSession), e.getMessage());
        }
    }

    public void sendTextMessageToUserById(String userId, String msg, String url, Object data) {
        try {
            WebSocketSession userSession = UserWSInfo.sessionMapById.get(userId);
            if (userSession != null) {
                String uri = Objects.requireNonNull(userSession.getUri()).getPath().substring(9);
                if (uri.equals(url) || url.isEmpty()) {
                    SendWebSocketMassage.sendTextMessage(userSession, HttpStatus.OK.value(), msg, data);
                }
            }
        } catch (Exception e) {
            log.error("在方法 sendTextMessageToUserById 发送消息时，出现了问题：{}", e.getMessage());
        }
    }

}
