package com.example.security2pro.repository.repository_interfaces;


import com.example.security2pro.domain.model.issue.Issue;

import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface IssueRepository {

    Issue getReferenceById(Long issueId);
    Set<Issue> findActiveIssueByUsername(String username);
    Set<Issue> findByProjectIdAndArchivedFalse(Long projectId);
    Set<Issue> findByCurrentSprintId(Long sprintId);
    Optional<Issue> findByIdWithAssignees(Long issueId);
    Issue save(Issue issue);
    Set<Issue> findAllByIdAndProjectIdAndArchivedFalse(Set<Long> issueDtoIds, Long projectId);
    Set<Issue> saveAll(Set<Issue> resultIssues);
    void deleteAllByIdInBatch(Set<Long> issueIds);
    Set<Issue> findByCurrentSprintIdIn(Set<Long> sprintIds);
    Set<Issue> findByProjectIdAndArchivedFalseAndCurrentSprintIsNull(Long projectId);
    Set<Issue> findAllById(Set<Long> issueId);
    Optional<Issue> findById(Long targetId);
    Optional<Issue> findByIdWithIssueRelationSet(Long issueId);

    Optional<Issue> findByIdWithCommentList(Long issueId);

    Set<Issue> findAllByProjectId(Long projectId);

    List<Issue> findAll();

}
