package com.example.security2pro.repository.repository_interfaces;

import com.example.security2pro.domain.model.Project;

public interface ProjectRepository {


    Project save(Project newProject);


    Project getReferenceById(Long projectId);


    void deleteById(Long projectId);


}
