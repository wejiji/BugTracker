package com.example.bugtracker.dto.projectmember;

import com.example.bugtracker.domain.enums.ProjectMemberRole;
import com.example.bugtracker.domain.model.ProjectMember;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectMemberReturnDto {

    private Long id;

    private String username;

    private String email;

    private Set<ProjectMemberRole> authorities;

    public ProjectMemberReturnDto( Long id
            , String username
            , String email
            , Set<ProjectMemberRole> authorities) {

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
