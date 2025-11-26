package com.Camp.dto.profile;

import com.Camp.domain.profile.Profile;
import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@Builder
public class ProfileResponse {

    private String nickname;
    private String statusMessage;
    private String intro;
    private List<String> interests;
    private int friends; // groups, steps 제거

    public static ProfileResponse from(Profile profile) {
        return ProfileResponse.builder()
                .nickname(profile.getNickname())
                .statusMessage(profile.getStatusMessage())
                .intro(profile.getIntro())
                .interests(convertToList(profile.getInterests()))
                .friends(profile.getFriendsCount())
                .build();
    }

    private static List<String> convertToList(String interests) {
        if (interests == null || interests.isBlank()) return Collections.emptyList();
        return Arrays.stream(interests.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }
}
