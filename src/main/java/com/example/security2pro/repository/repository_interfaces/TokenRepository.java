package com.example.security2pro.repository.repository_interfaces;

import com.example.security2pro.domain.model.auth.RefreshTokenData;

import java.util.Optional;

public interface TokenRepository {

    Optional<RefreshTokenData> readRefreshToken(String refreshToken);

    RefreshTokenData createNewToken(RefreshTokenData refreshTokenData);

    void deleteToken(String refreshTokenValue);
}
