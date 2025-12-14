package com.Camp.service;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmailVerificationService {

    private final Map<String, VerificationInfo> store = new ConcurrentHashMap<>();
    // 메일
    private final JavaMailSender mailSender;

    public EmailVerificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendCode(String email) {
        if (!email.toLowerCase().endsWith("@hansung.ac.kr")) {
            throw new IllegalArgumentException("한성대학교 이메일만 사용 가능합니다.");
        }

        String code = generateCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10); //10분 유효
        store.put(email, new VerificationInfo(code, expiresAt));

        //메일 발송
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setFrom("lee.dn010828@gmail.com");
            message.setSubject("[Campus Friend] 이메일 인증코드 안내");
            message.setText(
                    "안녕하세요!\n\n" +
                            "이메일 인증코드: " + code + "\n" +
                            "유효시간: 10분\n\n" +
                            "본인이 요청한 인증이 아니라면 이 메일을 무시해주세요."
            );

            mailSender.send(message);
            System.out.println("[이메일 인증코드 발송 완료] " + email + " -> " + code);

        } catch (MailException e) {
            // 여기 로그 보고 원인 파악
            e.printStackTrace();
            throw new IllegalStateException("메일 전송에 실패했습니다: " + e.getMessage(), e);
        }
    }


    public void verifyCode(String email, String code) {
        //검증 진행
        VerificationInfo info = store.get(email);
        if (info == null) {
            throw new IllegalArgumentException("인증코드를 먼저 요청해주세요.");
        }
        if (info.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("인증코드가 만료되었습니다. 다시 요청해주세요.");
        }
        if (!info.getCode().equals(code)) {
            throw new IllegalArgumentException("인증코드가 올바르지 않습니다.");
        }
    }

    private String generateCode() {
        Random random = new Random();
        int n = 100000 + random.nextInt(900000); // 100000 ~ 999999
        return String.valueOf(n);
    }
}
