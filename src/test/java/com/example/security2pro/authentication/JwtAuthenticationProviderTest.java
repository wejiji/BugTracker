package com.example.security2pro.authentication;

import com.example.security2pro.authentication.jwt.JwtAuthenticationProvider;
import com.example.security2pro.authentication.jwt.JwtAuthentication;
import com.example.security2pro.authentication.jwt.ProjectRolesConverter;
import com.example.security2pro.domain.model.auth.SecurityUser;
import com.example.security2pro.service.authentication.JwtTokenManager;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


public class JwtAuthenticationProviderTest {

    //token manager is not a mock - repository is
    private final JwtTokenManager jwtTokenManager
            = new JwtTokenManagerImplFake();

    private final ProjectRolesConverter projectRolesConverter = new ProjectRolesConverter();

    private JwtAuthenticationProvider jwtAuthenticationProvider
            = new JwtAuthenticationProvider(jwtTokenManager,projectRolesConverter);

    @Test
    public void authenticate_success(){

        String jwt = "jwtStringForAdmin";
        JwtAuthentication jwtAuthentication = new JwtAuthentication(jwt);
        //Execution
        Authentication jwtAuthenticationReturned= jwtAuthenticationProvider.authenticate(jwtAuthentication);

        assertThat(new SecurityUser("admin","jwtStringForAdmin", Set.of(new SimpleGrantedAuthority("ROLE_ADMIN")),true))
                .usingRecursiveComparison().isEqualTo(jwtAuthenticationReturned.getPrincipal());
        assertEquals(jwt,jwtAuthenticationReturned.getCredentials());
        assertTrue(jwtAuthenticationReturned.isAuthenticated());
        assertEquals(List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                ,jwtAuthenticationReturned.getAuthorities());

    }

    @Test
    public void supports_true(){

        assertTrue(jwtAuthenticationProvider.supports(JwtAuthentication.class));
    }

    @Test
    public void supports_false(){

        assertFalse(jwtAuthenticationProvider.supports(Authentication.class));
    }



}
