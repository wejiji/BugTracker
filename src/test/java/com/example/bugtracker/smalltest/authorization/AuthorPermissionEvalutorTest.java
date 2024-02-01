package com.example.bugtracker.smalltest.authorization;

import com.example.bugtracker.fake.authentication.JwtTokenManagerImplFake;
import com.example.bugtracker.fake.authentication.UserAndProjectRoleAuthenticationMock;
import com.example.bugtracker.authentication.jwt.ProjectRoles;
import com.example.bugtracker.databuilders.IssueTestDataBuilder;
import com.example.bugtracker.databuilders.ProjectTestDataBuilder;
import com.example.bugtracker.databuilders.UserTestDataBuilder;
import com.example.bugtracker.domain.enums.UserRole;
import com.example.bugtracker.domain.model.*;
import com.example.bugtracker.domain.model.auth.SecurityUser;
import com.example.bugtracker.domain.model.issue.Comment;
import com.example.bugtracker.domain.model.issue.Issue;
import com.example.bugtracker.fake.repository.CommentRepositoryFake;
import com.example.bugtracker.repository.repository_interfaces.CommentRepository;
import com.example.bugtracker.service.authorization.AuthorPermissionEvaluator;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import java.util.HashSet;
import java.util.Set;



import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class AuthorPermissionEvalutorTest {

    private final CommentRepository commentRepository
            = new CommentRepositoryFake();

    private final AuthorPermissionEvaluator authorPermissionEvaluator
            =new AuthorPermissionEvaluator(commentRepository);

    private final String projectIdForAuthorization= JwtTokenManagerImplFake.projectIdForAuthorization;

    @Test
    void supports_returnsTrue_givenCommentTypeInString(){
        assertTrue(
                authorPermissionEvaluator.supports("comment")
        );
    }

    @Test
    void supports_returnsFalse_givenOtherTypesInString(){
        assertFalse(
                authorPermissionEvaluator.supports("other")
        );
    }

    @Test
    void supports_returnsFalse_givenAnyObjectIncludingComment(){
        assertFalse(
                authorPermissionEvaluator.supports(new Object()));

        assertFalse(
                authorPermissionEvaluator.supports(new Comment(null,new IssueTestDataBuilder().build(),"desc")));
    }

    @Test
    void hasPermission_returnsTrue_givenAuthorizedAuthor(){
        //Setup
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

        //Execution
        boolean evaluationResult = authorPermissionEvaluator
                .hasPermission(
                        userAndProjectRoleAuthentication
                        , 1L // comment id
                        , "comment"
                        , "author");
        //Assertions
        assertTrue(evaluationResult);
    }


    @Test
    void hasPermission_returnsFalse_GivenWrongAuthor(){
        //Setup
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

        //Execution
        boolean evaluationResult = authorPermissionEvaluator
                .hasPermission(
                        userAndProjectRoleAuthentication
                        , 1L // comment id
                        , "comment"
                        , "author");

        //Assertions
        assertFalse(evaluationResult);
    }




}
