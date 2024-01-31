package com.example.security2pro.dto.issue;

import com.example.security2pro.domain.enums.IssuePriority;
import com.example.security2pro.domain.enums.IssueStatus;
import com.example.security2pro.domain.enums.IssueType;
import com.example.security2pro.domain.model.issue.Issue;

import com.example.security2pro.domain.model.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class IssueUpdateDto {

    @JsonProperty("issueId")
    @NotNull
    private final Long issueId;

    @JsonProperty("title")
    @NotBlank
    private final String title;

    @JsonProperty("description")
    @NotNull
    private final String description;

    @JsonProperty("assignees")
    @NotNull
    private final Set<String> assignees;

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
    public IssueUpdateDto(@JsonProperty("issueId")Long issueId
            , @JsonProperty("title")String title
            , @JsonProperty("description")String description
            , @JsonProperty("assignees")Set<String> assignees
            , @JsonProperty("priority")IssuePriority priority
            , @JsonProperty("status") IssueStatus status
            , @JsonProperty("type")IssueType type
            , @JsonProperty("currentSprintId") Long currentSprintId) {
        this.issueId = issueId;
        this.title = title;
        this.description = description;
        this.assignees = assignees;
        this.priority = priority;
        this.status = status;
        this.type = type;
        this.currentSprintId = currentSprintId;

    }

    public IssueUpdateDto(Issue issue){
        issueId = issue.getId();
        title =issue.getTitle();
        description = issue.getDescription();
        assignees = issue.getAssignees().stream().map(User::getUsername).collect(Collectors.toSet());
        priority = issue.getPriority();
        status = issue.getStatus();
        type = issue.getType();
        if(issue.getCurrentSprint().isPresent()){
            currentSprintId = issue.getCurrentSprint().get().getId();
        } else {
            currentSprintId=null;
        }
    }



}
