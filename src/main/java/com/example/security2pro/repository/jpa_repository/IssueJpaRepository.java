package com.example.security2pro.repository.jpa_repository;

import com.example.security2pro.domain.model.issue.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface IssueJpaRepository extends JpaRepository<Issue, Long>, RevisionRepository<Issue,Long,Long> {

    @Query("select i from Issue i where i.project.id=:projectId and i.archived=false")
    Set<Issue> findByProjectIdAndArchivedFalse(@Param("projectId") Long projectId);

    @Query("select i from Issue i where i.project.id=:projectId and i.archived=false and i.currentSprint is null")
    Set<Issue> findByProjectIdAndArchivedFalseAndCurrentSprintIsNull(@Param("projectId") Long projectId);

    @Query("select i from Issue i where i.currentSprint.id =:sprintId")
    Set<Issue> findByCurrentSprintId(Long sprintId);

    @Query("select i from Issue i where i.currentSprint.id in:sprintIds")
    Set<Issue> findByCurrentSprintIdIn(@Param("sprintIds") Collection<Long> sprintIds);

    @Query("select i from Issue i join fetch i.assignees where i.id =:issueId")
    Optional<Issue> findByIdWithAssignees(@Param("issueId") Long issueId);

    @Query("select i from Issue i where i.archived=false and i in(select i2 from Issue i2 join i2.assignees a2 where a2.username=:username)")
    //does not need to join fetch user. find any issue that has a given assignee username
    Set<Issue> findActiveIssueByUsername(@Param("username") String username);

    @Query("select i from Issue i where i.id in:ids and i.project.id=:projectId and i.archived=false")
    Set<Issue> findAllByIdAndProjectIdAndArchivedFalse(@Param("ids") Collection<Long> ids, @Param("projectId") Long projectId);


    @Query("select i from Issue i join fetch i.issueRelationSet where i.id=:issueId" )
    Optional<Issue> findByIdWithIssueRelationSet(Long issueId);

    @Query("select i from Issue i join fetch i.commentList where i.id=:issueId")
    Optional<Issue> findByIdWithCommentList(Long issueId);

    Set<Issue> findAllByProjectId(Long projectId);

    List<Issue> findAll();

}
