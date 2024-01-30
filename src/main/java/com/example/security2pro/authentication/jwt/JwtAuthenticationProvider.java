package com.example.security2pro.authentication.jwt;

import com.example.security2pro.service.authentication.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtTokenManager jwtTokenManager;

    private final ProjectRolesConverter projectRolesConverter;

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthentication.class.isAssignableFrom(authentication);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthentication jwtAuthentication = (JwtAuthentication) authentication;

        //JWT verification. An exception will be thrown by token manager if JWT is not valid
        Map<String, String> jwtVerifiedClaimsMap
                = jwtTokenManager.verifyAccessToken(jwtAuthentication.getJwt());

        //Creates a new authentication with authorities
        String username = jwtVerifiedClaimsMap.get("subject");
        String[] roles = jwtVerifiedClaimsMap.get("userRoles").split(",");

        List<SimpleGrantedAuthority> rolesList
                = Arrays.stream(roles).map(SimpleGrantedAuthority::new).toList();

        Set<ProjectRoles> projectRoles
                = projectRolesConverter.convertToRoles(
                jwtVerifiedClaimsMap.get("projectRoles"));

        return new JwtAuthentication(
                username, jwtAuthentication.getJwt(), rolesList, projectRoles);
    }


}
