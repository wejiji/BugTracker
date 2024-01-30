package com.example.security2pro.fake.repository;

import com.example.security2pro.databuilders.ProjectTestDataBuilder;
import com.example.security2pro.domain.model.Project;
import com.example.security2pro.repository.repository_interfaces.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class ProjectRepositoryFake implements ProjectRepository {

    private final Map<Long, Project> projectMap = new HashMap<>();

    private Long generatedId = 0L;

    @Override
    public Project save(Project newProject) {
        if(newProject.getId()==null){
            generatedId++;

            Project project = new ProjectTestDataBuilder()
                    .withId(generatedId)
                    .withName(newProject.getName())
                    .withDescription(newProject.getDescription())
                    .build();

            projectMap.put(project.getId(), project);
            return project;
        }
        projectMap.remove(newProject.getId());
        projectMap.put(newProject.getId(),newProject);
        return newProject;
    }

    @Override
    public Project getReferenceById(Long projectId) {
        if(projectMap.get(projectId)==null){
            throw new EntityNotFoundException ("project with id"+ projectId + "not found");
        }
        return projectMap.get(projectId);
    }

    @Override
    public void deleteById(Long projectId) {
        projectMap.remove(projectId);
    }
}
