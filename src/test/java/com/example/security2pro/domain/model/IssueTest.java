package com.example.security2pro.domain.model;

import com.example.security2pro.databuilders.IssueTestDataBuilder;
import com.example.security2pro.databuilders.SprintTestDataBuilder;
import com.example.security2pro.domain.enums.IssuePriority;
import com.example.security2pro.domain.enums.IssueStatus;
import com.example.security2pro.domain.enums.IssueType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class IssueTest {

    private Project project;
    private User user;
    private User user2;
    private Set<User> assignees;
    private Sprint sprint;
    private Sprint sprint2;


    @BeforeEach
    public void setUp(){
        project = mock(Project.class);
         user = mock(User.class);
        user2 = mock(User.class);
        assignees = Set.of(user,user2);
        sprint = mock(Sprint.class);
        sprint2 = mock(Sprint.class);
    }

    @Test
    public void changeStatus(){
        Issue issueNullStatus = new IssueTestDataBuilder().withStatus(null).build();
        Issue issueDone = new IssueTestDataBuilder().withStatus(IssueStatus.DONE).build();

        //Execution
        issueNullStatus.changeStatus(IssueStatus.IN_PROGRESS);
        issueDone.changeStatus(IssueStatus.IN_PROGRESS);

        //Assertions
        assertEquals(issueNullStatus.getStatus(),IssueStatus.IN_PROGRESS);
        assertEquals(issueDone.getStatus(), IssueStatus.IN_PROGRESS);
    }

    @Test
    public void createIssue(){
        //check if creation result is correct
        //assignees field will be converted to empty set if null is passed

        //Execution
        Issue issueCreated= Issue.createIssue(project,assignees,"issueTitle","issueDescription", IssuePriority.LOWEST, IssueStatus.IN_REVIEW, IssueType.BUG, sprint);
        Issue issueCreated2 = Issue.createIssue(project,null,"issueTitle","issueDescription", IssuePriority.LOWEST, IssueStatus.DONE, IssueType.BUG, sprint);

        //Assertions
        assertThat(issueCreated).satisfies(issue -> {assertIssueFields(issue, issue.getProject(),issue.getAssignees(),issue.getTitle(),issue.getDescription(),issue.getPriority(),issue.getStatus(),issue.getType(),issue.getCurrentSprint().get(),false);});
        assertThat(issueCreated2).satisfies(issue -> {assertIssueFields(issue, issue.getProject(), Collections.emptySet(),issue.getTitle(),issue.getDescription(),issue.getPriority(),issue.getStatus(),issue.getType(),issue.getCurrentSprint().get(),false);});
    }


    @Test
    public void endIssueWithProject(){
        // when an issue is ending with a project,
        // only archived field will change and not other fields.

        Issue issueCreated = new IssueTestDataBuilder().withProject(project).withAssignees(assignees).withSprint(sprint)
                .withArchived(false)
                .build();

        //Execution
        issueCreated.endIssueWithProject();

        //Assertions
        assertThat(issueCreated).satisfies(issue -> {assertIssueFields(issue, issue.getProject(),issue.getAssignees(),issue.getTitle(),issue.getDescription(),issue.getPriority(),issue.getStatus(),issue.getType(),issue.getCurrentSprint().get(),true);});
    }


    @Test
    public void assignCurrentSprint(){
        //change only currentSprint field


        Issue issueCreated = new IssueTestDataBuilder().withProject(project).withAssignees(assignees)
                .withSprint(sprint)
                .build();

        //Execution
        issueCreated.assignCurrentSprint(null);
        //Assertion
        assertThat(issueCreated).satisfies(issue -> {assertIssueFields(issue, issue.getProject(),issue.getAssignees(),issue.getTitle(),issue.getDescription(),issue.getPriority(),issue.getStatus(),issue.getType()
                ,null,false);});

        //Execution
        issueCreated.assignCurrentSprint(sprint2);
        //Assertion
        assertThat(issueCreated).satisfies(issue -> {assertIssueFields(issue, issue.getProject(),issue.getAssignees(),issue.getTitle(),issue.getDescription(),issue.getPriority(),issue.getStatus(),issue.getType()
                ,sprint2,false);});
    }

    @Test
    public void forceCompleteIssue() {
        //change archived to true, status to DONE, currentSprint to null

        Issue issueCreated = new IssueTestDataBuilder().withProject(project).withAssignees(assignees)
                .withStatus(IssueStatus.IN_REVIEW)
                .withSprint(sprint)
                .withArchived(false)
                .build();

        //Execution
        issueCreated.forceCompleteIssue();
        //Assertions
        assertThat(issueCreated).satisfies(issue->{assertIssueFields(issue, issue.getProject(),issue.getAssignees(),issue.getTitle(),issue.getDescription(),issue.getPriority()
                ,IssueStatus.DONE,issue.getType(),null,true);});

    }

    @Test
    public void simpleUpdate(){
        //changes title,priority,status,sprint fields

        Issue issueCreated = new IssueTestDataBuilder().withProject(project).withAssignees(assignees)
                .withName("issueTitle")
                .withPriority(IssuePriority.LOWEST)
                .withStatus(IssueStatus.IN_REVIEW)
                .withSprint(sprint)
                .build();

        String updatedTitle = "updatedTitle";
        IssuePriority updatedPriority = IssuePriority.MEDIUM;
        IssueStatus updatedStatus = IssueStatus.TODO;
        Sprint updatedSprint = mock(Sprint.class);

        //Execution
        issueCreated.simpleUpdate(updatedTitle,updatedPriority,updatedStatus,updatedSprint);
        //Assertions
        assertThat(issueCreated).satisfies(issue->{assertIssueFields(issue,issue.getProject(), issue.getAssignees(),updatedTitle,issue.getDescription()
                ,updatedPriority,updatedStatus,issue.getType(),updatedSprint,false);});

    }

    @Test
    public void detailUpdate(){
        //Issue issueCreated= new Issue(project,assignees,"issueTitle","issueDescription", IssuePriority.LOWEST, IssueStatus.IN_REVIEW, IssueType.BUG, sprint);

        Issue issueCreated = new IssueTestDataBuilder().withProject(project)
                .withAssignees(assignees)
                .withName("originalTitle")
                .withDescription("originalDescription")
                .withPriority(IssuePriority.HIGHEST)
                .withStatus(IssueStatus.IN_REVIEW)
                .withType(IssueType.NEW_FEATURE)
                .withSprint(sprint)
                .withArchived(false)
                .build();


        String updatedTitle = "updatedTitle";
        String updatedDescription ="updatedDescription";
        User user3 = mock(User.class);
        Set<User> updatedAssignees = Set.of(user3);
        IssuePriority updatedPriority = IssuePriority.LOW;
        IssueStatus updatedStatus = IssueStatus.TODO;
        IssueType updatedType = IssueType.IMPROVEMENT;
        Sprint updatedSprint = mock(Sprint.class);

        //Execution
        issueCreated.detailUpdate(updatedTitle,updatedDescription,updatedPriority,updatedStatus,updatedType,updatedSprint,updatedAssignees);

        //Assertions
        assertThat(issueCreated).satisfies(issue->{assertIssueFields(issue,issue.getProject(), updatedAssignees ,updatedTitle,updatedDescription
                ,updatedPriority,updatedStatus,updatedType,updatedSprint,false);});

    }


    @Test
    public void getAssigneesNames(){
        User user =new User("username1","","","","",true);
        User user2 =new User("username2","","","","",true);
        Set<User> assignees = Set.of(user,user2);
        Set<String> expectedAssigneesNames = assignees.stream().map(User::getUsername).collect(Collectors.toSet());

        Issue issue = new IssueTestDataBuilder().withAssignees(assignees).build();

        //Execution
        Set<String> assigneesNames = issue.getAssigneesNames();
        //Assertions
        assertEquals(issue.getAssigneesNames(),expectedAssigneesNames);
    }

    @Test
    public void getCurrentSprint(){

        Issue issue = new IssueTestDataBuilder().withSprint(sprint).build();
        Issue issue2 = new IssueTestDataBuilder().withSprint(null).build();
        //Execution
        Optional<Sprint> sprintOptional =issue.getCurrentSprint();
        Optional<Sprint> sprintOptional2 = issue2.getCurrentSprint();
        //Assertions
        assertThat(sprintOptional).isPresent();
        assertEquals(sprintOptional.get(),sprint);
        assertThat(sprintOptional2).isEmpty();
    }

    @Test
    public void getCurrentSprintIdInString(){
        Sprint sprint = new SprintTestDataBuilder().withId(67L).build();

        Issue issue = new IssueTestDataBuilder().withSprint(sprint).build();
        Issue issue2 = new IssueTestDataBuilder().withSprint(null).build();
        //Execution
        String sprintIdString =issue.getCurrentSprintIdInString();
        String sprintIdNull = issue2.getCurrentSprintIdInString();
        //Assertions
        assertEquals(sprintIdString,sprint.getId().toString());
        assertNull(sprintIdNull);
    }




    private void assertIssueFields(Issue issue ,Project expectedProject, Set<User> expectedAssignees, String expectedTitle, String expectedDescription, IssuePriority expectedPriority, IssueStatus expectedStatus, IssueType expectedType, Sprint expectedSprint, boolean archived ) {
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
