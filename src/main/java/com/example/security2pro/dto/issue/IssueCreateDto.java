package com.example.security2pro.dto.issue;

import com.example.security2pro.domain.enums.IssuePriority;
import com.example.security2pro.domain.enums.IssueStatus;
import com.example.security2pro.domain.enums.IssueType;
import com.example.security2pro.dto.issue.authorization.CreateDtoWithProjectId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
public class IssueCreateDto implements CreateDtoWithProjectId {

    @NotNull
    private final Long projectId;

    @NotBlank
    private final String title;

    @NotNull
    private final String description;

    private final Set<String> assignees;

    @NotNull
    private final IssuePriority priority;

    @NotNull
    private final IssueStatus status;

    @NotNull
    private final IssueType type;

    private final Long currentSprintId;

    public IssueCreateDto(Long projectId
            , String title
            , String description
            , Set<String> assignees
            , IssuePriority priority
            , IssueStatus status
            , IssueType type
            , Long currentSprintId) {

        this.projectId = projectId;
        this.title = title;
        this.description = description;
        if (assignees == null) {
            this.assignees = new HashSet<>();
        } else {
            this.assignees = assignees;
        }
        this.priority = priority;
        this.status = status;
        this.type = type;
        this.currentSprintId = currentSprintId;

    }

    public Optional<Long> getProjectId() {
        return Optional.of(projectId);
    }


}
