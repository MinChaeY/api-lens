package com.apilens.testcase.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apilens.testcase.dto.CreateTestCaseRequest;
import com.apilens.testcase.dto.TestCaseResponse;
import com.apilens.testcase.service.ApiTestCaseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(
        "/api/projects/{projectId}/endpoints/{endpointId}/test-cases"
)
@RequiredArgsConstructor
public class ApiTestCaseController {

    private final ApiTestCaseService apiTestCaseService;

    @PostMapping
    public ResponseEntity<TestCaseResponse> createTestCase(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("projectId") Long projectId,
            @PathVariable("endpointId") Long endpointId,
            @Valid @RequestBody CreateTestCaseRequest request
    ) {
        Long ownerId = Long.valueOf(jwt.getSubject());

        TestCaseResponse response =
                apiTestCaseService.createTestCase(
                        ownerId,
                        projectId,
                        endpointId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}