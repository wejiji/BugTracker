package com.example.bugtracker.repository.repository_impls;

import com.example.bugtracker.domain.model.ProjectMember;
import com.example.bugtracker.repository.jpa_repository.ProjectMemberJpaRepository;
import com.example.bugtracker.repository.repository_interfaces.ProjectMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Repository
public class ProjectMemberRepositoryImpl implements ProjectMemberRepository {

    private final ProjectMemberJpaRepository projectMemberJpaRepository;

    @Override
    public Optional<ProjectMember> findByUsernameAndProjectIdWithAuthorities(String username, Long projectId) {
        return projectMemberJpaRepository.findByUsernameAndProjectIdWithAuthorities(username, projectId);
    }

    @Override
    public Optional<ProjectMember> findById(Long projectMemberId) {
        return projectMemberJpaRepository.findById(projectMemberId);
    }

    @Override
    public Optional<ProjectMember> findByUsernameAndProjectId(String username, Long projectId) {
        return projectMemberJpaRepository.findByUsernameAndProjectId(username, projectId);
    }

    @Override
    public ProjectMember save(ProjectMember projectMember) {
        return projectMemberJpaRepository.save(projectMember);
    }

    @Override
    public ProjectMember getReferenceById(Long projectMemberId) {
        return projectMemberJpaRepository.getReferenceById(projectMemberId);
    }


    @Override
    public void deleteById(Long projectMemberId) {
        projectMemberJpaRepository.deleteById(projectMemberId);
    }

    @Override
    public Optional<ProjectMember> findByIdWithAuthorities(Long projectMemberId) {
        return projectMemberJpaRepository.findByIdWithAuthorities(projectMemberId);
    }

    @Override
    public Set<ProjectMember> findAllMemberByProjectIdWithAuthorities(Long projectId) {
        return projectMemberJpaRepository.findAllMemberByProjectIdWithAuthorities(projectId);
    }


    @Override
    public Set<ProjectMember> findAllByUsernameAndProjectIdWithUser(Set<String> passedAssigneesUsernames, Long projectId) {
        /*
         * Avoid the N+1 query problem due to the many-to-one relationship
         * between 'ProjectMember' and 'User' with Lazy fetch type.
         */
        return projectMemberJpaRepository
                .findAllByUsernameAndProjectIdWithUser(passedAssigneesUsernames, projectId);
    }

    @Override
    public Set<ProjectMember> findAllByProjectId(Long projectId) {
        return projectMemberJpaRepository.findAllByProjectId(projectId);
    }

    @Override
    public void deleteAllByIdInBatch(Set<Long> projectMemberIds) {
        projectMemberJpaRepository.deleteAllByIdInBatch(projectMemberIds);
    }

    public Set<ProjectMember> findAllByUsernameWithProjectMemberAuthorities(String username){
        return projectMemberJpaRepository.findAllByUsernameWithProjectMemberAuthorities(username);
    }
}
