package com.Camp.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponse {
    private String email;     // 유저 이메일
    private String nickname;  // 유저 닉네임
    private String token;     // 로그인 토큰

    private String message;   // 에러 메세지(실패 시)

    public LoginResponse(String email, String nickname, String token) {
        this.email = email;
        this.nickname = nickname;
        this.token = token;
    }

    // 에러 응답용 생성자 (message만 담기)
    public LoginResponse(String message) {
        this.message = message;
    }
}
