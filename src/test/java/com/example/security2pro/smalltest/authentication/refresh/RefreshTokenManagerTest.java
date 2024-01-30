package com.example.security2pro.smalltest.authentication.refresh;

import com.example.security2pro.databuilders.UserTestDataBuilder;
import com.example.security2pro.domain.enums.UserRole;
import com.example.security2pro.domain.model.User;
import com.example.security2pro.domain.model.auth.RefreshTokenData;
import com.example.security2pro.domain.model.auth.SecurityUser;
import com.example.security2pro.fake.authentication.AuthenticationFake;
import com.example.security2pro.fake.repository.TokenRepositoryFake;
import com.example.security2pro.fake.repository.UserRepositoryFake;
import com.example.security2pro.repository.repository_interfaces.TokenRepository;
import com.example.security2pro.repository.repository_interfaces.UserRepository;
import com.example.security2pro.service.authentication.RefreshTokenManager;
import com.example.security2pro.service.authentication.RefreshTokenManagerImpl;
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

class RefreshTokenManagerTest {

    private final UserRepository userRepository = new UserRepositoryFake();

    private final TokenRepository tokenRepository = new TokenRepositoryFake(userRepository);

    private final Clock clock = Clock.fixed(ZonedDateTime.of(
                    // Clock needs to be set to future date.
                    // JwtParser's parseSignedClaims method verify JWT against the current instant as well.

                    2030,
                    2,
                    1,
                    1,
                    10,
                    10,
                    1,
                    ZoneId.systemDefault()).toInstant(),
            ZoneId.systemDefault());

    private final int refreshMaxAgeInDays =1;

    private final RefreshTokenManager refreshTokenManager
            = new RefreshTokenManagerImpl(tokenRepository,clock,refreshMaxAgeInDays,userRepository);

    @Test
    void createRefreshToken(){
        //Setup
        User user = new UserTestDataBuilder()
                .withUsername("testUsername")
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_MEMBER))
                .build();
        user = userRepository.save(user);

        SecurityUser securityUser = new SecurityUser(user);

        Authentication authentication = new AuthenticationFake(securityUser,true);

        //Execution
        Cookie refreshToken = refreshTokenManager.createRefreshToken(authentication);

        //Assertions
        assertTrue(refreshToken.getSecure());
        assertTrue(refreshToken.isHttpOnly());
        assertEquals(refreshMaxAgeInDays*60*60,refreshToken.getMaxAge());
        assertEquals("refresh_token",refreshToken.getName());

        RefreshTokenData refreshTokenDataFound
                = tokenRepository.readRefreshToken(refreshToken.getValue()).get();

        assertEquals("testUsername",refreshTokenDataFound.getUser().getUsername());
        assertEquals(Set.of(UserRole.ROLE_TEAM_MEMBER),refreshTokenDataFound.getUser().getAuthorities());

        assertTrue(refreshTokenDataFound.getExpiryDate()
                .before(Date.from(clock.instant()
                        .plus(refreshMaxAgeInDays, ChronoUnit.DAYS).plusSeconds(30))));

        assertTrue(refreshTokenDataFound.getExpiryDate()
                .after(Date.from(clock.instant()
                        .plus(refreshMaxAgeInDays, ChronoUnit.DAYS).minusSeconds(30))));

        assertEquals(refreshToken.getValue(), refreshTokenDataFound.getRefreshTokenString());
    }
}
