package com.apilens.testcase.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "api_test_results")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiTestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "test_case_id", nullable = false)
    private ApiTestCase testCase;

    @Column(name = "request_url", nullable = false, length = 2000)
    private String requestUrl;

    @Column(name = "expected_status", nullable = false)
    private Integer expectedStatus;

    @Column(name = "actual_status")
    private Integer actualStatus;

    @Lob
    @Column(name = "expected_body", columnDefinition = "TEXT")
    private String expectedBody;

    @Lob
    @Column(name = "actual_body", columnDefinition = "TEXT")
    private String actualBody;

    @Column(nullable = false)
    private boolean passed;

    @Lob
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "response_time_ms", nullable = false)
    private Long responseTimeMs;

    @Column(name = "executed_at", nullable = false)
    private LocalDateTime executedAt;

    public ApiTestResult(
            ApiTestCase testCase,
            String requestUrl,
            Integer expectedStatus,
            Integer actualStatus,
            String expectedBody,
            String actualBody,
            boolean passed,
            String errorMessage,
            Long responseTimeMs
      ) {
          this.testCase = testCase;
          this.requestUrl = requestUrl;
          this.expectedStatus = expectedStatus;
          this.actualStatus = actualStatus;
          this.expectedBody = expectedBody;
          this.actualBody = actualBody;
          this.passed = passed;
          this.errorMessage = errorMessage;
          this.responseTimeMs = responseTimeMs;
      }

    @PrePersist
    public void prePersist() {
        this.executedAt = LocalDateTime.now();
    }
}
