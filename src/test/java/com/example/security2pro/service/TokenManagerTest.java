package com.example.security2pro.service;

import com.example.security2pro.databuilders.UserTestDataBuilder;
import com.example.security2pro.domain.enums.IssueStatus;
import com.example.security2pro.domain.enums.Role;
import com.example.security2pro.domain.model.User;
import com.example.security2pro.domain.model.auth.RefreshTokenData;
import com.example.security2pro.domain.model.auth.SecurityUser;
import com.example.security2pro.repository.TokenRepositoryFake;
import com.example.security2pro.repository.repository_interfaces.TokenRepository;
import com.example.security2pro.service.auth.TokenManager;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TokenManagerTest {

    private String signingKey= "ymLTU8rq833!=enZ%ojoqwidbuuwgwugyq/231!@^BFD8$#*scase$3aafewbg#7gkj88";

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
    int accessMaxAgeInMins = 3;
    private TokenManager tokenManager= new TokenManager(tokenRepository,signingKey,clock,refreshMaxAgeInDays,accessMaxAgeInMins);

    @Test
    public void createRefreshToken(){
        User user = new UserTestDataBuilder()
                .withUsername("testUsername")
                .withAuthorities(Set.of(Role.ROLE_TEAM_MEMBER))
                .build();

        SecurityUser securityUser = new SecurityUser(user);

        Authentication authentication = new AuthenticationFake(securityUser,true);

        Cookie refreshToken = tokenManager.createRefreshToken(authentication);

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


    @Test
    public void createAccessToken(){

        User user = new UserTestDataBuilder()
                .withUsername("testUsername")
                .withAuthorities(Set.of(Role.ROLE_TEAM_MEMBER))
                .build();

        SecurityUser securityUser = new SecurityUser(user);

        Authentication authentication = new AuthenticationFake(securityUser,true);


        String jwt= tokenManager.createAccessToken(authentication);

        SecretKey key = Keys.hmacShaKeyFor(
                signingKey.getBytes(StandardCharsets.UTF_8));

        Claims claims= Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();

        assertEquals(Date.from(clock.instant()),claims.getIssuedAt());
        assertEquals(Date.from(clock.instant().plus(accessMaxAgeInMins,ChronoUnit.MINUTES)),claims.getExpiration());
        assertEquals("testUsername",claims.getSubject());
        assertEquals("ROLE_TEAM_MEMBER",claims.get("roles"));
    }



    @Test
    public void verifyAccessToken_success(){

        User user = new UserTestDataBuilder()
                .withUsername("testUsername")
                .withAuthorities(Set.of(Role.ROLE_TEAM_MEMBER))
                .build();

        SecurityUser securityUser = new SecurityUser(user);

        SecretKey key = Keys.hmacShaKeyFor(
                signingKey.getBytes(StandardCharsets.UTF_8));

        Date issueDate = Date.from(clock.instant().minus(10,ChronoUnit.MINUTES));
        Date expiryDate = Date.from(clock.instant().plus(5,ChronoUnit.MINUTES));

        String jwt = Jwts.builder().claims(Map.of("roles", "ROLE_TEAM_MEMBER"))
                .subject(securityUser.getUsername())
                .issuedAt(issueDate)
                .expiration(expiryDate)
                .signWith(key)
                .compact();

        Claims claims = tokenManager.verifyAccessToken(jwt);

        assertEquals(issueDate,claims.getIssuedAt());
        assertEquals(expiryDate,claims.getExpiration());
        assertEquals("testUsername",claims.getSubject());
        assertEquals("ROLE_TEAM_MEMBER",claims.get("roles"));
    }

    @Test
    public void verifyAccessToken_throwsExceptionGivenExpiredJwt(){

        User user = new UserTestDataBuilder()
                .withUsername("testUsername")
                .withAuthorities(Set.of(Role.ROLE_TEAM_MEMBER))
                .build();

        SecurityUser securityUser = new SecurityUser(user);

        SecretKey key = Keys.hmacShaKeyFor(
                signingKey.getBytes(StandardCharsets.UTF_8));

        Date issueDate = Date.from(clock.instant().minus(10,ChronoUnit.MINUTES));
        Date expiryDate = Date.from(clock.instant().minus(5,ChronoUnit.MINUTES));

        String jwt = Jwts.builder().claims(Map.of("roles", "ROLE_TEAM_MEMBER"))
                .subject(securityUser.getUsername())
                .issuedAt(issueDate)
                .expiration(expiryDate)
                .signWith(key)
                .compact();

        assertThrows(BadCredentialsException.class,()-> tokenManager.verifyAccessToken(jwt));
    }



    private static Stream<Arguments> args_verifyAccessToken_throwsExceptionGivenInvalidJwt(){

        String invalidSigningKey = "invalidSigningKeyValue999999999999999999999999999999999";

        SecretKey invalidKey = Keys.hmacShaKeyFor(
                invalidSigningKey.getBytes(StandardCharsets.UTF_8));

        Clock clock2= Clock.fixed(ZonedDateTime.of(
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

        Date issueDate = Date.from(clock2.instant().minus(10,ChronoUnit.MINUTES));
        Date expiryDate = Date.from(clock2.instant().minus(5,ChronoUnit.MINUTES));

        String jwt = Jwts.builder().claims(Map.of("roles", "ROLE_TEAM_MEMBER"))
                .subject("testUsername")
                .issuedAt(issueDate)
                .expiration(expiryDate)
                .signWith(invalidKey)
                .compact();

        return Stream.of(
                Arguments.of("")
                ,Arguments.of("     ")
                ,Arguments.of("jwtNotValid")
                ,Arguments.of(jwt)
        );
    }


    @ParameterizedTest
    @MethodSource("args_verifyAccessToken_throwsExceptionGivenInvalidJwt")
    public void verifyAccessToken_throwsExceptionGivenInvalidJwt(String jwt){

        assertThrows(BadCredentialsException.class,()-> tokenManager.verifyAccessToken(""));
    }


    @Test
    public void verifyAccessToken_throwsExceptionGivenInvalidJwt(){

        assertThrows(BadCredentialsException.class,()-> tokenManager.verifyAccessToken(null));
    }







}
