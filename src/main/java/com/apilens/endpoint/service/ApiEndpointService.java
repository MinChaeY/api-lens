package com.apilens.endpoint.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apilens.endpoint.dto.ApiEndpointResponse;
import com.apilens.endpoint.repository.ApiEndpointRepository;
import com.apilens.project.exception.ProjectNotFoundException;
import com.apilens.project.repository.ApiProjectRepository;
import com.apilens.endpoint.domain.ApiEndpoint;
import com.apilens.endpoint.exception.EndpointNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApiEndpointService {

    private final ApiProjectRepository apiProjectRepository;
    private final ApiEndpointRepository apiEndpointRepository;

    @Transactional(readOnly = true)
    public List<ApiEndpointResponse> getEndpoints(
            Long ownerId,
            Long projectId
    ) {
        apiProjectRepository
                .findByIdAndOwnerId(projectId, ownerId)
                .orElseThrow(ProjectNotFoundException::new);

        return apiEndpointRepository
                .findAllByProjectIdOrderByPathAscHttpMethodAsc(projectId)
                .stream()
                .map(ApiEndpointResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ApiEndpointResponse getEndpoint(
            Long ownerId,
            Long projectId,
            Long endpointId
    ) {
        apiProjectRepository
                .findByIdAndOwnerId(projectId, ownerId)
                .orElseThrow(ProjectNotFoundException::new);

        ApiEndpoint endpoint = apiEndpointRepository
                .findByIdAndProjectId(endpointId, projectId)
                .orElseThrow(EndpointNotFoundException::new);

        return ApiEndpointResponse.from(endpoint);
    }
}