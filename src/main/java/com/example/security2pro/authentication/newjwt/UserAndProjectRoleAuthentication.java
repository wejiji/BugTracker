package com.example.security2pro.authentication.newjwt;

import com.example.security2pro.domain.model.auth.SecurityUser;
import org.springframework.security.core.Authentication;

import java.util.Set;

public interface UserAndProjectRoleAuthentication extends Authentication {

    SecurityUser getUser();

    Set<ProjectRoles> getProjectRoles();


}
