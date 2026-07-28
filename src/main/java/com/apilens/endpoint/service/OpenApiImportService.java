package com.apilens.endpoint.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apilens.endpoint.domain.ApiEndpoint;
import com.apilens.endpoint.domain.ApiHttpMethod;
import com.apilens.endpoint.dto.ImportEndpointsResponse;
import com.apilens.endpoint.exception.OpenApiImportException;
import com.apilens.endpoint.exception.OpenApiUrlNotRegisteredException;
import com.apilens.endpoint.repository.ApiEndpointRepository;
import com.apilens.project.domain.ApiProject;
import com.apilens.project.exception.ProjectNotFoundException;
import com.apilens.project.repository.ApiProjectRepository;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OpenApiImportService {

    private final ApiProjectRepository apiProjectRepository;
    private final ApiEndpointRepository apiEndpointRepository;

    @Transactional
    public ImportEndpointsResponse importEndpoints(
            Long ownerId,
            Long projectId
    ) {
        ApiProject project = apiProjectRepository
                .findByIdAndOwnerId(projectId, ownerId)
                .orElseThrow(ProjectNotFoundException::new);

        String openApiUrl = project.getOpenApiUrl();

        if (openApiUrl == null || openApiUrl.isBlank()) {
            throw new OpenApiUrlNotRegisteredException();
        }

        SwaggerParseResult parseResult;

        try {
            parseResult = new OpenAPIParser()
                    .readLocation(openApiUrl, null, null);
        } catch (RuntimeException exception) {
            throw new OpenApiImportException();
        }

        if (parseResult == null) {
            throw new OpenApiImportException();
        }

        OpenAPI openAPI = parseResult.getOpenAPI();

        if (openAPI == null || openAPI.getPaths() == null) {
            throw new OpenApiImportException();
        }

        List<ApiEndpoint> endpoints = new ArrayList<>();

        openAPI.getPaths().forEach((path, pathItem) -> {
            if (pathItem == null) {
                return;
            }

            pathItem.readOperationsMap().forEach(
                    (swaggerMethod, operation) -> {

                        ApiHttpMethod httpMethod =
                                ApiHttpMethod.valueOf(
                                        swaggerMethod.name()
                                );

                        ApiEndpoint endpoint = new ApiEndpoint(
                                project,
                                httpMethod,
                                path,
                                operation.getSummary(),
                                operation.getDescription(),
                                operation.getOperationId()
                        );

                        endpoints.add(endpoint);
                    }
            );
        });

        /*
         * 문서를 다시 불러오면 기존 엔드포인트를 지우고
         * 최신 문서 기준으로 다시 저장한다.
         */
        apiEndpointRepository.deleteAllByProjectId(projectId);
        apiEndpointRepository.flush();

        List<ApiEndpoint> savedEndpoints =
                apiEndpointRepository.saveAll(endpoints);

        return ImportEndpointsResponse.from(
                projectId,
                savedEndpoints
        );
    }
}