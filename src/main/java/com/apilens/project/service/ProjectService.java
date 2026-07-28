package com.apilens.project.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apilens.project.domain.ApiProject;
import com.apilens.project.dto.CreateProjectRequest;
import com.apilens.project.dto.ProjectResponse;
import com.apilens.project.repository.ApiProjectRepository;
import com.apilens.user.domain.User;
import com.apilens.user.exception.UserNotFoundException;
import com.apilens.user.repository.UserRepository;
import com.apilens.project.exception.ProjectNotFoundException;
import com.apilens.project.dto.UpdateProjectRequest;
import com.apilens.project.dto.RegisterOpenApiRequest;
import com.apilens.endpoint.repository.ApiEndpointRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ApiProjectRepository apiProjectRepository;
    private final UserRepository userRepository;
    private final ApiEndpointRepository apiEndpointRepository;

    @Transactional
    public ProjectResponse createProject(
            Long ownerId,
            CreateProjectRequest request
    ) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(UserNotFoundException::new);

        ApiProject project = new ApiProject(
                owner,
                request.name(),
                request.baseUrl(),
                request.description()
        );

        ApiProject savedProject =
                apiProjectRepository.save(project);

        return ProjectResponse.from(savedProject);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getMyProjects(Long ownerId) {
        return apiProjectRepository
            .findAllByOwnerIdOrderByCreatedAtDesc(ownerId)
            .stream()
            .map(ProjectResponse::from)
            .toList();
    }
    @Transactional(readOnly = true)
    public ProjectResponse getProject(
        Long ownerId,
        Long projectId
    ) {
        ApiProject project = apiProjectRepository
            .findByIdAndOwnerId(projectId, ownerId)
            .orElseThrow(ProjectNotFoundException::new);
            
            return ProjectResponse.from(project);
        }
    @Transactional
    public ProjectResponse updateProject(
        Long ownerId,
        Long projectId,
        UpdateProjectRequest request
    ) {
        ApiProject project = apiProjectRepository
            .findByIdAndOwnerId(projectId, ownerId)
            .orElseThrow(ProjectNotFoundException::new);

        project.update(
            request.name(),
            request.baseUrl(),
            request.description()
        );

        return ProjectResponse.from(project);
    }
    @Transactional
    public void deleteProject(
        Long ownerId,
        Long projectId
    ) {
        ApiProject project = apiProjectRepository
            .findByIdAndOwnerId(projectId, ownerId)
            .orElseThrow(ProjectNotFoundException::new);

        apiEndpointRepository.deleteAllByProjectId(projectId);
        apiProjectRepository.flush(); // Ensure endpoints are deleted before deleting the project
        
        apiProjectRepository.delete(project);
    }

    @Transactional
    public ProjectResponse registerOpenApi(
        Long ownerId,
        Long projectId,
        RegisterOpenApiRequest request
    ) {
        ApiProject project = apiProjectRepository
            .findByIdAndOwnerId(projectId, ownerId)
            .orElseThrow(ProjectNotFoundException::new);

        project.updateOpenApiUrl(request.openApiUrl());

        return ProjectResponse.from(project);
    }
}