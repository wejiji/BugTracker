package com.example.security2pro.repository;

import com.example.security2pro.domain.model.Project;
import com.example.security2pro.repository.repository_interfaces.ProjectRepository;

import java.util.HashMap;
import java.util.Map;

public class ProjectRepositoryFake implements ProjectRepository {

    private final Map<Long, Project> projectMap = new HashMap<>();

    private Long generatedId = Long.valueOf(0);

    @Override
    public Project save(Project newProject) {
        if(newProject.getId()==null){
            generatedId++;
            Project project = Project.builder()
                    .id(generatedId)
                    .name(newProject.getName())
                    .description(newProject.getDescription())
                    .build();
            projectMap.put(project.getId(),project);
            return project;
        }
        return projectMap.put(newProject.getId(),newProject);
    }

    @Override
    public Project getReferenceById(Long projectId) {
        return projectMap.get(projectId);
    }

    @Override
    public void deleteById(Long projectId) {
        projectMap.remove(projectId);
    }
}
