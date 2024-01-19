package com.example.security2pro.controller;


import com.example.security2pro.authentication.refresh.RefreshTokenAuthentication;
import com.example.security2pro.databuilders.UserTestDataBuilder;
import com.example.security2pro.domain.model.User;
import com.example.security2pro.domain.model.auth.SecurityUser;
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

//        User user = new UserTestDataBuilder().withUsername(customUser.username()).build();
//        SecurityUser securityUser = new SecurityUser(user);

        Authentication auth = null;
        if(customUser.username().equals("admin")){
            auth = new RefreshTokenAuthentication(
                    customUser.username()
                    , refresh
                    , Set.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        }

        if(customUser.username().equals("projectMember")){
            auth = new RefreshTokenAuthentication(
                    customUser.username()
                    , refresh
                    , Set.of(new SimpleGrantedAuthority("ROLE_PROJECT_MEMBER")));
        }

        if(customUser.username().equals("projectLead")){
            auth = new RefreshTokenAuthentication(
                    customUser.username()
                    , refresh
                    , Set.of(new SimpleGrantedAuthority("ROLE_PROJECT_LEAD")));
        }
        context.setAuthentication(auth);

        return context;
    }
}
