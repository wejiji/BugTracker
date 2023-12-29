package com.example.security2pro.databuilders;

import com.example.security2pro.domain.model.Issue;
import com.example.security2pro.domain.model.Project;
import com.example.security2pro.domain.model.ProjectMember;
import com.example.security2pro.domain.model.Sprint;
import com.example.security2pro.dto.project.ProjectDto;

import java.util.Set;

public class TestDataHelper {

    public static ProjectDto createProjectDtoWithTestData(Project project, Set<ProjectMember> projectMembers,
                                                          Set<Sprint> sprints, Set<Issue> issues) {
        return new ProjectDto(project, projectMembers, sprints, issues);
    }

    // Add more methods for creating specific entities or sets of entities as needed
}