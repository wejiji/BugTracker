package com.example.bugtracker.smalltest.authorization;


import com.example.bugtracker.domain.model.issue.Issue;
import com.example.bugtracker.service.authorization.ProjectMemberPermissionEvaluator;
import com.example.bugtracker.fake.authentication.UserAndProjectRoleAuthenticationMock;
import com.example.bugtracker.authentication.jwt.ProjectRoles;
import com.example.bugtracker.databuilders.*;
import com.example.bugtracker.domain.enums.ProjectMemberRole;
import com.example.bugtracker.domain.enums.UserRole;
import com.example.bugtracker.domain.model.*;
import com.example.bugtracker.domain.model.auth.SecurityUser;
import com.example.bugtracker.dto.issue.authorization.CreateDtoWithProjectId;
import com.example.bugtracker.dto.projectmember.ProjectMemberCreateDto;
import com.example.bugtracker.fake.repository.IssueRepositoryFake;
import com.example.bugtracker.fake.repository.ProjectMemberRepositoryFake;
import com.example.bugtracker.fake.repository.SprintRepositoryFake;
import com.example.bugtracker.repository.repository_interfaces.IssueRepository;
import com.example.bugtracker.repository.repository_interfaces.ProjectMemberRepository;
import com.example.bugtracker.repository.repository_interfaces.SprintRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.core.Authentication;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectMemberPermissionEvaluatorTest {

    private final ProjectMemberRepository projectMemberRepository = new ProjectMemberRepositoryFake();
    private final SprintRepository sprintRepository = new SprintRepositoryFake();

    private final IssueRepository issueRepository = new IssueRepositoryFake();

    private final ProjectMemberPermissionEvaluator projectMemberPermissionEvaluator
            = new ProjectMemberPermissionEvaluator(projectMemberRepository, sprintRepository, issueRepository);

    @Test
    void supports_returnsTrue_givenCreateDtoWithProjectIdInstances() {
        CreateDtoWithProjectId createDtoWithProjectId
                = new CreateDtoWithProjectId() {
            @Override
            public Optional<Long> getProjectId() {
                return Optional.empty();
            }
        };

        assertTrue(
                projectMemberPermissionEvaluator.supports(createDtoWithProjectId));
    }

    @Test
    void supports_returnsFalse_givenOtherObjects() {
        assertFalse(
                projectMemberPermissionEvaluator.supports(new Object()));
    }


    private static Object[] argsForPMPermissionEvaluatorSupportsMethodTest() {
        return new Object[]{
                new Object[]{"issue"},
                new Object[]{"sprint"},
                new Object[]{"project"},
                new Object[]{"projectMember"}
        };
    }

    @ParameterizedTest
    @MethodSource("argsForPMPermissionEvaluatorSupportsMethodTest")
    void supports_true_givenValidTargetTypes(String targetType) {
        assertTrue(
                projectMemberPermissionEvaluator.supports(targetType));
    }

    @Test
    void supports_false_givenOtherTypes() {
        assertFalse(
                projectMemberPermissionEvaluator.supports("notInTargetType"));
    }

    @Test
    void hasPermission_returnsTrue_givenAuthorizedUserForProjectWithProvidedId() {
        //Setup
        User user = new UserTestDataBuilder()
                .withId(30L)
                .withUsername("testUsername")
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_MEMBER))
                .build();

        SecurityUser securityUser = new SecurityUser(user);

        ProjectRoles projectRoles = new ProjectRoles(String.valueOf(10L), "ROLE_PROJECT_MEMBER");

        Authentication userAndProjectRoleAuthentication =
                new UserAndProjectRoleAuthenticationMock(securityUser, new HashSet<>(Set.of(projectRoles)));

        //Execution
        boolean evaluationResult = projectMemberPermissionEvaluator
                .hasPermission(
                        userAndProjectRoleAuthentication
                        , 10L
                        , "project"
                        , "ROLE_PROJECT_MEMBER");

        //Assertions
        assertTrue(evaluationResult);
    }

    @Test
    void hasPermission_returnsTrue_givenAuthorizedUserForSprintWithProvidedId() {
        //Setup
        Project project = new ProjectTestDataBuilder()
                .withId(10L)
                .build();

        Sprint sprint = new SprintTestDataBuilder()
                .withId(80L)
                .withProject(project)
                .build();

        User user = new UserTestDataBuilder()
                .withId(30L)
                .withUsername("testUsername")
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_MEMBER))
                .build();

        sprint = sprintRepository.save(sprint);

        SecurityUser securityUser = new SecurityUser(user);

        ProjectRoles projectRoles = new ProjectRoles(String.valueOf(10L), "ROLE_PROJECT_MEMBER");

        Authentication userAndProjectRoleAuthentication =
                new UserAndProjectRoleAuthenticationMock(securityUser, new HashSet<>(Set.of(projectRoles)));

        //Execution
        boolean evaluationResult = projectMemberPermissionEvaluator
                .hasPermission(
                        userAndProjectRoleAuthentication
                        , 80L
                        , "sprint"
                        , "ROLE_PROJECT_MEMBER");

        //Assertions
        assertTrue(evaluationResult);
    }

    @Test
    void hasPermission_returnsTrue_givenAuthorizedUserForIssueWithProvidedId() {
        //Setup
        Project project = new ProjectTestDataBuilder()
                .withId(10L)
                .build();

        Issue issue = new IssueTestDataBuilder()
                .withId(90L)
                .withProject(project)
                .build();

        User user = new UserTestDataBuilder()
                .withId(30L)
                .withUsername("testUsername")
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_MEMBER))
                .build();

        issue = issueRepository.save(issue);

        SecurityUser securityUser = new SecurityUser(user);

        ProjectRoles projectRoles = new ProjectRoles(String.valueOf(10L), "ROLE_PROJECT_MEMBER");

        Authentication userAndProjectRoleAuthentication =
                new UserAndProjectRoleAuthenticationMock(securityUser, new HashSet<>(Set.of(projectRoles)));

        //Execution
        boolean evaluationResult = projectMemberPermissionEvaluator
                .hasPermission(
                        userAndProjectRoleAuthentication
                        , 90L
                        , "issue"
                        , "ROLE_PROJECT_MEMBER");

        //Assertions
        assertTrue(evaluationResult);
    }

    @Test
    void hasPermission_returnsTrue_givenAuthorizedUserForProjectMemberWithProvidedId() {
        //Setup
        Project project = new ProjectTestDataBuilder()
                .withId(10L)
                .build();
        //logged in user
        User user = new UserTestDataBuilder()
                .withId(30L)
                .withUsername("testUsername")
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_MEMBER))
                .build();

        ProjectMember projectMember = new ProjectMemberTestDataBuilder()
                .withId(20L)
                .withProject(project)
                .withUser(user)
                .withAuthorities(Set.of(ProjectMemberRole.ROLE_PROJECT_LEAD)) //role that matches permission
                .build();
        //target user who owns resources
        User targetUser = new UserTestDataBuilder()
                .withId(23L)
                .withUsername("targetUsername")
                .build();

        ProjectMember targetProjectMember = new ProjectMemberTestDataBuilder()
                .withId(23L)
                .withProject(project)
                .withUser(targetUser)
                .build();
        projectMember = projectMemberRepository.save(projectMember);
        targetProjectMember = projectMemberRepository.save(targetProjectMember);

        SecurityUser securityUser = new SecurityUser(user);

        ProjectRoles projectRoles = new ProjectRoles(String.valueOf(10L), "ROLE_PROJECT_LEAD");

        Authentication userAndProjectRoleAuthentication =
                new UserAndProjectRoleAuthenticationMock(securityUser, new HashSet<>(Set.of(projectRoles)));

        //Execution
        boolean evaluationResult = projectMemberPermissionEvaluator
                .hasPermission(
                        userAndProjectRoleAuthentication
                        , 23L
                        , "projectMember"
                        , "ROLE_PROJECT_LEAD");

        //Assertions
        assertTrue(evaluationResult);
    }


    @Test
    void hasPermission_returnsFalse_givenUnauthorizedUser() {
        //Setup
        Project project = new ProjectTestDataBuilder()
                .withId(10L)
                .build();

        User user = new UserTestDataBuilder()
                .withId(30L)
                .withUsername("testUsername")
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_MEMBER))
                .build();

        SecurityUser securityUser = new SecurityUser(user);

        ProjectRoles projectRoles = new ProjectRoles(String.valueOf(10L), "ROLE_PROJECT_MEMBER");

        Authentication userAndProjectRoleAuthentication =
                new UserAndProjectRoleAuthenticationMock(securityUser, new HashSet<>(Set.of(projectRoles)));

        //Execution
        boolean evaluationResult = projectMemberPermissionEvaluator
                .hasPermission(
                        userAndProjectRoleAuthentication
                        , 23L
                        , "projectMember"
                        , "ROLE_PROJECT_LEAD");

        //Assertions
        assertFalse(evaluationResult);
    }


    @Test
    void hasPermission_returnsTrue_givenAuthorizedUserForCreateDtoImplementations() {
        //Setup
        //logged in user
        User user = new UserTestDataBuilder()
                .withId(30L)
                .withUsername("testUsername")
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_MEMBER))
                .build();

        CreateDtoWithProjectId createDtoWithProjectId
                = new ProjectMemberCreateDto(10L
                , "targetUsername"
                , Set.of(ProjectMemberRole.ROLE_PROJECT_MEMBER));

        SecurityUser securityUser = new SecurityUser(user);

        ProjectRoles projectRoles = new ProjectRoles(String.valueOf(10L), "ROLE_PROJECT_LEAD");

        Authentication userAndProjectRoleAuthentication =
                new UserAndProjectRoleAuthenticationMock(securityUser, new HashSet<>(Set.of(projectRoles)));

        //Execution
        boolean evaluationResult = projectMemberPermissionEvaluator
                .hasPermission(
                        userAndProjectRoleAuthentication
                        , createDtoWithProjectId
                        , "ROLE_PROJECT_LEAD");

        //Assertions
        assertTrue(evaluationResult);
    }

}
