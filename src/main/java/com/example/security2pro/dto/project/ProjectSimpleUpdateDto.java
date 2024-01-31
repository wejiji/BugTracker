package com.example.security2pro.dto.project;

import com.example.security2pro.domain.model.Project;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ProjectSimpleUpdateDto {


    @JsonProperty("id")
    private final Long id;

    @NotBlank
    @JsonProperty("name")
    private final String name;

    @JsonProperty("description")
    private final String description;

    public ProjectSimpleUpdateDto(Project project) {

        this.id = project.getId();
        this.name = project.getName();
        this.description = project.getDescription();
    }

    @JsonCreator
    public ProjectSimpleUpdateDto(@JsonProperty("id") Long id
            , @JsonProperty("name") String name
            , @JsonProperty("description") String description) {

        this.id = id;
        this.name = name;
        this.description = description;
    }


}
