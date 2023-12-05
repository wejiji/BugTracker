package com.example.security2pro.service.auth;

import com.example.security2pro.domain.model.auth.RefreshTokenData;
import com.example.security2pro.domain.model.auth.SecurityUser;
import com.example.security2pro.repository.auth.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class TokenManager {
    @Value("${jwt.signing.key}")
    private String signingKey;

    private final TokenRepository tokenRepository;

    private int REFRESH_MAX_AGE_IN_DAYS = 1;

    private int ACCESS_MAX_AGE_IN_MINS = 3;

    public Cookie createRefreshToken(Authentication auth, boolean rotationOn){
        String refreshToken = UUID.randomUUID().toString();

        Date expiryDate = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));

        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(REFRESH_MAX_AGE_IN_DAYS*60*60);//하루동안 유효

        //시간차가 나는 문제는?????????????
        List<String> rolesInString =auth.getAuthorities().stream().map(grantedAuthority -> grantedAuthority.getAuthority()).toList();
        SecurityUser securityUser=(SecurityUser) auth.getPrincipal();

        RefreshTokenData refreshTokenData = new RefreshTokenData(securityUser.getUsername(),expiryDate,rolesInString,refreshToken);
        tokenRepository.createNewToken(refreshTokenData);
        //username, refreshToken, lastModified ??

        return cookie;
    }

    public String createAccessToken(Authentication authentication) {

        SecretKey key = Keys.hmacShaKeyFor(
                signingKey.getBytes(StandardCharsets.UTF_8));

        SecurityUser securityUser =(SecurityUser)authentication.getPrincipal();
        List<String> roles=authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        String rolesString = String.join(",", roles);

        return Jwts.builder().claims(Map.of("roles", rolesString))
                .subject(securityUser.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_MAX_AGE_IN_MINS * 60 * 1000))
                .signWith(key)
                .compact();
    }


    public Claims verifyAccessToken(String jwt){
        //현재 signature과 expiration만 확인하는중.. 다른것 확인할것이 있나?
        SecretKey key = Keys.hmacShaKeyFor(
                signingKey.getBytes(StandardCharsets.UTF_8));

        try {
            Claims claims= Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();

            if(claims.getExpiration().before(new Date())){
                throw new JwtException("expired jwt");
            }
            return claims;

        } catch (UnsupportedJwtException e){ // spring security 에러로 바꾼다..??
            throw new BadCredentialsException("jwt format does not match",e);
        } catch (JwtException e){
            log.error("jwt exception cause by:" + e.getMessage());
            throw new BadCredentialsException(e.getMessage());
        } catch(IllegalArgumentException e){
            throw new BadCredentialsException("jwt missing",e);
        }
    }


    public RefreshTokenData readRefreshToken(String refreshTokenValue){
        return tokenRepository.readRefreshToken(refreshTokenValue);
    }


    public void deleteToken(String refreshTokenValue){
        tokenRepository.deleteToken(refreshTokenValue);
    }


}
