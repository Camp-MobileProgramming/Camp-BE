package com.Camp.domain;

public class User {
    private String email;
    private String nickname;
    private String password; // 과제용. 실제로는 암호화

    public User(String email, String nickname, String password) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
    }

    public String getEmail() { return email; }
    public String getNickname() { return nickname; }
    public String getPassword() { return password; }
}
