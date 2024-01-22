package com.example.security2pro.service.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class DelegetingPermissionEvaluator implements PermissionEvaluator {

    private final Set<CustomPermissionEvaluator> permissionEvaluatorSet;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        Optional<CustomPermissionEvaluator> evaluatorOptional= permissionEvaluatorSet.stream().filter(evaluator -> evaluator.supports(targetDomainObject)).findAny();
        return evaluatorOptional.map(
                permissionEvaluator ->
                        permissionEvaluator.hasPermission(authentication, targetDomainObject, permission))
                .orElse(false);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        Optional<CustomPermissionEvaluator> evaluatorOptional= permissionEvaluatorSet.stream().filter(evaluator -> evaluator.supports(targetType)).findAny();
        return evaluatorOptional.map(
                permissionEvaluator ->
                        permissionEvaluator.hasPermission(authentication, targetId, targetType, permission))
                .orElse(false);
    }
}
