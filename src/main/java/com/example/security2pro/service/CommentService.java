package com.example.security2pro.service;

import com.example.security2pro.domain.model.issue.Comment;
import com.example.security2pro.domain.model.issue.Issue;
import com.example.security2pro.dto.issue.onetomany.CommentCreateDto;
import com.example.security2pro.dto.issue.onetomany.CommentPageDto;
import com.example.security2pro.repository.repository_interfaces.CommentRepository;
import com.example.security2pro.repository.repository_interfaces.IssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final IssueRepository issueRepository;


    public CommentCreateDto createComment(CommentCreateDto commentCreateDto){
        Issue issue = issueRepository.findByIdWithCommentList(commentCreateDto.getIssueId()).get();
         //issue id check is done by permission evaluator before controller
        Comment savedComment = commentRepository.save(new Comment(null, issue, commentCreateDto.getDescription()));
        issue.addComment(savedComment);
        return new CommentCreateDto(savedComment);
    }


    public CommentPageDto findAllByIssueId(Long issueId, int offset, int limit){
        return commentRepository.findAllByIssueId(issueId, offset, limit);
    }


    public void deleteComment(Long issueId, Long commentId){
        Issue issue = issueRepository.findByIdWithCommentList(issueId).get();
        issue.deleteComment(commentId);
        commentRepository.deleteById(commentId);//this line is redundant because of JPA cascade.all relationship
    }


}
