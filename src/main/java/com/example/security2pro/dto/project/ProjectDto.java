package com.example.security2pro.dto.project;
import com.example.security2pro.domain.model.issue.Issue;
import com.example.security2pro.domain.model.Project;
import com.example.security2pro.domain.model.ProjectMember;
import com.example.security2pro.domain.model.Sprint;
import com.example.security2pro.dto.issue.IssueSimpleDto;
import com.example.security2pro.dto.sprint.SprintUpdateDto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;



@Getter
@Setter
public class ProjectDto {
    @JsonProperty("projectName")
    private final String projectName;
    @JsonProperty("projectMembers")
    private final Set<String> projectMembers;
    @JsonProperty("sprints")
    @Valid
    private final Set<SprintUpdateDto> sprints;
    @JsonProperty("issues")
    @Valid
    private final Set<IssueSimpleDto> issues;


    @JsonCreator
    public ProjectDto(String projectName, Set<String> projectMembers, Set<SprintUpdateDto> sprints, Set<IssueSimpleDto> issues) {
        this.projectName = projectName;
        this.projectMembers = Set.copyOf(projectMembers);
        this.sprints = Set.copyOf(sprints);
        this.issues = Set.copyOf(issues);

    }

    public ProjectDto(Project project, Set<ProjectMember> projectMembers, Set<Sprint> sprints, Set<Issue> projectIssues){
        this.projectName = project.getName();
        this.projectMembers = projectMembers.stream().map(projectMember -> projectMember.getUser().getUsername()).collect(Collectors.toSet());
        this.sprints = sprints.stream().map(SprintUpdateDto::new).collect(Collectors.toSet());
        this.issues = projectIssues.stream().map(IssueSimpleDto::new).collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ProjectDto that = (ProjectDto) object;
        return Objects.equals(projectName, that.projectName) && Objects.equals(projectMembers, that.projectMembers) && Objects.equals(sprints, that.sprints) && Objects.equals(issues, that.issues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectName, projectMembers, sprints, issues);
    }
}
