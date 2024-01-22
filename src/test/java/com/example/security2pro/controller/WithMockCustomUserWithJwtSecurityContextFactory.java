package com.example.security2pro.controller;


import com.example.security2pro.authentication.jwt.JwtAuthentication;
import com.example.security2pro.authentication.jwt.ProjectRoles;
import com.example.security2pro.authorization.ProjectMemberPermissionEvaluatorTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Collections;
import java.util.Set;


public class WithMockCustomUserWithJwtSecurityContextFactory
implements WithSecurityContextFactory<WithMockCustomUserWithJwt> {

    String projectId = ProjectMemberPermissionEvaluatorTest.projectIdForAuthorization;

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
                    , Set.of(new ProjectRoles(String.valueOf(projectId),"ROLE_PROJECT_MEMBER"))
            );
        }

        if(customUserWithJwt.username().equals("projectLead")){
            auth = new JwtAuthentication(
                    "projectLead","jwtStringForProjectLead"
                    , Set.of(new SimpleGrantedAuthority("ROLE_TEAM_MEMBER"))
                    , Set.of(new ProjectRoles(String.valueOf(projectId),"ROLE_PROJECT_LEAD"))
            );
        }
        context.setAuthentication(auth);

        return context;
    }

}
