//package com.example.security2pro.service.auth0;
//
//
//import com.example.security2pro.domain.model.auth.SecurityUser;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.JwtException;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.UnsupportedJwtException;
//import io.jsonwebtoken.security.Keys;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.crypto.SecretKey;
//import java.nio.charset.StandardCharsets;
//import java.time.Clock;
//import java.time.temporal.ChronoUnit;
//import java.util.*;
//
//
//@Slf4j
//@Component
//@Transactional
//public class JwtTokenManagerImpl implements JwtTokenManager {
//
//    private String signingKey;
//
//    private final int ACCESS_MAX_AGE_IN_MINS;
//
//    private final Clock clock;
//
//
//    public JwtTokenManagerImpl( @Value("${jwt.signing.key}") String signingKey, Clock clock, @Value("${access.age.max.minutes}") int accessMaxAgeInMins) {
//        this.signingKey = signingKey;
//        this.clock = clock;
//        ACCESS_MAX_AGE_IN_MINS = accessMaxAgeInMins;
//    }
//
//
//    public String createAccessToken(Authentication authentication) {
//
//        SecretKey key = Keys.hmacShaKeyFor(
//                signingKey.getBytes(StandardCharsets.UTF_8));
//
//
//        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
//        List<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
//        String rolesString = String.join(",", roles);
//
//        Date issueDate = Date.from(clock.instant());
//        Date expiryDate = Date.from(clock.instant().plus(ACCESS_MAX_AGE_IN_MINS, ChronoUnit.MINUTES));
//        return Jwts.builder().claims(Map.of("roles", rolesString))
//                .subject(securityUser.getUsername())
//                .issuedAt(issueDate)
//                .expiration(expiryDate)
//                .signWith(key)
//                .compact();
//    }
//
//
//    public Map<String, String> verifyAccessToken(String jwt) {
//        //현재 signature과 expiration만 확인하는중.. 다른것 확인할것이 있나?
//        SecretKey key = Keys.hmacShaKeyFor(
//                signingKey.getBytes(StandardCharsets.UTF_8));
//
//        try {
//            Claims claims = Jwts.parser()
//                    .verifyWith(key)
//                    .build()
//                    .parseSignedClaims(jwt)
//                    .getPayload();
//
//            if (claims.getExpiration().before(Date.from(clock.instant()))) {
//                throw new JwtException("expired jwt");
//            }
//
//            Map<String, String> verifiedClaimsMap = new HashMap<>();
//            verifiedClaimsMap.put("subject", claims.getSubject());
//            verifiedClaimsMap.put("roles", claims.get("roles").toString());
//            return verifiedClaimsMap;
//
//        } catch (UnsupportedJwtException e) { // spring security 에러로 바꾼다..??
//            throw new BadCredentialsException("jwt format does not match", e);
//        } catch (JwtException e) {
//            log.error("jwt exception cause by:" + e.getMessage());
//            throw new BadCredentialsException(e.getMessage());
//        } catch (IllegalArgumentException e) {
//            throw new BadCredentialsException("jwt missing", e);
//        }
//    }
//}