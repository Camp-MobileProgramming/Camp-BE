package com.Camp.controller;

import com.Camp.dto.profile.ProfileMemoResponse;
import com.Camp.dto.profile.ProfileMemoUpdateRequest;
import com.Camp.service.ProfileMemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileMemoController {

    private final ProfileMemoService memoService;

    private String extractNickname(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization 헤더 형식이 올바르지 않습니다.");
        }
        String encoded = authHeader.substring(7).trim();
        return URLDecoder.decode(encoded, StandardCharsets.UTF_8);
    }

    // GET /api/profile/{targetNickname}/memo
    @GetMapping("/{targetNickname}/memo")
    public ResponseEntity<ProfileMemoResponse> getMemo(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String targetNickname
    ) {
        String owner = extractNickname(authHeader);
        ProfileMemoResponse memo = memoService.getMemo(owner, targetNickname);
        if (memo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(memo);
    }

    // PUT /api/profile/{targetNickname}/memo
    @PutMapping("/{targetNickname}/memo")
    public ResponseEntity<ProfileMemoResponse> upsertMemo(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String targetNickname,
            @RequestBody ProfileMemoUpdateRequest request
    ) {
        String owner = extractNickname(authHeader);
        ProfileMemoResponse response = memoService.upsertMemo(owner, targetNickname, request);
        return ResponseEntity.ok(response);
    }
}
