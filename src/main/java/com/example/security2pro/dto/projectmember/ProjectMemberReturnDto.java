package com.example.security2pro.dto.projectmember;

import com.example.security2pro.domain.enums.ProjectMemberRole;
import com.example.security2pro.domain.model.ProjectMember;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

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
    public ProjectMemberReturnDto(Long id, String username, String email, Set<ProjectMemberRole> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.authorities = authorities;
    }

    public ProjectMemberReturnDto(ProjectMember projectMember){
        id = projectMember.getId();
        username = projectMember.getUser().getUsername();
        email = projectMember.getUser().getEmail();
        authorities = projectMember.getAuthorities();
    }


}
