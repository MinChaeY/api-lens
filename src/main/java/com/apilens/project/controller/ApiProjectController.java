package com.apilens.project.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import com.apilens.project.dto.CreateProjectRequest;
import com.apilens.project.dto.ProjectResponse;
import com.apilens.project.service.ProjectService;
import com.apilens.project.dto.UpdateProjectRequest;
import com.apilens.project.dto.RegisterOpenApiRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ApiProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateProjectRequest request
    ) {
        Long ownerId = Long.valueOf(jwt.getSubject());

        ProjectResponse response =
                projectService.createProject(ownerId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getMyProjects(
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long ownerId = Long.valueOf(jwt.getSubject());

        List<ProjectResponse> responses = projectService.getMyProjects(ownerId);

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProject(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long projectId
    ) {
        Long ownerId = Long.valueOf(jwt.getSubject());

        ProjectResponse response = projectService.getProject(ownerId, projectId);

        return ResponseEntity.ok(response);
    }
    @PutMapping("/{projectId}")
        public ResponseEntity<ProjectResponse> updateProject(
                @AuthenticationPrincipal Jwt jwt,
        @PathVariable("projectId") Long projectId,
        @Valid @RequestBody UpdateProjectRequest request
    ) {
        Long ownerId = Long.valueOf(jwt.getSubject());
        
        ProjectResponse response = projectService.updateProject(
                ownerId,
                projectId,
                request);
        
        return ResponseEntity.ok(response);
        }
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable("projectId") Long projectId
        ) {
                Long ownerId = Long.valueOf(jwt.getSubject());
        
                projectService.deleteProject(ownerId, projectId);
        
                return ResponseEntity.noContent().build();
        }
    @PutMapping("/{projectId}/openapi")
    public ResponseEntity<ProjectResponse> registerOpenApi(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("projectId") Long projectId,
            @Valid @RequestBody RegisterOpenApiRequest request
    ) {
        Long ownerId = Long.valueOf(jwt.getSubject());

        ProjectResponse response = projectService.registerOpenApi(
                ownerId,
                projectId,
                request
        );

        return ResponseEntity.ok(response);
    }

}