package com.example.bugtracker.smalltest.authentication.jwt;

import com.example.bugtracker.databuilders.ProjectMemberTestDataBuilder;
import com.example.bugtracker.databuilders.ProjectTestDataBuilder;
import com.example.bugtracker.domain.enums.ProjectMemberRole;
import com.example.bugtracker.domain.model.Project;
import com.example.bugtracker.domain.model.ProjectMember;
import com.example.bugtracker.fake.authentication.AuthenticationFake;
import com.example.bugtracker.fake.authentication.JwtTokenManagerImplFake;
import com.example.bugtracker.fake.repository.ProjectMemberRepositoryFake;
import com.example.bugtracker.repository.repository_interfaces.ProjectMemberRepository;
import com.example.bugtracker.service.authentication.JwtTokenManager;
import com.example.bugtracker.service.authentication.JwtTokenManagerImpl;
import com.example.bugtracker.authentication.jwt.ProjectRolesConverter;
import com.example.bugtracker.databuilders.UserTestDataBuilder;
import com.example.bugtracker.domain.enums.UserRole;
import com.example.bugtracker.domain.model.User;
import com.example.bugtracker.domain.model.auth.SecurityUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
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


import static org.junit.jupiter.api.Assertions.*;

class JwtTokenManagerImplTest {
    /*
     * clock should be set to a future date in this test class
     * since JwtParser's parseSignedClaims method verifies JWTs against the current instant.
     */
    private final String signingKey = "ymLTU8rq833!=enZ%ojoqwidbuuwgwugyq/231!@^BFD8$#*scase$3aafewbg#7gkj88";

    private final Clock clock = Clock.fixed(ZonedDateTime.of(
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
    private final ProjectRolesConverter projectRolesConverter = new ProjectRolesConverter();

    private final ProjectMemberRepository projectMemberRepository = new ProjectMemberRepositoryFake();
    private final JwtTokenManager jwtTokenManager
            = new JwtTokenManagerImpl(
            signingKey
            , clock
            , accessMaxAgeInMins
            , projectRolesConverter
            , projectMemberRepository);

    private final String projectIdForAuthorization= JwtTokenManagerImplFake.projectIdForAuthorization;

    @Test
    void createAccessToken_createsAccessToken_givenJwtAuthenticationWithSecurityUser() {
        //Setup
        User user = new UserTestDataBuilder()
                .withUsername("testUsername")
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_MEMBER))
                .build();
        SecurityUser securityUser = new SecurityUser(user);

        Project project = new ProjectTestDataBuilder()
                .withId(Long.valueOf(projectIdForAuthorization)).build();

        ProjectMember projectMember = new ProjectMemberTestDataBuilder()
                .withUser(user)
                .withProject(project)
                .withAuthorities(Set.of(ProjectMemberRole.ROLE_PROJECT_LEAD))
                .build();

        projectMemberRepository.save(projectMember);

        Authentication authentication = new AuthenticationFake(securityUser, true);

        //Execution
        String jwt = jwtTokenManager.createAccessToken(authentication);

        //Assertions
        SecretKey key = Keys.hmacShaKeyFor(
                signingKey.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();

        assertEquals(Date.from(clock.instant()), claims.getIssuedAt());

        assertEquals(Date.from(clock.instant().plus(accessMaxAgeInMins, ChronoUnit.MINUTES))
                , claims.getExpiration());

        assertEquals("testUsername", claims.getSubject());
        assertEquals("ROLE_TEAM_MEMBER", claims.get("userRoles"));

        assertEquals("[" + projectIdForAuthorization + ":ROLE_PROJECT_LEAD]"
                , claims.get("projectRoles"));
    }

    @Test
    void verifyAccessToken_verifiesJwtAndReturnsAMapWithClaims_givenValidJwt() {
        //Setup
        User user = new UserTestDataBuilder()
                .withUsername("testUsername")
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_MEMBER))
                .build();

        SecurityUser securityUser = new SecurityUser(user);

        SecretKey key = Keys.hmacShaKeyFor(
                signingKey.getBytes(StandardCharsets.UTF_8));

        Date issueDate = Date.from(clock.instant().minus(10, ChronoUnit.MINUTES));
        Date expiryDate = Date.from(clock.instant().plus(5, ChronoUnit.MINUTES));

        String jwt = Jwts.builder().claims(Map.of("userRoles", "ROLE_TEAM_MEMBER"
                        , "projectRoles", "[" + projectIdForAuthorization + ":ROLE_PROJECT_LEAD]"))
                .subject(securityUser.getUsername())
                .issuedAt(issueDate)
                .expiration(expiryDate)
                .signWith(key)
                .compact();

        //Execution
        Map<String, String> verifiedClaims = jwtTokenManager.verifyAccessToken(jwt);

        //Assertions
        assertEquals("testUsername", verifiedClaims.get("subject"));
        assertEquals("ROLE_TEAM_MEMBER", verifiedClaims.get("userRoles"));
        assertEquals("[" + projectIdForAuthorization + ":ROLE_PROJECT_LEAD]"
                , verifiedClaims.get("projectRoles"));
    }

    @Test
    void verifyAccessToken_throwsException_givenExpiredJwt() {
        //Setup
        User user = new UserTestDataBuilder()
                .withUsername("testUsername")
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_MEMBER))
                .build();

        SecurityUser securityUser = new SecurityUser(user);

        SecretKey key = Keys.hmacShaKeyFor(
                signingKey.getBytes(StandardCharsets.UTF_8));

        Date issueDate = Date.from(clock.instant().minus(10, ChronoUnit.MINUTES));
        Date expiryDate = Date.from(clock.instant().minus(5, ChronoUnit.MINUTES));

        String jwt
                = Jwts.builder().claims(Map.of("userRoles", "ROLE_TEAM_MEMBER"))
                .subject(securityUser.getUsername())
                .issuedAt(issueDate)
                .expiration(expiryDate)
                .signWith(key)
                .compact();

        //Execution& Assertions
        assertThrows(BadCredentialsException.class,
                () -> jwtTokenManager.verifyAccessToken(jwt));
    }

    @Test
    void verifyAccessToken_throwsException_givenJwtWithInvalidSignature() {
        //Setup
        String invalidSigningKey = "invalidSigningKeyValue999999999999999999999999999999999";

        SecretKey invalidKey = Keys.hmacShaKeyFor(
                invalidSigningKey.getBytes(StandardCharsets.UTF_8));

        Date issueDate = Date.from(clock.instant().minus(10, ChronoUnit.MINUTES));
        Date futureExpiryDate = Date.from(clock.instant().plus(5, ChronoUnit.MINUTES));

        String jwtWithInvalidSignature
                = Jwts.builder().claims(Map.of("roles", "ROLE_TEAM_MEMBER"))
                .subject("testUsername")
                .issuedAt(issueDate)
                .expiration(futureExpiryDate)
                .signWith(invalidKey)
                .compact();

        //Execution & Assertions
        assertThrows(BadCredentialsException.class,
                () -> jwtTokenManager.verifyAccessToken(jwtWithInvalidSignature));
    }

    @Test
    void verifyAccessToken_throwsException_givenInvalidJwtString() {
        assertThrows(BadCredentialsException.class,
                () -> jwtTokenManager.verifyAccessToken("invalidJwt"));
    }

}
