package com.example.security2pro.controller;

import com.example.security2pro.dto.issue.onetomany.CommentCreateDto;
import com.example.security2pro.dto.issue.onetomany.CommentDto;

import com.example.security2pro.dto.issue.onetomany.CommentPageDto;
import com.example.security2pro.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor
@Transactional
public class CommentController {

    private final CommentService commentService;

    @PostMapping("issues/{issueId}/comments")
    @PreAuthorize("hasPermission(#issueId,'issue','ROLE_PROJECT_LEAD') or hasPermission(#issueId,'issue','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public CommentCreateDto createActivity(@PathVariable Long issueId, @Validated @RequestBody CommentCreateDto commentCreateDto){

        return commentService.createComment(commentCreateDto);
    }

    @GetMapping("/issues/{issueId}/comments")
    @PreAuthorize("hasPermission(#issueId,'issue','ROLE_PROJECT_LEAD') or hasPermission(#issueId,'issue','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public CommentPageDto getIssueActivities(@PathVariable Long issueId
            , @RequestParam(value="offset", defaultValue = "0") int offset
            , @RequestParam(value="limit", defaultValue = "2") int limit){

        return commentService.findAllByIssueId(issueId,offset,limit);
    }


    @DeleteMapping("issues/{issueId}/comments/{commentId}")
    @PreAuthorize("hasPermission(#issueId,'issue','ROLE_PROJECT_LEAD') or hasPermission(#commentId,'comment','author') or hasRole('ADMIN')")
    public void deleteActivity(@PathVariable Long issueId, @PathVariable Long commentId){

        commentService.deleteComment(issueId,commentId);
    }



}
