package com.example.bugtracker.dto.issue;

import com.example.bugtracker.domain.enums.IssuePriority;
import com.example.bugtracker.domain.enums.IssueStatus;
import com.example.bugtracker.domain.model.issue.Issue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;


@Setter
@Getter
public class IssueSimpleDto {

    @JsonProperty("id")
    @NotNull
    private final Long id;

    @JsonProperty("title")
    @NotBlank
    private final String title;

    @JsonProperty("priority")
    @NotNull
    private final IssuePriority priority;

    @JsonProperty("status")
    @NotNull
    private final IssueStatus status;

    @JsonProperty("currentSprintId")
    private final Long currentSprintId;


    @JsonCreator
    public IssueSimpleDto(@JsonProperty("id") Long id
            , @JsonProperty("title") String title
            , @JsonProperty("priority") IssuePriority priority
            , @JsonProperty("status") IssueStatus status
            , @JsonProperty("currentSprintId") Long currentSprintId) {

        this.id = id;
        this.title = title;
        this.priority = priority;
        this.status = status;
        this.currentSprintId = currentSprintId;
    }

    public IssueSimpleDto(Issue issue) {

        id = issue.getId();
        title = issue.getTitle();
        priority = issue.getPriority();
        status = issue.getStatus();
        if (issue.getCurrentSprint().isPresent()) {
            currentSprintId = issue.getCurrentSprint().get().getId();
        } else {
            currentSprintId = null;
        }
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        IssueSimpleDto that = (IssueSimpleDto) object;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title) && priority == that.priority && status == that.status && Objects.equals(currentSprintId, that.currentSprintId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, priority, status, currentSprintId);
    }
}
