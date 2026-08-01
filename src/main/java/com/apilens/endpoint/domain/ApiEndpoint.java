package com.apilens.endpoint.domain;

import java.time.LocalDateTime;

import com.apilens.project.domain.ApiProject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "api_endpoints",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_api_endpoint_project_method_path",
                        columnNames = {
                                "project_id",
                                "http_method",
                                "path"
                        }
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiEndpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private ApiProject project;

    @Enumerated(EnumType.STRING)
    @Column(name = "http_method", nullable = false, length = 10)
    private ApiHttpMethod httpMethod;

    @Column(nullable = false, length = 1000)
    private String path;

    @Column(length = 200)
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "operation_id", length = 200)
    private String operationId;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ApiEndpoint(
            ApiProject project,  //해당 엔드포인트가 소속된 API 프로젝트
            ApiHttpMethod httpMethod,  //GET, POST, PUT 등의 HTTP 메서드
            String path,  ///users, /projects/{id} 등의 API 경로
            String summary, //Swagger에 작성된 간단한 API 설명
            String description,  //Swagger에 작성된 상세 설명
            String operationId  //OpenAPI 문서에 정의된 고유 작업 이름
    ) {
        this.project = project;
        this.httpMethod = httpMethod;
        this.path = path;
        this.summary = summary;
        this.description = description;
        this.operationId = operationId;
    }

    public void updateMetadata(
            String summary,
            String description,
            String operationId
    ) {
        this.summary = summary;
        this.description = description;
        this.operationId = operationId;
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateOpenApiInfo(
            String summary,
            String description,
            String operationId
    ) {
        this.summary = summary;
        this.description = description;
        this.operationId = operationId;
    }
}