package com.example.bugtracker.smalltest.authentication.refresh;

import com.example.bugtracker.authentication.refresh.RefreshTokenAuthentication;
import com.example.bugtracker.authentication.refresh.RefreshTokenAuthenticationProvider;
import com.example.bugtracker.databuilders.UserTestDataBuilder;
import com.example.bugtracker.domain.enums.UserRole;
import com.example.bugtracker.domain.model.User;
import com.example.bugtracker.domain.model.auth.RefreshTokenData;
import com.example.bugtracker.domain.model.auth.SecurityUser;
import com.example.bugtracker.fake.repository.TokenRepositoryFake;
import com.example.bugtracker.fake.repository.UserRepositoryFake;
import com.example.bugtracker.repository.repository_interfaces.TokenRepository;
import com.example.bugtracker.repository.repository_interfaces.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenAuthenticationProviderTest {
    /*
     * Refresh tokens will be considered valid as long as its expiry date is later than the given point in time.
     * So a fixed instant of any point in time can be used for testing.
     */

    private final UserRepository userRepository = new UserRepositoryFake();

    private final TokenRepository tokenRepository = new TokenRepositoryFake(userRepository);

    private final Clock clock = Clock.fixed(ZonedDateTime.of(
                    2030,
                    1,
                    1,
                    1,
                    10,
                    10,
                    1,
                    ZoneId.systemDefault()).toInstant(),
            ZoneId.systemDefault());

    private final RefreshTokenAuthenticationProvider refreshTokenAuthenticationProvider
            = new RefreshTokenAuthenticationProvider(tokenRepository, clock);


    @Test
    void supports_returnsTrue_givenRefreshTokenAuthenticationSubclasses() {
        assertTrue(refreshTokenAuthenticationProvider.supports(
                RefreshTokenAuthentication.class));
    }

    @Test
    void supports_returnsFalse_givenOtherAuthenticationsThanRefreshTokenAuthentication() {
        assertFalse(refreshTokenAuthenticationProvider.supports(
                Authentication.class));
    }

    @Test
    void authenticate_throwsException_givenNonExistentRefreshToken() {
        //Setup
        Cookie refreshToken = new Cookie("testRefresh", "testValueRefreshTokenString");
        RefreshTokenAuthentication refreshTokenAuthentication
                = new RefreshTokenAuthentication(refreshToken);

        //Execution & Assertions
        assertThrows(AuthenticationCredentialsNotFoundException.class,
                () -> refreshTokenAuthenticationProvider.authenticate(refreshTokenAuthentication));
    }


    @Test
    void authenticate_throwsException_givenExpiredRefreshToken() {
        //Setup
        Cookie refreshToken = new Cookie("testRefresh", "testValueRefreshTokenString");

        User user = new UserTestDataBuilder().build();
        userRepository.save(user);

        RefreshTokenData refreshTokenData = new RefreshTokenData(
                null
                , user
                , Date.from(clock.instant().minus(1, ChronoUnit.DAYS))
                , "testValueRefreshTokenString"
        );
        tokenRepository.createNewToken(refreshTokenData);

        RefreshTokenAuthentication refreshTokenAuthentication
                = new RefreshTokenAuthentication(refreshToken);

        //Execution & Assertions
        assertThrows(BadCredentialsException.class,
                () -> refreshTokenAuthenticationProvider.authenticate(refreshTokenAuthentication));
    }


    @Test
    void authenticate_authenticatesUserAndReturnsRefreshAuthentication_givenUnauthenticatedRefreshAuthentication() {
        //Setup
        Cookie refreshToken = new Cookie("testRefresh", "testValueRefreshTokenString");
        User user = new UserTestDataBuilder()
                .withUsername("testUsername")
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_LEAD))
                .build();
        userRepository.save(user);
        RefreshTokenData refreshTokenData = new RefreshTokenData(
                null
                , user
                , Date.from(clock.instant().plus(1, ChronoUnit.DAYS))
                , "testValueRefreshTokenString"
        );
        tokenRepository.createNewToken(refreshTokenData);
        RefreshTokenAuthentication refreshTokenAuthentication = new RefreshTokenAuthentication(refreshToken);

        //Execution
        Authentication refreshTokenAuthenticationReturned
                = refreshTokenAuthenticationProvider.authenticate(refreshTokenAuthentication);

        //Assertions
        assertTrue(refreshTokenAuthenticationReturned.isAuthenticated());
        assertThat(refreshToken).usingRecursiveComparison().isEqualTo(refreshTokenAuthenticationReturned.getCredentials());
        assertThat(
                new SecurityUser(
                        "testUsername"
                        , refreshToken.getValue()
                        , Set.of(new SimpleGrantedAuthority("ROLE_TEAM_LEAD"))
                        , true))
                .usingRecursiveComparison()
                .isEqualTo(refreshTokenAuthenticationReturned.getPrincipal());
    }

}
