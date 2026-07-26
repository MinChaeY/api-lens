package com.apilens.user.dto;

public record LoginResponse(
        Long userId,
        String name
) {
}