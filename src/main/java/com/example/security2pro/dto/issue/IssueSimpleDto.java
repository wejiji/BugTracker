package com.example.security2pro.dto.issue;

import com.example.security2pro.domain.enums.IssuePriority;
import com.example.security2pro.domain.enums.IssueStatus;
import com.example.security2pro.domain.enums.IssueType;
import com.example.security2pro.domain.model.Issue;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class IssueSimpleDto {

    @JsonProperty("id")
    @NotNull
    private Long id;
    @JsonProperty("title")
    @NotBlank
    private String title;
    @JsonProperty("priority")
    @NotNull
    private IssuePriority priority;
    @JsonProperty("status")
    @NotNull
    private IssueStatus status;

    @JsonProperty("currentSprintId")
    private Long currentSprintId;



    public IssueSimpleDto() {}

    @JsonCreator
    public IssueSimpleDto( @JsonProperty("id")Long id, @JsonProperty("title")String title, @JsonProperty("priority") IssuePriority priority,    @JsonProperty("status")IssueStatus status, @JsonProperty("currentSprintId") Long currentSprintId) {
        this.id = id;
        this.title = title;
        this.priority = priority;
        this.status = status;
        this.currentSprintId = currentSprintId;
    }

    public IssueSimpleDto(Issue issue){
        id = issue.getId();
        title = issue.getTitle();
        priority = issue.getPriority();
        status = issue.getStatus();
        currentSprintId = issue.getCurrentSprint().getId();
    }



}
