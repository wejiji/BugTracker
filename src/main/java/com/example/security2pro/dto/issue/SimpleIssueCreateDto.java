package com.example.security2pro.dto.issue;

import com.example.security2pro.domain.enums.IssuePriority;
import com.example.security2pro.domain.enums.IssueStatus;
import com.example.security2pro.domain.enums.IssueType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
public class SimpleIssueCreateDto implements CreateDtoWithProjectId{

    @JsonProperty("project")
    @NotNull
    private Long projectId;
    @JsonProperty("title")
    @NotBlank
    private String title;
    @JsonProperty("description")
    private String description;
    @JsonProperty("assignees")
    private Set<String> assignees;
    @JsonProperty("completeDate")
    private LocalDateTime completeDate;
    @JsonProperty("priority")
    @NotNull
    private IssuePriority priority;
    @JsonProperty("status")
    @NotNull
    private IssueStatus status;
    @JsonProperty("type")
    @NotNull
    private IssueType type;
    @JsonProperty("currentSprintId")
    private Long currentSprintId;

    public SimpleIssueCreateDto(){}

    @JsonCreator
    public SimpleIssueCreateDto(@JsonProperty("project") Long projectId, @JsonProperty("title")String title, @JsonProperty("description")String description, @JsonProperty("assignees")Set<String> assignees, @JsonProperty("completeDate")LocalDateTime completeDate, @JsonProperty("priority")IssuePriority priority, @JsonProperty("status") IssueStatus status, @JsonProperty("type")IssueType type, @JsonProperty("currentSprintId") Long currentSprintId) {
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.assignees = assignees;
        this.completeDate = completeDate;
        this.priority = priority;
        this.status = status;
        this.type = type;
        this.currentSprintId = currentSprintId;

    }

    public Optional<Long> getProjectId() {
        return Optional.of(projectId);
    }


}
