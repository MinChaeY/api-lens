package com.apilens.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apilens.user.dto.SignUpRequest;
import com.apilens.user.service.UserService;
import com.apilens.user.dto.LoginRequest;
import com.apilens.user.dto.LoginResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Long> signUp(
            @Valid @RequestBody SignUpRequest request
    ) {
        Long userId = userService.signUp(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userId);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
        @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}