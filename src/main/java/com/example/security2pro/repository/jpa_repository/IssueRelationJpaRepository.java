package com.example.security2pro.repository.jpa_repository;

import com.example.security2pro.domain.model.IssueRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface IssueRelationJpaRepository extends JpaRepository<IssueRelation, Long> {
//    @Query("select ir from IssueRelation ir join fetch ir.affectedIssue join fetch ir.causeIssue where ir.affectedIssue.id=:affectedIssueId")
//    public Set<IssueRelation> findAllByAffectedIssueIdWithIssues(@Param("affectedIssueId") Long affectedIssueId);

    @Query("select ir from IssueRelation ir where ir.affectedIssue.id=:affectedIssueId")
    public Set<IssueRelation> findAllByAffectedIssueId(@Param("affectedIssueId") Long affectedIssueId);

    @Query("select ir from IssueRelation ir where ir.affectedIssue.id in:issueIds or ir.causeIssue.id in:issueIds")
    public Set<IssueRelation> findAllByIssueIds(@Param("issueIds") Collection<Long> issueIds);

    @Query("select ir from IssueRelation ir where ir.id in:ids and ir.affectedIssue.id=:affectedIssueId")
    Set<IssueRelation> findAllByIdAndAffectedIssueId(@Param("ids") Collection<Long> ids, @Param("affectedIssueId") Long affectedIssueId);

    @Query("select ir from IssueRelation ir where  ir.affectedIssue.id=:affectedIssueId and ir.causeIssue.id in :causeIssueIds")
    Set<IssueRelation> findAllByAffectedIssueIdAndCauseIssueIds(@Param("affectedIssueId") Long affectedIssueId, @Param("causeIssueIds")Collection<Long> causeIssueIds);

    @Query("select ir from IssueRelation ir where  ir.affectedIssue.id=:affectedIssueId and ir.causeIssue.id =:causeIssueId")
    Optional<IssueRelation> findByAffectedIssueIdAndCauseIssueId(@Param("affectedIssueId") Long affectedIssueId, @Param("causeIssueId")Long causeIssueId);


    //========================
    @Query("delete from IssueRelation ir where ir.affectedIssue.id=:affectedIssueId and ir.causeIssue.id =:causeIssueId")
    void deleteByAffectedIssueIdAndCauseIssueId(@Param("affectedIssueId") Long affectedIssueId, @Param("causeIssueId")Long causeIssueId);

}
