package com.Camp.dto.follow;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PendingFriendResponse {
    private Long id;
    private String nickname; // 요청 보낸 사람의 닉네임
}