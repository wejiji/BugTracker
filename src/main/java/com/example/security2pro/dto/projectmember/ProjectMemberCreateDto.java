package com.example.security2pro.dto.projectmember;

import com.example.security2pro.domain.enums.ProjectMemberRole;
import com.example.security2pro.domain.model.ProjectMember;
import com.example.security2pro.dto.issue.authorization.CreateDtoWithProjectId;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.Set;

@Getter
@Setter
public class ProjectMemberCreateDto implements CreateDtoWithProjectId {

    @NotNull
    @JsonProperty("projectId")
    private final Long projectId;

    @NotNull
    @JsonProperty("username")
    private final String username;

    @NotNull
    @JsonProperty("authorities")
    private final Set<ProjectMemberRole> authorities;

    @JsonCreator
    public ProjectMemberCreateDto(@JsonProperty("projectId") Long projectId
            , @JsonProperty("username") String username
            , @JsonProperty("authorities")Set<ProjectMemberRole> authorities) {
        this.projectId = projectId;
        this.username = username;
        this.authorities = authorities;
    }

    public ProjectMemberCreateDto(ProjectMember projectMember){
        projectId = projectMember.getProject().getId();
        username = projectMember.getUser().getUsername();
        authorities = projectMember.getAuthorities();
    }

    public Optional<Long> getProjectId() {
        return Optional.of(projectId);
    }

}
