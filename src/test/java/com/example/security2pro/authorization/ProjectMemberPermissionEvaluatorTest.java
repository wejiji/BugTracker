package com.example.security2pro.authorization;


import com.example.security2pro.domain.model.issue.Issue;
import com.example.security2pro.service.authorization.ProjectMemberPermissionEvaluator;
import com.example.security2pro.authentication.UserAndProjectRoleAuthenticationMock;
import com.example.security2pro.authentication.jwt.ProjectRoles;
import com.example.security2pro.databuilders.*;
import com.example.security2pro.domain.enums.ProjectMemberRole;
import com.example.security2pro.domain.enums.UserRole;
import com.example.security2pro.domain.model.*;
import com.example.security2pro.domain.model.auth.SecurityUser;
import com.example.security2pro.dto.issue.authorization.CreateDtoWithProjectId;
import com.example.security2pro.dto.projectmember.ProjectMemberCreateDto;
import com.example.security2pro.repository.IssueRepositoryFake;
import com.example.security2pro.repository.ProjectMemberRepositoryFake;
import com.example.security2pro.repository.SprintRepositoryFake;
import com.example.security2pro.repository.repository_interfaces.IssueRepository;
import com.example.security2pro.repository.repository_interfaces.ProjectMemberRepository;
import com.example.security2pro.repository.repository_interfaces.SprintRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.core.Authentication;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectMemberPermissionEvaluatorTest {

    public static String projectIdForAuthorization = String.valueOf(10L);

    private ProjectMemberRepository projectMemberRepository = new ProjectMemberRepositoryFake();
    private SprintRepository sprintRepository= new SprintRepositoryFake();

    private IssueRepository issueRepository = new IssueRepositoryFake();

    private ProjectMemberPermissionEvaluator projectMemberPermissionEvaluator
            =new ProjectMemberPermissionEvaluator(projectMemberRepository,sprintRepository,issueRepository);


    @Test
    public void supports_returnsTrue_forCreateDtoWithProjectIdInstances(){
        CreateDtoWithProjectId createDtoWithProjectId
                =new CreateDtoWithProjectId() {
            @Override
            public Optional<Long> getProjectId() {
                return Optional.empty();
            }
        };

        assertTrue(
                projectMemberPermissionEvaluator.supports(createDtoWithProjectId));
    }

    @Test
    public void supports_returnsFalse_forOtherObjects(){
        assertFalse(
                projectMemberPermissionEvaluator.supports(new Object()));
    }


    private static Object[] argsForPMPermissionEvaluatorSupportsMethodTest(){
        return new Object[]{
                new Object[]{"issue"},
                new Object[]{"sprint"},
                new Object[]{"project"},
                new Object[]{"projectMember"}
        };
    }

    @ParameterizedTest
    @MethodSource("argsForPMPermissionEvaluatorSupportsMethodTest")
    public void supports_true_forValidTargetTypes(String targetType){
        assertTrue(
                projectMemberPermissionEvaluator.supports(targetType));
    }

    @Test
    public void supports_false_forOtherTypes(){
        assertFalse(
                projectMemberPermissionEvaluator.supports("notInTargetType"));
    }



    @Test
    public void hasPermission_fromIdAndType_success_targetTypeProject(){

        User user = new UserTestDataBuilder()
                .withId(30L)
                .withUsername("testUsername")
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_MEMBER))
                .build();

        SecurityUser securityUser = new SecurityUser(user);

        ProjectRoles projectRoles = new ProjectRoles(projectIdForAuthorization,"ROLE_PROJECT_MEMBER");


        Authentication userAndProjectRoleAuthentication =
                new UserAndProjectRoleAuthenticationMock(securityUser, new HashSet<>(Set.of(projectRoles)));

        boolean evaluationResult = projectMemberPermissionEvaluator
                .hasPermission(
                        userAndProjectRoleAuthentication
                        , 10L
                        , "project"
                        , "ROLE_PROJECT_MEMBER");

        assertTrue(evaluationResult);
    }



    @Test
    public void hasPermission_fromIdAndType_success_targetTypeSprint(){

        Project project = new ProjectTestDataBuilder()
                .withId(Long.valueOf(projectIdForAuthorization))
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

        ProjectRoles projectRoles = new ProjectRoles(projectIdForAuthorization,"ROLE_PROJECT_MEMBER");

        Authentication userAndProjectRoleAuthentication =
                new UserAndProjectRoleAuthenticationMock(securityUser, new HashSet<>(Set.of(projectRoles)));

        boolean evaluationResult = projectMemberPermissionEvaluator
                .hasPermission(
                        userAndProjectRoleAuthentication
                        , 80L
                        , "sprint"
                        , "ROLE_PROJECT_MEMBER");

        assertTrue(evaluationResult);
    }


    @Test
    public void hasPermission_fromIdAndType_success_targetTypeIssue(){

        Project project = new ProjectTestDataBuilder()
                .withId(Long.valueOf(projectIdForAuthorization))
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

        ProjectRoles projectRoles = new ProjectRoles(projectIdForAuthorization,"ROLE_PROJECT_MEMBER");

        Authentication userAndProjectRoleAuthentication =
                new UserAndProjectRoleAuthenticationMock(securityUser, new HashSet<>(Set.of(projectRoles)));

        boolean evaluationResult = projectMemberPermissionEvaluator
                .hasPermission(
                        userAndProjectRoleAuthentication
                        , 90L
                        , "issue"
                        , "ROLE_PROJECT_MEMBER");

        assertTrue(evaluationResult);
    }


    @Test
    public void hasPermission_fromIdAndType_success_targetTypeProjectMember(){

        Project project = new ProjectTestDataBuilder()
                .withId(Long.valueOf(projectIdForAuthorization))
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
//                .withAuthorities(Set.of(Role.ROLE_PROJECT_MEMBER))
                .build();

        projectMember = projectMemberRepository.save(projectMember);
        targetProjectMember = projectMemberRepository.save(targetProjectMember);

        SecurityUser securityUser = new SecurityUser(user);

        ProjectRoles projectRoles = new ProjectRoles(projectIdForAuthorization,"ROLE_PROJECT_LEAD");

        Authentication userAndProjectRoleAuthentication =
                new UserAndProjectRoleAuthenticationMock(securityUser, new HashSet<>(Set.of(projectRoles)));

        boolean evaluationResult = projectMemberPermissionEvaluator
                .hasPermission(
                        userAndProjectRoleAuthentication
                        , 23L
                        , "projectMember"
                        , "ROLE_PROJECT_LEAD");

        assertTrue(evaluationResult);
    }


    @Test
    public void hasPermission_fromIdAndType_failGivenInvalidPermission(){

        Project project = new ProjectTestDataBuilder()
                .withId(Long.valueOf(projectIdForAuthorization))
                .build();

        User user = new UserTestDataBuilder()
                .withId(30L)
                .withUsername("testUsername")
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_MEMBER))
                .build();


        SecurityUser securityUser = new SecurityUser(user);

        ProjectRoles projectRoles = new ProjectRoles(projectIdForAuthorization,"ROLE_PROJECT_MEMBER");

        Authentication userAndProjectRoleAuthentication =
                new UserAndProjectRoleAuthenticationMock(securityUser, new HashSet<>(Set.of(projectRoles)));

        boolean evaluationResult = projectMemberPermissionEvaluator
                .hasPermission(
                        userAndProjectRoleAuthentication
                        , 23L
                        , "projectMember"
                        , "ROLE_PROJECT_LEAD");

        assertFalse(evaluationResult);
    }


    @Test
    public void hasPermission_fromObject(){
//        Project project = new ProjectTestDataBuilder()
//                .withId(10L)
//                .build();
        //logged in user
        User user = new UserTestDataBuilder()
                .withId(30L)
                .withUsername("testUsername")
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_MEMBER))
                .build();


        CreateDtoWithProjectId createDtoWithProjectId
                = new ProjectMemberCreateDto(Long.valueOf(projectIdForAuthorization),"targetUsername",Set.of(ProjectMemberRole.ROLE_PROJECT_MEMBER));


        SecurityUser securityUser = new SecurityUser(user);

        ProjectRoles projectRoles = new ProjectRoles(projectIdForAuthorization,"ROLE_PROJECT_LEAD");

        Authentication userAndProjectRoleAuthentication =
                new UserAndProjectRoleAuthenticationMock(securityUser, new HashSet<>(Set.of(projectRoles)));

        boolean evaluationResult = projectMemberPermissionEvaluator
                .hasPermission(
                        userAndProjectRoleAuthentication
                        , createDtoWithProjectId
                        , "ROLE_PROJECT_LEAD");

        assertTrue(evaluationResult);


    }

}
