package com.example.bugtracker.repository.repository_impls;


import com.example.bugtracker.domain.model.auth.RefreshTokenData;
import com.example.bugtracker.repository.jpa_repository.TokenJpaRepository;
import com.example.bugtracker.repository.repository_interfaces.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TokenRepositoryImpl implements TokenRepository {


    private final TokenJpaRepository tokenJpaRepository;

    @Override
    public Optional<RefreshTokenData> readRefreshToken(String refreshToken) {
        return tokenJpaRepository.findByRefreshTokenStringWithUserAuthorities(refreshToken);
    }

    public RefreshTokenData createNewToken(RefreshTokenData refreshTokenData) {
        // An auto-generated key will be assigned for the 'id' field by the database.

        Optional<RefreshTokenData> refreshTokenDataOptional
                = tokenJpaRepository.findByUsername(refreshTokenData.getUser().getUsername());
        if (refreshTokenDataOptional.isEmpty()) { //Creates a new token data
            return tokenJpaRepository.save(refreshTokenData);
        }

        RefreshTokenData tobeSaved = new RefreshTokenData(
                refreshTokenDataOptional.get().getId()
                , refreshTokenData.getUser()
                , refreshTokenData.getExpiryDate()
                , refreshTokenData.getRefreshTokenString());

        return tokenJpaRepository.save(tobeSaved);
    }

    public void deleteToken(String refreshTokenValue) {

        tokenJpaRepository.deleteByRefreshTokenString(refreshTokenValue);
    }


}
