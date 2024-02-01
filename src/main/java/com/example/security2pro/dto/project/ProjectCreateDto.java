package com.example.security2pro.dto.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectCreateDto {

    @NotBlank
    private final String name;

    @NotNull
    private final String description;

    public ProjectCreateDto(String name, String description) {

        this.name = name;
        this.description = description;
    }


}
