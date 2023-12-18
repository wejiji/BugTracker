package com.example.security2pro.dto.issue;

import com.example.security2pro.domain.enums.IssuePriority;
import com.example.security2pro.domain.enums.IssueStatus;
import com.example.security2pro.domain.enums.IssueType;
import com.example.security2pro.domain.model.*;
import com.fasterxml.jackson.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class IssueCreateDto {

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

    @JsonProperty("issueRelationDtoList")
    @Valid
    private Set<IssueRelationDto> issueRelationDtoList = new HashSet<>();

    public IssueCreateDto(){}

    @JsonCreator
    public IssueCreateDto(@JsonProperty("title")String title, @JsonProperty("description")String description, @JsonProperty("assignees")Set<String> assignees, @JsonProperty("completeDate")LocalDateTime completeDate, @JsonProperty("priority")IssuePriority priority, @JsonProperty("status") IssueStatus status, @JsonProperty("type")IssueType type, @JsonProperty("currentSprintId") Long currentSprintId, @JsonProperty("activityDtoList")Set<ActivityDto> activityDtoList, @JsonProperty("issueRelationDtoList")Set<IssueRelationDto> issueRelationDtoList) {
        this.title = title;
        this.description = description;
        this.assignees = assignees;
        this.completeDate = completeDate;
        this.priority = priority;
        this.status = status;
        this.type = type;
        this.currentSprintId = currentSprintId;
        this.issueRelationDtoList = issueRelationDtoList;
    }



    public IssueCreateDto(Issue issue ,Set<Activity> activities, Set<IssueRelation> issueRelationList){
        title =issue.getTitle();
        description = issue.getDescription();
        assignees = issue.getAssignees().stream().map(User::getUsername).collect(Collectors.toSet());
        completeDate = issue.getCompleteDate();
        priority = issue.getPriority();
        status = issue.getStatus();
        type = issue.getType();
       issueRelationDtoList = issueRelationList.stream().map(IssueRelationDto::new).collect(Collectors.toSet());
    }


}
