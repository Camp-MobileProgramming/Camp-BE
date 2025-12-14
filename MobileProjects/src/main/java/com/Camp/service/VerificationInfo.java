package com.Camp.service;

import java.time.LocalDateTime;

public class VerificationInfo {
    private String code;
    private LocalDateTime expiresAt;

    public VerificationInfo(String code, LocalDateTime expiresAt) {
        this.code = code;
        this.expiresAt = expiresAt;
    }

    public String getCode() {return code;}
    public LocalDateTime getExpiresAt() {return expiresAt;}
}
