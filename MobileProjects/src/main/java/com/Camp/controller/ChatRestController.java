package com.Camp.controller;
import com.Camp.domain.chat.ChatMessage;
import com.Camp.domain.chat.ChatRoom;
import com.Camp.domain.user.User;
import com.Camp.service.ChatService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatService chatService;

    // 프론트 요청  GET /api/chats?me=A&target=B
    @GetMapping
    public List<ChatMessageDto> getChatHistory(
            @RequestParam String me,
            @RequestParam String target
    ) {
        // 방 조회
        ChatRoom room = chatService.getOrCreateRoom(me, target);
        List<ChatMessage> messages = chatService.getMessages(room);

        // DTO변환후 반환
        return messages.stream().map(ChatMessageDto::from).toList();
    }

    @Data
    static class ChatMessageDto {
        private String senderNickname;
        private String content;
        private Long ts; // timestamp(ms)

        static ChatMessageDto from(ChatMessage m) {
            ChatMessageDto dto = new ChatMessageDto();
            dto.setSenderNickname(m.getSender().getNickname());
            dto.setContent(m.getContent());

            if (m.getSentAt() != null) {
                dto.setTs(m.getSentAt()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli());
            }
            return dto;
        }
    }

    @GetMapping("/rooms")
    public List<ChatRoomSummaryDto> getChatRooms(@RequestParam String me) {
        // 나 자신 User 조회
        User meUser = chatService.getUserByNickname(me);

        // 내가 속한 모든 방
        List<ChatRoom> rooms = chatService.getRoomsForUser(meUser);

        return rooms.stream()
                .map(room -> {
                    ChatRoomSummaryDto dto = new ChatRoomSummaryDto();
                    dto.setRoomKey(room.getRoomKey());

                    // 상대방 닉네임
                    User userA = room.getUserA();
                    User userB = room.getUserB();
                    User other = userA.getId().equals(meUser.getId()) ? userB : userA;
                    dto.setOtherNickname(other.getNickname());

                    // 마지막 메시지
                    ChatMessage last = chatService.getLastMessage(room);
                    if (last != null) {
                        dto.setLastMessage(last.getContent());
                        if (last.getSentAt() != null) {
                            long ts = last.getSentAt()
                                    .atZone(ZoneId.systemDefault())
                                    .toInstant()
                                    .toEpochMilli();
                            dto.setLastTs(ts);
                        }
                    }

                    // 아직 안 읽은 개수는 구현 안 했으니 0으로
                    dto.setUnreadCount(0);

                    return dto;
                })
                .toList();
    }

    @Data
    static class ChatRoomSummaryDto {
        private String roomKey;
        private String otherNickname;
        private String lastMessage;
        private Long lastTs;
        private int unreadCount;
    }
}
