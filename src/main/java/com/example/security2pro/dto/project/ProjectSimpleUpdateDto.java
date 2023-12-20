package com.example.security2pro.dto.project;

import com.example.security2pro.domain.model.Project;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ProjectSimpleUpdateDto {


    private Long id;

    @NotBlank
    private String name;

    private String description;

    public ProjectSimpleUpdateDto(){}

    public ProjectSimpleUpdateDto(Project project){
        /// how can I add project's creator here?
        this.id = project.getId();
        this.name = project.getName();
        this.description = project.getDescription();
    }


    public ProjectSimpleUpdateDto(String name, String description) {
        this.name = name;
        this.description = description;
    }


}
