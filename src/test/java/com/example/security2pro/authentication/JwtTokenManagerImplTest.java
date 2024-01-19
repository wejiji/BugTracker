package com.example.security2pro.authentication;

import com.example.security2pro.authentication.newjwt.JwtTokenManagerWithProjectRoles;
import com.example.security2pro.authentication.newjwt.ProjectRoles;
import com.example.security2pro.authentication.newjwt.ProjectRolesConverter;
import com.example.security2pro.databuilders.UserTestDataBuilder;
import com.example.security2pro.domain.enums.refactoring.UserRole;
import com.example.security2pro.domain.model.User;
import com.example.security2pro.domain.model.auth.SecurityUser;
import com.example.security2pro.service.auth0.JwtTokenManager;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static com.example.security2pro.authorization.ProjectMemberPermissionEvaluatorTest.projectId;
import static org.junit.jupiter.api.Assertions.*;

public class JwtTokenManagerImplTest {

    private String signingKey= "ymLTU8rq833!=enZ%ojoqwidbuuwgwugyq/231!@^BFD8$#*scase$3aafewbg#7gkj88";

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


    int accessMaxAgeInMins = 3;
    private ProjectRolesConverter projectRolesConverter = new ProjectRolesConverter();
    private JwtTokenManager jwtTokenManager = new JwtTokenManagerWithProjectRoles(signingKey,clock,accessMaxAgeInMins,projectRolesConverter);




    @Test
    public void createAccessToken(){

        User user = new UserTestDataBuilder()
                .withUsername("testUsername")
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_MEMBER))
                .build();

        SecurityUser securityUser = new SecurityUser(user);

        ProjectRoles projectRoles = new ProjectRoles(String.valueOf(10L),"ROLE_PROJECT_MEMBER");


        Authentication userAndProjectRoleAuthentication =
                new UserAndProjectRoleAuthenticationMock(securityUser, new HashSet<>(Set.of(projectRoles)));


        String jwt= jwtTokenManager.createAccessToken(userAndProjectRoleAuthentication);

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
        assertEquals("ROLE_TEAM_MEMBER",claims.get("userRoles"));
        assertEquals("["+projectId+":ROLE_PROJECT_MEMBER]",claims.get("projectRoles"));
    }



    @Test
    public void verifyAccessToken_success(){

        User user = new UserTestDataBuilder()
                .withUsername("testUsername")
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_MEMBER))
                .build();

        SecurityUser securityUser = new SecurityUser(user);

        SecretKey key = Keys.hmacShaKeyFor(
                signingKey.getBytes(StandardCharsets.UTF_8));

        Date issueDate = Date.from(clock.instant().minus(10,ChronoUnit.MINUTES));
        Date expiryDate = Date.from(clock.instant().plus(5,ChronoUnit.MINUTES));

        String jwt = Jwts.builder().claims(Map.of("userRoles", "ROLE_TEAM_MEMBER", "projectRoles","ROLE_PROJECT_LEAD"))
                .subject(securityUser.getUsername())
                .issuedAt(issueDate)
                .expiration(expiryDate)
                .signWith(key)
                .compact();

        Map<String,String> verifiedClaims = jwtTokenManager.verifyAccessToken(jwt);

//        assertEquals(issueDate,claims.getIssuedAt());
//        assertEquals(expiryDate,claims.getExpiration());
        assertEquals("testUsername",verifiedClaims.get("subject"));
        assertEquals("ROLE_TEAM_MEMBER",verifiedClaims.get("userRoles"));
        assertEquals("ROLE_PROJECT_LEAD",verifiedClaims.get("projectRoles"));
    }

    @Test
    public void verifyAccessToken_throwsExceptionGivenExpiredJwt(){

        User user = new UserTestDataBuilder()
                .withUsername("testUsername")
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_MEMBER))
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

        assertThrows(BadCredentialsException.class,()-> jwtTokenManager.verifyAccessToken(jwt));
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

        assertThrows(BadCredentialsException.class,()-> jwtTokenManager.verifyAccessToken(""));
    }


    @Test
    public void verifyAccessToken_throwsExceptionGivenInvalidJwt(){

        assertThrows(BadCredentialsException.class,()-> jwtTokenManager.verifyAccessToken(null));
    }







}
