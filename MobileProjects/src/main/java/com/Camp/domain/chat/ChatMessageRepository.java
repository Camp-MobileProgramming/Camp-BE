package com.Camp.domain.chat;


import com.Camp.domain.chat.ChatMessage;
import com.Camp.domain.chat.ChatRoom;
import com.Camp.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByRoomOrderBySentAtAsc(ChatRoom room);
    Optional<ChatMessage> findTopByRoomOrderBySentAtDesc(ChatRoom room);
    // 특정 유저가 아직 안 읽은 전체 메시지 수
    int countByReceiverAndReadFlagIsFalse(User receiver);

    // 특정 방에서 내가 안 읽은 메시지 수
    int countByRoomAndReceiverAndReadFlagIsFalse(ChatRoom room, User receiver);

    // 특정 방에서 내가 안 읽은 메시지 목록 (읽음 처리용)
    List<ChatMessage> findByRoomAndReceiverAndReadFlagIsFalse(ChatRoom room, User receiver);
}
