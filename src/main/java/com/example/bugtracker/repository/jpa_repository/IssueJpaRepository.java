package com.example.bugtracker.repository.jpa_repository;

import com.example.bugtracker.domain.model.issue.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.*;

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

    @Query("select distinct i from Issue i left join fetch i.assignees where i.id =:issueId")
    Optional<Issue> findByIdWithAssignees(@Param("issueId") Long issueId);

    @Query("select distinct i from Issue i where i.archived=false and i in(select i2 from Issue i2 join i2.assignees a2 where a2.username=:username)")
    //does not need to join fetch user. find any issue that has a given assignee username
    Set<Issue> findActiveIssueByUsername(@Param("username") String username);

    @Query("select i from Issue i where i.id in:ids and i.project.id=:projectId and i.archived=false")
    Set<Issue> findAllByIdAndProjectIdAndArchivedFalse(@Param("ids") Collection<Long> ids, @Param("projectId") Long projectId);


    @Query("select distinct i from Issue i left join fetch i.issueRelationSet where i.id=:issueId" )
    Optional<Issue> findByIdWithIssueRelationSet(@Param("issueId") Long issueId);

    @Query("select distinct i from Issue i left join fetch i.commentList where i.id=:issueId")
    Optional<Issue> findByIdWithCommentList(@Param("issueId") Long issueId);

    Set<Issue> findAllByProjectId(Long projectId);

    List<Issue> findAll();

    @Query("select distinct i from Issue i left join fetch i.commentList "
           + "where i in ("
           + " select i2 from Issue i2"
           + " join i2.commentList cl"
           + " where i2.id=:issueId and cl.id=:parentId)")
    Optional<Issue> findByIdWithCommentListWithParent(@Param("issueId")Long issueId,@Param("parentId") Long parentId);
}
