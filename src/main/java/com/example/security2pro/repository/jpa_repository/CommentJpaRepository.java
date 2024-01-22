package com.example.security2pro.repository.jpa_repository;

import com.example.security2pro.domain.model.issue.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Collection;
import java.util.List;


@Repository
public interface CommentJpaRepository extends JpaRepository<Comment, Long> {

    //@Query("select ac from Activity ac where ac.issue.id =:issueId")
    public Page<Comment> findAllByIssueId(Long issueId, Pageable pageable);

    //@Query("select ac from Activity ac where ac.issue.id in :issueIds")
    public List<Comment> findByIssueIdIn(Collection<Long> issueIds);



}
