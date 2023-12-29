package com.example.security2pro.dto.projectmember;

import com.example.security2pro.domain.enums.Role;
import com.example.security2pro.domain.model.ProjectMember;
import com.example.security2pro.dto.issue.authorization.CreateDtoWithProjectId;
import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.Set;

@Getter
@Setter
public class ProjectMemberCreateDto implements CreateDtoWithProjectId {

    @NotNull
    private Long projectId;
    @NotNull
    private String username;
    @NotNull
    private Set<Role> authorities;

    public ProjectMemberCreateDto() {
    }

    @JsonCreator
    public ProjectMemberCreateDto(String username,Set<Role> authorities) {
        this.username = username;
        this.authorities = authorities;
    }

    public ProjectMemberCreateDto(ProjectMember projectMember){
        username = projectMember.getUser().getUsername();
        authorities = projectMember.getAuthorities();
    }

    public Optional<Long> getProjectId() {
        return Optional.of(projectId);
    }

}
