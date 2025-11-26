package com.Camp.controller;

import com.Camp.dto.profile.ProfileResponse;
import com.Camp.dto.profile.ProfileUpdateRequest;
import com.Camp.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    // FriendController랑 똑같이 사용
    private String extractNickname(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization 헤더 형식이 올바르지 않습니다.");
        }
        String encoded = authHeader.substring(7).trim();
        return URLDecoder.decode(encoded, StandardCharsets.UTF_8);
    }

    /**
     * 프로필 조회 (내/상대 모두)
     * GET /api/profile/{nickname}
     */
    @GetMapping("/{nickname}")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable String nickname) {
        try {
            ProfileResponse response = profileService.getProfile(nickname);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // 프론트에서 404를 받으면 "기본값 표시" 로직 타게 했으니까 404로 응답
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 내 프로필 저장/업데이트
     * PUT /api/profile
     * Authorization: Bearer {encodedNickname}
     */
    @PutMapping
    public ResponseEntity<ProfileResponse> updateProfile(
            @RequestHeader("Authorization") String auth,
            @RequestBody ProfileUpdateRequest req
    ){
        String nickname = extractNickname(auth);
        return ResponseEntity.ok(profileService.upsertProfile(nickname, req));
    }

}
