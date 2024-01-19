package com.example.security2pro.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestMethodSecurityController {

    @GetMapping("/test-preauth/{projectId}")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public String preauthtest(@PathVariable Long projectId){
        return "test got through! with "+projectId +" project id~~";
    }
}
