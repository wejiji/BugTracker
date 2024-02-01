package com.example.bugtracker.repository.repository_interfaces;

import com.example.bugtracker.domain.model.ProjectMember;

import java.util.Optional;
import java.util.Set;

public interface ProjectMemberRepository {
    Optional<ProjectMember> findByUsernameAndProjectIdWithAuthorities(String username, Long projectId);

    Optional<ProjectMember> findById(Long projectMemberId);

    Optional<ProjectMember> findByUsernameAndProjectId(String username, Long projectId);

    ProjectMember save(ProjectMember projectMember);

    ProjectMember getReferenceById(Long projectMemberId);

    void deleteById(Long projectMemberId);

    Optional<ProjectMember> findByIdWithAuthorities(Long projectMemberId);

    Set<ProjectMember> findAllMemberByProjectIdWithAuthorities(Long projectId);

    Set<ProjectMember> findAllByUsernameAndProjectIdWithUser(Set<String> passedAssigneesUsernames, Long projectId);

    Set<ProjectMember> findAllByProjectId(Long projectId);

    void deleteAllByIdInBatch(Set<Long> projectMemberIds);

    Set<ProjectMember> findAllByUsernameWithProjectMemberAuthorities(String username);

}
