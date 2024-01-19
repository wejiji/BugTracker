package com.example.security2pro.controller;


import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserWithRefreshTokenSecurityContextFactory.class)
public @interface WithMockCustomUserWithRefreshToken {

    String username() default "admin";

    String name() default "yeaji";
}