package com.example.security2pro.repository.repository_interfaces;


import com.example.security2pro.domain.model.Comment;
import com.example.security2pro.dto.issue.onetomany.CommentPageDto;

import java.util.Optional;

public interface CommentRepository {

    CommentPageDto findAllByIssueId(Long issueId, int offset, int limit);

    void deleteById(Long id);

    Comment save(Comment comment);

    Optional<Comment> findById(Long commentId);

}
