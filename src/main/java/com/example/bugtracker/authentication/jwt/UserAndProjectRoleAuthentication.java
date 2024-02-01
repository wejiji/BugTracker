package com.example.bugtracker.authentication.jwt;

import com.example.bugtracker.domain.model.auth.SecurityUser;
import org.springframework.security.core.Authentication;

import java.util.Set;

public interface UserAndProjectRoleAuthentication extends Authentication {
    SecurityUser getUser();

    Set<ProjectRoles> getProjectRoles();

}
