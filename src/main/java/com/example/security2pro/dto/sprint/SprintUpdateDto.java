package com.example.security2pro.dto.sprint;

import com.example.security2pro.dto.project.ProjectDto;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SprintUpdateDto {
    @Valid
    private ActiveSprintUpdateDto activeSprintUpdateDto;
    @Valid
    private ProjectDto projectDto;

    public SprintUpdateDto() {
    }

    public SprintUpdateDto(ActiveSprintUpdateDto activeSprintUpdateDto, ProjectDto projectDto) {
        this.activeSprintUpdateDto = activeSprintUpdateDto;
        this.projectDto = projectDto;
    }
}
