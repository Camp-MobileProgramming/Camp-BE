package com.Camp.service;

import com.Camp.domain.User;
import com.Camp.domain.UserRepository;
import com.Camp.dto.LoginRequest;
import com.Camp.dto.LoginResponse;
import com.Camp.security.JwtTokenProvider;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {
    private UserRepository userRepository;
    private JwtTokenProvider jwtTokenProvider;

    public LoginService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public LoginResponse login(LoginRequest request) {
        System.out.println(" [LoginService] 로그인 요청 email=" + request.getEmail());

        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            System.out.println(" [LoginService] 해당 이메일 없음");
            throw new IllegalArgumentException("아이디 또는 비밀번호가 틀렸습니다.");
        }

        User user = userOpt.get();
        System.out.println(" [LoginService] 이메일 존재 nickname=" + user.getNickname());

        if (!user.getPassword().equals(request.getPassword())) {
            System.out.println(" [LoginService] 비밀번호 불일치");
            throw new IllegalArgumentException("아이디 또는 비밀번호가 틀렸습니다.");
        }

        String token = jwtTokenProvider.createToken(user);
        System.out.println("[LoginService] 로그인 성공, JWT 생성: " + token);

        LoginResponse loginResponse = new LoginResponse(user.getEmail(), user.getNickname(), token);
        return loginResponse;
    }
}
