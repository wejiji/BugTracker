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
    @JsonProperty("id")
    private final Long id;

    @JsonProperty("name")
    @NotBlank
    private final String name;

    @JsonProperty("description")
    private final String description;

    @JsonProperty("startDate")
    @NotNull
    private final LocalDateTime startDate;

    @JsonProperty("endDate")
    @NotNull
    private final LocalDateTime endDate;

    @JsonProperty("sprintIssueHistories")
    private final List<SprintIssueHistoryDto> sprintIssueHistories = new ArrayList<>();


    @JsonCreator
    public ArchivedSprintDto(@JsonProperty("id") Long id
            , @JsonProperty("name") String name
            , @JsonProperty("description") String description
            , @JsonProperty("startDate") LocalDateTime startDate
            , @JsonProperty("endDate") LocalDateTime endDate
            , @JsonProperty("sprintIssueHistories") List<SprintIssueHistoryDto> sprintIssueHistories) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sprintIssueHistories.addAll(sprintIssueHistories);
    }

    public ArchivedSprintDto(Sprint sprint, List<SprintIssueHistory> sprintIssueHistories) {
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
