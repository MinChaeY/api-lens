package com.apilens.global.exception;

public record ErrorResponse(
        String code,
        String message
) {
}