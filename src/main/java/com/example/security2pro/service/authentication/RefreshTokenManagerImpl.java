package com.example.security2pro.service.authentication;


import com.example.security2pro.domain.model.User;
import com.example.security2pro.domain.model.auth.RefreshTokenData;
import com.example.security2pro.domain.model.auth.SecurityUser;
import com.example.security2pro.repository.repository_interfaces.TokenRepository;
import com.example.security2pro.repository.repository_interfaces.UserRepository;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.temporal.ChronoUnit;
import java.util.*;


@Slf4j
@Component
@Transactional
public class RefreshTokenManagerImpl implements RefreshTokenManager {
    private final UserRepository userRepository;

    private final TokenRepository tokenRepository;
    private final int refreshMaxAgeInDays;
    private final Clock clock;

    public RefreshTokenManagerImpl(
            TokenRepository tokenRepository
            , Clock clock
            , @Value("${refresh.age.max.days}") int refreshMaxAgeInDays
            , UserRepository userRepository) {

        this.tokenRepository = tokenRepository;
        this.clock = clock;
        this.refreshMaxAgeInDays = refreshMaxAgeInDays;
        this.userRepository = userRepository;
    }

    /**
     * Creates and saves a refresh token in the repository
     *
     * @param auth An 'Authentication' object that has 'SecurityUser' as its 'Principal'.
     * @return The created and saved refresh token
     */
    public Cookie createRefreshToken(Authentication auth) {
        String refreshToken = UUID.randomUUID().toString();

        Date expiryDate =
                Date.from(clock.instant().plus(1, ChronoUnit.DAYS));

        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(refreshMaxAgeInDays * 60 * 60);


        SecurityUser user = (SecurityUser) auth.getPrincipal();
        User foundUser = userRepository.findUserByUsername(user.getUsername()).get();

        RefreshTokenData refreshTokenData = new RefreshTokenData(null, foundUser, expiryDate
                , refreshToken);

        tokenRepository.createNewToken(refreshTokenData);

        return cookie;
    }

    /**
     * Fetches 'RefreshTokenData' with the provided refresh token value from the repository.
     * The retrieved 'RefreshTokenData' will be verified by 'RefreshTokenAuthenticationProvider' for its validity.
     *
     * @param refreshTokenValue The value of the refresh token to be checked by the repository if it exists.
     * @return A 'RefreshTokenData' if the provided refresh token exists.
     * If it doesn't exist, an 'EmptyResultDataAccessException' is thrown from the repository.
     */
    @Override
    public Optional<RefreshTokenData> readRefreshToken(String refreshTokenValue) {
        return tokenRepository.readRefreshToken(refreshTokenValue);
    }

    /**
     * @param refreshTokenValue The value of the refresh token to be deleted.
     */
    @Override
    public void deleteToken(String refreshTokenValue) {
        tokenRepository.deleteToken(refreshTokenValue);
    }

}