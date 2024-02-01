package com.example.security2pro.dto.issue.onetomany;

import com.example.security2pro.domain.model.issue.Comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCreateDto {

    @NotNull
    private final Long issueId;

    @NotBlank
    private final String description;

    public CommentCreateDto(Long issueId, String description) {

        this.issueId = issueId;
        this.description = description;
    }

    public CommentCreateDto(Comment comment){
        issueId = comment.getIssue().getId();
        description = comment.getDescription();
    }

}
