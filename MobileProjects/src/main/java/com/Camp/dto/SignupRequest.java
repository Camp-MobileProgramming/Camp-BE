package com.Camp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SignupRequest {
    private String email;
    private String nickname;
    private String password;
    private String code; //이메일 인증 코드
}
