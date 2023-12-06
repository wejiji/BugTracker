package com.example.security2pro.authentication.refresh;


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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RefreshTokenAuthenticationProvider implements AuthenticationProvider {


    private final TokenManager tokenManager;

    private final UserService userService;


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

        Date date= new Date(); // 이부분 다시 쓸것
        if(!date.toInstant().isBefore(foundRefreshTokenData.getExpiryDate().toInstant())){// 아직 만료되지 않았는지- date으로 비교
            //만료되었다면
            log.error("expired refresh token");
            throw new BadCredentialsException("invalid refresh token");
        }
        //만료안되었다면-그리고 부적절한게 쓰인것이 아니라면 -  오케이
        String username = foundRefreshTokenData.getUsername();
        // 로테이션의 경우 새로 리프레시 할때마다 기존의 리프레시 토큰은 만료시켜 버려야 한다.. 어떻게 할까???


        SecurityUser securityUser= (SecurityUser)userService.loadUserByUsername(username);
        //has to be fixed - !!!! use foundRefreshToken's authorities.


        Collection<? extends GrantedAuthority> a = securityUser.getAuthorities();
        //credentials 는 null 로 처리해버리고 되돌려주기. authentication은 완료된것으로 아래 생성자에서 설정

        tokenManager.deleteToken(foundRefreshTokenData.getRefreshTokenString());
        return new RefreshTokenAuthentication(securityUser,refreshToken,a);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(RefreshTokenAuthentication.class);
    }







}
