package com.example.security2pro.dto.project;

import com.example.security2pro.domain.model.Project;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ProjectSimpleUpdateDto {


    private final Long id;

    @NotBlank
    private final String name;

    private final String description;

    public ProjectSimpleUpdateDto(Project project){
        /// how can I add project's creator here?
        this.id = project.getId();
        this.name = project.getName();
        this.description = project.getDescription();
    }


    public ProjectSimpleUpdateDto(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }


}
