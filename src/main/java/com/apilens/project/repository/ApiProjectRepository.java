package com.apilens.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.apilens.project.domain.ApiProject;

public interface ApiProjectRepository
        extends JpaRepository<ApiProject, Long> {

    List<ApiProject> findAllByOwnerIdOrderByCreatedAtDesc(Long ownerId);
}