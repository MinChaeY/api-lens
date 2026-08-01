package com.apilens.testcase.dto;

import java.time.LocalDateTime;

import com.apilens.testcase.domain.ApiTestCase;

public record TestCaseResponse(
        Long id,
        Long endpointId,
        String name,
        String requestHeaders,
        String requestBody,
        Integer expectedStatus,
        String expectedBody,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static TestCaseResponse from(ApiTestCase testCase) {
        return new TestCaseResponse(
                testCase.getId(),
                testCase.getEndpoint().getId(),
                testCase.getName(),
                testCase.getRequestHeaders(),
                testCase.getRequestBody(),
                testCase.getExpectedStatus(),
                testCase.getExpectedBody(),
                testCase.getCreatedAt(),
                testCase.getUpdatedAt()
        );
    }
}