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

public class CommentDto {
    @JsonProperty("id")
    @NotNull
    private final Long id;

    @JsonProperty("description")
    @NotBlank
    private final String description;

    @JsonCreator
    public CommentDto(@JsonProperty("id")Long id, @JsonProperty("description") String description) {
        this.id = id;
        this.description = description;
    }

    public CommentDto(Comment comment){
        id = comment.getId();
        description = comment.getDescription();
    }



}
