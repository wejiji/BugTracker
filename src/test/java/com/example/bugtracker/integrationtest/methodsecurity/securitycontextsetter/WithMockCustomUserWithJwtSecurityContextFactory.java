package com.example.bugtracker.integrationtest.methodsecurity.securitycontextsetter;


import com.example.bugtracker.authentication.jwt.JwtAuthentication;
import com.example.bugtracker.authentication.jwt.ProjectRoles;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Collections;
import java.util.Set;


public class WithMockCustomUserWithJwtSecurityContextFactory
implements WithSecurityContextFactory<WithMockCustomUserWithJwt> {

    public static String projectIdForProjectRole= String.valueOf(10L);

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUserWithJwt customUserWithJwt) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Authentication auth = null;
        if(customUserWithJwt.username().equals("admin")){
            auth = new JwtAuthentication(
                    "admin","jwtStringForAdmin"
                    , Set.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                    , Collections.emptySet()
            );
        }

        if(customUserWithJwt.username().equals("projectMember")){
            auth = new JwtAuthentication(
                    "projectMember","jwtStringForProjectMember"
                    , Set.of(new SimpleGrantedAuthority("ROLE_TEAM_MEMBER"))
                    , Set.of(new ProjectRoles(String.valueOf(projectIdForProjectRole),"ROLE_PROJECT_MEMBER"))
            );
        }

        if(customUserWithJwt.username().equals("projectLead")){
            auth = new JwtAuthentication(
                    "projectLead","jwtStringForProjectLead"
                    , Set.of(new SimpleGrantedAuthority("ROLE_TEAM_MEMBER"))
                    , Set.of(new ProjectRoles(String.valueOf(projectIdForProjectRole),"ROLE_PROJECT_LEAD"))
            );
        }
        context.setAuthentication(auth);

        return context;
    }

}
