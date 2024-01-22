package com.example.security2pro.service.authentication;

import com.example.security2pro.domain.model.auth.RefreshTokenData;
import jakarta.servlet.http.Cookie;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public interface RefreshTokenManager {

    public Cookie createRefreshToken(Authentication auth);
    public RefreshTokenData readRefreshToken(String refreshTokenValue);
    public void deleteToken(String refreshTokenValue);
}
