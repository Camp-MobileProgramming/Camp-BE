package com.Camp.domain.friendship;

import com.Camp.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendShipRepository extends JpaRepository<FriendShip, Long> {

    // 내 친구 목록
    List<FriendShip> findByOwner(User owner);

    // 이미 친구인지 체크
    boolean existsByOwnerAndFriend(User owner, User friend);

    //친구 수 카운트 (owner기준)
    long countByOwner(User owner);

}