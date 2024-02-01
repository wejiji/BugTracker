package com.example.bugtracker.integrationtest.methodsecurity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestMethodSecurityController {

    @GetMapping("/test-preauth/{projectId}")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public String preauthtest(@PathVariable Long projectId, Authentication authentication){
        return "authorization success! with "+projectId +" project id with principal" + authentication.getName();
    }

    @GetMapping("/test-preauth/user-role-test/{projectId}")
    @PreAuthorize("hasRole('TEAM_LEAD')")
    public String preauthtest2(@PathVariable Long projectId, Authentication authentication){
        return "authorization success! with "+projectId +" project id with principal" + authentication.getName();
    }

}
