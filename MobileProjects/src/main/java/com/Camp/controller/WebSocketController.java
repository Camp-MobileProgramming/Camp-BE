package com.Camp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketController extends TextWebSocketHandler {
    private final ObjectMapper om = new ObjectMapper();

    // 세션 → 메타(어느 방에 속했는지, 닉네임 등)
    private final Map<WebSocketSession, Meta> metas = new ConcurrentHashMap<>();

    @Data
    static class Meta {
        String userId;
        String postId;
        String nickname;  // 추가: 닉네임
    }

    @Data
    static class Msg {
        String type;      // "join" | "loc"
        String userId;    // join에서 받음, loc에서는 서버가 채움
        String postId;    // join에서 받음, loc에서는 서버가 채움
        Double lat;       // loc에서 받음
        Double lng;       // loc에서 받음
        String sessionId; // 서버가 채움
        Long ts;          // 서버가 채움 (ms)

        String nickname;  // 추가: 닉네임 (join에서 받음, loc에서는 서버가 채움)
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession s) {
        metas.put(s, new Meta()); // 아직 방/유저 정보 없음
    }

    @Override
    public void afterConnectionClosed(WebSocketSession s, CloseStatus st) {
        metas.remove(s);
    }

    @Override
    protected void handleTextMessage(WebSocketSession s, TextMessage m) throws Exception {
        Msg msg = om.readValue(m.getPayload(), Msg.class);
        msg.setSessionId(s.getId());
        msg.setTs(System.currentTimeMillis());

        if ("join".equalsIgnoreCase(msg.getType())) {
            var meta = metas.get(s);
            if (meta != null) {
                meta.setUserId(msg.getUserId());
                meta.setPostId(msg.getPostId());
                meta.setNickname(msg.getNickname()); //닉네임 저장
            }
            // 본인에게만 ACK
            s.sendMessage(new TextMessage(om.writeValueAsString(msg)));
            return;
        }

        if ("loc".equalsIgnoreCase(msg.getType())) {
            var sender = metas.get(s);
            if (sender == null || sender.getPostId() == null) return; // join 안하면 무시

            // oc 메시지에 서버에서 유저 정보 채워넣기
            msg.setUserId(sender.getUserId());
            msg.setPostId(sender.getPostId());
            msg.setNickname(sender.getNickname()); // 닉네임 세팅

            String payload = om.writeValueAsString(msg);

            // 같은 방(postId)에게만 전송
            for (var e : metas.entrySet()) {
                var ss = e.getKey();
                var mm = e.getValue();
                if (ss.isOpen() && sender.getPostId().equals(mm.getPostId())) {
                    ss.sendMessage(new TextMessage(payload));
                }
            }
        }
    }
}
