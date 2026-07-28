package com.apilens.endpoint.dto;

import com.apilens.endpoint.domain.ApiEndpoint;
import com.apilens.endpoint.domain.ApiHttpMethod;

public record ApiEndpointResponse(
        Long id,
        ApiHttpMethod httpMethod,
        String path,
        String summary,
        String description,
        String operationId
) {

    public static ApiEndpointResponse from(ApiEndpoint endpoint) {
        return new ApiEndpointResponse(
                endpoint.getId(),
                endpoint.getHttpMethod(),
                endpoint.getPath(),
                endpoint.getSummary(),
                endpoint.getDescription(),
                endpoint.getOperationId()
        );
    }
}