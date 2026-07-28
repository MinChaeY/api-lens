package com.apilens.endpoint.exception;

public class OpenApiImportException extends RuntimeException {

    public OpenApiImportException() {
        super("OpenAPI 문서를 불러오거나 해석할 수 없습니다.");
    }
}