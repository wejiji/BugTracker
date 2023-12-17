package com.example.security2pro.dto.projectmember;

import com.example.security2pro.domain.enums.Role;
import com.example.security2pro.domain.model.ProjectMember;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ProjectMemberCreateDto {

    @NotNull
    private Long userId;
    @NotNull
    private Set<Role> authorities;

    public ProjectMemberCreateDto() {
    }

    @JsonCreator
    public ProjectMemberCreateDto( Long userId,Set<Role> authorities) {
        this.userId = userId;
        this.authorities = authorities;
    }

    public ProjectMemberCreateDto(ProjectMember projectMember){
        userId = projectMember.getUser().getId();
        authorities = projectMember.getAuthorities();
    }

}
