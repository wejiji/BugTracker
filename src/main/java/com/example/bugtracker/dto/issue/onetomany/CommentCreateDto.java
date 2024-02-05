package com.example.bugtracker.dto.issue.onetomany;

import com.example.bugtracker.domain.model.issue.Comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCreateDto {

    @NotBlank
    private final String description;

    private final Long parentId;

    public CommentCreateDto(String description, Long parentId) {

        this.description = description;
        this.parentId = parentId;
    }

    public CommentCreateDto(Comment comment){
        description = comment.getDescription();
        if(comment.getParent()==null){
            parentId =null;
        } else {
            parentId = comment.getParent().getId();
        }
    }

}
