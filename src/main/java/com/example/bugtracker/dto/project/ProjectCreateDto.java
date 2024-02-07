package com.example.bugtracker.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectCreateDto {

    @NotBlank
    private String name;

    @NotNull
    private String description;

    public ProjectCreateDto(String name, String description) {

        this.name = name;
        this.description = description;
    }


}
