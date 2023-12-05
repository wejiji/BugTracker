package com.example.security2pro.dto;

import com.example.security2pro.domain.model.Issue;
import com.example.security2pro.domain.model.Sprint;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class ActiveSprintDto {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    @NotBlank
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("startDate")
    @NotNull
    private LocalDateTime startDate;
    @JsonProperty("endDate")
    @NotNull
    private LocalDateTime endDate;
    @JsonProperty("issues")
    @Valid
    private Set<IssueSimpleDto> issues = new HashSet<>();

    public ActiveSprintDto() {}

    @JsonCreator
    public ActiveSprintDto(@JsonProperty("id") Long id,  @JsonProperty("name")String name,    @JsonProperty("description")String description, @JsonProperty("startDate")LocalDateTime startDate, @JsonProperty("endDate") LocalDateTime endDate,     @JsonProperty("issues")Set<IssueSimpleDto> issues) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.issues = issues;
    }

    public ActiveSprintDto(Sprint sprint, Set<Issue> issues){
        id = sprint.getId();
        name = sprint.getName();
        startDate = sprint.getStartDate();
        endDate = sprint.getEndDate();
        if(issues!=null && !issues.isEmpty()){
            this.issues.addAll(issues.stream().map(IssueSimpleDto::new).collect(Collectors.toSet()));
            //this.issues = issues.stream().map(IssueSimpleDto::new).;
        }
    }

    @Override
    public String toString() {
        return "ActiveSprintSimpleDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", activeIssues=" + issues +
                '}';
    }
}
