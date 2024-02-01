package com.example.bugtracker.authentication.jwt;

import com.example.bugtracker.domain.enums.ProjectMemberRole;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class ProjectRoles {
    private final Long projectId;

    private final Set<ProjectMemberRole> roles;

    public ProjectRoles(String projectId, String roles) {
        this.projectId = Long.valueOf(projectId);
        this.roles = Arrays.stream(roles.split(","))
                .map(ProjectMemberRole::valueOf)
                .collect(Collectors.toSet());
    }

    public ProjectRoles(Long projectId, Set<ProjectMemberRole> roles) {
        this.projectId = projectId;
        this.roles = roles;
    }

    public String inString() {
        return "["
               + projectId
               + ":"
               + roles.stream().map(Enum::name).collect(Collectors.joining(","))
               + "]";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ProjectRoles that = (ProjectRoles) object;
        return Objects.equals(projectId, that.projectId) && Objects.equals(roles, that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, roles);
    }

}
