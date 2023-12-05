package com.example.security2pro.dto;

import com.example.security2pro.domain.model.Project;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectCreationForm {


    private Long id;

    @NotBlank
    private String name;

    private String description;

    public  ProjectCreationForm(){}

    public ProjectCreationForm(Project project){
        /// how can I add project's creator here?
        this.id = project.getId();
        this.name = project.getName();
        this.description = project.getDescription();
    }


    public ProjectCreationForm(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return "ProjectCreationForm{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
