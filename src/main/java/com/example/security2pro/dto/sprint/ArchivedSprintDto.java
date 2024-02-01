package com.example.security2pro.dto.sprint;

import com.example.security2pro.domain.model.Sprint;
import com.example.security2pro.domain.model.SprintIssueHistory;
import com.example.security2pro.dto.sprinthistory.SprintIssueHistoryDto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ArchivedSprintDto {

    @NotNull
    private final Long id;

    @NotBlank
    private final String name;

    @NotNull
    private final String description;

    @NotNull
    private final LocalDateTime startDate;

    @NotNull
    private final LocalDateTime endDate;

    private final List<SprintIssueHistoryDto> sprintIssueHistories = new ArrayList<>();

    public ArchivedSprintDto(Long id
            , String name
            , String description
            , LocalDateTime startDate
            , LocalDateTime endDate
            , List<SprintIssueHistoryDto> sprintIssueHistories) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sprintIssueHistories.addAll(sprintIssueHistories);
    }

    public ArchivedSprintDto(Sprint sprint
            , List<SprintIssueHistory> sprintIssueHistories) {
        id = sprint.getId();
        name = sprint.getName();
        description = sprint.getDescription();
        startDate = sprint.getStartDate();
        endDate = sprint.getEndDate();
        if (sprintIssueHistories != null && !sprintIssueHistories.isEmpty()) {
            this.sprintIssueHistories.addAll(sprintIssueHistories.stream().map(SprintIssueHistoryDto::new).collect(Collectors.toList()));
        } else {
            sprintIssueHistories = new ArrayList<>();
        }
    }


}
