package com.example.bugtracker.service.authentication;

import com.example.bugtracker.authentication.jwt.ProjectRoles;
import com.example.bugtracker.authentication.jwt.ProjectRolesConverter;
import com.example.bugtracker.domain.model.ProjectMember;
import com.example.bugtracker.domain.model.auth.SecurityUser;
import com.example.bugtracker.repository.repository_interfaces.ProjectMemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenManagerImpl implements JwtTokenManager {
    private final String signingKey;
    private final int accessMaxAgeInMins;
    private final Clock clock;
    private final ProjectRolesConverter projectRolesConverter;
    private final ProjectMemberRepository projectMemberRepository;
    private static final String USER_ROLE_CLAIM_KEY = "userRoles";
    private static final String PROJECT_ROLE_CLAIM_KEY = "projectRoles";

    public JwtTokenManagerImpl(
            @Value("${jwt.signing.key}") String signingKey
            , Clock clock
            , @Value("${access.age.max.minutes}") int accessMaxAgeInMins
            , ProjectRolesConverter projectRolesConverter
            , ProjectMemberRepository projectMemberRepository) {

        this.signingKey = signingKey;
        this.clock = clock;
        this.accessMaxAgeInMins = accessMaxAgeInMins;
        this.projectRolesConverter = projectRolesConverter;
        this.projectMemberRepository = projectMemberRepository;
    }

    /**
     * Creates and returns a JWT access token with 'ProjectMemberRole' authorities after successful authentication.
     * The authorities are set based on the fetched roles from the project member repository for each project.
     *
     * @param authentication An 'Authentication' object that has 'SecurityUser' as its 'Principal'.
     * @return A String value of JWT.
     */
    @Override
    public String createAccessToken(Authentication authentication) {
        SecretKey key = Keys.hmacShaKeyFor(
                signingKey.getBytes(StandardCharsets.UTF_8));

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        List<String> roles
                = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String rolesString = String.join(",", roles);

        Set<ProjectMember> projectMemberSet = projectMemberRepository
                .findAllByUsernameWithProjectMemberAuthorities(
                        securityUser.getUsername());

        Set<ProjectRoles> projectRolesSet = projectMemberSet.stream()
                .map(projectMember -> new ProjectRoles(
                        projectMember.getProject().getId()
                        , projectMember.getAuthorities()))
                .collect(Collectors.toCollection(HashSet::new));

        String projectRolesString = projectRolesConverter.convertToString(projectRolesSet);
        log.info("fetched project roles of the user: " + projectRolesString);

        Date issueDate = Date.from(clock.instant());
        Date expiryDate = Date.from(clock.instant().plus(accessMaxAgeInMins, ChronoUnit.MINUTES));
        return Jwts.builder().claims(Map.of(
                        USER_ROLE_CLAIM_KEY, rolesString, PROJECT_ROLE_CLAIM_KEY, projectRolesString))
                .subject(securityUser.getUsername())
                .issuedAt(issueDate)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }


    /**
     * Verifies the signature and expiry date of the JWT.
     * An exception will be generated if the JWT is invalid.
     * All exceptions are translated to a 'BadCredentialsException,' a subclass of 'AuthenticationException.'
     *
     * @param jwt The JWT to be verified.
     * @return A Map<String,String> of claim name and value pairs.
     */
    @Override
    public Map<String, String> verifyAccessToken(String jwt) {
        SecretKey key = Keys.hmacShaKeyFor(
                signingKey.getBytes(StandardCharsets.UTF_8));

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();

            if (claims.getExpiration().before(Date.from(clock.instant()))) {
                throw new JwtException("expired jwt");
            }

            Map<String, String> verifiedClaimsMap = new HashMap<>();
            verifiedClaimsMap.put("subject", claims.getSubject());
            verifiedClaimsMap.put(USER_ROLE_CLAIM_KEY, claims.get(USER_ROLE_CLAIM_KEY).toString());
            verifiedClaimsMap.put(PROJECT_ROLE_CLAIM_KEY, claims.get(PROJECT_ROLE_CLAIM_KEY).toString());
            return verifiedClaimsMap;

        } catch (UnsupportedJwtException e) {
            throw new BadCredentialsException("jwt format does not match", e);
        } catch (JwtException e) {
            log.error("jwt exception cause by:" + e.getMessage());
            throw new BadCredentialsException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException("jwt missing", e);
        }
    }
}
