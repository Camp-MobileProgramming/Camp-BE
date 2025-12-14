package com.Camp.domain.friendship;

import com.Camp.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    // 내가 받은 PENDING 상태 요청들
    List<FriendRequest> findByReceiverAndStatus(User receiver, FriendRequestStatus status);

    // 특정 요청자 → 나에게 온 PENDING 요청 하나 찾기
    Optional<FriendRequest> findByRequesterAndReceiverAndStatus(
            User requester,
            User receiver,
            FriendRequestStatus status
    );

    // 이미 같은 요청이 PENDING 인지 확인
    boolean existsByRequesterAndReceiverAndStatus(
            User requester,
            User receiver,
            FriendRequestStatus status
    );
}
