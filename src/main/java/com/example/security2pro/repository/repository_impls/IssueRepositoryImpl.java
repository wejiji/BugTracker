package com.example.security2pro.repository.repository_impls;

import com.example.security2pro.domain.model.Issue;
import com.example.security2pro.repository.jpa_repository.CommentJpaRepository;
import com.example.security2pro.repository.jpa_repository.IssueJpaRepository;
import com.example.security2pro.repository.repository_interfaces.IssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@RequiredArgsConstructor
@Repository
public class IssueRepositoryImpl implements IssueRepository {

    private final IssueJpaRepository issueJpaRepository;

    private final CommentJpaRepository commentJpaRepository;
    @Override
    public Issue getReferenceById(Long issueId) {
        return issueJpaRepository.getReferenceById(issueId);
    }

    @Override
    public Set<Issue> findActiveIssueByUsername(String username) {
        return issueJpaRepository.findActiveIssueByUsername(username);
    }

    @Override
    public Set<Issue> findByProjectIdAndArchivedFalse(Long projectId) {
        return issueJpaRepository.findByProjectIdAndArchivedFalse(projectId);
    }

    @Override
    public Set<Issue> findByCurrentSprintId(Long sprintId) {
        return issueJpaRepository.findByCurrentSprintId(sprintId);
    }

    @Override
    public Optional<Issue> findByIdWithAssignees(Long issueId) {
        return issueJpaRepository.findByIdWithAssignees(issueId);
    }

    @Override
    public Issue save(Issue issue) {
        return issueJpaRepository.save(issue);
    }

    @Override
    public Set<Issue> findAllByIdAndProjectIdAndArchivedFalse(Set<Long> issueDtoIds, Long projectId) {
        return issueJpaRepository.findAllByIdAndProjectIdAndArchivedFalse(issueDtoIds,projectId);
    }

    @Override
    public Set<Issue> saveAll(Set<Issue> resultIssues) {
        return new HashSet<>(issueJpaRepository.saveAll(resultIssues));
    }

    @Override
    public void deleteAllByIdInBatch(Set<Long> issueIds) {
        issueJpaRepository.deleteAllByIdInBatch(issueIds);
    }

    @Override
    public Set<Issue> findByCurrentSprintIdIn(Set<Long> sprintIds) {
        return issueJpaRepository.findByCurrentSprintIdIn(sprintIds);
    }

    @Override
    public Set<Issue> findByProjectIdAndArchivedFalseAndCurrentSprintIsNull(Long projectId) {
        return issueJpaRepository.findByProjectIdAndArchivedFalseAndCurrentSprintIsNull(projectId);
    }

    @Override
    public Set<Issue> findAllById(Set<Long> issueId) {
        return new HashSet<>(issueJpaRepository.findAllById(issueId));
    }

    @Override
    public Optional<Issue> findById(Long targetId) {
        return issueJpaRepository.findById(targetId);
    }

    @Override
    public Optional<Issue> findByIdWithIssueRelationSet(Long issueId) {
        return issueJpaRepository.findByIdWithIssueRelationSet(issueId);
    }

    @Override
    public Optional<Issue> findByIdWithCommentList(Long issueId) {
        return issueJpaRepository.findByIdWithCommentList(issueId);
    }

    @Override
    public Set<Issue> findAllByProjectId(Long projectId) {
        return issueJpaRepository.findAllByProjectId(projectId);
    }

    @Override
    public List<Issue> findAll() {
        return issueJpaRepository.findAll();
    }


}
