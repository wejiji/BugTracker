package com.example.bugtracker.smalltest.authorization;

import com.example.bugtracker.fake.authentication.AuthenticationFake;
import com.example.bugtracker.databuilders.UserTestDataBuilder;
import com.example.bugtracker.domain.model.auth.SecurityUser;
import com.example.bugtracker.fake.authorization.permissionevaluator.AObject;
import com.example.bugtracker.fake.authorization.permissionevaluator.AObjectTrueCustomPermissionEvaluator;
import com.example.bugtracker.fake.authorization.permissionevaluator.BObject;
import com.example.bugtracker.fake.authorization.permissionevaluator.BObjectFalseCustomPermissionEvaluator;
import com.example.bugtracker.service.authorization.CustomPermissionEvaluator;
import com.example.bugtracker.service.authorization.DelegetingPermissionEvaluator;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DelegatingPermissionEvaluatorTest {

    private CustomPermissionEvaluator ATrueEvaluator
            = new AObjectTrueCustomPermissionEvaluator();

    private CustomPermissionEvaluator BFalseEvaluator
            = new BObjectFalseCustomPermissionEvaluator();

    private final Set<CustomPermissionEvaluator> permissionEvaluatorSet
            = Set.of(ATrueEvaluator, BFalseEvaluator);

    DelegetingPermissionEvaluator delegetingPermissionEvaluatorWithEvaluators
            = new DelegetingPermissionEvaluator(permissionEvaluatorSet);

    DelegetingPermissionEvaluator delegetingPermissionEvaluatorWithNoEvaluators
            = new DelegetingPermissionEvaluator(Collections.emptySet());

    @Test
    void testWhenNoEvaluatorsAvailable() {
        Authentication authentication = new AuthenticationFake(new SecurityUser(new UserTestDataBuilder().build()), true);
        assertFalse(delegetingPermissionEvaluatorWithNoEvaluators.hasPermission(authentication, new AObject(), "permission"));

    }

    @Test
    void testWithA() {
        Authentication authentication = new AuthenticationFake(new SecurityUser(new UserTestDataBuilder().build()), true);
        assertTrue(delegetingPermissionEvaluatorWithEvaluators.hasPermission(authentication, new AObject(), "permission"));
        assertTrue(delegetingPermissionEvaluatorWithEvaluators.hasPermission(authentication, "Aid", "A", "permission"));
    }

    @Test
    void testWithB() {
        Authentication authentication = new AuthenticationFake(new SecurityUser(new UserTestDataBuilder().build()), true);
        assertFalse(delegetingPermissionEvaluatorWithEvaluators.hasPermission(authentication, new BObject(), "permission"));
        assertFalse(delegetingPermissionEvaluatorWithEvaluators.hasPermission(authentication, "Bid", "B", "permission"));
    }


}
