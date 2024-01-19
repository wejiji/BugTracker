package com.example.security2pro.service.auth0;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public interface JwtTokenManager {

    public String createAccessToken(Authentication authentication);
    public Map<String,String> verifyAccessToken(String jwt);
}