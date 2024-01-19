package com.example.security2pro.authorization;

import com.example.security2pro.service.CustomPermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;

public class BObjectFalseCustomPermissionEvaluator implements CustomPermissionEvaluator {
    @Override
    public boolean supports(Object object) {
        return object instanceof BObject;
    }

    @Override
    public boolean supports(String targetType) {
        return targetType.equals("B");
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
