package com.Camp.security;

import com.Camp.domain.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final String issuer = "Campus-Friend";
    private final String secret = "Lee-Dong-Nyoung";

    private Algorithm algorithm;

    public JwtTokenProvider() {
        this.algorithm =Algorithm.HMAC256(secret);
    }

    //로그인 성공시 토큰생성
    public String createToken(User user) {
        Instant now = Instant.now();
        String token =  JWT.create()
                .withIssuer(issuer)
                .withSubject(user.getEmail())      // 주체: 이메일
                .withClaim("nickname", user.getNickname())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plus(1, ChronoUnit.HOURS))) // 1시간 유효
                .sign(algorithm);
        System.out.println("JWT 생성됨: " + token);
        return token;
    }

    //확장을 위해 검증/파싱용
    public DecodedJWT verify(String token) {
        return JWT.require(algorithm)
                .withIssuer(issuer)
                .build()
                .verify(token);
    }
}
