package com.apilens.testcase.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Modifying(flushAutomatically = true)
    @Query("""
            delete from ApiTestCase testCase
            where testCase.endpoint.project.id = :projectId
            """)
    void deleteAllByProjectId(
            @Param("projectId") Long projectId
    );
}