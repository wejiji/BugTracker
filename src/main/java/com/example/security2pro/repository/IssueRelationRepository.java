package com.example.security2pro.repository;

import com.example.security2pro.domain.model.IssueRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Set;

@Repository
public interface IssueRelationRepository extends JpaRepository<IssueRelation, Long> {
    @Query("select ir from IssueRelation ir join fetch ir.affectedIssue join fetch ir.causeIssue where ir.affectedIssue.id=:affectedIssueId")
    public Set<IssueRelation> findByAffectedIssue(@Param("affectedIssueId") Long affectedIssueId);

    @Query("select ir from IssueRelation ir where ir.affectedIssue.id= :issueId or ir.causeIssue.id= :issueId")
    public Set<IssueRelation> findAllByIssueId(@Param("issueId") Long issueId);

    @Query("select ir from IssueRelation ir where ir.affectedIssue.id in:issueIds or ir.causeIssue.id in:issueIds")
    public Set<IssueRelation> findAllByIssueIds(@Param("issueIds") Collection<Long> issueIds);


    @Query("select ir from IssueRelation ir where ir.id in:ids and ir.affectedIssue.id=:affectedIssueId")
    Set<IssueRelation> findAllByIdAndAffectedIssueId(@Param("ids") Collection<Long> ids, @Param("affectedIssueId") Long affectedIssueId);


}
