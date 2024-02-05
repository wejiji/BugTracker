package com.example.bugtracker.repository.jpa_repository;

import com.example.bugtracker.domain.model.issue.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.Collection;
import java.util.List;


@Repository
public interface CommentJpaRepository extends JpaRepository<Comment, Long> {
    @Query("select c from Comment c join fetch c.parent where c.issue =:issueId")
    Page<Comment> findAllByIssueIdWithParent(Long issueId, Pageable pageable);
    List<Comment> findByIssueIdIn(Collection<Long> issueIds);

}
