package com.example.bugtracker.smalltest.authentication.jwt;

import com.example.bugtracker.authentication.jwt.JwtAuthenticationProvider;
import com.example.bugtracker.authentication.jwt.JwtAuthentication;
import com.example.bugtracker.authentication.jwt.ProjectRolesConverter;
import com.example.bugtracker.domain.model.auth.SecurityUser;
import com.example.bugtracker.fake.authentication.JwtTokenManagerImplFake;
import com.example.bugtracker.service.authentication.JwtTokenManager;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


class JwtAuthenticationProviderTest {
    private final JwtTokenManager jwtTokenManager = new JwtTokenManagerImplFake();
    private final ProjectRolesConverter projectRolesConverter = new ProjectRolesConverter();
    private final JwtAuthenticationProvider jwtAuthenticationProvider
            = new JwtAuthenticationProvider(jwtTokenManager, projectRolesConverter);

    @Test
    void supports_returnsTrue_givenJwtAuthenticationSubclass() {
        assertTrue(jwtAuthenticationProvider.supports(JwtAuthentication.class));
    }

    @Test
    void supports_returnsFalse_givenOtherAuthenticationThanJwtAuthentication() {
        assertFalse(jwtAuthenticationProvider.supports(Authentication.class));
    }

    @Test
    void authenticate_throwsException_givenInvalidJwt() {
        //Setup
        String jwt = "invalid"; // The fake token manager will throw an exception given this value
        JwtAuthentication jwtAuthentication = new JwtAuthentication(jwt);

        //Execution& Assertions
        assertThrows(BadCredentialsException.class,
                () -> jwtAuthenticationProvider.authenticate(jwtAuthentication));
    }

    @Test
    void authenticate_authenticatesUserAndReturnsAuthenticatedJwtAuthentication_givenAllFieldValues() {

        //Setup
        String jwt = "jwtStringForAdmin";
        JwtAuthentication jwtAuthentication = new JwtAuthentication(jwt);

        //Execution
        Authentication jwtAuthenticationReturned
                = jwtAuthenticationProvider.authenticate(jwtAuthentication);
        // The returned authentication will have a set of hard coded values
        // set by 'JwtTokenManagerImplFake' as fake extracted claims.

        //Assertions
        SecurityUser expectedSecurityUser
                // A 'SecurityUser' with the hard coded values set by 'JwtTokenManagerImplFake'
                = new SecurityUser(
                "admin"
                , "jwtStringForAdmin"
                , Set.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                , true);

        assertThat(jwtAuthenticationReturned.getPrincipal())
                .usingRecursiveComparison()
                .isEqualTo(expectedSecurityUser);

        assertEquals(jwt, jwtAuthenticationReturned.getCredentials());
        assertTrue(jwtAuthenticationReturned.isAuthenticated());

        assertEquals(List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                , jwtAuthenticationReturned.getAuthorities());
    }


}
