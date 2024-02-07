package com.example.bugtracker.service.authorization;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Slf4j
public class DelegetingPermissionEvaluator implements PermissionEvaluator {
    /*
     * Delegates authorization to 'CustomPermissionEvaluators'.
     * Will return false if there is no 'CustomPermissionEvaluator' that supports the provided authorization type.
     */

    private final Set<CustomPermissionEvaluator> permissionEvaluatorSet;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {

        log.info("Delegating permission evaluator will find the evaluator that supports the given authentication type from the following evaluators: "+
                 permissionEvaluatorSet.stream().map(permissionEvaluator -> permissionEvaluator.getClass().getSimpleName()).collect(Collectors.joining(", ")));

        Optional<CustomPermissionEvaluator> evaluatorOptional = permissionEvaluatorSet.stream()
                .filter(evaluator -> evaluator.supports(targetDomainObject))
                .findAny();


        log.info("evaluator found present?: "+evaluatorOptional.isPresent());

        return evaluatorOptional.map(
                        permissionEvaluator ->
                                permissionEvaluator.hasPermission(authentication, targetDomainObject, permission))
                .orElse(false);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {

        Optional<CustomPermissionEvaluator> evaluatorOptional = permissionEvaluatorSet.stream()
                .filter(evaluator -> evaluator.supports(targetType))
                .findAny();

        return evaluatorOptional.map(
                        permissionEvaluator ->
                                permissionEvaluator.hasPermission(authentication, targetId, targetType, permission))
                .orElse(false);
    }
}
