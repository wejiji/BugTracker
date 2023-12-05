package com.example.security2pro.dto;

import com.example.security2pro.domain.enums.Role;
import com.example.security2pro.domain.model.ProjectMember;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ProjectMemberDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("username")
    private String username;
    @JsonProperty("email")
    private String email;
    @JsonProperty("authorities")
    private Set<Role> authorities;

    public ProjectMemberDto() {
    }

    @JsonCreator
    public ProjectMemberDto(Long id, String username, String email, Set<Role> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.authorities = authorities;
    }


    public ProjectMemberDto(ProjectMember projectMember){
        id = projectMember.getId();
        username = projectMember.getUser().getUsername();
        email = projectMember.getUser().getEmail();
        authorities = projectMember.getAuthorities();
    }


}
