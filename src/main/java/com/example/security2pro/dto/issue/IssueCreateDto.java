package com.example.security2pro.dto.issue;

import com.example.security2pro.domain.enums.IssuePriority;
import com.example.security2pro.domain.enums.IssueStatus;
import com.example.security2pro.domain.enums.IssueType;
import com.example.security2pro.dto.issue.authorization.CreateDtoWithProjectId;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
public class IssueCreateDto implements CreateDtoWithProjectId {

    @JsonProperty("project")
    @NotNull
    private final Long projectId;
    @JsonProperty("title")
    @NotBlank
    private final String title;
    @JsonProperty("description")
    private final String description;
    @JsonProperty("assignees")
    private final Set<String> assignees;
    @JsonProperty("completeDate")
    private final LocalDateTime completeDate;
    @JsonProperty("priority")
    @NotNull
    private final IssuePriority priority;
    @JsonProperty("status")
    @NotNull
    private final IssueStatus status;
    @JsonProperty("type")
    @NotNull
    private final IssueType type;
    @JsonProperty("currentSprintId")
    private final Long currentSprintId;

    @JsonCreator
    public IssueCreateDto(@JsonProperty("project") Long projectId, @JsonProperty("title")String title, @JsonProperty("description")String description, @JsonProperty("assignees")Set<String> assignees, @JsonProperty("completeDate")LocalDateTime completeDate, @JsonProperty("priority")IssuePriority priority, @JsonProperty("status") IssueStatus status, @JsonProperty("type")IssueType type, @JsonProperty("currentSprintId") Long currentSprintId) {
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
