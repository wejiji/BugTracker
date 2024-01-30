package com.example.security2pro.springtest.methodsecurity.securitycontextsetter;


import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserWithJwtSecurityContextFactory.class)
public @interface WithMockCustomUserWithJwt {

    String username() default "projectLead";

    String name() default "projectLead";
}