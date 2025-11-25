package com.Camp.service;

import com.Camp.domain.user.User;
import com.Camp.domain.user.UserRepository;
import com.Camp.dto.signup.SignupRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SignupServiceTests {

    @Mock
    UserRepository userRepository;

    @Mock
    EmailVerificationService emailVerificationService;

    @InjectMocks
    SignupService signupService;

    @Test
    @DisplayName("유효한 요청이면 이메일 인증코드를 검증하고 유저를 저장한다")
    void signup_success() {
        // given
        SignupRequest request = new SignupRequest();
        request.setEmail("test@hansung.ac.kr");
        request.setNickname("dongnyoung");
        request.setPassword("password1234!");
        request.setCode("123456");

        // 이메일/닉네임 중복 없음
        when(userRepository.findByEmail("test@hansung.ac.kr"))
                .thenReturn(Optional.empty());
        when(userRepository.findByNickname("dongnyoung"))
                .thenReturn(Optional.empty());

        // when
        signupService.signup(request);

        // then
        // 1) 이메일 인증코드 검증이 호출됐는지
        verify(emailVerificationService)
                .verifyCode("test@hansung.ac.kr", "123456");

        // 2) userRepository.save()가 올바른 User로 호출됐는지
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User savedUser = captor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo("test@hansung.ac.kr");
        assertThat(savedUser.getNickname()).isEqualTo("dongnyoung");
        assertThat(savedUser.getPassword()).isEqualTo("password1234!");
    }
}