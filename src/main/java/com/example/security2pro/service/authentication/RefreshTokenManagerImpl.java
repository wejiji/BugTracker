package com.example.security2pro.service.authentication;


import com.example.security2pro.domain.model.auth.RefreshTokenData;
import com.example.security2pro.domain.model.auth.SecurityUser;
import com.example.security2pro.repository.repository_interfaces.TokenRepository;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.temporal.ChronoUnit;
import java.util.*;


@Slf4j
@Component
@Transactional
public class RefreshTokenManagerImpl implements RefreshTokenManager {

    private final TokenRepository tokenRepository;

    private final int refreshMaxAgeInDays;

    private final Clock clock;


    public RefreshTokenManagerImpl(TokenRepository tokenRepository, Clock clock, @Value("${refresh.age.max.days}") int refreshMaxAgeInDays) {
        this.tokenRepository = tokenRepository;
        this.clock = clock;
        this.refreshMaxAgeInDays = refreshMaxAgeInDays;
    }


    public Cookie createRefreshToken(Authentication auth) {
        String refreshToken = UUID.randomUUID().toString();

        Date expiryDate =
                Date.from(clock.instant().plus(1, ChronoUnit.DAYS));

        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(refreshMaxAgeInDays * 60 * 60);//하루동안 유효

        List<String> rolesInString = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        SecurityUser user = (SecurityUser) auth.getPrincipal();

        RefreshTokenData refreshTokenData = new RefreshTokenData(user.getUsername(), expiryDate, rolesInString, refreshToken);
        tokenRepository.createNewToken(refreshTokenData);
        //username, refreshToken, lastModified ??

        return cookie;
    }

    @Override
    public RefreshTokenData readRefreshToken(String refreshTokenValue) {
        return tokenRepository.readRefreshToken(refreshTokenValue);
    }

    @Override
    public void deleteToken(String refreshTokenValue) {
        tokenRepository.deleteToken(refreshTokenValue);
    }

}