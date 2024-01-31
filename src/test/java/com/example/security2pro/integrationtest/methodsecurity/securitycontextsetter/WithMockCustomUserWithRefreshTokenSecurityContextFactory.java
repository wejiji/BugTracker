package com.example.security2pro.integrationtest.methodsecurity.securitycontextsetter;


import com.example.security2pro.authentication.refresh.RefreshTokenAuthentication;
import jakarta.servlet.http.Cookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Set;

public class WithMockCustomUserWithRefreshTokenSecurityContextFactory
implements WithSecurityContextFactory<WithMockCustomUserWithRefreshToken> {


    @Override
    public SecurityContext createSecurityContext(WithMockCustomUserWithRefreshToken customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Cookie refresh = new Cookie("refresh_token","refreshTokenStringValue");

        Authentication auth = null;
        if(customUser.username().equals("teamMember")){
            auth = new RefreshTokenAuthentication(
                    customUser.username()
                    , refresh
                    , Set.of(new SimpleGrantedAuthority("ROLE_TEAM_MEMBER")));
        }


        if(customUser.username().equals("teamLead")){
            auth = new RefreshTokenAuthentication(
                    customUser.username()
                    , refresh
                    , Set.of(new SimpleGrantedAuthority("ROLE_TEAM_LEAD")));
        }


        context.setAuthentication(auth);

        return context;
    }
}
