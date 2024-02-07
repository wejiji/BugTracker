package com.example.bugtracker.dto.issue;

import com.example.bugtracker.domain.enums.IssuePriority;
import com.example.bugtracker.domain.enums.IssueStatus;
import com.example.bugtracker.domain.enums.IssueType;
import com.example.bugtracker.domain.model.issue.Issue;

import com.example.bugtracker.domain.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IssueUpdateDto {

    @NotNull
    private Long issueId;

    @NotBlank
    private String title;

    @NotNull
    private String description;

    @NotNull
    private Set<String> assignees;

    @NotNull
    private IssuePriority priority;

    @NotNull
    private IssueStatus status;

    @NotNull
    private IssueType type;

    private Long currentSprintId;

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
