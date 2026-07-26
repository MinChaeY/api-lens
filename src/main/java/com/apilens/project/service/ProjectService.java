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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ApiProjectRepository apiProjectRepository;
    private final UserRepository userRepository;

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
}