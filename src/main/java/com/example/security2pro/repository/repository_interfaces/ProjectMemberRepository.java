package com.example.security2pro.repository.repository_interfaces;

import com.example.security2pro.domain.model.ProjectMember;

import java.util.Arrays;
import java.util.Map;
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

    Set<ProjectMember> findAllMemberByProjectIdWithUser(Long projectId);

    Set<ProjectMember> findAllByUsernameAndProjectIdWithUser(Set<String> passedAssigneesUsernames, Long projectId);

    Set<ProjectMember> findAllByProjectId(Long projectId);

    void deleteAllByIdInBatch(Set<Long> projectMemberIds);

}
