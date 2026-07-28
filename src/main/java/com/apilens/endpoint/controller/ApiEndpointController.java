package com.apilens.endpoint.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import com.apilens.endpoint.dto.ImportEndpointsResponse;
import com.apilens.endpoint.service.OpenApiImportService;
import com.apilens.endpoint.dto.ApiEndpointResponse;
import com.apilens.endpoint.service.ApiEndpointService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/projects/{projectId}/endpoints")
@RequiredArgsConstructor
public class ApiEndpointController {

    private final OpenApiImportService openApiImportService;
    private final ApiEndpointService apiEndpointService;

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

    @GetMapping
    public ResponseEntity<List<ApiEndpointResponse>> getEndpoints(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("projectId") Long projectId
    ) {
        Long ownerId = Long.valueOf(jwt.getSubject());

        List<ApiEndpointResponse> responses =
                apiEndpointService.getEndpoints(
                        ownerId,
                        projectId
                );

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{endpointId}")
    public ResponseEntity<ApiEndpointResponse> getEndpoint(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("projectId") Long projectId,
            @PathVariable("endpointId") Long endpointId
    ) {
        Long ownerId = Long.valueOf(jwt.getSubject());

        ApiEndpointResponse response =
                apiEndpointService.getEndpoint(
                        ownerId,
                        projectId,
                        endpointId
                );

        return ResponseEntity.ok(response);
    }
}