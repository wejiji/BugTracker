package com.example.security2pro.authentication;

import com.example.security2pro.authentication.refresh.RefreshTokenAuthentication;
import com.example.security2pro.domain.model.auth.SecurityUser;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class RefreshTokenAuthenticationTest {


    @Test
    public void testConstructor_authenticatedAuthentication(){

        Cookie refreshToken = new Cookie("testRefresh","testValueRefreshTokenString");

        RefreshTokenAuthentication refreshTokenAuthentication = new RefreshTokenAuthentication(refreshToken);

        assertFalse(refreshTokenAuthentication.isAuthenticated());
        assertEquals(refreshToken,refreshTokenAuthentication.getCredentials());
        assertNull(refreshTokenAuthentication.getPrincipal());
        assertThat(refreshTokenAuthentication.getAuthorities()).isEmpty();
    }



    @Test
    public void testConstructor_notAuthenticatedAuthentication(){

        Cookie refreshToken = new Cookie("testRefresh","testValueRefreshTokenString");

//        User user = new UserTestDataBuilder()
//                .withId(30L)
//                .withUsername("testUsername")
//                .build();
//
//        SecurityUser securityUser = new SecurityUser(user);

        RefreshTokenAuthentication refreshTokenAuthentication
                = new RefreshTokenAuthentication(
                        "testUsername",refreshToken,Set.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        assertTrue(refreshTokenAuthentication.isAuthenticated());
        assertEquals(refreshToken, refreshTokenAuthentication.getCredentials());
        assertThat(new SecurityUser("testUsername","testValueRefreshTokenString",Set.of(new SimpleGrantedAuthority("ROLE_ADMIN")),true))
                .usingRecursiveComparison()
                .isEqualTo(refreshTokenAuthentication.getPrincipal());
        assertEquals(new HashSet<>(Set.of(new SimpleGrantedAuthority("ROLE_ADMIN"))), new HashSet<>(refreshTokenAuthentication.getAuthorities()));
    }

}
