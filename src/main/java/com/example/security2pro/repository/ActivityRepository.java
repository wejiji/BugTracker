package com.example.security2pro.repository;

import com.example.security2pro.domain.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Set;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    @Query("select count(ac) from Activity ac where ac.issue.id =:issueId and ac.type=ISSUE_HISTORY")
    public int findIssueHistoryCountByIssueId(@Param("issueId")Long issueId);

    @Query("select ac from Activity ac where ac.issue.id =:issueId and ac.type=ISSUE_HISTORY")
    public Set<Activity> findIssueHistoryByIssueId(@Param("issueId")Long issueId);

    @Query("select ac from Activity ac where ac.issue.id =:issueId")
    public Set<Activity> findByIssueId(@Param("issueId")Long issueId);

    @Query("select ac from Activity ac where ac.issue.id in :issueIds")
    public Set<Activity> findByIssueIds(@Param("issueIds") Collection<Long> issueIds);



}
