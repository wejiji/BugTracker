package com.example.security2pro.authorization;

import com.example.security2pro.authentication.UserAndProjectRoleAuthenticationMock;
import com.example.security2pro.authentication.jwt.ProjectRoles;
import com.example.security2pro.databuilders.IssueTestDataBuilder;
import com.example.security2pro.databuilders.ProjectTestDataBuilder;
import com.example.security2pro.databuilders.UserTestDataBuilder;
import com.example.security2pro.domain.enums.UserRole;
import com.example.security2pro.domain.model.*;
import com.example.security2pro.domain.model.auth.SecurityUser;
import com.example.security2pro.domain.model.issue.Comment;
import com.example.security2pro.domain.model.issue.Issue;
import com.example.security2pro.repository.CommentRepositoryFake;
import com.example.security2pro.repository.ProjectMemberRepositoryFake;
import com.example.security2pro.repository.repository_interfaces.CommentRepository;
import com.example.security2pro.repository.repository_interfaces.ProjectMemberRepository;
import com.example.security2pro.service.authorization.AuthorPermissionEvaluator;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import java.util.HashSet;
import java.util.Set;

import static com.example.security2pro.authorization.ProjectMemberPermissionEvaluatorTest.projectIdForAuthorization;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class AuthorPermissionEvalutorTest {

    private CommentRepository commentRepository
            = new CommentRepositoryFake();

    private ProjectMemberRepository projectMemberRepository
            = new ProjectMemberRepositoryFake();

    private AuthorPermissionEvaluator authorPermissionEvaluator
            =new AuthorPermissionEvaluator(commentRepository, projectMemberRepository);


    @Test
    public void supports_returnsTrueForCommentType(){
        assertTrue(
                authorPermissionEvaluator.supports("comment")
        );
    }

    @Test
    public void supports_returnsFalseForOthers(){
        assertFalse(
                authorPermissionEvaluator.supports("other")
        );
    }

    @Test
    public void supports_returnsFalseForAnyObject(){
        assertFalse(
                authorPermissionEvaluator.supports(new Object()));
    }


    @Test
    public void hasPermission_success(){

        Project project = new ProjectTestDataBuilder()
                .withId(Long.valueOf(projectIdForAuthorization))
                .build();

        Issue issue = new IssueTestDataBuilder()
                .withId(90L)
                .withProject(project)
                .build();

        Comment comment = Comment.createCommentWithCreatorSet(
                1L
                ,issue
                ,"comment description"
                ,"testCreator");

        User user = new UserTestDataBuilder()
                .withId(30L)
                .withUsername("testCreator")
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_MEMBER))
                .build();

        comment = commentRepository.save(comment);

        SecurityUser securityUser = new SecurityUser(user);

        ProjectRoles projectRoles = new ProjectRoles(projectIdForAuthorization,"ROLE_PROJECT_MEMBER");

        Authentication userAndProjectRoleAuthentication =
                new UserAndProjectRoleAuthenticationMock(securityUser, new HashSet<>(Set.of(projectRoles)));

        boolean evaluationResult = authorPermissionEvaluator
                .hasPermission(
                        userAndProjectRoleAuthentication
                        , 1L // comment id
                        , "comment"
                        , "author");

        assertTrue(evaluationResult);
    }


    @Test
    public void hasPermission_failGivenWrongAuthor(){

        Project project = new ProjectTestDataBuilder()
                .withId(10L)
                .build();

        Issue issue = new IssueTestDataBuilder()
                .withId(90L)
                .withProject(project)
                .build();

        Comment comment = Comment.createCommentWithCreatorSet(
                1L
                ,issue
                ,"comment description"
                ,"testCreator");

        User user = new UserTestDataBuilder()
                .withId(30L)
                .withUsername("notTheCreator")
                .build();


        comment = commentRepository.save(comment);



        SecurityUser securityUser = new SecurityUser(user);

        ProjectRoles projectRoles = new ProjectRoles(String.valueOf(10L),"ROLE_PROJECT_MEMBER");

        Authentication userAndProjectRoleAuthentication =
                new UserAndProjectRoleAuthenticationMock(securityUser, new HashSet<>(Set.of(projectRoles)));

        boolean evaluationResult = authorPermissionEvaluator
                .hasPermission(
                        userAndProjectRoleAuthentication
                        , 1L // comment id
                        , "comment"
                        , "author");

        assertFalse(evaluationResult);
    }




}
