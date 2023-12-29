package com.example.security2pro.databuilders;

import com.example.security2pro.domain.enums.Role;
import com.example.security2pro.domain.model.Project;
import com.example.security2pro.domain.model.ProjectMember;
import com.example.security2pro.domain.model.User;

import java.util.Set;

import static com.example.security2pro.domain.enums.Role.ROLE_PROJECT_MEMBER;

public class ProjectMemberTestDataBuilder {
    private Project project;
    private User user;
    private Set<Role> authorities = Set.of(ROLE_PROJECT_MEMBER);

    public ProjectMemberTestDataBuilder withProject(Project project) {
        this.project = project;
        return this;
    }

    public ProjectMemberTestDataBuilder withUser(User user) {
        this.user = user;
        return this;
    }

    public ProjectMemberTestDataBuilder withAuthorities(Set<Role> authorities) {
        this.authorities = authorities;
        return this;
    }

    public ProjectMember build() {
        return new ProjectMember(project, user, authorities);
    }
}