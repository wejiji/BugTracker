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

    public CommentDto(Long id, String description) {

        this.id = id;
        this.description = description;
    }

    public CommentDto(Comment comment) {
        id = comment.getId();
        description = comment.getDescription();
    }


}
