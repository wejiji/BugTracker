package com.example.bugtracker.repository.repository_interfaces;

import com.example.bugtracker.domain.model.auth.RefreshTokenData;

import java.util.Optional;

public interface TokenRepository {

    Optional<RefreshTokenData> readRefreshToken(String refreshToken);

    RefreshTokenData createNewToken(RefreshTokenData refreshTokenData);

    void deleteToken(String refreshTokenValue);
}
