package com.apilens.user.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apilens.user.dto.MeResponse;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/me")
    public MeResponse getMyInfo(
            @AuthenticationPrincipal Jwt jwt
    ) {
        return new MeResponse(
                Long.valueOf(jwt.getSubject()),
                jwt.getClaimAsString("email"),
                jwt.getClaimAsString("role")
        );
    }
}