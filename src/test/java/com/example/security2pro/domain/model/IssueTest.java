package com.example.security2pro.domain.model;

import com.example.security2pro.databuilders.IssueTestDataBuilder;
import com.example.security2pro.databuilders.ProjectTestDataBuilder;
import com.example.security2pro.databuilders.SprintTestDataBuilder;
import com.example.security2pro.databuilders.UserTestDataBuilder;
import com.example.security2pro.domain.enums.IssuePriority;
import com.example.security2pro.domain.enums.IssueStatus;
import com.example.security2pro.domain.enums.IssueType;

import com.example.security2pro.domain.enums.refactoring.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class IssueTest {

    private Project project;
    private Sprint sprint;
    private User user;
    private User user2;
    private Set<User> assignees ;

    @BeforeEach
    public void setUp(){
        project = new ProjectTestDataBuilder()
                .withId(1L)
                .withName("projectTitle")
                .withDescription("projectDescription")
                .build();

         sprint = new SprintTestDataBuilder()
                .withId(1L)
                .withName("sprintTitle")
                .withDescription("sprintDescription")
                .withStartDate(LocalDateTime.now())
                .withEndDate(LocalDateTime.now().plusDays(1))
                .build();

        user = new UserTestDataBuilder()
                .withUsername("testUsername")
                .withPassword("testPassword")
                .withFirstName("testFirstName")
                .withLastName("testLastName")
                .withEmail("testUser@gmail.com")
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_MEMBER))
                .withEnabled(true)
                .build();

         user2 = new UserTestDataBuilder()
                .withUsername("testUsername2")
                .withPassword("testPassword2")
                .withFirstName("testFirstName2")
                .withLastName("testLastName2")
                .withEmail("testUser2@gmail.com")
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_MEMBER))
                .withEnabled(true)
                .build();

        assignees = new HashSet<>(Set.of(user,user2));
    }

    @Test
    public void changeStatus(){

        //Setup
        Issue issueDone = new IssueTestDataBuilder()
                .withId(1L)
                .withProject(project)
                .withAssignees(assignees)
                .withTitle("title")
                .withDescription("description")
                .withPriority(IssuePriority.LOWEST)
                .withStatus(IssueStatus.DONE)
                .withType(IssueType.NEW_FEATURE)
                .withSprint(sprint)
                .build();
        assertFalse(issueDone.isArchived());
        //Execution
        issueDone.changeStatus(IssueStatus.IN_PROGRESS);
        //Assertions
        assertEquals( IssueStatus.IN_PROGRESS,issueDone.getStatus());
        //assert all other fields haven't changed
        assertThat(issueDone).satisfies(issue -> {assertIssueFields(issue
                ,1L
                ,project
                ,assignees
                ,"title"
                ,"description"
                ,IssuePriority.LOWEST
                ,IssueStatus.IN_PROGRESS
                ,IssueType.NEW_FEATURE
                ,sprint
                ,false);});
    }

    @Test
    public void changeStatusFromNull(){

        //Setup
        Issue issueNullStatus = new IssueTestDataBuilder()
                .withId(1L)
                .withProject(project)
                .withAssignees(assignees)
                .withTitle("title")
                .withDescription("description")
                .withPriority(IssuePriority.LOWEST)
                .withStatus(null)
                .withType(IssueType.NEW_FEATURE)
                .withSprint(sprint)
                .build();
        assertFalse(issueNullStatus.isArchived());
        //Execution
        issueNullStatus.changeStatus(IssueStatus.IN_PROGRESS);
        //Assertions
        assertEquals(IssueStatus.IN_PROGRESS,issueNullStatus.getStatus());
        //assert all other fields haven't changed
        assertThat(issueNullStatus).satisfies(issue -> {assertIssueFields(issue
                ,1L
                ,project
                ,assignees
                ,"title"
                ,"description"
                ,IssuePriority.LOWEST
                ,IssueStatus.IN_PROGRESS
                ,IssueType.NEW_FEATURE
                ,sprint
                ,false);});
    }


    @Test
    public void createIssue(){
        //check if creation result is correct

        //Execution
        Issue issueCreated= Issue.createIssue(
                1L
                ,project
                ,assignees
                ,"issueTitle"
                ,"issueDescription"
                ,IssuePriority.LOWEST
                ,IssueStatus.IN_REVIEW
                ,IssueType.BUG
                ,sprint);
        assertFalse(issueCreated.isArchived());

        //Assertions
        assertThat(issueCreated).satisfies(issue -> {assertIssueFields(issue
                ,1L
                ,project
                ,assignees
                ,"issueTitle"
                ,"issueDescription"
                ,IssuePriority.LOWEST
                ,IssueStatus.IN_REVIEW
                ,IssueType.BUG
                ,sprint
                ,false);});
    }

    @Test
    public void createIssue_nullAssignee(){
        //assert that 'assignees' field is set to empty set when null is passed

        //Execution
        Issue issueCreated = Issue.createIssue(
                1L
                ,project
                ,null
                ,"issueTitle"
                ,"issueDescription"
                ,IssuePriority.LOWEST
                ,IssueStatus.DONE
                ,IssueType.BUG
                ,sprint);
        assertFalse(issueCreated.isArchived());

        //Assertions
        assertThat(issueCreated).satisfies(issue -> {assertIssueFields(issue
                ,1L
                , issue.getProject()
                , Collections.emptySet()
                ,"issueTitle"
                ,"issueDescription"
                ,IssuePriority.LOWEST
                ,IssueStatus.DONE
                ,IssueType.BUG
                ,sprint
                ,false);});
    }



    @Test
    public void endIssueWithProject(){
        // test function that will be used when an issue is ending with a project
        // only 'archived' field will change
        // note that 'status' field WILL NOT change to 'IssueStatus.DONE'

        //Setup
        Issue issueCreated = new IssueTestDataBuilder()
                .withId(1L)
                .withProject(project)
                .withAssignees(assignees)
                .withTitle("title")
                .withDescription("description")
                .withPriority(IssuePriority.LOWEST)
                .withStatus(IssueStatus.TODO)
                .withType(IssueType.NEW_FEATURE)
                .withSprint(sprint)
                .build();

        assertFalse(issueCreated.isArchived());

        //Execution
        issueCreated.endIssueWithProject();

        //Assertions
        assertTrue(issueCreated.isArchived());
        assertThat(issueCreated).satisfies(issue -> {assertIssueFields(issue
                ,1L
                ,project
                ,assignees
                ,"title"
                ,"description"
                ,IssuePriority.LOWEST
                ,IssueStatus.TODO
                ,IssueType.NEW_FEATURE
                ,null
                ,true);});
    }


    @Test
    public void assignCurrentSprint(){
        //change only currentSprint field

        //Setup
        Issue issueCreated = new IssueTestDataBuilder()
                .withId(1L)
                .withProject(project)
                .withAssignees(assignees)
                .withTitle("title")
                .withDescription("description")
                .withPriority(IssuePriority.LOWEST)
                .withStatus(IssueStatus.TODO)
                .withType(IssueType.NEW_FEATURE)
                .withSprint(sprint)
                .build();
        assertFalse(issueCreated.isArchived());

        //Execution
        issueCreated.assignCurrentSprint(null);
        //Assertion
        assertThat(issueCreated).satisfies(issue -> {assertIssueFields(issue
                ,1L
                ,project
                ,assignees
                ,"title"
                ,"description"
                ,IssuePriority.LOWEST
                ,IssueStatus.TODO
                ,IssueType.NEW_FEATURE
                ,null
                ,false);});

        //Execution
        issueCreated.assignCurrentSprint(sprint);
        //Assertion
        assertThat(issueCreated).satisfies(issue -> {assertIssueFields(issue
                ,1L
                ,project
                ,assignees
                ,"title"
                ,"description"
                ,IssuePriority.LOWEST
                ,IssueStatus.TODO
                ,IssueType.NEW_FEATURE
                ,sprint
                ,false);});
    }

    @Test
    public void forceCompleteIssue() {
        //change archived to true, status to DONE, currentSprint to null

        //Setup
        Issue issueCreated = new IssueTestDataBuilder()
                .withId(1L)
                .withProject(project)
                .withAssignees(assignees)
                .withTitle("title")
                .withDescription("description")
                .withPriority(IssuePriority.LOWEST)
                .withStatus(IssueStatus.TODO)
                .withType(IssueType.NEW_FEATURE)
                .withSprint(sprint)
                .build();
        assertFalse(issueCreated.isArchived());

        //Execution
        issueCreated.forceCompleteIssue();

        //Assertions
        assertTrue(issueCreated.isArchived());
        assertThat(issueCreated).satisfies(issue->{assertIssueFields(issue
                ,1L
                , project
                , assignees
                ,"title"
                ,"description"
                ,IssuePriority.LOWEST
                ,IssueStatus.DONE
                ,IssueType.NEW_FEATURE
                ,null
                ,true);});

    }

    @Test
    public void simpleUpdate(){
        //changes title,priority,status,sprint fields

        //Setup
        Issue issueCreated = new IssueTestDataBuilder()
                .withId(1L)
                .withProject(project)
                .withAssignees(assignees)
                .withTitle("title")
                .withDescription("originalDescription")
                .withPriority(IssuePriority.LOWEST)
                .withStatus(IssueStatus.TODO)
                .withType(IssueType.NEW_FEATURE)
                .withSprint(sprint)
                .build();
        assertFalse(issueCreated.isArchived());

        Sprint updatedSprint = new SprintTestDataBuilder()
                .withId(2L)
                .withName("sprintTitle2")
                .withDescription("sprintDescription2")
                .withStartDate(LocalDateTime.now())
                .withEndDate(LocalDateTime.now().plusDays(1))
                .build();

        //Execution
        issueCreated.simpleUpdate("updatedTitle"
                ,IssuePriority.MEDIUM
                ,IssueStatus.IN_PROGRESS
                ,updatedSprint);
        //Assertions
        assertThat(issueCreated).satisfies(issue->{assertIssueFields(issue
                ,1L
                ,project
                ,assignees
                ,"updatedTitle"
                ,"originalDescription"
                ,IssuePriority.MEDIUM
                ,IssueStatus.IN_PROGRESS
                ,IssueType.NEW_FEATURE
                ,updatedSprint
                ,false);});

    }

    @Test
    public void detailUpdate(){
        //Issue issueCreated= new Issue(project,assignees,"issueTitle","issueDescription", IssuePriority.LOWEST, IssueStatus.IN_REVIEW, IssueType.BUG, sprint);

        //Setup
        Issue issueCreated = new IssueTestDataBuilder().withProject(project)
                .withId(1L)
                .withAssignees(assignees)
                .withTitle("originalTitle")
                .withDescription("originalDescription")
                .withPriority(IssuePriority.HIGHEST)
                .withStatus(IssueStatus.IN_REVIEW)
                .withType(IssueType.NEW_FEATURE)
                .withSprint(sprint)
                .build();
        assertFalse(issueCreated.isArchived());

        User user3 = new UserTestDataBuilder()
                .withUsername("testUsername3")
                .withPassword("testPassword3")
                .withFirstName("testFirstName3")
                .withLastName("testLastName3")
                .withEmail("testUser3@gmail.com")
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_MEMBER))
                .withEnabled(true)
                .build();
        Set<User> updatedAssignees = Set.of(user3);
        Sprint updatedSprint = new SprintTestDataBuilder()
                .withId(2L)
                .withName("sprintTitle2")
                .withDescription("sprintDescription2")
                .withStartDate(LocalDateTime.now())
                .withEndDate(LocalDateTime.now().plusDays(1))
                .build();

        //Execution
        issueCreated.detailUpdate("updatedTitle"
                ,"updatedDescription"
                ,IssuePriority.MEDIUM
                ,IssueStatus.DONE
                ,IssueType.IMPROVEMENT
                ,updatedSprint
                ,updatedAssignees);

        //Assertions
        assertThat(issueCreated).satisfies(issue->{assertIssueFields(issue
                ,1L
                ,issue.getProject()
                , updatedAssignees
                ,"updatedTitle"
                ,"updatedDescription"
                ,IssuePriority.MEDIUM
                ,IssueStatus.DONE
                ,IssueType.IMPROVEMENT
                ,updatedSprint
                ,false);});

    }


    @Test
    public void getAssigneesNames(){

        User user = new UserTestDataBuilder().withUsername("username1").build();
        User user2 = new UserTestDataBuilder().withUsername("username2").build();

        Set<User> assignees = Set.of(user,user2);
        Set<String> expectedAssigneesNames = assignees.stream().map(User::getUsername).collect(Collectors.toSet());

        Issue issue = new IssueTestDataBuilder().withAssignees(assignees).build();

        //Execution
        Set<String> assigneesNames = issue.getAssigneesNames();
        //Assertions
        assertEquals(expectedAssigneesNames, assigneesNames);
    }

    @Test
    public void getCurrentSprint(){
        Issue issue = new IssueTestDataBuilder().withSprint(sprint).build();
        //Execution
        Optional<Sprint> sprintOptional =issue.getCurrentSprint();
        //Assertions
        assertThat(sprintOptional).isPresent();
        assertEquals(sprintOptional.get(),sprint);
    }

    @Test
    public void getCurrentSprintNull(){
        Issue issue2 = new IssueTestDataBuilder().withSprint(null).build();
        //Execution
        Optional<Sprint> sprintOptional2 = issue2.getCurrentSprint();
        //Assertions
        assertThat(sprintOptional2).isEmpty();
    }



    @Test
    public void getCurrentSprintIdInString(){
        Sprint sprint = new SprintTestDataBuilder().withId(67L).build();

        Issue issue = new IssueTestDataBuilder().withSprint(sprint).build();
        //Execution
        String sprintIdString =issue.getCurrentSprintIdInString();
        //Assertions
        assertEquals(String.valueOf(67L),sprintIdString);
    }

    @Test
    public void getCurrentSprintIdInStringNull(){
        Issue issue2 = new IssueTestDataBuilder().withSprint(null).build();
        //Execution
        String sprintIdNull = issue2.getCurrentSprintIdInString();
        //Assertions
        assertNull(sprintIdNull);
    }





    @Test
    public void addIssueRelation_create(){
        //jpa automatically update ISSUERELATION table
        // when an issue relation is added to an issue's 'issueRelation' collection field
        Issue affected = new IssueTestDataBuilder().withId(10L).build();
        Issue cause = new IssueTestDataBuilder().withId(20L).build();
        IssueRelation issueRelation
                = IssueRelation.createIssueRelation(
                affected
                ,cause
                ,"cause is the root cause of affected");

        //Execution
        affected.addIssueRelation(issueRelation);
        //Assertions
        assertEquals(1, affected.getIssueRelationSet().size());
        IssueRelation issueRelationFound = affected.getIssueRelationSet().stream().findAny().get();
        assertEquals("cause is the root cause of affected",issueRelationFound.getRelationDescription());
    }


    @Test
    public void addIssueRelation_update(){
        //jpa automatically update ISSUERELATION table
        // when an issue relation is updated through issue's 'issueRelation' collection field
        Issue affected = new IssueTestDataBuilder().withId(10L).build();
        Issue cause = new IssueTestDataBuilder().withId(20L).build();
        IssueRelation issueRelation
                = IssueRelation.createIssueRelation(
                        affected
                        ,cause
                        ,"original relation description");
        affected.addIssueRelation(issueRelation);
        assertEquals(1,affected.getIssueRelationSet().size());

        IssueRelation updatedissueRelation
                = IssueRelation.createIssueRelation(
                        affected
                        ,cause
                        ,"updated relation description");

        //Execution
        affected.addIssueRelation(updatedissueRelation);

        //Assertions
        assertEquals(1,affected.getIssueRelationSet().size());
        IssueRelation issueRelationFound= affected.getIssueRelationSet().stream().findAny().get();
        assertEquals("updated relation description", issueRelationFound.getRelationDescription());
    }

    @Test
    public void deleteIssueRelation_success(){
        //jpa automatically delete ISSUERELATION table
        // when an issue relation is deleted from issue's 'issueRelations' collection field
        Issue affected = new IssueTestDataBuilder().withId(10L).build();
        Issue cause = new IssueTestDataBuilder().withId(20L).build();
        IssueRelation issueRelation
                = IssueRelation.createIssueRelation(
                affected
                ,cause
                ,"relation description");
        affected.addIssueRelation(issueRelation);
        assertEquals(1,affected.getIssueRelationSet().size());

        //Execution
        affected.deleteIssueRelation(issueRelation.getCauseIssue().getId());

        //Assertions
        assertEquals(0,affected.getIssueRelationSet().size());
    }

    @Test
    public void deleteIssueRelation_throwsException(){
        //jpa automatically delete ISSUERELATION table
        // when an issue relation is deleted from issue's 'issueRelations' collection field
        Issue affected = new IssueTestDataBuilder().withId(10L).build();
        Issue cause = new IssueTestDataBuilder().withId(20L).build();
        IssueRelation issueRelation
                = IssueRelation.createIssueRelation(
                affected
                ,cause
                ,"relation description");
        affected.addIssueRelation(issueRelation);
        assertEquals(1,affected.getIssueRelationSet().size());

        Long causeIdNotExist = 30L;
        //Execution

        assertThrows( IllegalArgumentException.class ,()->affected.deleteIssueRelation(causeIdNotExist));

        //Assertions
        assertEquals(1,affected.getIssueRelationSet().size());
    }

    @Test
    public void addComment(){
        Issue issue = new IssueTestDataBuilder().withId(10L).build();
        Comment comment = new Comment(1L,null,"comment description");
        assertEquals(0, issue.getCommentList().size());
        //Execution
        issue.addComment(comment);
        //Assertions
        assertEquals(1,issue.getCommentList().size());
        Comment addedComment = issue.getCommentList().get(0);
        assertEquals("comment description",addedComment.getDescription());
        assertThat(issue).usingRecursiveComparison().isEqualTo(addedComment.getIssue());
    }

    @Test
    public void deleteComment_success(){
        Issue issue = new IssueTestDataBuilder().withId(10L).build();
        Comment comment = new Comment(1L,null,"comment description");
        assertEquals(0, issue.getCommentList().size());
        issue.addComment(comment);
        assertEquals(1,issue.getCommentList().size());

        //Execution
        issue.deleteComment(comment.getId());
        //Assertions
        assertEquals(0,issue.getCommentList().size());
    }

    @Test
    public void deleteComment_throwsException(){
        Issue issue = new IssueTestDataBuilder().withId(10L).build();
        Comment comment = new Comment(1L,null,"comment description");
        assertEquals(0, issue.getCommentList().size());
        issue.addComment(comment);
        assertEquals(1,issue.getCommentList().size());

        Long commentIdNotExist = 30L;
        //Execution && Assertions
        assertThrows(IllegalArgumentException.class, ()->issue.deleteComment(commentIdNotExist));
    }




    private void assertIssueFields(Issue issue ,Long expectedId, Project expectedProject, Set<User> expectedAssignees, String expectedTitle, String expectedDescription, IssuePriority expectedPriority, IssueStatus expectedStatus, IssueType expectedType, Sprint expectedSprint, boolean archived ) {
        assertEquals(expectedId,issue.getId());
        assertEquals(expectedProject,issue.getProject());
        assertEquals(expectedAssignees, issue.getAssignees());
        assertEquals(expectedTitle, issue.getTitle());
        assertEquals(expectedDescription, issue.getDescription());
        assertEquals(expectedPriority, issue.getPriority());
        assertEquals(expectedStatus, issue.getStatus());
        assertEquals(expectedType, issue.getType());
        if(expectedSprint==null){
            assertThat(issue.getCurrentSprint()).isEmpty();
        } else {
            assertEquals(expectedSprint, issue.getCurrentSprint().get());
        }
        assertEquals(issue.isArchived(),archived);
    }



}
