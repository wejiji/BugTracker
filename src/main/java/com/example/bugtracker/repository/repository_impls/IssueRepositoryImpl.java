package com.example.bugtracker.repository.repository_impls;

import com.example.bugtracker.domain.model.issue.Issue;
import com.example.bugtracker.repository.jpa_repository.IssueJpaRepository;
import com.example.bugtracker.repository.repository_interfaces.IssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;


@RequiredArgsConstructor
@Repository
public class IssueRepositoryImpl implements IssueRepository {

    private final IssueJpaRepository issueJpaRepository;

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
        /*
         * Fetches 'IssueRelations' of the 'Issue' with the provided ID by retrieving the associated 'Issue'.
         * JOIN FETCH is necessary to efficiently retrieve 'IssueRelation' and prevent the N+1 query problem,
         * given the many-to-one relationship between 'IssueRelation' and 'User' with Lazy fetch type.
         */
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

    @Override
    public void deleteById(Long issueId) {
        issueJpaRepository.deleteById(issueId);
    }

    @Override
    public Optional<Issue> findByIdWithCommentListWithParent(Long issueId, Long parentId) {
        return issueJpaRepository.findByIdWithCommentListWithParent(issueId, parentId);
    }


}
