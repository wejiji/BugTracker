package com.example.bugtracker.repository.repository_interfaces;


import com.example.bugtracker.domain.model.issue.Comment;
import com.example.bugtracker.dto.issue.onetomany.CommentPageDto;

import java.util.Optional;

public interface CommentRepository {

    CommentPageDto findAllByIssueId(Long issueId, int offset, int limit);

    void deleteById(Long id);

    Comment save(Comment comment);

    Optional<Comment> findById(Long commentId);

}
