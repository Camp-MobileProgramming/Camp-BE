package com.Camp.dto.profile;

import lombok.Getter;
import java.util.List;

@Getter
public class ProfileUpdateRequest {
    private String statusMessage;
    private String intro;
    private List<String> interests;
    private Integer friends; // groups, steps 삭제
}
