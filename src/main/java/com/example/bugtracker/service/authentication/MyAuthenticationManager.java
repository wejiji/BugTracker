package com.example.bugtracker.service.authentication;

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
    private final AuthenticationManager authenticationManager;
    public MyAuthenticationManager(List<AuthenticationProvider> authenticationProviders){
        authenticationManager = new ProviderManager(authenticationProviders);
    }
    @Override
    public Authentication authenticate(Authentication authentication){
            return authenticationManager.authenticate(authentication);
    }





}
