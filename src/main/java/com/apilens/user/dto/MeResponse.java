package com.apilens.user.dto;

public record MeResponse(
        Long userId,
        String email,
        String role
) {
}