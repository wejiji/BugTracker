package com.example.bugtracker.smalltest.authentication.refresh;

import com.example.bugtracker.authentication.refresh.RefreshTokenAuthentication;
import com.example.bugtracker.domain.model.auth.SecurityUser;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenAuthenticationTest {

    @Test
    void RefreshTokenAuthentication_createsAndReturnsUnauthenticatedRefreshAuthentication_givenRefreshToken() {
        //Setup
        Cookie refreshToken = new Cookie("testRefresh", "testValueRefreshTokenString");

        //Execution
        RefreshTokenAuthentication refreshTokenAuthentication
                = new RefreshTokenAuthentication(refreshToken);

        //Assertions
        assertFalse(refreshTokenAuthentication.isAuthenticated());
        assertEquals(refreshToken, refreshTokenAuthentication.getCredentials());
        assertNull(refreshTokenAuthentication.getPrincipal());
        assertThat(refreshTokenAuthentication.getAuthorities()).isEmpty();
    }


    @Test
    void RefreshTokenAuthentication_createsAndReturnsAuthenticatedRefreshAuthentication_givenAllFieldValuesIncludingUser() {
        //Setup
        Cookie refreshToken = new Cookie("testRefresh", "testValueRefreshTokenString");

        //Execution
        RefreshTokenAuthentication refreshTokenAuthentication
                = new RefreshTokenAuthentication(
                "testUsername", refreshToken, Set.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        //Assertions
        assertTrue(refreshTokenAuthentication.isAuthenticated());
        assertEquals(refreshToken, refreshTokenAuthentication.getCredentials());

        SecurityUser expectedSecurityUser
                = new SecurityUser(
                "testUsername"
                , "testValueRefreshTokenString"
                , Set.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                , true);

        assertThat(refreshTokenAuthentication.getPrincipal())
                .usingRecursiveComparison()
                .isEqualTo(expectedSecurityUser);
        assertEquals(new HashSet<>(Set.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                , new HashSet<>(refreshTokenAuthentication.getAuthorities()));
    }

}
