package com.Camp.domain.chat;


import com.Camp.domain.chat.ChatMessage;
import com.Camp.domain.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByRoomOrderBySentAtAsc(ChatRoom room);
    Optional<ChatMessage> findTopByRoomOrderBySentAtDesc(ChatRoom room);
}
