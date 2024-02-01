package com.example.security2pro.dto.sprint;

import com.example.security2pro.dto.issue.authorization.CreateDtoWithProjectId;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
public class SprintCreateDto implements CreateDtoWithProjectId {

    @NotNull
    private final Long projectId;

    @NotBlank
    private final String name;

    @NotNull
    private final String description;

    @NotNull
    private final LocalDateTime startDate;

    @NotNull
    private final LocalDateTime endDate;

    public SprintCreateDto( Long projectId
            , String name
            , String description
            ,LocalDateTime startDate
            , LocalDateTime endDate) {

        this.projectId = projectId;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;

    }

    public Optional<Long> getProjectId() {
        return Optional.of(projectId);
    }


}
