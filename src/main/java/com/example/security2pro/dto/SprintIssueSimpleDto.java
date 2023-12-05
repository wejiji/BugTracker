package com.example.security2pro.dto;

import com.example.security2pro.domain.enums.IssuePriority;
import com.example.security2pro.domain.enums.IssueStatus;
import com.example.security2pro.domain.model.SprintIssueHistory;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class SprintIssueSimpleDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("title")
    @NotBlank
    private String title;
    @JsonProperty("status")
    @NotNull
    private IssueStatus status;
    @JsonProperty("priority")
    @NotNull
    private IssuePriority priority;

    //private Long issueId;


    public SprintIssueSimpleDto() {
    }

    @JsonCreator
    public SprintIssueSimpleDto(Long id, String title, IssueStatus status, IssuePriority priority) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.priority = priority;
    }

    public SprintIssueSimpleDto(SprintIssueHistory sprintIssueHistory){
        id = sprintIssueHistory.getId();
        title = sprintIssueHistory.getIssue().getTitle();
        status = sprintIssueHistory.getIssue().getStatus();
        priority = sprintIssueHistory.getIssue().getPriority();

    }



}
