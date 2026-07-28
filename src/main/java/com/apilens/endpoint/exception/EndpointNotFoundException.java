package com.apilens.endpoint.exception;

public class EndpointNotFoundException extends RuntimeException {

    public EndpointNotFoundException() {
        super("API 엔드포인트를 찾을 수 없습니다.");
    }
}