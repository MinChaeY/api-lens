package com.apilens.testcase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.apilens.testcase.domain.ApiTestResult;

public interface ApiTestResultRepository extends JpaRepository<ApiTestResult, Long> {

    @Modifying(flushAutomatically = true)
    @Query("delete from ApiTestResult tr where tr.testCase.id = :testCaseId")
    void deleteAllByTestCaseId(@Param("testCaseId") Long testCaseId);

    @Modifying(flushAutomatically = true)
    @Query("delete from ApiTestResult tr where tr.testCase.endpoint.project.id = :projectId")
    void deleteAllByProjectId(@Param("projectId") Long projectId);
}
