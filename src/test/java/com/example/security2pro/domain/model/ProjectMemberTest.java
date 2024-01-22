package com.example.security2pro.domain.model;

import com.example.security2pro.databuilders.ProjectMemberTestDataBuilder;
import com.example.security2pro.databuilders.ProjectTestDataBuilder;
import com.example.security2pro.databuilders.UserTestDataBuilder;
import com.example.security2pro.domain.enums.ProjectMemberRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ProjectMemberTest {

    /*
     * exception is supposed to be thrown when ProjectMemberRole does not start with "ROLE_PROJECT_"
     * but that test is covered by ProjectMemberRoleTest class, not by ProjectMemberTest
     *
     * when ProjectMemberTestData is instantiated, each field is initialized with default value when no argument is passed for the field,
     *
     * each test of this class will test at most one method of ProjectMember class
     * every test of this class is a small test
     */

    private static Object[] getProjectMemberCreationParams(){
        /*
        * when passed an empty set or null for an 'authorities' field argument,
        * default value is assigned, which is "ROLE_PROJECT_MEMBER
        * tests with successful four argument cases
        */
        return new Object[]{
                new Object[]{Collections.emptySet(), Set.of(ProjectMemberRole.ROLE_PROJECT_MEMBER)},
                new Object[]{null, Set.of(ProjectMemberRole.ROLE_PROJECT_MEMBER)},
                new Object[]{Set.of(ProjectMemberRole.ROLE_PROJECT_MEMBER, ProjectMemberRole.ROLE_PROJECT_TEST)
                        , Set.of(ProjectMemberRole.ROLE_PROJECT_MEMBER, ProjectMemberRole.ROLE_PROJECT_TEST)},
                new Object[]{Set.of(ProjectMemberRole.ROLE_PROJECT_LEAD),Set.of(ProjectMemberRole.ROLE_PROJECT_LEAD)}
        };
    }


    @ParameterizedTest
    @MethodSource("getProjectMemberCreationParams")
    void createProjectMember_createsAndReturnsProjectMember(Set<ProjectMemberRole> authorities, Set<ProjectMemberRole> resultAuthorities) {
        /* success tests
        * tests also if auto generated id is assigned
        * when null is passed for 'id' field of ProjectMember
        *
        * does not test if a passed id is assigned as expected - should be added in the future
         */

        //Setup
        Project project = new ProjectTestDataBuilder().build();
        User user = new UserTestDataBuilder().build();

        //Execution
        ProjectMember projectMember = ProjectMember.createProjectMember(
                null
                ,project
                ,user
                ,authorities);

        //Assertions
        assertNotNull(projectMember.getId());
        assertThat(projectMember.getProject()).usingRecursiveComparison().isEqualTo(project);
        assertThat(projectMember.getUser()).usingRecursiveComparison().isEqualTo(user);
        assertEquals(resultAuthorities, projectMember.getAuthorities());
    }



    @Test
    void createProjectMember_throwsException_whenBothProjectMemberAndLeadRolesArePassed() {
        /*
         * tests if IllegalArgumentException is thrown
         * when both 'ROLE_PROJECT_MEMBER' and 'ROLE_PROJECT_LEAD' are passed as 'authorities' field argument
         */

        //Setup
        Project project = new ProjectTestDataBuilder().build();
        User user = new UserTestDataBuilder().build();
        Set<ProjectMemberRole> authorities
                = Set.of(ProjectMemberRole.ROLE_PROJECT_MEMBER, ProjectMemberRole.ROLE_PROJECT_LEAD);

        //Exception & Assertions
        assertThrows(IllegalArgumentException.class,
                ()-> ProjectMember.createProjectMember(1L,project,user,authorities));
    }



    @Test
    void updateRole_throwsException_whenEmptyAuthoritiesIsPassed() {
        /*
         * tests if IllegalArgumentException is thrown
         * when no authority is passed
         */

        //Setup
        ProjectMember projectMember = new ProjectMemberTestDataBuilder().build();

        //Execution & Assertions
        Set<ProjectMemberRole> authorities = Collections.emptySet();
        assertThrows(IllegalArgumentException.class,
                ()-> projectMember.updateRole(authorities));
    }

    @Test
    void updateRole_throwsException_whenBothProjectMemberAndLeadRolesArePassed() {
        /*
        * tests if IllegalArgumentException is thrown
        * when both member and lead roles are passed as 'authorities' field argument
        */

        //Setup
        ProjectMember projectMember = new ProjectMemberTestDataBuilder().build();

        //Execution & Assertions
        Set<ProjectMemberRole> authorities = Set.of(ProjectMemberRole.ROLE_PROJECT_MEMBER, ProjectMemberRole.ROLE_PROJECT_LEAD);
        assertThrows(IllegalArgumentException.class,
                ()-> projectMember.updateRole(authorities));
    }

    @Test
    void updateRole_updatesProjectMemberAuthorities() {
        // tests if 'authorities' field is updated correctly
        // success case test

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