package com.example.security2pro.authentication;

import com.example.security2pro.authentication.jwt.ProjectRoles;
import com.example.security2pro.authentication.jwt.UserAndProjectRoleAuthentication;
import com.example.security2pro.domain.model.auth.SecurityUser;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;

public class UserAndProjectRoleAuthenticationMock implements UserAndProjectRoleAuthentication {

    SecurityUser user;

    Set<ProjectRoles> projectRoles;

    @Override
    public SecurityUser getUser() {
        return user;
    }

    @Override
    public Set<ProjectRoles> getProjectRoles() {
        return projectRoles;
    }

    public UserAndProjectRoleAuthenticationMock(SecurityUser user, Set<ProjectRoles> projectRoles){
        this.user = user;
        this.projectRoles = projectRoles;

    }

    public void setUser(SecurityUser user){
        this.user = user;
    }

    public void setProjectRoles(Set<ProjectRoles> projectRoles){
        this.projectRoles = projectRoles;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return user;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    }

    @Override
    public String getName() {
        return null;
    }
}
