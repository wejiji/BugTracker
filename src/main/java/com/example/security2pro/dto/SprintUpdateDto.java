package com.example.security2pro.dto;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SprintUpdateDto {
    @Valid
    private ActiveSprintDto activeSprintDto;
    @Valid
    private ProjectDto projectDto;

    public SprintUpdateDto() {
    }

    public SprintUpdateDto(ActiveSprintDto activeSprintDto, ProjectDto projectDto) {
        this.activeSprintDto = activeSprintDto;
        this.projectDto = projectDto;
    }
}
