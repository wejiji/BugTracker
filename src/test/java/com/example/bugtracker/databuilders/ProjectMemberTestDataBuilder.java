package com.example.bugtracker.databuilders;

import com.example.bugtracker.domain.enums.ProjectMemberRole;
import com.example.bugtracker.domain.model.Project;
import com.example.bugtracker.domain.model.ProjectMember;
import com.example.bugtracker.domain.model.User;

import java.util.Set;

import static com.example.bugtracker.domain.enums.ProjectMemberRole.ROLE_PROJECT_MEMBER;


public class ProjectMemberTestDataBuilder {
    private Long id;
    private Project project;
    private User user;
    private Set<ProjectMemberRole> authorities = Set.of(ROLE_PROJECT_MEMBER);

    public ProjectMemberTestDataBuilder withId(Long id) {
        this.id =id;
        return this;
    }

    public ProjectMemberTestDataBuilder withProject(Project project) {
        this.project = project;
        return this;
    }

    public ProjectMemberTestDataBuilder withUser(User user) {
        this.user = user;
        return this;
    }

    public ProjectMemberTestDataBuilder withAuthorities(Set<ProjectMemberRole> authorities) {
        this.authorities = authorities;
        return this;
    }

    public ProjectMember build() {
        return ProjectMember.createProjectMember(id,project, user, authorities);
    }
}