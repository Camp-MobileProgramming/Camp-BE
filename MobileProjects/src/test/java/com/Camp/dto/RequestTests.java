package com.Camp.dto;

import com.Camp.dto.signup.EmailVerificationRequest;
import com.Camp.dto.signup.SignupRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class RequestTests {

    @Test
    void emailVerificationRequest() {
        //given
        EmailVerificationRequest request = new EmailVerificationRequest();

        //when
        request.setEmail("test@hansung.ac.kr");

        //then
        assertEquals("test@hansung.ac.kr",request.getEmail());
    }

    @Test
    void signupRequest(){
        //given
        SignupRequest request = new SignupRequest();

        //when
        request.setEmail("test@hansung.ac.kr");
        request.setNickname("하이요");
        request.setPassword("12345678");
        request.setCode("123123");

        assertEquals("test@hansung.ac.kr",request.getEmail());
        assertEquals("하이요",request.getNickname());
        assertEquals("12345678",request.getPassword());
        assertEquals("123123",request.getCode());
    }
}
