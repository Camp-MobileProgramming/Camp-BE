package com.Camp.domain.chat;

import com.Camp.domain.chat.ChatRoom;
import com.Camp.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByRoomKey(String roomKey);

    List<ChatRoom> findByUserAOrUserB(User userA, User userB);
}
