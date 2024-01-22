package com.example.security2pro.authentication;

import com.example.security2pro.authentication.refresh.RefreshTokenAuthentication;
import com.example.security2pro.authentication.refresh.RefreshTokenAuthenticationProvider;
import com.example.security2pro.databuilders.UserTestDataBuilder;
import com.example.security2pro.domain.model.User;
import com.example.security2pro.domain.model.auth.RefreshTokenData;
import com.example.security2pro.domain.model.auth.SecurityUser;
import com.example.security2pro.repository.TokenRepositoryFake;
import com.example.security2pro.repository.UserRepositoryFake;
import com.example.security2pro.repository.repository_interfaces.TokenRepository;
import com.example.security2pro.repository.repository_interfaces.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class RefreshTokenAuthenticationProviderTest {
/*
         * refresh token will be considered valid as long as it is earlier than the given point in time
     * in the application, it will always be
     * the validity of refresh token only depends on whether its expiry date is prior to a given point in time
 */

    private final TokenRepository tokenRepository = new TokenRepositoryFake();

    private final UserRepository userRepository = new UserRepositoryFake();
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

    private RefreshTokenAuthenticationProvider refreshTokenAuthenticationProvider
            = new RefreshTokenAuthenticationProvider(tokenRepository,userRepository,clock);
    @Test
    public void authenticate_throwsExceptionGivenRefreshTokenNotExistInDB(){

        Cookie refreshToken = new Cookie("testRefresh","testValueRefreshTokenString");

        RefreshTokenAuthentication refreshTokenAuthentication = new RefreshTokenAuthentication(refreshToken);

        assertThrows(BadCredentialsException.class, ()-> refreshTokenAuthenticationProvider.authenticate(refreshTokenAuthentication) );
    }


    @Test
    public void authenticate_throwsExceptionGivenExpiredRefreshToken(){
        Cookie refreshToken = new Cookie("testRefresh","testValueRefreshTokenString");

        RefreshTokenData refreshTokenData = new RefreshTokenData(
                "testUsername"
                , Date.from(clock.instant().minus(1, ChronoUnit.DAYS))
                , List.of("ROLE_ADMIN")
                , "testValueRefreshTokenString"
        );

        tokenRepository.createNewToken(refreshTokenData); //save

        RefreshTokenAuthentication refreshTokenAuthentication = new RefreshTokenAuthentication(refreshToken);

        assertThrows(BadCredentialsException.class, ()-> refreshTokenAuthenticationProvider.authenticate(refreshTokenAuthentication) );
    }


    @Test
    public void authenticate_success() {
        Cookie refreshToken = new Cookie("testRefresh", "testValueRefreshTokenString");

        User user = new UserTestDataBuilder().withUsername("testUsername").build();
        userRepository.save(user);

        RefreshTokenData refreshTokenData = new RefreshTokenData(
                "testUsername"
                , Date.from(clock.instant().plus(1, ChronoUnit.DAYS))
                , List.of("ROLE_ADMIN")
                , "testValueRefreshTokenString"
        );

        tokenRepository.createNewToken(refreshTokenData); //save

        RefreshTokenAuthentication refreshTokenAuthentication = new RefreshTokenAuthentication(refreshToken);

        Authentication refreshTokenAuthenticationReturned
                = refreshTokenAuthenticationProvider.authenticate(refreshTokenAuthentication);

        assertTrue(refreshTokenAuthenticationReturned.isAuthenticated());
        assertThat(refreshToken).usingRecursiveComparison().isEqualTo(refreshTokenAuthenticationReturned.getCredentials());
        assertThat(new SecurityUser("testUsername",refreshToken.getValue(), Set.of(new SimpleGrantedAuthority("ROLE_ADMIN")),true)).usingRecursiveComparison().isEqualTo(refreshTokenAuthenticationReturned.getPrincipal());
    }


    @Test
    public void supports_true(){

        assertTrue(refreshTokenAuthenticationProvider.supports(RefreshTokenAuthentication.class));
    }

    @Test
    public void supports_false(){

        assertFalse(refreshTokenAuthenticationProvider.supports(Authentication.class));
    }





}
