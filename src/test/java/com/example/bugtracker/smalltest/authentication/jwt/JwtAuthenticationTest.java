package com.example.bugtracker.smalltest.authentication.jwt;

import com.example.bugtracker.authentication.jwt.JwtAuthentication;
import com.example.bugtracker.authentication.jwt.ProjectRoles;
import com.example.bugtracker.domain.model.auth.SecurityUser;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class JwtAuthenticationTest {

    @Test
    void JwtAuthentication_createsAndReturnsUnAuthenticatedJwtAuthentication_givenJwt() {
        //Setup
        String jwt = "jwtString";
        //Execution
        JwtAuthentication jwtAuthentication
                = new JwtAuthentication(jwt);
        //Assertions
        assertEquals(jwt, jwtAuthentication.getJwt());
        assertFalse(jwtAuthentication.isAuthenticated());
    }

    @Test
    void JwtAuthentication_createsAndReturnsAuthenticatedJwtAuthentication_givenAllFieldValues() {
        //Setup
        String jwt = "jwtString";
        ProjectRoles projectRoles = new ProjectRoles(String.valueOf(10L), "ROLE_PROJECT_MEMBER");

        //Execution
        JwtAuthentication jwtAuthentication
                = new JwtAuthentication("testUsername"
                , jwt
                , Set.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                , Set.of(projectRoles));

        //Assertions
        assertEquals(jwt, jwtAuthentication.getJwt());
        assertTrue(jwtAuthentication.isAuthenticated());
        assertEquals("jwtString", jwtAuthentication.getCredentials());

        assertThat(
                new SecurityUser(
                        "testUsername"
                        , "jwtString"
                        , Set.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                        , true))
                .usingRecursiveComparison()
                .isEqualTo(jwtAuthentication.getPrincipal());

        assertThat(jwtAuthentication.getProjectRoles())
                .usingRecursiveComparison()
                .isEqualTo(Set.of(projectRoles));
    }


}
