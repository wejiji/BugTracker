package com.example.security2pro.repository.repository_interfaces;

import com.example.security2pro.domain.model.IssueRelation;

import java.util.Optional;
import java.util.Set;

public interface IssueRelationRepository {
    Optional<IssueRelation> findByAffectedIssueIdAndCauseIssueId(Long affectedIssueId, Long causeIssueId);

    IssueRelation save(IssueRelation issueRelation);
    void deleteByAffectedIssueIdAndCauseIssueId(Long issueId, Long causeIssueId);
    Set<IssueRelation> findAllByAffectedIssueId(Long affectedIssueId);
    Set<IssueRelation> findAllByAffectedIssueIds(Set<Long> affectedIssueIds);
    void deleteAllByIdInBatch(Set<Long> relations);

}

