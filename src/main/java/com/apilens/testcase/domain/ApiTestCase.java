package com.apilens.testcase.domain;

import java.time.LocalDateTime;

import com.apilens.endpoint.domain.ApiEndpoint;

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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "api_test_cases")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiTestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "endpoint_id", nullable = false)
    private ApiEndpoint endpoint;

    @Column(nullable = false, length = 200)
    private String name;

    @Lob
    @Column(name = "request_headers", columnDefinition = "TEXT")
    private String requestHeaders;

    @Lob
    @Column(name = "request_body", columnDefinition = "TEXT")
    private String requestBody;

    @Column(name = "expected_status", nullable = false)
    private Integer expectedStatus;

    @Lob
    @Column(name = "expected_body", columnDefinition = "TEXT")
    private String expectedBody;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ApiTestCase(
            ApiEndpoint endpoint,
            String name,
            String requestHeaders,
            String requestBody,
            Integer expectedStatus,
            String expectedBody
    ) {
        this.endpoint = endpoint;
        this.name = name;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
        this.expectedStatus = expectedStatus;
        this.expectedBody = expectedBody;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}