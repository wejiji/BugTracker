package com.example.security2pro.dto.projectmember;

import com.example.security2pro.domain.enums.ProjectMemberRole;
import com.example.security2pro.domain.model.ProjectMember;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
public class ProjectMemberReturnDto {
    @JsonProperty("id")
    private final Long id;

    @JsonProperty("username")
    private final String username;

    @JsonProperty("email")
    private final String email;

    @JsonProperty("authorities")
    private final Set<ProjectMemberRole> authorities;


    @JsonCreator
    public ProjectMemberReturnDto(@JsonProperty("id") Long id
            , @JsonProperty("username") String username
            , @JsonProperty("email") String email
            , @JsonProperty("authorities") Set<ProjectMemberRole> authorities) {

        this.id = id;
        this.username = username;
        this.email = email;
        this.authorities = authorities;
    }

    public ProjectMemberReturnDto(ProjectMember projectMember) {

        id = projectMember.getId();
        username = projectMember.getUser().getUsername();
        email = projectMember.getUser().getEmail();
        authorities = projectMember.getAuthorities();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ProjectMemberReturnDto that = (ProjectMemberReturnDto) object;
        return Objects.equals(id, that.id) && Objects.equals(username, that.username) && Objects.equals(email, that.email) && Objects.equals(authorities, that.authorities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, authorities);
    }
}
