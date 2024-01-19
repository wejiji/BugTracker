package com.example.security2pro.authentication.newjwt;

import com.example.security2pro.domain.model.auth.SecurityUser;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;


@Getter
public class JwtAuthenticationWithProjectAuthority extends AbstractAuthenticationToken implements UserAndProjectRoleAuthentication{

    private final String jwt;
    private SecurityUser user;
    private Set<ProjectRoles> projectRoles;

    public JwtAuthenticationWithProjectAuthority(String jwt){
        super(null);
        this.jwt = jwt;
        setAuthenticated(false);
    }

    public JwtAuthenticationWithProjectAuthority(String username, String jwt, Collection<?extends GrantedAuthority> authorities, Set<ProjectRoles> projectRoles){
        super(authorities);
        this.user = new SecurityUser(username, jwt, authorities,true);
        this.jwt = jwt;
        this.projectRoles = projectRoles;
        setAuthenticated(true);
    }


    @Override
    public Object getCredentials() {
        return jwt;
    }

    @Override
    public Object getPrincipal() {
        return user;
    }


}
