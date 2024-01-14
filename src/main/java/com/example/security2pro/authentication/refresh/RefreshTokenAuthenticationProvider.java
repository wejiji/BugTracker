package com.example.security2pro.authentication.refresh;


import com.example.security2pro.domain.enums.Role;
import com.example.security2pro.service.auth.TokenManager;
import com.example.security2pro.domain.model.auth.RefreshTokenData;
import com.example.security2pro.domain.model.auth.SecurityUser;
import com.example.security2pro.service.UserService;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
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


    private final TokenManager tokenManager;

    private final UserService userService;

    private final Clock clock;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        RefreshTokenAuthentication auth2 = (RefreshTokenAuthentication) authentication;
        Cookie refreshToken = (Cookie)auth2.getCredentials();
        //check tokenRepository if the refreshToken exists

        RefreshTokenData foundRefreshTokenData=null;
        try {
           foundRefreshTokenData = tokenManager.readRefreshToken(refreshToken.getValue());
        } catch(EmptyResultDataAccessException e){
            log.error("given refresh token value does not exist in db",e);
            throw new BadCredentialsException("invalid refresh token");
        } catch(NonTransientDataAccessException e){
            log.error("invalid token",e);
            throw new BadCredentialsException("invalid refresh token");
        } catch(TransientDataAccessException e){
            log.error("db error",e);
            throw new InternalAuthenticationServiceException("db error");
        }


        Date dateNow = Date.from(clock.instant());
        if(!clock.instant().isBefore(foundRefreshTokenData.getExpiryDate().toInstant())){
            log.error("expired refresh token");
            throw new BadCredentialsException("invalid refresh token");
        }
        String username = foundRefreshTokenData.getUsername();

        SecurityUser securityUser= (SecurityUser)userService.loadUserByUsername(username);

        Collection<? extends GrantedAuthority> a = Arrays.stream(foundRefreshTokenData.getRoles().split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toCollection(HashSet::new));

        tokenManager.deleteToken(foundRefreshTokenData.getRefreshTokenString());
        return new RefreshTokenAuthentication(securityUser,refreshToken,a);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(RefreshTokenAuthentication.class);
    }







}
