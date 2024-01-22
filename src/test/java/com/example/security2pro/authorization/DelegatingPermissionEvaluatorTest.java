package com.example.security2pro.authorization;

import com.example.security2pro.authentication.AuthenticationFake;
import com.example.security2pro.databuilders.UserTestDataBuilder;
import com.example.security2pro.domain.model.auth.SecurityUser;
import com.example.security2pro.service.authorization.CustomPermissionEvaluator;
import com.example.security2pro.service.authorization.DelegetingPermissionEvaluator;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DelegatingPermissionEvaluatorTest {

    private CustomPermissionEvaluator ATrueEvaluator
            = new AObjectTrueCustomPermissionEvaluator();

    private CustomPermissionEvaluator BFalseEvaluator
            = new BObjectFalseCustomPermissionEvaluator();

    private final Set<CustomPermissionEvaluator> permissionEvaluatorSet
            = Set.of(ATrueEvaluator,BFalseEvaluator);

    DelegetingPermissionEvaluator delegetingPermissionEvaluatorWithEvaluators
            = new DelegetingPermissionEvaluator(permissionEvaluatorSet);

    DelegetingPermissionEvaluator delegetingPermissionEvaluatorWithNoEvaluators
            = new DelegetingPermissionEvaluator(Collections.emptySet());
    @Test
    public void testWhenNoEvaluatorsAvailable(){
        Authentication authentication = new AuthenticationFake(new SecurityUser(new UserTestDataBuilder().build()),true);
        assertFalse(delegetingPermissionEvaluatorWithNoEvaluators.hasPermission(authentication,new AObject(),"permission"));

    }

    @Test
    public void testWithA(){
        Authentication authentication = new AuthenticationFake(new SecurityUser(new UserTestDataBuilder().build()),true);
        assertTrue(delegetingPermissionEvaluatorWithEvaluators.hasPermission(authentication, new AObject(),"permission"));
        assertTrue(delegetingPermissionEvaluatorWithEvaluators.hasPermission(authentication,"Aid","A","permission"));
    }

    @Test
    public void testWithB(){
        Authentication authentication = new AuthenticationFake(new SecurityUser(new UserTestDataBuilder().build()),true);
        assertFalse(delegetingPermissionEvaluatorWithEvaluators.hasPermission(authentication,new BObject(),"permission"));
        assertFalse(delegetingPermissionEvaluatorWithEvaluators.hasPermission(authentication,"Bid", "B","permission"));
    }




}
