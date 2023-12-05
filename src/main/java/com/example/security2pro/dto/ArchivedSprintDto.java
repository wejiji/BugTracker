package com.example.security2pro.dto;

import com.example.security2pro.domain.model.Sprint;
import com.example.security2pro.domain.model.SprintIssueHistory;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
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
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ArchivedSprintDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    @NotBlank
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("startDate")
    @NotNull
    private LocalDateTime startDate;
    @JsonProperty("endDate")
    @NotNull
    private LocalDateTime endDate;
    @JsonProperty("sprintIssueHistories")
    private List<SprintIssueHistoryDto> sprintIssueHistories = new ArrayList<>();

    public ArchivedSprintDto() {
    }

    @JsonCreator
    public ArchivedSprintDto(Long id, String name, String description, LocalDateTime startDate, LocalDateTime endDate, List<SprintIssueHistoryDto> sprintIssueHistories) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sprintIssueHistories = sprintIssueHistories;
    }

    public ArchivedSprintDto(Sprint sprint, List<SprintIssueHistory> sprintIssueHistories){
        id = sprint.getId();
        name = sprint.getName();
        startDate = sprint.getStartDate();
        endDate = sprint.getEndDate();
        if(sprintIssueHistories!=null && !sprintIssueHistories.isEmpty()){
            this.sprintIssueHistories = sprintIssueHistories.stream().map(SprintIssueHistoryDto::new).collect(Collectors.toList());
        }
    }

}
