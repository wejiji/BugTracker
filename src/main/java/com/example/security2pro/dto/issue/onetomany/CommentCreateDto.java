package com.example.security2pro.dto.issue.onetomany;

import com.example.security2pro.domain.model.Comment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCreateDto {

    @JsonProperty("issueId")
    @NotNull
    private final Long issueId;

    @JsonProperty("description")
    @NotBlank
    private final String description;


    @JsonCreator
    public CommentCreateDto(@JsonProperty("issueId")Long issueId, @JsonProperty("description") String description) {
        this.issueId = issueId;
        this.description = description;
    }

    public CommentCreateDto(Comment comment){
        issueId = comment.getIssue().getId();
        description = comment.getDescription();
    }

}
