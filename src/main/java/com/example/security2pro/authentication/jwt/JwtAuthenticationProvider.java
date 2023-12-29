package com.example.security2pro.authentication.jwt;

import com.example.security2pro.service.auth.TokenManager;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final TokenManager tokenManager;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthentication jwtAuthentication = (JwtAuthentication) authentication;

        //jwt verification. Exception will be thrown by token manager if not valid
        Claims claims = tokenManager.verifyAccessToken(jwtAuthentication.getJwt());

        //creating new authentication with authorities
        String username = claims.getSubject();
        String[] roles = claims.get("roles").toString().split(",");
        List<SimpleGrantedAuthority> rolesList = Arrays.stream(roles).map(SimpleGrantedAuthority::new).toList();
        return new JwtAuthentication(
                username, jwtAuthentication.getJwt(), rolesList);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(JwtAuthentication.class);
    }










}
