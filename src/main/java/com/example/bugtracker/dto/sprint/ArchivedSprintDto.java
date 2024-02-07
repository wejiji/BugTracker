package com.example.bugtracker.dto.sprint;

import com.example.bugtracker.domain.model.Sprint;
import com.example.bugtracker.domain.model.SprintIssueHistory;
import com.example.bugtracker.dto.sprinthistory.SprintIssueHistoryDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArchivedSprintDto {

    @NotNull
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private String description;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    private List<SprintIssueHistoryDto> sprintIssueHistories = new ArrayList<>();

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
