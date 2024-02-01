package com.example.bugtracker.repository.repository_interfaces;

import com.example.bugtracker.domain.model.Project;

public interface ProjectRepository {
    Project save(Project newProject);

    Project getReferenceById(Long projectId);

    void deleteById(Long projectId);



}
