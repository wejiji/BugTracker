package com.example.security2pro.repository.repository_impls;

import com.example.security2pro.domain.model.Project;
import com.example.security2pro.repository.jpa_repository.ProjectJpaRepository;
import com.example.security2pro.repository.repository_interfaces.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepository {

    private final ProjectJpaRepository projectJpaRepository;

    @Override
    public Project save(Project newProject) {
        return projectJpaRepository.save(newProject);
    }

    @Override
    public Project getReferenceById(Long projectId) {
        return projectJpaRepository.getReferenceById(projectId);
    }

    @Override
    public void deleteById(Long projectId) {
        projectJpaRepository.deleteById(projectId);
    }
}
