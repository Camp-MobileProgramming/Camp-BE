package com.Camp.service;

import com.Camp.domain.user.UserRepository;
import com.Camp.dto.signup.SignupRequest;
import com.Camp.domain.user.User;
import org.springframework.stereotype.Service;

@Service
public class SignupService {
    private final UserRepository userRepository;
    private final EmailVerificationService emailVerificationService;

    public SignupService(UserRepository userRepository,
                         EmailVerificationService emailVerificationService) {
        this.userRepository = userRepository;
        this.emailVerificationService = emailVerificationService;
    }

    public void sendVerificationCode(String email) {
        emailVerificationService.sendCode(email); //인증코드 이메일 보내기
    }

    public void signup(SignupRequest request) { //회원가입
        // 이메일 인증코드 검증
        emailVerificationService.verifyCode(request.getEmail(), request.getCode());

        // 중복 체크
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        if (userRepository.findByNickname(request.getNickname()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        //생성
        User user = new User(
                request.getEmail(),
                request.getNickname(),
                request.getPassword()
        );

        userRepository.save(user); //저장
        System.out.println(" [SignupService] 회원가입 성공!");
        System.out.println("     이메일: " + request.getEmail());
        System.out.println("     닉네임: " + request.getNickname());
        System.out.println("     비밀번호: " + request.getPassword());

    }
}
