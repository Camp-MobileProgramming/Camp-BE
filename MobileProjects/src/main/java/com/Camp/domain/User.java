package com.Camp.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private String email;
    private String nickname;
    private String password; // 과제용. 실제로는 암호화

    public User( String email, String nickname, String password) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
    }
}
