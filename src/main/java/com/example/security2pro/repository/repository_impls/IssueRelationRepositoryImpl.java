package com.example.security2pro.repository.repository_impls;

import com.example.security2pro.domain.model.IssueRelation;
import com.example.security2pro.repository.jpa_repository.IssueRelationJpaRepository;
import com.example.security2pro.repository.repository_interfaces.IssueRelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class IssueRelationRepositoryImpl implements IssueRelationRepository {

    IssueRelationJpaRepository issueRelationJpaRepository;
    @Override
    public Optional<IssueRelation> findByAffectedIssueIdAndCauseIssueId(Long issueId, Long causeIssueId) {
        return issueRelationJpaRepository.findByAffectedIssueIdAndCauseIssueId(issueId, causeIssueId);
    }

    @Override
    public IssueRelation save(IssueRelation issueRelation) {
        return issueRelationJpaRepository.save(issueRelation);
    }

    @Override
    public void deleteByAffectedIssueIdAndCauseIssueId(Long issueId, Long causeIssueId) {
        issueRelationJpaRepository.deleteByAffectedIssueIdAndCauseIssueId(issueId, causeIssueId);
    }

    @Override
    public Set<IssueRelation> findAllByAffectedIssueId(Long affectedIssueId) {
        return issueRelationJpaRepository.findAllByAffectedIssueId(affectedIssueId);
    }

    @Override
    public Set<IssueRelation> findAllByAffectedIssueIds(Set<Long> issueIds) {
        return issueRelationJpaRepository.findAllByIssueIds(issueIds);
    }

    @Override
    public void deleteAllByIdInBatch(Set<Long> relations) {
        issueRelationJpaRepository.deleteAllByIdInBatch(relations);
    }
}
