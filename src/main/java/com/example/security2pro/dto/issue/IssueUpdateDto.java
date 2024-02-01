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

    @NotNull
    private final Long issueId;

    @NotBlank
    private final String title;

    @NotNull
    private final String description;

    @NotNull
    private final Set<String> assignees;

    @NotNull
    private final IssuePriority priority;

    @NotNull
    private final IssueStatus status;

    @NotNull
    private final IssueType type;

    private final Long currentSprintId;

    public IssueUpdateDto(
            Long issueId
            , String title
            , String description
            , Set<String> assignees
            , IssuePriority priority
            , IssueStatus status
            , IssueType type
            , Long currentSprintId) {

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
