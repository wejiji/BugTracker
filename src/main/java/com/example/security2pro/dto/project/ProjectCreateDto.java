package com.example.security2pro.dto.project;

import com.example.security2pro.domain.model.Project;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectCreateDto {


    @NotBlank
    private final String name;

    private final String description;


    public ProjectCreateDto(Project project){
        /// how can I add project's creator here?
        this.name = project.getName();
        this.description = project.getDescription();
    }


    public ProjectCreateDto(String name, String description) {
        this.name = name;
        this.description = description;
    }


}
