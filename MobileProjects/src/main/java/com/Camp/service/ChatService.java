package com.Camp.service;

import com.Camp.domain.chat.ChatMessage;
import com.Camp.domain.chat.ChatMessageRepository;
import com.Camp.domain.chat.ChatRoom;
import com.Camp.domain.chat.ChatRoomRepository;
import com.Camp.domain.user.User;
import com.Camp.domain.user.UserRepository;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    private String buildRoomKey(User a, User b) {
        Long id1 = a.getId();
        Long id2 = b.getId();
        return (id1 < id2) ? (id1 + "#" + id2) : (id2 + "#" + id1);
    }

    public User getUserByNickname(String nickname) {
        return userRepository.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다: " + nickname));
    }

    public ChatRoom getOrCreateRoom(String myNickname, String targetNickname) {
        User me = getUserByNickname(myNickname);
        User other = getUserByNickname(targetNickname);

        String roomKey = buildRoomKey(me, other);

        return chatRoomRepository.findByRoomKey(roomKey)
                .orElseGet(() -> chatRoomRepository.save(
                        ChatRoom.builder()
                                .userA(me)
                                .userB(other)
                                .roomKey(roomKey)
                                .build()
                ));
    }

    public ChatRoom getRoomByRoomKey(String roomKey) {
        return chatRoomRepository.findByRoomKey(roomKey)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));
    }

    public ChatMessage saveMessage(ChatRoom room, User sender, User receiver, String content) {
        ChatMessage msg = ChatMessage.builder()
                .room(room)
                .sender(sender)
                .receiver(receiver)
                .content(content)
                .sentAt(LocalDateTime.now())
                .readFlag(false)
                .build();

        return chatMessageRepository.save(msg);
    }

    public List<ChatMessage> getMessages(ChatRoom room) {
        return chatMessageRepository.findByRoomOrderBySentAtAsc(room);
    }

    public List<ChatRoom> getRoomsForUser(User me) {
        return chatRoomRepository.findByUserAOrUserB(me, me);
    }

    public ChatMessage getLastMessage(ChatRoom room) {
        return chatMessageRepository.findTopByRoomOrderBySentAtDesc(room)
                .orElse(null);
    }
}
