package com.example.security2pro.authentication.refresh;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;


@Transactional
@RequiredArgsConstructor
@Slf4j
public class RefreshAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if(request.getCookies()==null || Arrays.stream(request.getCookies()).noneMatch(cookie-> cookie.getName().equals("refresh_token"))){
            log.error("refresh cookie does not exist");
            filterChain.doFilter(request,response);
            return;//this shouldn't be forgotten!! because the below will execute after returning back to this filter
        }

        Optional<Cookie> refreshToken = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals("refresh_token")).findFirst();
        RefreshTokenAuthentication auth = new RefreshTokenAuthentication(refreshToken.get());
        try{
            auth = (RefreshTokenAuthentication) authenticationManager.authenticate(auth);
        } catch(AuthenticationException | EmptyResultDataAccessException e){
            filterChain.doFilter(request,response);
            return;
        }

        SecurityContextHolder.getContext()
                .setAuthentication(auth);

        filterChain.doFilter(request,response);
    }


    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request) {
        return !request.getServletPath().equals("/api/login");
            //if not /login, do not filter
    }


}
