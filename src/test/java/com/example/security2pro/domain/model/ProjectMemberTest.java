package com.example.security2pro.domain.model;

import com.example.security2pro.domain.enums.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ProjectMemberTest {

    private static Object[] getProjectMemberCreationParams(){

        Project project = mock(Project.class);
        User user = mock(User.class);
        Role role = mock(Role.class);
        Role role2 = mock(Role.class);

        return new Object[]{
                new Object[]{project,user, Collections.emptySet(), Set.of(Role.ROLE_PROJECT_MEMBER)},
                new Object[]{project,user, null, Set.of(Role.ROLE_PROJECT_MEMBER)},
                new Object[]{project,user,Set.of(role), Set.of(role)},
                new Object[]{project,user,Set.of(role, role2),Set.of(role, role2)}
        };
    }


    @ParameterizedTest
    @MethodSource("getProjectMemberCreationParams")
    void createProjectMember(Project project, User user, Set<Role> authorities, Set<Role> resultAuthorities) {
        ProjectMember projectMember = ProjectMember.createProjectMember(project,user,authorities);

        assertEquals(project, projectMember.getProject());
        assertEquals(user, projectMember.getUser());
        assertEquals(resultAuthorities, projectMember.getAuthorities());

    }

    @Test
    void updateRole() {
    }
}