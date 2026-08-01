package com.apilens.testcase.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.apilens.testcase.domain.ApiTestCase;

public interface ApiTestCaseRepository
        extends JpaRepository<ApiTestCase, Long> {

    List<ApiTestCase> findAllByEndpointIdOrderByCreatedAtDesc(
            Long endpointId
    );

    Optional<ApiTestCase> findByIdAndEndpointId(
            Long testCaseId,
            Long endpointId
    );

    void deleteAllByEndpointId(Long endpointId);
}