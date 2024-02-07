package com.example.bugtracker.dto.project;
import com.example.bugtracker.domain.model.issue.Issue;
import com.example.bugtracker.domain.model.Project;
import com.example.bugtracker.domain.model.ProjectMember;
import com.example.bugtracker.domain.model.Sprint;
import com.example.bugtracker.dto.issue.IssueSimpleDto;
import com.example.bugtracker.dto.sprint.SprintUpdateDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;



@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectDto {

    @NotNull
    private String projectName;

    @NotBlank
    private Set<String> projectMembers;

    @Valid
    private Set<SprintUpdateDto> sprints;

    @Valid
    private Set<IssueSimpleDto> issues;


    public ProjectDto(
            String projectName
            , Set<String> projectMembers
            , Set<SprintUpdateDto> sprints
            , Set<IssueSimpleDto> issues) {

        this.projectName = projectName;
        this.projectMembers = Set.copyOf(projectMembers);
        this.sprints = Set.copyOf(sprints);
        this.issues = Set.copyOf(issues);

    }

    public ProjectDto(
            Project project
            , Set<ProjectMember> projectMembers
            , Set<Sprint> sprints
            , Set<Issue> projectIssues){

        this.projectName = project.getName();
        this.projectMembers = projectMembers.stream()
                .map(projectMember -> projectMember.getUser().getUsername())
                .collect(Collectors.toSet());

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
