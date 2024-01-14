package com.example.security2pro.domain.model;

import com.example.security2pro.databuilders.ProjectMemberTestDataBuilder;
import com.example.security2pro.databuilders.ProjectTestDataBuilder;
import com.example.security2pro.databuilders.UserTestDataBuilder;
import com.example.security2pro.domain.enums.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ProjectMemberTest {

    private static Object[] getProjectMemberCreationParams(){


        return new Object[]{
                new Object[]{Collections.emptySet(), Set.of(Role.ROLE_PROJECT_MEMBER)},
                new Object[]{ null, Set.of(Role.ROLE_PROJECT_MEMBER)},
                new Object[]{Set.of(Role.ROLE_PROJECT_MEMBER,Role.ROLE_ADMIN), Set.of(Role.ROLE_PROJECT_MEMBER,Role.ROLE_ADMIN)},
                new Object[]{Set.of(Role.ROLE_PROJECT_MEMBER),Set.of(Role.ROLE_PROJECT_MEMBER)}
        };
    }


    @ParameterizedTest
    @MethodSource("getProjectMemberCreationParams")
    void createProjectMember(Set<Role> authorities, Set<Role> resultAuthorities) {
        Project project = new ProjectTestDataBuilder().build();

        User user = new UserTestDataBuilder().build();

        ProjectMember projectMember = ProjectMember.createProjectMember(
                1L
                ,project
                ,user
                ,authorities);

        assertEquals(1L,projectMember.getId());
        assertThat(projectMember.getProject()).usingRecursiveComparison().isEqualTo(project);
        assertThat(projectMember.getUser()).usingRecursiveComparison().isEqualTo(user);
        assertEquals(resultAuthorities, projectMember.getAuthorities());
    }

    @Test
    void updateRole_throwsExceptionWhenRoleInvalid() {
        ProjectMember projectMember = new ProjectMemberTestDataBuilder().build();

        assertThrows(IllegalArgumentException.class,()-> projectMember.updateRole(Set.of(Role.ROLE_TEAM_MEMBER)));

    }

    @Test
    void updateRole_success() {
        ProjectMember projectMember = new ProjectMemberTestDataBuilder().withAuthorities(Set.of(Role.ROLE_PROJECT_MEMBER)).build();

        projectMember.updateRole(Set.of(Role.ROLE_PROJECT_LEAD));
    }


}