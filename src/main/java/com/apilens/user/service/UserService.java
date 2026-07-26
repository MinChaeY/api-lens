package com.apilens.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apilens.user.domain.User;
import com.apilens.user.dto.SignUpRequest;
import com.apilens.user.exception.DuplicateEmailException;
import com.apilens.user.repository.UserRepository;
import com.apilens.user.exception.DuplicateEmailException;
import com.apilens.user.dto.LoginRequest;
import com.apilens.user.dto.LoginResponse;
import com.apilens.user.exception.InvalidCredentialsException;
import com.apilens.global.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public Long signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("이미 사용 중인 이메일입니다.");
        }

        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getEmail(),
                encodedPassword,
                request.getName()
        );

        return userRepository.save(user).getId();
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() ->
                    new InvalidCredentialsException(
                            "이메일 또는 비밀번호가 올바르지 않습니다."
                    )
            );

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException(
                "이메일 또는 비밀번호가 올바르지 않습니다."
        );
    }
    String accessToken = jwtTokenProvider.createAccessToken(user);
    return new LoginResponse(
        user.getId(),
        user.getName(),
        accessToken
        );
    }
}