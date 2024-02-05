package com.example.bugtracker.dto.issue.onetomany;

import com.example.bugtracker.domain.model.issue.Comment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CommentDto {

    @NotNull
    private final Long id;

    @NotBlank
    private final String description;

    private final Long parentId;

    public CommentDto(Long id, String description, Long parentId) {

        this.id = id;
        this.description = description;
        this.parentId = parentId;
    }

    public CommentDto(Comment comment) {
        id = comment.getId();
        description = comment.getDescription();
        if(comment.getParent()==null){
            parentId = null;
        } else {
            parentId = comment.getParent().getId();
        }

    }


}
