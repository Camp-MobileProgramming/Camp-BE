package com.Camp.service;

import com.Camp.domain.friendship.*;
import com.Camp.domain.user.User;
import com.Camp.domain.user.UserRepository;
import com.Camp.dto.follow.FriendResponse;
import com.Camp.dto.follow.PendingFriendResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FriendService {

    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final FriendShipRepository friendshipRepository;
    private final FriendShipRepository friendShipRepository;

    /**
     * 수락 대기중인 친구 요청 목록 조회 (내가 receiver인 PENDING 요청)
     */
    public List<PendingFriendResponse> getPendingRequests(String myNickname) {
        User me = userRepository.findByNickname(myNickname)
                .orElseThrow(() -> new IllegalArgumentException("해당 닉네임의 유저가 없습니다: " + myNickname));

        List<FriendRequest> pending = friendRequestRepository
                .findByReceiverAndStatus(me, FriendRequestStatus.PENDING);

        return pending.stream()
                .map(fr -> new PendingFriendResponse(fr.getRequester().getId(),fr.getRequester().getNickname()))
                .collect(Collectors.toList());
    }

    /**
     * 친구 요청 수락
     */
    public void acceptFriendRequest(String myNickname, String requesterNickname) {
        User me = userRepository.findByNickname(myNickname)
                .orElseThrow(() -> new IllegalArgumentException("해당 닉네임의 유저가 없습니다: " + myNickname));

        User requester = userRepository.findByNickname(requesterNickname)
                .orElseThrow(() -> new IllegalArgumentException("요청자의 닉네임을 찾을 수 없습니다: " + requesterNickname));

        // 1) PENDING 상태의 요청 찾기
        FriendRequest request = friendRequestRepository
                .findByRequesterAndReceiverAndStatus(requester, me, FriendRequestStatus.PENDING)
                .orElseThrow(() -> new IllegalArgumentException("해당 친구 요청이 존재하지 않거나 이미 처리되었습니다."));

        // 2) 상태 변경
        request.accept(); // 상태만 ACCEPTED 로 변경(트랜잭션 안이므로 flush 시 반영)

        // 3) 이미 친구인지 확인 후 친구 관계 생성 (양방향)
        if (!friendshipRepository.existsByOwnerAndFriend(me, requester)) {
            friendshipRepository.save(new FriendShip(me, requester));
        }
        if (!friendshipRepository.existsByOwnerAndFriend(requester, me)) {
            friendshipRepository.save(new FriendShip(requester, me));
        }
    }

    /**
     * 친구 요청 보내기 (추후 프론트에 붙일 용도)
     */
    public void sendFriendRequest(String myNickname, String receiverNickname) {
        if (myNickname.equals(receiverNickname)) {
            throw new IllegalArgumentException("자기 자신에게 친구 요청을 보낼 수 없습니다.");
        }

        User requester = userRepository.findByNickname(myNickname)
                .orElseThrow(() -> new IllegalArgumentException("해당 닉네임의 유저가 없습니다: " + myNickname));

        User receiver = userRepository.findByNickname(receiverNickname)
                .orElseThrow(() -> new IllegalArgumentException("상대 닉네임을 찾을 수 없습니다: " + receiverNickname));

        // 이미 친구인지 체크
        if (friendshipRepository.existsByOwnerAndFriend(requester, receiver)) {
            throw new IllegalArgumentException("이미 친구입니다.");
        }

        // 이미 보낸 PENDING 요청이 있는지 체크
        boolean alreadyPending = friendRequestRepository
                .existsByRequesterAndReceiverAndStatus(requester, receiver, FriendRequestStatus.PENDING);

        if (alreadyPending) {
            throw new IllegalArgumentException("이미 보낸 친구 요청이 있습니다.");
        }

        FriendRequest friendRequest = new FriendRequest(requester, receiver);
        friendRequestRepository.save(friendRequest);
    }
    public List<FriendResponse> getFriendList(String nickname){
        User me = userRepository.findByNickname(nickname)
                .orElseThrow(()->new IllegalArgumentException("사용자찾을 수 없음.."));

        return friendShipRepository.findByOwner(me).stream()
                .map(fs -> new FriendResponse(fs.getFriend().getId(),fs.getFriend().getNickname()))
                .collect(Collectors.toList());
    }
    @Transactional
    public void rejectFriendRequest(String myNickname, String requesterNickname) {
        User me = userRepository.findByNickname(myNickname)
                .orElseThrow(()->new IllegalArgumentException("사용자찾을 수 없음.."));
        User requester = userRepository.findByNickname(requesterNickname)
                .orElseThrow(()->new IllegalArgumentException("사용자찾을 수 없음.."));

        FriendRequest req = friendRequestRepository
                .findByRequesterAndReceiverAndStatus(
                        requester, me, FriendRequestStatus.PENDING
                )
                .orElseThrow(() -> new IllegalArgumentException("대기중인 친구 요청이 없습니다."));
        req.reject();
    }
}