package com.example.security2pro.authentication.refresh;

import com.example.security2pro.repository.repository_interfaces.TokenRepository;
import com.example.security2pro.domain.model.auth.RefreshTokenData;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RefreshTokenAuthenticationProvider implements AuthenticationProvider {

    private final TokenRepository tokenRepository;

    private final Clock clock;
    private static final String INVALID_REFRESH_MESSAGE = "invalid refresh token";

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        RefreshTokenAuthentication auth2 = (RefreshTokenAuthentication) authentication;
        Cookie refreshToken = (Cookie) auth2.getCredentials();

        RefreshTokenData foundRefreshTokenData = null;
        try {
            Optional<RefreshTokenData> refreshTokenDataOptional
                    =tokenRepository.readRefreshToken(refreshToken.getValue());

            if(refreshTokenDataOptional.isEmpty()){
                throw new AuthenticationCredentialsNotFoundException("");
            }
            foundRefreshTokenData= refreshTokenDataOptional.get();
            log.info("refresh token existence verified");

        } catch (EmptyResultDataAccessException e) {
            log.error("given refresh token value does not exist in db", e);
            throw new BadCredentialsException(INVALID_REFRESH_MESSAGE, e);
        } catch (NonTransientDataAccessException e) {
            log.error("invalid token", e);
            throw new BadCredentialsException(INVALID_REFRESH_MESSAGE, e);
        } catch (TransientDataAccessException e) {
            log.error("db error", e);
            throw new InternalAuthenticationServiceException("db error", e);
        }

        boolean valid = foundRefreshTokenData.checkExpiration(clock.instant());
        if (!valid) {
            log.error("expired refresh token");
            tokenRepository.deleteToken(foundRefreshTokenData.getRefreshTokenString());
            throw new BadCredentialsException(INVALID_REFRESH_MESSAGE);
        }

        Collection<? extends GrantedAuthority> authoritiesFound
                = foundRefreshTokenData.getUser().getAuthorities().stream()
                .map(auth -> new SimpleGrantedAuthority(auth.name()))
                .collect(Collectors.toCollection(HashSet::new));

        return new RefreshTokenAuthentication(
                foundRefreshTokenData.getUser().getUsername()
                , refreshToken
                , authoritiesFound);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return RefreshTokenAuthentication.class.isAssignableFrom(authentication);
    }


}
