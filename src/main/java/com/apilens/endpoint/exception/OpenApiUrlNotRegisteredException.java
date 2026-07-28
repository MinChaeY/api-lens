package com.apilens.endpoint.exception;

public class OpenApiUrlNotRegisteredException
        extends RuntimeException {

    public OpenApiUrlNotRegisteredException() {
        super("OpenAPI 문서 주소가 등록되지 않았습니다.");
    }
}