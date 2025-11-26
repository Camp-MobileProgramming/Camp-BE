package com.Camp.dto.profile;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileMemoResponse {
    private String targetNickname;
    private String content;
}


