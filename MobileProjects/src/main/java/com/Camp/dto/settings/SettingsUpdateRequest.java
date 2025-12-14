package com.Camp.dto.settings;

// 프론트에서 항상 보내긴 하지만, 널 허용해두면 나중에 부분 업데이트도 가능
public record SettingsUpdateRequest(
        Boolean locationShare,
        String locationVisibility,
        Boolean chatAlarm,
        Boolean campRequestAlarm
){

}