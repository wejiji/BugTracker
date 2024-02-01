package com.example.bugtracker.fake.authorization.permissionevaluator;

import com.example.bugtracker.service.authorization.CustomPermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;

public class AObjectTrueCustomPermissionEvaluator implements CustomPermissionEvaluator {


    @Override
    public boolean supports(Object object) {
        return object instanceof AObject;
    }

    @Override
    public boolean supports(String targetType) {
        return targetType.equals("A");
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return true;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return true;
    }
}
