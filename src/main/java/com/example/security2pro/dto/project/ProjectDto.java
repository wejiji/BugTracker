package com.example.security2pro.dto.project;
import com.example.security2pro.domain.model.Issue;
import com.example.security2pro.domain.model.Project;
import com.example.security2pro.domain.model.ProjectMember;
import com.example.security2pro.domain.model.Sprint;
import com.example.security2pro.dto.issue.IssueSimpleDto;
import com.example.security2pro.dto.sprint.ActiveSprintUpdateDto;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;


@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ProjectDto {
    @JsonProperty("projectName")
    private String projectName;
    @JsonProperty("projectMembers")
    private Set<String> projectMembers;
    @JsonProperty("sprints")
    @Valid
    private Set<ActiveSprintUpdateDto> sprints = new HashSet<>();
    //Sprints & issues that belong to them
    @JsonProperty("issues")
    @Valid
    private Set<IssueSimpleDto> issues = new HashSet<>();
    // The ones that don't belong to any sprints

    public ProjectDto(){}

    @JsonCreator
    public ProjectDto(String projectName, Set<String> projectMembers, Set<ActiveSprintUpdateDto> sprints, Set<IssueSimpleDto> issues) {
        this.projectName = projectName;
        this.projectMembers = projectMembers;
        this.sprints = sprints;
        this.issues = issues;
    }

    public ProjectDto(Project project, Set<ProjectMember> projectMembers, Set<Sprint> sprints, Set<Issue> projectIssues){
        this.projectName = project.getName();
        this.projectMembers = projectMembers.stream().map(projectMember -> projectMember.getUser().getUsername()).collect(Collectors.toSet());
        this.sprints = sprints.stream().map(ActiveSprintUpdateDto::new).collect(Collectors.toSet());
        this.issues = projectIssues.stream().map(IssueSimpleDto::new).collect(Collectors.toSet());
    }


//    public ProjectDto(Project project, Set<ProjectMember> projectMembers, Set<Sprint> sprints, Set<Issue> issuesWithSprint, Set<Issue> issuesWithoutSprint){
//        this.projectName = project.getName();
//
//        this.projectMembers = projectMembers.stream().map(projectMember -> projectMember.getUser().getUsername()).collect(Collectors.toSet());
//
//        // - check if sprints exists, if issues with sprint exists, if issues without sprint exists
//
//       if(!sprints.isEmpty()){ // sprint exists
//           if(!issuesWithSprint.isEmpty()){ //some sprint has issues
//               Map<Sprint,List<Issue>> sprintListMap = new HashMap<>();
//               sprints.forEach(sprint-> sprintListMap.put(sprint,null));
//               Map<Sprint,List<Issue>> sprintsWithIssues= issuesWithSprint.stream()
//                       .filter(issue->issue.getCurrentSprint()!=null)// was added because groupingBy requires NonNull.
//                       .collect(groupingBy(Issue::getCurrentSprint));
//               sprintListMap.putAll(sprintsWithIssues); //merge two maps
//               this.sprints = sprintListMap.entrySet().stream().map(sprintEntry -> new ActiveSprintUpdateDto(sprintEntry.getKey())).collect(Collectors.toCollection(HashSet::new));
//           } else { // all sprint empty
//               this.sprints = sprints.stream().map(ActiveSprintUpdateDto::new).collect(Collectors.toCollection(HashSet::new));
//           }
//       }
//
//        this.issues = issuesWithoutSprint.stream().map(IssueSimpleDto::new).collect(Collectors.toSet());
//    }
//    @Override
//    public String toString() {
//        return "ProjectDto{" +
//                "projectName='" + projectName + '\'' +
//                ", projectMembers=" + String.join(", ",projectMembers) +
//                ", sprints=" + sprints+
//                ", issues=" + issues +
//                '}';
//    }
}
