package com.example.bugtracker.smalltest.domain.model;

import com.example.bugtracker.databuilders.ProjectMemberTestDataBuilder;
import com.example.bugtracker.databuilders.ProjectTestDataBuilder;
import com.example.bugtracker.databuilders.UserTestDataBuilder;
import com.example.bugtracker.domain.enums.ProjectMemberRole;
import com.example.bugtracker.domain.model.Project;
import com.example.bugtracker.domain.model.ProjectMember;
import com.example.bugtracker.domain.model.User;
import com.example.bugtracker.exception.directmessageconcretes.ProjectMemberInvalidRoleArgumentException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ProjectMemberTest {

    //When 'ProjectMemberTestData' is instantiated, each field is initialized with default value when no argument is passed for the field.


    private static Object[] getProjectMemberCreationParams() {
        /*
         * When passed an empty set or null for an 'authorities' field argument,
         * default value is assigned, which is 'ROLE_PROJECT_MEMBER'.
         * Tests with four successful cases.
         */
        return new Object[]{
                new Object[]{Collections.emptySet(), Set.of(ProjectMemberRole.ROLE_PROJECT_MEMBER)},
                new Object[]{null, Set.of(ProjectMemberRole.ROLE_PROJECT_MEMBER)},
                new Object[]{Set.of(ProjectMemberRole.ROLE_PROJECT_MEMBER, ProjectMemberRole.ROLE_PROJECT_TEST)
                        , Set.of(ProjectMemberRole.ROLE_PROJECT_MEMBER, ProjectMemberRole.ROLE_PROJECT_TEST)},
                new Object[]{Set.of(ProjectMemberRole.ROLE_PROJECT_LEAD), Set.of(ProjectMemberRole.ROLE_PROJECT_LEAD)}
        };
    }


    @ParameterizedTest
    @MethodSource("getProjectMemberCreationParams")
    void createProjectMember_createsAndReturnsProjectMember_givenFieldValues(
            Set<ProjectMemberRole> authorities
            , Set<ProjectMemberRole> resultAuthorities) {

        //Setup
        Project project = new ProjectTestDataBuilder().build();
        User user = new UserTestDataBuilder().build();

        //Execution
        ProjectMember projectMember = ProjectMember.createProjectMember(
                null
                , project
                , user
                , authorities);

        //Assertions
        assertNull(projectMember.getId());
        assertThat(projectMember.getProject()).usingRecursiveComparison().isEqualTo(project);
        assertThat(projectMember.getUser()).usingRecursiveComparison().isEqualTo(user);
        assertEquals(resultAuthorities, projectMember.getAuthorities());
    }


    @Test
    void createProjectMember_throwsException_givenBothProjectMemberAndLeadRoles() {
        /*
         * Tests if an exception is thrown
         * when 'authorities' collection field contains both 'ROLE_PROJECT_MEMBER' and 'ROLE_PROJECT_LEAD'.
         */

        //Setup
        Project project = new ProjectTestDataBuilder().build();
        User user = new UserTestDataBuilder().build();
        Set<ProjectMemberRole> authorities
                = Set.of(ProjectMemberRole.ROLE_PROJECT_MEMBER, ProjectMemberRole.ROLE_PROJECT_LEAD);

        //Exception & Assertions
        assertThrows(ProjectMemberInvalidRoleArgumentException.class,
                () -> ProjectMember.createProjectMember(1L, project, user, authorities));
    }


    @Test
    void updateRole_throwsException_givenEmptyAuthorities() {
        // Tests if an exception is thrown when no authority is passed.

        //Setup
        ProjectMember projectMember = new ProjectMemberTestDataBuilder().build();

        //Execution & Assertions
        Set<ProjectMemberRole> authorities = Collections.emptySet();
        assertThrows(ProjectMemberInvalidRoleArgumentException.class,
                () -> projectMember.updateRole(authorities));
    }

    @Test
    void updateRole_throwsException_givenBothProjectMemberAndLeadRoles() {
        /*
         * Tests if an exception is thrown
         * when 'authorities' collection field contains both 'ROLE_PROJECT_MEMBER' and 'ROLE_PROJECT_LEAD'.
         */

        //Setup
        ProjectMember projectMember = new ProjectMemberTestDataBuilder().build();

        //Execution & Assertions
        Set<ProjectMemberRole> authorities = Set.of(ProjectMemberRole.ROLE_PROJECT_MEMBER, ProjectMemberRole.ROLE_PROJECT_LEAD);
        assertThrows(ProjectMemberInvalidRoleArgumentException.class,
                () -> projectMember.updateRole(authorities));
    }

    @Test
    void updateRole_updatesProjectMemberAuthorities_givenUpdatedRoles() {
        // Tests if 'authorities' field is updated correctly

        //Setup
        ProjectMember projectMember = new ProjectMemberTestDataBuilder()
                .withAuthorities(Set.of(ProjectMemberRole.ROLE_PROJECT_MEMBER))
                .build();

        //Execution
        projectMember.updateRole(Set.of(ProjectMemberRole.ROLE_PROJECT_LEAD));

        //Assertions
        ProjectMember expectedProjectMember = new ProjectMemberTestDataBuilder()
                .withAuthorities(Set.of(ProjectMemberRole.ROLE_PROJECT_LEAD))
                .build();

        assertThat(projectMember)
                .usingRecursiveComparison()
                .isEqualTo(expectedProjectMember);
    }

}