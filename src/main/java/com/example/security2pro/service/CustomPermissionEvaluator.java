package com.example.security2pro.service;

import org.springframework.security.access.PermissionEvaluator;

public interface CustomPermissionEvaluator extends PermissionEvaluator {

    public boolean supports(Object object);

    public boolean supports(String targetType);


}
