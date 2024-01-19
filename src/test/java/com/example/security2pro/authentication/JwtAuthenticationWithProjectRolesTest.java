package com.example.security2pro.authentication;

import com.example.security2pro.authentication.newjwt.JwtAuthenticationWithProjectAuthority;
import com.example.security2pro.authentication.newjwt.ProjectRoles;
import com.example.security2pro.domain.model.auth.SecurityUser;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class JwtAuthenticationWithProjectRolesTest {



    @Test
    public void testConstructor_unAuthenticated(){

        String jwt = "jwtString";

        JwtAuthenticationWithProjectAuthority jwtAuthentication
                = new JwtAuthenticationWithProjectAuthority(jwt);

        assertEquals(jwt ,jwtAuthentication.getJwt());
        assertFalse(jwtAuthentication.isAuthenticated());
    }

    @Test
    public void testConstructor_authenticated(){

        String jwt = "jwtString";

        ProjectRoles projectRoles = new ProjectRoles(String.valueOf(10L),"ROLE_PROJECT_MEMBER");

        JwtAuthenticationWithProjectAuthority jwtAuthentication
                = new JwtAuthenticationWithProjectAuthority("testUsername"
                , jwt
                , Set.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                ,Set.of(projectRoles));

        assertEquals(jwt ,jwtAuthentication.getJwt());
        assertTrue(jwtAuthentication.isAuthenticated());
        assertEquals("jwtString",jwtAuthentication.getCredentials());

        assertThat(new SecurityUser("testUsername","jwtString", Set.of(new SimpleGrantedAuthority("ROLE_ADMIN")),true))
                .usingRecursiveComparison().isEqualTo(jwtAuthentication.getPrincipal());
        assertThat(jwtAuthentication.getProjectRoles()).usingRecursiveComparison().isEqualTo(Set.of(projectRoles));

    }







}
