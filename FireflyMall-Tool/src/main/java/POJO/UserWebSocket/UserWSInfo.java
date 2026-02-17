package POJO.UserWebSocket;

import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UserWSInfo {

    public static final Set<Long> onlineUser = new HashSet<>();  // id
    public static final Map<WebSocketSession, Long> onlineUserMap = new ConcurrentHashMap<>();  // session -> id
    public static final Map<Long, WebSocketSession> sessionMapById = new ConcurrentHashMap<>(); // id -> session
    public static final Map<WebSocketSession, Long> sessionNameMap = new ConcurrentHashMap<>();   //  用于存储 session -> id
    public static final Map<Long, WebSocketSession> merchantSessionMap = new ConcurrentHashMap<>();
    public static final Map<WebSocketSession, Long> merchantIdMap = new ConcurrentHashMap<>();

}
