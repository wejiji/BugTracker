package com.example.security2pro.repository;

import com.example.security2pro.domain.model.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Collection;
import java.util.List;


@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    //@Query("select ac from Activity ac where ac.issue.id =:issueId")
    public Page<Activity> findAllByIssueId(Long issueId, Pageable pageable);

    //@Query("select ac from Activity ac where ac.issue.id in :issueIds")
    public List<Activity> findByIssueIdIn( Collection<Long> issueIds);



}
