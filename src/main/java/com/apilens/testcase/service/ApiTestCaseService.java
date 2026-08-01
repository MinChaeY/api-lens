package com.apilens.testcase.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apilens.endpoint.domain.ApiEndpoint;
import com.apilens.endpoint.exception.EndpointNotFoundException;
import com.apilens.endpoint.repository.ApiEndpointRepository;
import com.apilens.project.exception.ProjectNotFoundException;
import com.apilens.project.repository.ApiProjectRepository;
import com.apilens.testcase.domain.ApiTestCase;
import com.apilens.testcase.dto.CreateTestCaseRequest;
import com.apilens.testcase.dto.TestCaseResponse;
import com.apilens.testcase.exception.TestCaseNotFoundException;
import com.apilens.testcase.repository.ApiTestCaseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApiTestCaseService {

    private final ApiProjectRepository apiProjectRepository;
    private final ApiEndpointRepository apiEndpointRepository;
    private final ApiTestCaseRepository apiTestCaseRepository;

    @Transactional
    public TestCaseResponse createTestCase(
            Long ownerId,
            Long projectId,
            Long endpointId,
            CreateTestCaseRequest request
    ) {
        apiProjectRepository
                .findByIdAndOwnerId(projectId, ownerId)
                .orElseThrow(ProjectNotFoundException::new);

        ApiEndpoint endpoint = apiEndpointRepository
                .findByIdAndProjectId(endpointId, projectId)
                .orElseThrow(EndpointNotFoundException::new);

        ApiTestCase testCase = new ApiTestCase(
                endpoint,
                request.name(),
                request.requestHeaders(),
                request.requestBody(),
                request.expectedStatus(),
                request.expectedBody()
        );

        ApiTestCase savedTestCase =
                apiTestCaseRepository.save(testCase);

        return TestCaseResponse.from(savedTestCase);
    }

    @Transactional(readOnly = true)
    public List<TestCaseResponse> getTestCases(
            Long ownerId,
            Long projectId,
            Long endpointId
    ) {
        apiProjectRepository
                .findByIdAndOwnerId(projectId, ownerId)
                .orElseThrow(ProjectNotFoundException::new);

        apiEndpointRepository
                .findByIdAndProjectId(endpointId, projectId)
                .orElseThrow(EndpointNotFoundException::new);

        return apiTestCaseRepository
                .findAllByEndpointIdOrderByCreatedAtDesc(endpointId)
                .stream()
                .map(TestCaseResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TestCaseResponse getTestCase(
            Long ownerId,
            Long projectId,
            Long endpointId,
            Long testCaseId
    ) {
        apiProjectRepository
                .findByIdAndOwnerId(projectId, ownerId)
                .orElseThrow(ProjectNotFoundException::new);

        apiEndpointRepository
                .findByIdAndProjectId(endpointId, projectId)
                .orElseThrow(EndpointNotFoundException::new);

        ApiTestCase testCase = apiTestCaseRepository
                .findByIdAndEndpointId(testCaseId, endpointId)
                .orElseThrow(TestCaseNotFoundException::new);

        return TestCaseResponse.from(testCase);
    }
}