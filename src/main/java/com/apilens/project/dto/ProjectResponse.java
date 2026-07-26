package com.apilens.project.dto;

import java.time.LocalDateTime;

import com.apilens.project.domain.ApiProject;

public record ProjectResponse(
        Long id,
        String name,
        String baseUrl,
        String description,
        LocalDateTime createdAt
) {

    public static ProjectResponse from(ApiProject project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getBaseUrl(),
                project.getDescription(),
                project.getCreatedAt()
        );
    }
}