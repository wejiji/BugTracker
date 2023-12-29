package com.example.security2pro.controller;

import com.example.security2pro.service.auth.TokenManager;
import com.example.security2pro.domain.model.auth.SecurityUser;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final UserDetailsService userDetailsService;

    @Autowired
    private final TokenManager tokenManager;



    @PostMapping("/api/login")
    public Map<String,String> login(
            @CurrentSecurityContext(expression = "authentication")
                            Authentication authentication
            , HttpServletResponse response
            ){

        //여기는 성공할 경우만 오게되므로 성공케이스에 관한것만 적으면 된다.??
        //after successful authentication
        //=================================================

            response.setContentType("application/json");
            Cookie refreshTokenValue= tokenManager.createRefreshToken(authentication,true);
            String jwt=tokenManager.createAccessToken(authentication);

            //the result of getPrincipal() here should be String representation of a username.....!!!!
            // getPrincipal() is SecurityUser
            response.addCookie(refreshTokenValue);
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            return Map.of("access_token",jwt, "username",securityUser.getUsername());
    }


    @PostMapping("/api/logout")
    public ResponseEntity<String> logout(HttpServletRequest request){

        //should there be a separate filter for this?
        //and- security context still needs to be cleared!!!

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
