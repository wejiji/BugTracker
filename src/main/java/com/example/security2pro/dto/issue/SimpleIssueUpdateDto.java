package com.example.security2pro.dto.issue;

import com.example.security2pro.domain.enums.IssuePriority;
import com.example.security2pro.domain.enums.IssueStatus;
import com.example.security2pro.domain.enums.IssueType;
import com.example.security2pro.domain.model.Issue;

import com.example.security2pro.domain.model.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class SimpleIssueUpdateDto {

    @JsonProperty("issueId")
    @NotNull
    private Long issueId;

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


    public SimpleIssueUpdateDto(){}

    @JsonCreator
    public SimpleIssueUpdateDto(@JsonProperty("issueId")Long issueId, @JsonProperty("title")String title, @JsonProperty("description")String description, @JsonProperty("assignees")Set<String> assignees, @JsonProperty("completeDate")LocalDateTime completeDate, @JsonProperty("priority")IssuePriority priority, @JsonProperty("status") IssueStatus status, @JsonProperty("type")IssueType type, @JsonProperty("currentSprintId") Long currentSprintId) {
        this.issueId = issueId;
        this.title = title;
        this.description = description;
        this.assignees = assignees;
        this.completeDate = completeDate;
        this.priority = priority;
        this.status = status;
        this.type = type;
        this.currentSprintId = currentSprintId;

    }

    public SimpleIssueUpdateDto(Issue issue){
        issueId = issue.getId();
        title =issue.getTitle();
        description = issue.getDescription();
        assignees = issue.getAssignees().stream().map(User::getUsername).collect(Collectors.toSet());
        completeDate = issue.getCompleteDate();
        priority = issue.getPriority();
        status = issue.getStatus();
        type = issue.getType();
        if(issue.getCurrentSprint().isPresent()){
            currentSprintId = issue.getCurrentSprint().get().getId();
        }
    }



}
