package com.example.bugtracker.authentication.jwt;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request
            , @NonNull HttpServletResponse response
            , @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String jwt = extractJwtFromHeader(authHeader);
        JwtAuthentication auth = new JwtAuthentication(jwt);
        try {
            auth = (JwtAuthentication) authenticationManager.authenticate(auth);
        } catch (AuthenticationException e) {
            log.error(e.getMessage());
            doFilter(request, response, filterChain);
            return;
        }

        SecurityContextHolder.getContext()
                .setAuthentication(auth);

        doFilter(request, response, filterChain);
    }

    private String extractJwtFromHeader(String authHeader) {
        String jwt = null;
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer")) {
            //No Auth header and no Bearer
            return null;
        }
        //Bearer but no token
        authHeader = authHeader.trim();
        if (authHeader.equals("Bearer")) {
            log.error("jwt missing");
            throw new BadCredentialsException("jwt missing");
        }
        int space = authHeader.indexOf(" ");
        if (space == -1) {
            throw new BadCredentialsException("jwt missing");
        }
        jwt = authHeader.substring(space + 1);

        return jwt;
    }


    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request) {
        return request.getServletPath().equals("/login")
                || request.getServletPath().startsWith("/swagger")
                || request.getServletPath().startsWith("/v3/api-docs");
    }

}
