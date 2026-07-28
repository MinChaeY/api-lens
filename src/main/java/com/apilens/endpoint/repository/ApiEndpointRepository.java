package com.apilens.endpoint.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.apilens.endpoint.domain.ApiEndpoint;
import com.apilens.endpoint.domain.ApiHttpMethod;

public interface ApiEndpointRepository
        extends JpaRepository<ApiEndpoint, Long> {

    List<ApiEndpoint>
            findAllByProjectIdOrderByPathAscHttpMethodAsc(
                    Long projectId
            );

    Optional<ApiEndpoint>
            findByProjectIdAndHttpMethodAndPath(
                    Long projectId,
                    ApiHttpMethod httpMethod,
                    String path
            );
    Optional<ApiEndpoint>
            findByIdAndProjectId(
                    Long endpointId,
                    Long projectId
            );

    void deleteAllByProjectId(Long projectId);
}