package com.example.security2pro.repository;

import com.example.security2pro.domain.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Set;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    //@Query("select ac from Activity ac where ac.issue.id =:issueId")
    public Set<Activity> findAllByIssueId(Long issueId);

    //@Query("select ac from Activity ac where ac.issue.id in :issueIds")
    public Set<Activity> findByIssueIdIn( Collection<Long> issueIds);



}
