package com.example.bugtracker.dto.project;

import com.example.bugtracker.domain.model.Project;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectSimpleUpdateDto {

    @NotBlank
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private String description;

    public ProjectSimpleUpdateDto(Project project) {

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
