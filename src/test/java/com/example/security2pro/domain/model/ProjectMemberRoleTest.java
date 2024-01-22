package com.example.security2pro.domain.model;

import com.example.security2pro.domain.enums.ProjectMemberRole;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectMemberRoleTest {

    @Test
    void startsWith(){
        //Verifies that all the roles of 'ProjectMemberRole' start with 'ROLE_PROJECT'

        assertTrue(Arrays
                .stream(ProjectMemberRole.values())
                .allMatch(role-> role.name().startsWith("ROLE_PROJECT")));
    }
}
