package com.example.security2pro.service;

import com.example.security2pro.ProjectMemberPermissionEvaluator;
import com.example.security2pro.databuilders.ProjectMemberTestDataBuilder;
import com.example.security2pro.databuilders.ProjectTestDataBuilder;
import com.example.security2pro.databuilders.SprintTestDataBuilder;
import com.example.security2pro.databuilders.UserTestDataBuilder;
import com.example.security2pro.domain.enums.Role;
import com.example.security2pro.domain.model.Project;
import com.example.security2pro.domain.model.ProjectMember;
import com.example.security2pro.domain.model.Sprint;
import com.example.security2pro.domain.model.User;
import com.example.security2pro.domain.model.auth.SecurityUser;
import com.example.security2pro.repository.IssueRepositoryFake;
import com.example.security2pro.repository.ProjectMemberRepositoryFake;
import com.example.security2pro.repository.SprintRepositoryFake;
import com.example.security2pro.repository.repository_interfaces.IssueRepository;
import com.example.security2pro.repository.repository_interfaces.ProjectMemberRepository;
import com.example.security2pro.repository.repository_interfaces.SprintRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectMemberPermissionEvaluatorTest {


    private ProjectMemberRepository projectMemberRepository = new ProjectMemberRepositoryFake();
    private SprintRepository sprintRepository= new SprintRepositoryFake();

    private IssueRepository issueRepository = new IssueRepositoryFake();

    private ProjectMemberPermissionEvaluator projectMemberPermissionEvaluator
            =new ProjectMemberPermissionEvaluator(projectMemberRepository,sprintRepository,issueRepository);

    @Test
    public void hasPermission_fromIdAndType_success(){

        Project project = new ProjectTestDataBuilder()
                .withId(10L)
                .build();

        User user = new UserTestDataBuilder()
                .withId(30L)
                .withUsername("testUsername")
                .build();


        ProjectMember projectMember = new ProjectMemberTestDataBuilder()
                .withId(20L)
                .withProject(project)
                .withUser(user)
                .withAuthorities(Set.of(Role.ROLE_PROJECT_MEMBER))
                .build();

        projectMember = projectMemberRepository.save(projectMember);

        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication = new AuthenticationFake(securityUser,true);

        boolean evaluationResult = projectMemberPermissionEvaluator
                .hasPermission(
                        authentication
                        , 10L
                        , "project"
                        , "ROLE_PROJECT_MEMBER");

        assertTrue(evaluationResult);
    }



//    @Test
//    public void hasPermission_fromIdAndType_success_Sprint(){
//
//        Project project = new ProjectTestDataBuilder()
//                .withId(10L)
//                .build();
//
//        Sprint sprint = new SprintTestDataBuilder()
//                .withId(80L)
//                .withProject(project)
//                .build();
//
//        User user = new UserTestDataBuilder()
//                .withId(30L)
//                .withUsername("testUsername")
//                .build();
//
//        ProjectMember projectMember = new ProjectMemberTestDataBuilder()
//                .withId(20L)
//                .withProject(project)
//                .withUser(user)
//                .withAuthorities(Set.of(Role.ROLE_PROJECT_MEMBER))
//                .build();
//
//        projectMember = projectMemberRepository.save(projectMember);
//
//        SecurityUser securityUser = new SecurityUser(user);
//        Authentication authentication = new AuthenticationFake(securityUser,true);
//
//        boolean evaluationResult = projectMemberPermissionEvaluator
//                .hasPermission(
//                        authentication
//                        , 80L
//                        , "sprint"
//                        , "ROLE_PROJECT_MEMBER");
//
//        assertTrue(evaluationResult);
//    }

    @Test
    public void hasPermission_fromIdAndType_failGivenInvalidPermission(){

        Project project = new ProjectTestDataBuilder()
                .withId(10L)
                .build();

        User user = new UserTestDataBuilder()
                .withId(30L)
                .withUsername("testUsername")
                .build();


        ProjectMember projectMember = new ProjectMemberTestDataBuilder()
                .withId(20L)
                .withProject(project)
                .withUser(user)
                .withAuthorities(Set.of(Role.ROLE_PROJECT_MEMBER))
                .build();

        projectMember = projectMemberRepository.save(projectMember);

        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication = new AuthenticationFake(securityUser,true);

        boolean evaluationResult = projectMemberPermissionEvaluator
                .hasPermission(
                        authentication
                        , 10L
                        , "project"
                        , "ROLE_PROJECT_LEAD");

        assertFalse(evaluationResult);
    }


    @Test
    public void hasPermission_fromObject(){


    }

}
