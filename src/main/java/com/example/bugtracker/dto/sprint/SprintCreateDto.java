package com.example.bugtracker.dto.sprint;

import com.example.bugtracker.dto.issue.authorization.CreateDtoWithProjectId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SprintCreateDto implements CreateDtoWithProjectId {

    @NotNull
    private Long projectId;

    @NotBlank
    private String name;

    @NotNull
    private String description;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

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
