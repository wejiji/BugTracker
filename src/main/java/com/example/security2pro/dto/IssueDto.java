package com.example.security2pro.dto;

import com.example.security2pro.domain.enums.IssuePriority;
import com.example.security2pro.domain.enums.IssueStatus;
import com.example.security2pro.domain.enums.IssueType;
import com.example.security2pro.domain.model.*;
import com.fasterxml.jackson.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class IssueDto {

    //validation of issueId field is done in the controller
    //@ConvertGroup should not be used.
    @JsonProperty("issueId")
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

    @JsonProperty("activityDtoList")
    @Valid
    private Set<ActivityDto> activityDtoList = new HashSet<>();
    @JsonProperty("issueRelationDtoList")
    @Valid
    private Set<IssueRelationDto> issueRelationDtoList = new HashSet<>();

    public IssueDto(){}

    @JsonCreator
    public IssueDto( @JsonProperty("issueId")Long issueId, @JsonProperty("title")String title,  @JsonProperty("description")String description,  @JsonProperty("assignees")Set<String> assignees,   @JsonProperty("completeDate")LocalDateTime completeDate,  @JsonProperty("priority")IssuePriority priority,  @JsonProperty("status") IssueStatus status,   @JsonProperty("type")IssueType type, @JsonProperty("currentSprintId") Long currentSprintId, @JsonProperty("activityDtoList")Set<ActivityDto> activityDtoList,   @JsonProperty("issueRelationDtoList")Set<IssueRelationDto> issueRelationDtoList) {
        this.issueId = issueId;
        this.title = title;
        this.description = description;
        this.assignees = assignees;
        this.completeDate = completeDate;
        this.priority = priority;
        this.status = status;
        this.type = type;
        this.currentSprintId = currentSprintId;
        this.activityDtoList = activityDtoList;
        this.issueRelationDtoList = issueRelationDtoList;
    }



    public IssueDto( Issue issue, Set<Activity> activities, Set<IssueRelation> issueRelationList){
        issueId = issue.getId();
        title =issue.getTitle();
        description = issue.getDescription();
        assignees = issue.getAssignees().stream().map(User::getUsername).collect(Collectors.toSet());
        completeDate = issue.getCompleteDate();
        priority = issue.getPriority();
        status = issue.getStatus();
        type = issue.getType();
        activityDtoList = activities.stream().map(ActivityDto::new).collect(Collectors.toSet());
        issueRelationDtoList = issueRelationList.stream().map(IssueRelationDto::new).collect(Collectors.toSet());
    }


    @Override
    public String toString() {
        return "IssueUpdateForm{" +
                "issueId=" + issueId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", assignees=" + assignees +
                ", completeDate=" + completeDate +
                ", priority=" + priority +
                ", status=" + status +
                ", type=" + type +
                ", currentSprintId=" + currentSprintId +
                ", activityDtoList=" + activityDtoList +
                '}';
    }
}
