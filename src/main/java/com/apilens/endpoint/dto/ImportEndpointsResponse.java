package com.apilens.endpoint.dto;

import java.util.List;

import com.apilens.endpoint.domain.ApiEndpoint;

public record ImportEndpointsResponse(
        Long projectId,
        int importedCount,
        List<ApiEndpointResponse> endpoints
) {

    public static ImportEndpointsResponse from(
            Long projectId,
            List<ApiEndpoint> endpoints
    ) {
        List<ApiEndpointResponse> responses = endpoints.stream()
                .map(ApiEndpointResponse::from)
                .toList();

        return new ImportEndpointsResponse(
                projectId,
                responses.size(),
                responses
        );
    }
}