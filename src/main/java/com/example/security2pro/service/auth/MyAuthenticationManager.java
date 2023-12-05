package com.example.security2pro.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class MyAuthenticationManager implements AuthenticationManager {

    // 굳이 extend 할 필요가 있을까 싶다.?/?? Authentication Manager 을 설정하도록 요구하므로 냅둬야 할듯.
    private AuthenticationManager authenticationManager;

    public MyAuthenticationManager(List<AuthenticationProvider> authenticationProviders){
        authenticationManager = new ProviderManager(authenticationProviders);
    }


    @Override
    public Authentication authenticate(Authentication authentication){
            return authenticationManager.authenticate(authentication);
    }





}
