package com.example.security2pro.repository.repository_interfaces;

import com.example.security2pro.domain.model.auth.RefreshTokenData;

public interface TokenRepository {

    RefreshTokenData readRefreshToken(String refreshToken);

    void createNewToken(RefreshTokenData refreshTokenData);

    void deleteToken(String refreshTokenValue);
}
