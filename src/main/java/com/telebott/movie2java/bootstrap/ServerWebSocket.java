package com.telebott.movie2java.bootstrap;

import com.alibaba.fastjson.JSONObject;

import com.telebott.movie2java.dao.AuthDao;
import com.telebott.movie2java.dao.SmsRecordDao;
import com.telebott.movie2java.entity.User;
import com.telebott.movie2java.service.SmsBaoService;
import com.telebott.movie2java.util.WebSocketUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;

@ServerEndpoint(value = "/")
@Component
@Getter
public class ServerWebSocket {
    @Autowired
    private AuthDao authDao;
    @Autowired
    public SmsRecordDao smsRecordDao;
    @Autowired
    private SmsBaoService smsBaoService;
    private static ServerWebSocket self;
    /**
     * 在线人数
     */
    public static int onlineNumber = 0;

    /**
     * 所有的对象
     */
    public static List<ServerWebSocket> webSockets = new CopyOnWriteArrayList<ServerWebSocket>();
    /**
     * 会话
     */
    private Session session;
    private InetSocketAddress remoteAddress;
    private long count = 0;
    private String token;
    private User user;
    private final Timer timer = new Timer();

    public static long getOnline(){
        long counts = 0;
        for (ServerWebSocket socket : webSockets) {
            counts += socket.count;
        }
        return counts;
    }
    @PostConstruct
    public void init() {
//        System.out.println("websocket 加载");
        self = this;
    }

    /**
     * 出现错误
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
//        error.printStackTrace();
    }

    /**
     * 建立连接
     *
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) {
        remoteAddress = WebSocketUtil.getRemoteAddress(session);
        onlineNumber++;
        webSockets.add(this);
        this.session = session;
//        session.getBasicRemote().send;
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                sendMessage("H");
//            }
//        }, 1000, 1000 * 10);
        System.out.println(remoteAddress.getAddress());
    }

    /**
     * 连接关闭
     */
    @OnClose
    public void onClose() {
        onlineNumber--;
        webSockets.remove(this);
        timer.cancel();
//        System.out.println("有连接关闭！ 当前在线人数" + onlineNumber);
    }

    /**
     * 收到客户端的消息
     *
     * @param message 消息
     * @param session 会话
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        JSONObject object = (JSONObject) JSONObject.parse(message);
        count = object.getLong("count");
        System.out.println(count);
    }

    /**
     * 发送消息
     *
     * @param message 消息
     */

    public void sendMessage(String message) {
        if (session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送消息至指定在线人
     *
     * @param message
     * @param token
     */
//    public static void sendTo(WebSocketData message, String token) {
//        try {
//            for (ServerWebSocket socket : webSockets) {
//                if (socket.token.equals(token)) {
//                    socket.sendMessage(message);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 发送消息至所有人
     *
     * @param message
     */
//    public static void sendTo(WebSocketData message) {
//        try {
//            for (ServerWebSocket socket : webSockets) {
//                socket.sendMessage(message);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
