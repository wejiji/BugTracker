package com.example.security2pro.authentication;

import com.example.security2pro.databuilders.UserTestDataBuilder;
import com.example.security2pro.domain.enums.refactoring.UserRole;
import com.example.security2pro.domain.model.User;
import com.example.security2pro.domain.model.auth.RefreshTokenData;
import com.example.security2pro.domain.model.auth.SecurityUser;
import com.example.security2pro.repository.TokenRepositoryFake;
import com.example.security2pro.repository.repository_interfaces.TokenRepository;
import com.example.security2pro.service.auth0.RefreshTokenManager;
import com.example.security2pro.service.auth0.RefreshTokenManagerImpl;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RefreshTokenManagerTest {

    private TokenRepository tokenRepository = new TokenRepositoryFake();
    private Clock clock = Clock.fixed(ZonedDateTime.of(
                    //clock needs to be set to future date
                    //JwtParser's parseSignedClaims method verify jwts against current time

                    2030,
                    2,
                    1,
                    1,
                    10,
                    10,
                    1,
                    ZoneId.systemDefault()).toInstant(),
            ZoneId.systemDefault());

    int refreshMaxAgeInDays =1;

    private RefreshTokenManager refreshTokenManager = new RefreshTokenManagerImpl(tokenRepository,clock,refreshMaxAgeInDays);



    @Test
    public void createRefreshToken(){
        User user = new UserTestDataBuilder()
                .withUsername("testUsername")
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_MEMBER))
                .build();

        SecurityUser securityUser = new SecurityUser(user);

        Authentication authentication = new AuthenticationFake(securityUser,true);

        Cookie refreshToken = refreshTokenManager.createRefreshToken(authentication);

        assertTrue(refreshToken.getSecure());
        assertTrue(refreshToken.isHttpOnly());
        assertEquals(refreshMaxAgeInDays*60*60,refreshToken.getMaxAge());
        assertEquals("refresh_token",refreshToken.getName());

        RefreshTokenData refreshTokenDataFound= tokenRepository.readRefreshToken(refreshToken.getValue());

        assertEquals("testUsername",refreshTokenDataFound.getUsername());
        assertEquals("ROLE_TEAM_MEMBER",refreshTokenDataFound.getRoles());
        assertTrue(refreshTokenDataFound.getExpiryDate().before(Date.from(clock.instant().plus(refreshMaxAgeInDays, ChronoUnit.DAYS).plusSeconds(30))));
        assertTrue(refreshTokenDataFound.getExpiryDate().after(Date.from(clock.instant().plus(refreshMaxAgeInDays, ChronoUnit.DAYS).minusSeconds(30))));
        assertEquals(refreshToken.getValue(), refreshTokenDataFound.getRefreshTokenString());

    }
}
