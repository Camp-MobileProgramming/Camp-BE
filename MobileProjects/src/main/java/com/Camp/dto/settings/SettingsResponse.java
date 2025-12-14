package com.Camp.dto.settings;

import com.Camp.domain.settings.LocationVisibility;
import com.Camp.domain.settings.UserSettings;

public record SettingsResponse(
        boolean locationShare,
        String locationVisibility,
        boolean chatAlarm,
        boolean campRequestAlarm
) {
    public static SettingsResponse from(UserSettings settings) {
        return new SettingsResponse(
                settings.isLocationShare(),
                settings.getLocationVisibility().name().toLowerCase(),
                settings.isChatAlarm(),
                settings.isCampRequestAlarm()
        );
    }
}