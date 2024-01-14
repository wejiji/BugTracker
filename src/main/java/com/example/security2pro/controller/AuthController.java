package com.example.security2pro.controller;

import com.example.security2pro.service.UserService;
import com.example.security2pro.service.auth.TokenManager;
import com.example.security2pro.domain.model.auth.SecurityUser;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
public class AuthController {

    private final PasswordEncoder passwordEncoder;

    //private final UserDetailsService userDetailsService;
    private final UserService userService;

    private final TokenManager tokenManager;

    private final Clock clock;

    //refresh token existence is checked through filters before getting to controller endpoints

    @PostMapping("/api/login")
    public Map<String,String> login(
            @CurrentSecurityContext(expression = "authentication")
                            Authentication authentication
            , HttpServletResponse response
            ){
        //after successful authentication

        Cookie refreshTokenValue= tokenManager.createRefreshToken(authentication);
        String jwt=tokenManager.createAccessToken(authentication);

        //the result of getPrincipal() here should be String representation of a username
        // getPrincipal() is SecurityUser
        response.setContentType("application/json");
        response.addCookie(refreshTokenValue);
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        return Map.of("access_token",jwt, "username",securityUser.getUsername());
    }


    @PostMapping("/api/logout")
    public ResponseEntity<String> logout(HttpServletRequest request){

        Optional<Cookie> refreshToken = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals("refresh_token")).findFirst();
        if(refreshToken.isPresent()){
            refreshToken.get().setMaxAge(0);
            tokenManager.deleteToken(refreshToken.get().getValue());
            SecurityContextHolder.clearContext();
            return new ResponseEntity<>("successfully logged out",HttpStatus.OK);
        } else {
            return new ResponseEntity<>("bad attempt",HttpStatus.BAD_REQUEST);//????
        }
    }


}
