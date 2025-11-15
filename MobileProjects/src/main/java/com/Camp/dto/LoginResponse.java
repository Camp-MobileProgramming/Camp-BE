package com.Camp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginResponse {
    private String token;

    public LoginResponse() {

    }

    public LoginResponse(String token) {
        this.token = token;
    }
}
