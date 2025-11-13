package com.Camp.controller;

import com.Camp.dto.EmailVerificationRequest;
import com.Camp.dto.SignupRequest;
import com.Camp.service.SignupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SignupController {

    private final SignupService signupService;

    public SignupController(SignupService signupService) {
        this.signupService = signupService;
    }

    // 1) 이메일로 인증코드 보내기
    @PostMapping("/signup/email")
    public ResponseEntity<String> sendVerification(@RequestBody EmailVerificationRequest request) {
        try {
            signupService.sendVerificationCode(request.getEmail());
            return ResponseEntity.ok("인증코드를 전송했습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("인증코드 전송 중 오류가 발생했습니다.");
        }
    }

    // 2) 인증코드 포함 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest request) {
        try {
            signupService.signup(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("회원가입 중 오류가 발생했습니다.");
        }
    }
}