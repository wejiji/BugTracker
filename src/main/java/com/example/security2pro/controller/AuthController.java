package com.example.security2pro.controller;

import com.example.security2pro.domain.model.auth.SecurityUser;
import com.example.security2pro.service.authentication.JwtTokenManager;
import com.example.security2pro.service.authentication.RefreshTokenManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final JwtTokenManager jwtTokenManager;

    private final RefreshTokenManager refreshTokenManager;

    @PostMapping("/api/login")
    public Map<String,String> login(
            @CurrentSecurityContext(expression = "authentication")
                            Authentication authentication
            , HttpServletRequest request, HttpServletResponse response
            ){

        log.info("authentication success with principal "+ authentication.getName());

        Optional<Cookie> refreshTokenFromAuth= Optional.empty();
        if(request.getCookies()!=null && authentication.getCredentials()!=null){
            refreshTokenFromAuth
                    = Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equals("refresh_token")
                                      && cookie.getValue().equals(
                                              ((Cookie)authentication.getCredentials()).getValue()))
                    .findFirst();
        }

        if(refreshTokenFromAuth.isEmpty()){ // Basic authentication
            Cookie cookie = refreshTokenManager.createRefreshToken(authentication);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            response.addCookie(cookie);
        }

        String jwt= jwtTokenManager.createAccessToken(authentication);

        response.setContentType("application/json");

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        return Map.of("access_token",jwt, "username",securityUser.getUsername());
    }


    @PostMapping("/api/logout")
    public ResponseEntity<String> logout(HttpServletRequest request){

        if(request.getCookies()==null){
            return new ResponseEntity<>("bad attempt",HttpStatus.BAD_REQUEST);
        }
        Optional<Cookie> refreshToken
                = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("refresh_token"))
                .findFirst();

        if(refreshToken.isPresent()){
            refreshToken.get().setMaxAge(0);
            refreshTokenManager.deleteToken(refreshToken.get().getValue());

            SecurityContextHolder.clearContext();
            return new ResponseEntity<>("successfully logged out",HttpStatus.OK);
        } else {
            return new ResponseEntity<>("bad attempt",HttpStatus.BAD_REQUEST);
        }
    }


}
