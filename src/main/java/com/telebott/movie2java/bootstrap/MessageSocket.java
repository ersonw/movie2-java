package com.telebott.movie2java.bootstrap;

import com.telebott.movie2java.dao.AuthDao;
import com.telebott.movie2java.entity.User;
import com.telebott.movie2java.util.AESUtils;
import com.telebott.movie2java.util.TimeUtil;
import com.telebott.movie2java.util.WebSocketUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.transport.support.EnumerationIterator;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

@ServerEndpoint(value = "/message/{token}")
@Component
@Getter
@Slf4j
public class MessageSocket {
    @Autowired
    private AuthDao authDao;
    private static MessageSocket self;
    public static int onlineNumber = 0;
    public static List<MessageSocket> webSockets = new CopyOnWriteArrayList<MessageSocket>();

    private InetSocketAddress remoteAddress;
    private final Timer timer = new Timer();
    private Session session;
    private User user;

    @PostConstruct
    public void init() {
        log.info("Message WebSocket 加载");
        self = this;
    }
    public void deleteUser(User user){
        for (MessageSocket socket: webSockets) {
            if (socket.user != null && socket.user.getId() == user.getId()){
                try {
                    socket.session.close(new CloseReason( CloseReason.CloseCodes.CANNOT_ACCEPT, "other login"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        this.user = user;
    }
    @OnOpen
    public void onOpen(Session session, @PathParam("token")String token) {
        User user = self.authDao.findUserByToken(token);
        if (user == null){
            try {
                session.close(new CloseReason( CloseReason.CloseCodes.NO_EXTENSION, "user not login"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        log.error("[{}] {} 登录消息系统", TimeUtil.getNowDate(),user.getPhone());
        deleteUser(user);
        remoteAddress = WebSocketUtil.getRemoteAddress(session);
        onlineNumber++;
        this.session = session;
        webSockets.add(this);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendMessage("H");
            }
        }, 1000, 1000 * 15);
    }
    @OnClose
    public void onClose() {
        onlineNumber--;
        webSockets.remove(this);
        timer.cancel();
    }
    @OnMessage
    public void onMessage(String m, Session session) {
        System.out.printf(m);
        System.out.printf(AESUtils.Decrypt(m));
    }
    @OnError
    public void onError(Session session, Throwable error) {
//        error.printStackTrace();
    }
    public void sendMessage(String message) {
        if (session.isOpen()) {
            try {
                session.getBasicRemote().sendText(AESUtils.Encrypt(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
