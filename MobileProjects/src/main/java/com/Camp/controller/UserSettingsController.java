package com.Camp.controller;

import com.Camp.dto.settings.SettingsResponse;
import com.Camp.dto.settings.SettingsUpdateRequest;
import com.Camp.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class UserSettingsController {

    private final UserSettingsService userSettingsService;

    private String extractNickname(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization 헤더 형식이 올바르지 않습니다.");
        }
        String encoded = authHeader.substring(7).trim();
        return URLDecoder.decode(encoded, StandardCharsets.UTF_8);
    }

    @GetMapping("/me")
    public SettingsResponse getMySettings(
            @RequestHeader("Authorization") String authHeader
    ) {
        String nickname = extractNickname(authHeader);
        return userSettingsService.getMySettings(nickname);
    }

    @PutMapping("/me")
    public SettingsResponse updateMySettings(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody SettingsUpdateRequest request
    ) {
        String nickname = extractNickname(authHeader);
        return userSettingsService.updateMySettings(nickname, request);
    }
}
