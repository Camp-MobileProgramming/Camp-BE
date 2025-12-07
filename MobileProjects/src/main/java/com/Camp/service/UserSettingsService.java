// service/UserSettingsService.java
package com.Camp.service;

import com.Camp.domain.settings.LocationVisibility;
import com.Camp.domain.settings.UserSettings;
import com.Camp.domain.settings.UserSettingsRepository;
import com.Camp.domain.user.User;
import com.Camp.domain.user.UserRepository;
import com.Camp.dto.settings.SettingsResponse;
import com.Camp.dto.settings.SettingsUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserSettingsService {

    private final UserRepository userRepository;
    private final UserSettingsRepository userSettingsRepository;

    @Transactional(readOnly = true)
    public SettingsResponse getMySettings(String nickname) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        UserSettings settings = userSettingsRepository.findByUser(user)
                .orElseGet(() -> userSettingsRepository.save(UserSettings.createDefault(user)));

        return SettingsResponse.from(settings);
    }

    public SettingsResponse updateMySettings(String nickname, SettingsUpdateRequest req) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        UserSettings settings = userSettingsRepository.findByUser(user)
                .orElseGet(() -> UserSettings.createDefault(user));

        LocationVisibility visibility = null;
        if (req.locationVisibility() != null) {
            visibility = switch (req.locationVisibility()) {
                case "all" -> LocationVisibility.ALL;
                case "friends" -> LocationVisibility.FRIENDS;
                case "none" -> LocationVisibility.NONE;
                default -> throw new IllegalArgumentException("잘못된 위치 공개 설정입니다.");
            };
        }

        settings.update(
                req.locationShare(),
                visibility,
                req.chatAlarm(),
                req.campRequestAlarm()
        );

        UserSettings saved = userSettingsRepository.save(settings);
        return SettingsResponse.from(saved);
    }
}
