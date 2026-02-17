package com.fm.WSTool.WebSocket;

import POJO.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class SendWebSocketMassage {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void sendTextMessage(WebSocketSession session, int code, String message, Object data) throws IOException {
        if(session.isOpen()){
            Result result = new Result(code, message, data);
            String sendMessageJson = objectMapper.writeValueAsString(result);
            session.sendMessage(new TextMessage(sendMessageJson));
        }else{
            log.warn("当前的WebSocket会话已关闭");
        }

    }

}
