package com.Camp.controller;

import com.Camp.domain.chat.ChatRoom;
import com.Camp.domain.user.User;
import com.Camp.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper om;
    private final ChatService chatService;

    /**
     * 세션별 메타정보:
     * - 어떤 방에 조인했는지(roomKey)
     * - 내가 누구인지(nickname)
     */
    private final Map<WebSocketSession, Meta> metas = new ConcurrentHashMap<>();

    @Data
    static class Meta {
        String roomKey;
        String nickname;
    }

    /**
     * 클라 ↔ 서버 간 주고받는 메시지 포맷
     */
    @Data
    static class Msg {
        private String type;           // "join" | "chat"

        // join 전용
        private String myNickname;
        private String targetNickname;

        // chat 전용
        private String content;

        // 서버가 채워주는 필드
        private String roomKey;
        private String senderNickname;
        private Long ts;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        metas.put(session, new Meta());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        metas.remove(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Msg msg = om.readValue(message.getPayload(), Msg.class);
        msg.setTs(System.currentTimeMillis());

        if ("join".equalsIgnoreCase(msg.getType())) {
            handleJoin(session, msg);
            return;
        }

        if ("chat".equalsIgnoreCase(msg.getType())) {
            handleChat(session, msg);
        }
    }

    /**
     * join:
     *  - myNickname, targetNickname 기반으로 방을 만들거나 찾고
     *  - roomKey, 내 nickname을 Meta에 저장
     */
    private void handleJoin(WebSocketSession session, Msg msg) throws Exception {
        if (msg.getMyNickname() == null || msg.getTargetNickname() == null) {
            throw new IllegalArgumentException("join 메시지에는 myNickname, targetNickname이 필요합니다.");
        }

        ChatRoom room = chatService.getOrCreateRoom(msg.getMyNickname(), msg.getTargetNickname());

        Meta meta = metas.get(session);
        if (meta != null) {
            meta.setRoomKey(room.getRoomKey());
            meta.setNickname(msg.getMyNickname());
        }

        // 원하면 join-ack 보내기 (프론트에서 roomKey 쓰고 싶을 때 사용)
        Msg ack = new Msg();
        ack.setType("join-ack");
        ack.setRoomKey(room.getRoomKey());
        ack.setSenderNickname(msg.getMyNickname());
        ack.setTs(System.currentTimeMillis());

        session.sendMessage(new TextMessage(om.writeValueAsString(ack)));
    }

    /**
     * chat:
     *  - Meta에서 roomKey, 내 nickname 꺼내기
     *  - room/user 찾아서 DB에 메시지 저장
     *  - 같은 roomKey 세션들에게 브로드캐스트
     */
    private void handleChat(WebSocketSession session, Msg msg) throws Exception {
        Meta meta = metas.get(session);
        if (meta == null || meta.getRoomKey() == null || meta.getNickname() == null) {
            // join 안 한 상태에서 chat이면 무시
            return;
        }

        String roomKey = meta.getRoomKey();
        String senderNickname = meta.getNickname();

        ChatRoom room = chatService.getRoomByRoomKey(roomKey);
        User sender = chatService.getUserByNickname(senderNickname);

        // 방의 userA / userB 중 sender가 아닌 쪽이 receiver
        User userA = room.getUserA();
        User userB = room.getUserB();
        User receiver = userA.getId().equals(sender.getId()) ? userB : userA;

        // DB 저장
        chatService.saveMessage(room, sender, receiver, msg.getContent());

        // 클라에게 보낼 payload 구성
        Msg out = new Msg();
        out.setType("chat");
        out.setRoomKey(roomKey);
        out.setSenderNickname(sender.getNickname());
        out.setContent(msg.getContent());
        out.setTs(System.currentTimeMillis());

        String payload = om.writeValueAsString(out);

        // 같은 roomKey 가진 세션에게만 전송
        for (var entry : metas.entrySet()) {
            WebSocketSession s = entry.getKey();
            Meta m = entry.getValue();

            if (s.isOpen() && roomKey.equals(m.getRoomKey())) {
                s.sendMessage(new TextMessage(payload));
            }
        }
    }
}
