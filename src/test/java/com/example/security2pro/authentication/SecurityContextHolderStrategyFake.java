package com.example.security2pro.authentication;


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
