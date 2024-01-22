package com.example.security2pro.authentication.jwt;

import com.example.security2pro.domain.enums.ProjectMemberRole;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class ProjectRoles {
    private Long projectId;

    private Set<ProjectMemberRole> roles;

    public ProjectRoles(String projectId, String roles) {
        this.projectId = Long.valueOf(projectId);
        this.roles = Arrays.stream(roles.split(",")).map(ProjectMemberRole::valueOf).collect(Collectors.toSet());
    }

    public ProjectRoles(Long projectId, Set<ProjectMemberRole> roles) {
        this.projectId = projectId;
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "["
                +projectId
                +":"
                +roles.stream().map(Enum::name).collect(Collectors.joining(","))
                +"]" ;
    }
}
