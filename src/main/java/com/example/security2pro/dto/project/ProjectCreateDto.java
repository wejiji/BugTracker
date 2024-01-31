package com.example.security2pro.dto.project;

import com.example.security2pro.domain.model.Project;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectCreateDto {

    @JsonProperty("name")
    @NotBlank
    private final String name;
    @JsonProperty("description")
    private final String description;



    @JsonCreator
    public ProjectCreateDto(@JsonProperty("name") String name
            , @JsonProperty("description") String description) {
        this.name = name;
        this.description = description;
    }


}
