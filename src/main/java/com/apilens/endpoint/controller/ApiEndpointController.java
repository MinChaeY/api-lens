package com.apilens.endpoint.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apilens.endpoint.dto.ImportEndpointsResponse;
import com.apilens.endpoint.service.OpenApiImportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/projects/{projectId}/endpoints")
@RequiredArgsConstructor
public class ApiEndpointController {

    private final OpenApiImportService openApiImportService;

    @PostMapping("/import")
    public ResponseEntity<ImportEndpointsResponse> importEndpoints(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("projectId") Long projectId
    ) {
        Long ownerId = Long.valueOf(jwt.getSubject());

        ImportEndpointsResponse response =
                openApiImportService.importEndpoints(
                        ownerId,
                        projectId
                );

        return ResponseEntity.ok(response);
    }
}