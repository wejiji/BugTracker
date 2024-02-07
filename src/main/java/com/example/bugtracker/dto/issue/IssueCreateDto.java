package com.example.bugtracker.dto.issue;

import com.example.bugtracker.domain.enums.IssuePriority;
import com.example.bugtracker.domain.enums.IssueStatus;
import com.example.bugtracker.domain.enums.IssueType;
import com.example.bugtracker.dto.issue.authorization.CreateDtoWithProjectId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IssueCreateDto implements CreateDtoWithProjectId {

    @NotNull
    private Long projectId;

    @NotBlank
    private String title;

    @NotNull
    private String description;

    private Set<String> assignees;

    @NotNull
    private IssuePriority priority;

    @NotNull
    private IssueStatus status;

    @NotNull
    private IssueType type;

    private Long currentSprintId;

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
