package com.example.bugtracker.dto.projectmember;

import com.example.bugtracker.domain.enums.ProjectMemberRole;
import com.example.bugtracker.domain.model.ProjectMember;
import com.example.bugtracker.dto.issue.authorization.CreateDtoWithProjectId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectMemberCreateDto implements CreateDtoWithProjectId {

    @NotNull
    private Long projectId;

    @NotBlank
    private String username;

    @NotNull
    private Set<ProjectMemberRole> authorities;

    public ProjectMemberCreateDto(Long projectId
            , String username
            , Set<ProjectMemberRole> authorities) {

        this.projectId = projectId;
        this.username = username;
        this.authorities = authorities;
    }

    public ProjectMemberCreateDto(ProjectMember projectMember) {

        projectId = projectMember.getProject().getId();
        username = projectMember.getUser().getUsername();
        authorities = projectMember.getAuthorities();
    }

    public Optional<Long> getProjectId() {
        return Optional.of(projectId);
    }

}
