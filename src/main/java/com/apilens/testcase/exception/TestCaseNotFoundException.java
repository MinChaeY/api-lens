package com.apilens.testcase.exception;

public class TestCaseNotFoundException extends RuntimeException {

    public TestCaseNotFoundException() {
        super("API 테스트 케이스를 찾을 수 없습니다.");
    }
}