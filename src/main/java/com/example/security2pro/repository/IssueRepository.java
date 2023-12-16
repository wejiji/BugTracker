package com.example.security2pro.repository;

import com.example.security2pro.domain.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long>, RevisionRepository<Issue,Long,Long> {

//    @Query("select i from Issue i where i.project.id=:projectId and i.archived=false")
//    Set<Issue> findActiveExistingIssuesByProjectId(@Param("projectId") Long projectId);
    @Query("select i from Issue i where i.project.id=:projectId and i.archived=false")
    Set<Issue> findByProjectIdAndArchivedFalse(@Param("projectId") Long projectId);



    //shows issues that don't belong to any sprint. for a main page
//    @Query("select i from Issue i where i.project.id=:projectId and i.archived=false and i.currentSprint is null")
//    Set<Issue> findActiveIssuesWithoutSprintByProjectId(@Param("projectId") Long projectId);
    @Query("select i from Issue i where i.project.id=:projectId and i.archived=false and i.currentSprint is null")
    Set<Issue> findByProjectIdAndArchivedFalseAndCurrentSprintIsNull(@Param("projectId") Long projectId);


    //issues that belong to currently active sprint. for a main page
//    @Query("select i from Issue i where i.project.id=:projectId and i.archived=false and i.currentSprint is not null")
//    Set<Issue> findIssuesByProjectIdThatBelongToActiveSprints(@Param("projectId") Long projectId);
    @Query("select i from Issue i where i.project.id=:projectId and i.archived=false and i.currentSprint is not null")
    Set<Issue> findByProjectIdAndArchivedFalseAndCurrentSprintIsNotNull(@Param("projectId") Long projectId);


//    @Query("select i from Issue i where i.currentSprint.id =:sprintId")
//    Set<Issue> findIssuesByCurrentSprintId(@Param("sprintId") Long sprintId);
    @Query("select i from Issue i where i.currentSprint.id =:sprintId")
    Set<Issue> findByCurrentSprintId(Long sprintId);


    @Query("select i from Issue i where i.currentSprint.id in:sprintIds")
    Set<Issue> findByCurrentSprintIdIn(@Param("sprintIds") Collection<Long> sprintIds);


    @Query("select i from Issue i join fetch i.assignees where i.id =:issueId and i.project.id=:projectId")
    Optional<Issue> findByIdAndProjectIdWithAssignees(@Param("issueId") Long issueId , @Param("projectId") Long projectId);

    @Query("select i from Issue i where i.archived=false and i in(select i2 from Issue i2 join i2.assignees a2 where a2.username=:username)")
    //does not need user. find any issue that has a given assignee username
    Set<Issue> findActiveIssueByAssignee(@Param("username") String username);

    @Query("select i from Issue i where i.id in:ids and i.project.id=:projectId and i.archived=false")
    Set<Issue> findAllByIdAndProjectIdAndArchivedFalse(@Param("ids") Collection<Long> ids, @Param("projectId") Long projectId);


}
