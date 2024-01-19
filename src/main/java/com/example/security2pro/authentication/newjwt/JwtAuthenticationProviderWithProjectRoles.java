package com.example.security2pro.authentication.newjwt;

import com.example.security2pro.service.auth0.JwtTokenManager;
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
public class JwtAuthenticationProviderWithProjectRoles implements AuthenticationProvider {

    private final JwtTokenManager jwtTokenManager;

    private final ProjectRolesConverter projectRolesConverter;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationWithProjectAuthority jwtAuthentication = (JwtAuthenticationWithProjectAuthority) authentication;

        //jwt verification. Exception will be thrown by token manager if not valid
        Map<String,String> jwtVerifiedClaimsMap = jwtTokenManager.verifyAccessToken(jwtAuthentication.getJwt());

        //creating new authentication with authorities
        String username = jwtVerifiedClaimsMap.get("subject");
        String[] roles = jwtVerifiedClaimsMap.get("userRoles").split(",");
        List<SimpleGrantedAuthority> rolesList= Arrays.stream(roles).map(SimpleGrantedAuthority::new).toList();
        Set<ProjectRoles> projectRoles = projectRolesConverter.convertToRoles(jwtVerifiedClaimsMap.get("projectRoles"));

        return new JwtAuthenticationWithProjectAuthority(
                username, jwtAuthentication.getJwt(), rolesList,projectRoles);
    }

    @Override
    public boolean supports(Class<?> authentication) {

        return JwtAuthenticationWithProjectAuthority.class.isAssignableFrom(authentication);
    }


}
