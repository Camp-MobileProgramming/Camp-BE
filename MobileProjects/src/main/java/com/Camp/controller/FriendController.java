package com.Camp.controller;

import com.Camp.domain.friendship.FriendShip;
import com.Camp.dto.follow.AcceptFriendRequestDto;
import com.Camp.dto.follow.FriendResponse;
import com.Camp.dto.follow.PendingFriendResponse;
import com.Camp.dto.follow.SendFriendRequestDto;
import com.Camp.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    // Authorization: Bearer {nickname} 에서 nickname 추출
    private String extractNickname(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization 헤더 형식이 올바르지 않습니다.");
        }
        String encoded = authHeader.substring(7).trim();
        return URLDecoder.decode(encoded, StandardCharsets.UTF_8);
    }

    /**
     * 수락 대기중인 친구 요청 목록 조회
     */
    @GetMapping("/pending")
    public ResponseEntity<List<PendingFriendResponse>> getPendingRequests(
            @RequestHeader("Authorization") String authorization
    ) {
        String myNickname = extractNickname(authorization);
        List<PendingFriendResponse> pending = friendService.getPendingRequests(myNickname);
        return ResponseEntity.ok(pending);
    }

    /**
     * 친구 요청 수락
     */
    @PostMapping("/accept")
    public ResponseEntity<Void> acceptFriendRequest(
            @RequestHeader("Authorization") String authorization,
            @RequestBody AcceptFriendRequestDto dto
    ) {
        String myNickname = extractNickname(authorization);
        friendService.acceptFriendRequest(myNickname, dto.getRequesterNickname());
        return ResponseEntity.ok().build();
    }

    /**
     * (옵션) 친구 요청 보내기 – 추후 프론트에서 사용 가능
     */
    @PostMapping("/request")
    public ResponseEntity<Void> sendFriendRequest(
            @RequestHeader("Authorization") String authorization,
            @RequestBody SendFriendRequestDto dto
    ) {
        String myNickname = extractNickname(authorization);
        friendService.sendFriendRequest(myNickname, dto.getReceiverNickname());
        return ResponseEntity.ok().build();
    }
    @GetMapping("/list")
    public ResponseEntity<List<FriendResponse>> getFriends(
            @RequestHeader("Authorization") String authorization
    ) {
        String myNickname = extractNickname(authorization);
        List<FriendResponse> friends = friendService.getFriendList(myNickname);
        return ResponseEntity.ok(friends);
    }
}
