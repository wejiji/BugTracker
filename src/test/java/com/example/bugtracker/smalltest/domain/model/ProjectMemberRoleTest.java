package com.example.bugtracker.smalltest.domain.model;

import com.example.bugtracker.authentication.jwt.ProjectRoles;
import com.example.bugtracker.domain.enums.ProjectMemberRole;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectMemberRoleTest {

    @Test
    void startsWith(){
        //Verifies that all the roles of 'ProjectMemberRole' start with 'ROLE_PROJECT'

        assertTrue(Arrays
                .stream(ProjectMemberRole.values())
                .allMatch(role-> role.name().startsWith("ROLE_PROJECT")));
    }

    @Test
    void projectMemberRoleSetEqualityTest() {

        Set<ProjectMemberRole> set1 = new HashSet<>(Set.of(ProjectMemberRole.ROLE_PROJECT_MEMBER));
        Set<ProjectMemberRole> set2 = new HashSet<>(Set.of(ProjectMemberRole.ROLE_PROJECT_MEMBER));

        assertEquals(set1,set2);

        ProjectRoles projectRoles1 = new ProjectRoles(1L, set1);
        ProjectRoles projectRoles2 = new ProjectRoles(1L, set2);

        assertEquals(projectRoles1,projectRoles2);

    }
}
