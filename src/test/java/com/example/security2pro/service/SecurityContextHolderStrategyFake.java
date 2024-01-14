package com.example.security2pro.service;

import com.example.security2pro.domain.model.auth.SecurityUser;
import org.aspectj.lang.annotation.RequiredTypes;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;


public class SecurityContextHolderStrategyFake implements SecurityContextHolderStrategy {

    private SecurityContext securityContext = new SecurityContextFake();


    @Override
    public void clearContext() {
        securityContext.setAuthentication(null);
    }

    @Override
    public SecurityContext getContext() {
        return securityContext;
    }

    @Override
    public void setContext(SecurityContext context) {
        this.securityContext = context;
    }

    @Override
    public SecurityContext createEmptyContext() {
        return new SecurityContextFake();
    }
}
