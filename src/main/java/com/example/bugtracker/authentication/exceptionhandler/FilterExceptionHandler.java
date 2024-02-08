package com.example.bugtracker.authentication.exceptionhandler;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

@Component
@RequiredArgsConstructor
@Slf4j
public class FilterExceptionHandler extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try{
            filterChain.doFilter(request, response);
        } catch(Exception e){
            log.error("exception e -" + e.getMessage(),e);

            if(e instanceof AccessDeniedException) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
            if(e instanceof ServletException) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
            else {
                throw e;
            }
        }


    }
}
