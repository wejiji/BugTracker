package com.example.security2pro.dto;

import com.example.security2pro.domain.enums.Role;
import com.example.security2pro.domain.model.ProjectMember;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ProjectMemberCreateForm {


    @NotNull
    private Long userId;
    @NotNull
    private Set<Role> authorities;

    public ProjectMemberCreateForm() {
    }

    public ProjectMemberCreateForm(Long userId, Set<Role> authorities) {
        this.userId = userId;
        this.authorities = authorities;
    }

    public ProjectMemberCreateForm(ProjectMember projectMember){
        userId = projectMember.getUser().getId();
        authorities = projectMember.getAuthorities();
    }

}
