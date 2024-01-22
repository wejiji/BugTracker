package com.example.security2pro.domain.model;

import com.example.security2pro.databuilders.IssueTestDataBuilder;
import com.example.security2pro.databuilders.ProjectTestDataBuilder;
import com.example.security2pro.databuilders.SprintTestDataBuilder;
import com.example.security2pro.databuilders.UserTestDataBuilder;
import com.example.security2pro.domain.enums.IssuePriority;
import com.example.security2pro.domain.enums.IssueStatus;
import com.example.security2pro.domain.enums.IssueType;

import com.example.security2pro.domain.enums.UserRole;
import com.example.security2pro.domain.model.issue.Comment;
import com.example.security2pro.domain.model.issue.Issue;
import com.example.security2pro.domain.model.issue.IssueRelation;
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


class IssueTest {
    /*
     * Issue instances are constructed sometimes by using Issue class's 'createIssue' method and other times by using IssueTestData builder.
     * when IssueTestData is instantiated, each field is initialized with default value when no argument is passed for the field,
     * while 'createIssue' method of Issue class will require all the arguments it needs
     *
     * each test of this class will test at most one method of Issue class.
     * every test of this class is a small test
     *
     *
     */
    private Project project;
    private Sprint sprint;
    private User user;
    private User user2;
    private Set<User> assignees;
    private Long defaultId = 99L;

    @BeforeEach
    void setUp() {
        // the details of each instance in this setUp method
        // is not very important

        project = new ProjectTestDataBuilder()
                .withId(defaultId)
                .withName("projectTitle")
                .build();

        sprint = new SprintTestDataBuilder()
                .withId(defaultId)
                .withName("sprintTitle")
                .build();

        user = new UserTestDataBuilder()
                .withId(defaultId)
                .withUsername("testUsername")
                .build();

        user2 = new UserTestDataBuilder()
                .withId(defaultId)
                .withUsername("testUsername2")
                .build();

        assignees = new HashSet<>(Set.of(user, user2));
    }

    @Test
    void changeStatus_success() {
        // tests if the method changes only 'status' field

        //Setup
        Issue issue = new IssueTestDataBuilder().withStatus(IssueStatus.DONE).build();

        //Execution
        issue.changeStatus(IssueStatus.IN_PROGRESS);

        //Assertions - check if all other fields stays the same as well
        Issue expectedIssue = new IssueTestDataBuilder().withStatus(IssueStatus.IN_PROGRESS).build();
        assertThat(issue)
                .usingRecursiveComparison()
                .isEqualTo(expectedIssue);
    }


    @Test
    void createIssue_success() {
        /*
         * boolean field 'archived' is set to 'false' when Issue is first created
         *
         * this tests a success case
         */

        //Execution
        Issue issueCreated = Issue.createIssue(
                1L
                , project
                , assignees
                , "issueTitle"
                , "issueDescription"
                , IssuePriority.LOWEST
                , IssueStatus.IN_REVIEW
                , IssueType.BUG
                , sprint);
        assertFalse(issueCreated.isArchived());

        //Assertions
        assertThat(issueCreated).satisfies(issue -> {
            assertIssueFields(issue
                    , 1L
                    , project
                    , assignees
                    , "issueTitle"
                    , "issueDescription"
                    , IssuePriority.LOWEST
                    , IssueStatus.IN_REVIEW
                    , IssueType.BUG
                    , sprint
                    , false);
        });
    }

    @Test
    void createIssue_nullAssignee() {
        /*
         * this tests if 'assignees' field is set to an empty set
         * when null 'assignees' is passed
         * for 'createIssue' argument
         */

        //Execution
        Issue issueCreated = Issue.createIssue(
                1L
                , project
                , null
                , "issueTitle"
                , "issueDescription"
                , IssuePriority.LOWEST
                , IssueStatus.DONE
                , IssueType.BUG
                , sprint);
        assertFalse(issueCreated.isArchived());

        //Assertions
        assertThat(issueCreated).satisfies(issue -> {
            assertIssueFields(issue
                    , 1L
                    , issue.getProject()
                    , Collections.emptySet()
                    , "issueTitle"
                    , "issueDescription"
                    , IssuePriority.LOWEST
                    , IssueStatus.DONE
                    , IssueType.BUG
                    , sprint
                    , false);
        });
    }


    @Test
    void endIssueWithProject() {
        /*
         * tests if 'archived' field is set to true
         * and 'currentSprint' field is set to null
         * note that 'status' field will not change to 'IssueStatus.DONE'
         */

        //Setup
        Issue issueCreated = new IssueTestDataBuilder()
                .withStatus(IssueStatus.TODO)
                .withSprint(sprint)
                .withArchived(false)
                .build();

        //Execution
        issueCreated.endIssueWithProject();

        //Assertions
        Issue expectedIssue = new IssueTestDataBuilder()
                .withStatus(IssueStatus.TODO)
                .withArchived(true)
                .withSprint(null)
                .build();

        assertThat(issueCreated)
                .usingRecursiveComparison()
                .isEqualTo(expectedIssue);
    }


    @Test
    void assignCurrentSprint_assignCurrentSprint() {
        /*
        * 'assignCurrentSprint' methods updates only 'currentSprint' field
         * two success test cases in this test method where,
         * in the first case, 'currentSprint' field is expected to change from a sprint object to null
         * in the second case, 'currentSprint' field is expected to change from null to a sprint object
         */

        //Setup
        Issue issueCreated = new IssueTestDataBuilder().withSprint(sprint).build();

        //Execution - the first case
        issueCreated.assignCurrentSprint(null);

        //Assertion
        Issue expectedIssue = new IssueTestDataBuilder().withSprint(null).build();
        assertThat(issueCreated)
                .usingRecursiveComparison()
                .isEqualTo(expectedIssue);

        //Execution - the second case
        issueCreated.assignCurrentSprint(sprint);

        //Assertion
        Issue expectedIssue2 = new IssueTestDataBuilder().withSprint(sprint).build();
        assertThat(issueCreated)
                .usingRecursiveComparison()
                .isEqualTo(expectedIssue2);
    }


    @Test
    void assignCurrentSprint_throwsException_whenArchivedSprintPassed() {
        /*
         * 'assignCurrentSprint' methods updates only 'currentSprint' field
         * two test cases in this test method where,
         * in the first case, 'currentSprint' field is expected to change from a sprint object to null
         * in the second case, 'currentSprint' field is expected to change from null to a sprint object
         */

        //Setup
        Issue issueCreated = new IssueTestDataBuilder().withSprint(null).build();
        Sprint archivedSprint = new SprintTestDataBuilder().withArchived(true).build();

        //Execution & Assertions
        assertThrows(IllegalArgumentException.class,
                ()->issueCreated.assignCurrentSprint(archivedSprint));

    }





    @Test
    void forceCompleteIssue() {
        /*
         * 'forceCompleteIssue' method is expected to set
         * 'status' field to 'DONE',
         * 'currentSprint' field to null,
         * 'archived' field to true
         */

        //Setup
        Issue issueCreated = new IssueTestDataBuilder()
                .withStatus(IssueStatus.TODO)
                .withSprint(sprint)
                .withArchived(false)
                .build();

        //Execution
        issueCreated.forceCompleteIssue();

        //Assertions
        Issue expectedIssue = new IssueTestDataBuilder()
                .withStatus(IssueStatus.DONE)
                .withSprint(null)
                .withArchived(true)
                .build();

        assertThat(issueCreated)
                .usingRecursiveComparison()
                .isEqualTo(expectedIssue);
    }

//    @Test
//    void simpleUpdate() {
//        // 'simpleUpdate' method updates 'title', 'priority', 'status' and 'currentSprint' fields
//        // whether the sprint belongs to the same
//
//        //Setup
//        Issue issueCreated = new IssueTestDataBuilder()
//                .withProject(project)
//                .withTitle("title")
//                .withPriority(IssuePriority.LOWEST)
//                .withStatus(IssueStatus.TODO)
//                .withSprint(sprint)
//                .build();
//        assertFalse(issueCreated.isArchived());
//
//        Sprint updatedSprint = new SprintTestDataBuilder()
//                .withId(2L)
//                .withName("sprintTitle2")
//                .withStartDate(LocalDateTime.now())
//                .withEndDate(LocalDateTime.now().plusDays(1))
//                .build();
//
//        //Execution
//        issueCreated.simpleUpdate("updatedTitle"
//                , IssuePriority.MEDIUM
//                , IssueStatus.IN_PROGRESS
//                , updatedSprint);
//        //Assertions
//        assertThat(issueCreated).satisfies(issue -> {
//            assertIssueFields(issue
//                    , 1L
//                    , project
//                    , assignees
//                    , "updatedTitle"
//                    , "originalDescription"
//                    , IssuePriority.MEDIUM
//                    , IssueStatus.IN_PROGRESS
//                    , IssueType.NEW_FEATURE
//                    , updatedSprint
//                    , false);
//        });
//
//    }

    @Test
    void detailUpdate_updatesIssue() {
        /*
        * tests a success case
        *
        * for now, there is no exception case test where Sprint with true 'archived' field is passed,
        * and that can be added in the future
        * 'assignCurrentSprint' method is responsible for this validation and this test class has a test method for it
        *
         * fields that are updated by 'detailUpdate' method:
         * 'title', 'description', 'priority', 'status', 'type', 'currentSprint', 'assignees'
         *
         * fields that are not updated by 'detailUpdate' method:
         * 'id', 'project', 'issueRelationSet' and 'commentList'
         */

        //Setup
        Issue issueCreated = new IssueTestDataBuilder().withProject(project)
                .withId(1L)
                .withProject(project)
                .withAssignees(assignees)
                .withTitle("originalTitle")
                .withDescription("originalDescription")
                .withPriority(IssuePriority.HIGHEST)
                .withStatus(IssueStatus.IN_REVIEW)
                .withType(IssueType.NEW_FEATURE)
                .withSprint(sprint)
                .withArchived(false)
                .build();

        User user3 = new UserTestDataBuilder()
                .withUsername("testUsername3")
                .build();
        Set<User> updatedAssignees = Set.of(user3);

        Sprint updatedSprint = new SprintTestDataBuilder()
                .withId(2L)
                .withName("sprintTitle2")
                .build();

        //Execution
        issueCreated.detailUpdate("updatedTitle"
                , "updatedDescription"
                , IssuePriority.MEDIUM
                , IssueStatus.DONE
                , IssueType.IMPROVEMENT
                , updatedSprint
                , updatedAssignees);

        //Assertions
        assertThat(issueCreated).satisfies(issue -> {
            assertIssueFields(issue
                    , 1L
                    , issue.getProject()
                    , updatedAssignees
                    , "updatedTitle"
                    , "updatedDescription"
                    , IssuePriority.MEDIUM
                    , IssueStatus.DONE
                    , IssueType.IMPROVEMENT
                    , updatedSprint
                    , false);
        });
    }

    @Test
    void getAssigneesNames() {
        // tests if a correct Set of usernames of 'assignees' is returned by 'getAssigneesNames'

        //Setup
        User user = new UserTestDataBuilder().withUsername("username1").build();
        User user2 = new UserTestDataBuilder().withUsername("username2").build();

        Set<User> assignees = Set.of(user, user2);
        Set<String> expectedAssigneesNames = assignees.stream().map(User::getUsername).collect(Collectors.toSet());

        Issue issue = new IssueTestDataBuilder().withAssignees(assignees).build();

        //Execution
        Set<String> assigneesNames = issue.getAssigneesNames();

        //Assertions
        assertEquals(expectedAssigneesNames, assigneesNames);
    }

    @Test
    void getCurrentSprint() {
        //tests if a correct Optional<Sprint> is returned when 'currentSprint' field is not null

        //Setup
        Issue issue = new IssueTestDataBuilder().withSprint(sprint).build();
        //Execution
        Optional<Sprint> sprintOptional = issue.getCurrentSprint();
        //Assertions
        assertThat(sprintOptional).isPresent();
        assertEquals(sprintOptional.get(), sprint);
    }

    @Test
    void getCurrentSprintNull() {
        // tests if Optional.empty is returned when 'currentSprint' field is null

        //Setup
        Issue issue2 = new IssueTestDataBuilder().withSprint(null).build();
        //Execution
        Optional<Sprint> sprintOptional2 = issue2.getCurrentSprint();
        //Assertions
        assertThat(sprintOptional2).isEmpty();
    }


    @Test
    void getCurrentSprintIdInString_whenCurrentSprintIsNotNull() {
        // checks if 'currentSprint' field object's id is converted from Long to String and returned

        //Setup
        Sprint sprint = new SprintTestDataBuilder().withId(67L).build();
        Issue issue = new IssueTestDataBuilder().withSprint(sprint).build();
        //Execution
        String sprintIdString = issue.getCurrentSprintIdInString();
        //Assertions
        assertEquals(String.valueOf(67L), sprintIdString);
    }

    @Test
    void getCurrentSprintIdInString_whenCurrentSprintIsNull() {
        // checks if 'currentSprint' field object's id is converted from Long to String and returned

        //Setup
        Issue issue2 = new IssueTestDataBuilder().withSprint(null).build();
        //Execution
        String sprintIdNull = issue2.getCurrentSprintIdInString();
        //Assertions
        assertNull(sprintIdNull);
    }


    @Test
    void addIssueRelation_createSuccess() {
        /*
         * tests a success case where a passed IssueRelation is added to 'issueRelationSet'
         * when 'issueRelationSet' does not have the IssueRelation that has the same 'causeIssue' already.
         * the equality check is solely based on 'id' field of 'causeIssue' of an IssueRelation
         *
         * also tests the 'affectedIssue' field of the argument IssueRelation is set correctly
         * since the relationship between Issue and IssueRelation is bidirectional
         *
         * only a single IssueRelation object is passed as an argument
         * so it is not necessary to test for multiple arguments
         */
        Issue affected = new IssueTestDataBuilder().withId(10L).build();
        Issue cause = new IssueTestDataBuilder().withId(20L).build();
        IssueRelation issueRelation
                = IssueRelation.createIssueRelation(
                affected, cause, "cause is the root cause of affected");

        //Execution
        affected.addIssueRelation(issueRelation);

        //Assertions
        assertEquals(1, affected.getIssueRelationSet().size());
        IssueRelation issueRelationFound = affected.getIssueRelationSet().stream().findAny().get();
        assertEquals("cause is the root cause of affected", issueRelationFound.getRelationDescription());
        assertThat(affected)
                .usingRecursiveComparison()
                .isEqualTo(issueRelation.getAffectedIssue());
    }


    @Test
    void addIssueRelation_updateSuccess() {
        /*
         * tests a success case where an existing IssueRelation of 'issueRelationSet' is updated
         * when 'issueRelationSet' has the IssueRelation that has the same 'causeIssue' already.
         * the equality check is solely based on 'id' field of 'causeIssue'
         *
         * also tests the 'affectedIssue' field of the argument IssueRelation stays the same
         * since the relationship between Issue and IssueRelation is bidirectional
         *
         * only a single IssueRelation object is passed as an argument
         * so it is not necessary to test for multiple arguments
         */

        //Setup
        Issue affected = new IssueTestDataBuilder().withId(10L).build();
        Issue cause = new IssueTestDataBuilder().withId(20L).build();
        IssueRelation issueRelation
                = IssueRelation.createIssueRelation(
                        affected, cause, "original relation description");
        affected.addIssueRelation(issueRelation);
        assertEquals(1, affected.getIssueRelationSet().size());

        IssueRelation updatedissueRelation
                = IssueRelation.createIssueRelation(
                        affected, cause, "updated relation description");

        //Execution
        affected.addIssueRelation(updatedissueRelation);

        //Assertions
        assertEquals(1, affected.getIssueRelationSet().size());
        IssueRelation issueRelationFound = affected.getIssueRelationSet().stream().findAny().get();
        assertEquals("updated relation description", issueRelationFound.getRelationDescription());
        assertThat(affected)
                .usingRecursiveComparison()
                .isEqualTo(issueRelation.getAffectedIssue());
    }

    @Test
    void deleteIssueRelation_success() {
        /*
        * tests a success case where an IssueRelation object is removed from 'issueRelationSet'
        * , which will effectively delete the IssueRelation object
        *
        * also tests if the deleted IssueRelation object's 'affectedIssue' is set to null
        *  since the relationship between Issue and IssueRelation is bidirectional
         */

        Issue affected = new IssueTestDataBuilder().withId(10L).build();
        Issue cause = new IssueTestDataBuilder().withId(20L).build();
        IssueRelation issueRelation
                = IssueRelation.createIssueRelation(
                affected, cause, "relation description");
        affected.addIssueRelation(issueRelation);
        assertEquals(1, affected.getIssueRelationSet().size());

        //Execution
        affected.deleteIssueRelation(issueRelation.getCauseIssue().getId());

        //Assertions
        assertEquals(0, affected.getIssueRelationSet().size());
        assertNull(issueRelation.getAffectedIssue());
    }

    @Test
    void deleteIssueRelation_throwsException() {
        /*
         * tests if an exception is thrown
         * when the issueRelation to be deleted is not considered to exist in 'issueRelationSet'
         * the equality check is based solely on 'id' field of 'causeIssue' of an IssueRelation object
         *
         */

        //Setup
        Issue affected = new IssueTestDataBuilder().withId(10L).build();
        Issue cause = new IssueTestDataBuilder().withId(20L).build();
        IssueRelation issueRelation
                = IssueRelation.createIssueRelation(
                affected, cause, "relation description");
        affected.addIssueRelation(issueRelation);
        assertEquals(1, affected.getIssueRelationSet().size());

        Long causeIdNotExist = 30L;
        //Execution
        assertThrows(IllegalArgumentException.class,
                () -> affected.deleteIssueRelation(causeIdNotExist));

        //Assertions
        //checks the 'issueRelationSet' field stays the same
        assertEquals(1, affected.getIssueRelationSet().size());
    }

    @Test
    void addComment() {
        /*
         * tests a success case where a comment is added to 'commentList' of an Issue
         * no validation in 'addComment' method
         *
         * also tests if comment's 'issue' field is set correctly
         * since the relationship between Issue and Comment is bidirectional
         */

        //Setup
        Issue issue = new IssueTestDataBuilder().withId(10L).build();
        Comment comment = new Comment(1L, null, "comment description");
        assertEquals(0, issue.getCommentList().size());

        //Execution
        issue.addComment(comment);

        //Assertions
        assertEquals(1, issue.getCommentList().size());
        Comment addedComment = issue.getCommentList().get(0);
        assertEquals("comment description", addedComment.getDescription());
        assertThat(issue)
                .usingRecursiveComparison()
                .isEqualTo(addedComment.getIssue());
    }

    @Test
    void deleteComment_success() {
        /*
         * tests if a comment is deleted given the id of the comment
         *
         * also tests if comment's 'issue' field is set to null
         * since the relationship between Issue and Comment is bidirectional
         */

        Issue issue = new IssueTestDataBuilder().withId(10L).build();
        Comment comment = new Comment(1L, null, "comment description");
        assertEquals(0, issue.getCommentList().size());
        issue.addComment(comment);
        assertEquals(1, issue.getCommentList().size());

        //Execution
        issue.deleteComment(comment.getId());
        //Assertions
        assertEquals(0, issue.getCommentList().size());
    }

    @Test
    void deleteComment_throwsException() {
        /*
        * tests if IllegalArgumentException is thrown
        * when given the comment id that does not exist
         */

        //Setup
        Issue issue = new IssueTestDataBuilder().withId(10L).build();
        Comment comment = new Comment(1L, null, "comment description");
        assertEquals(0, issue.getCommentList().size());
        issue.addComment(comment);
        assertEquals(1, issue.getCommentList().size());

        Long commentIdNotExist = 30L;

        //Execution && Assertions
        assertThrows(IllegalArgumentException.class,
                () -> issue.deleteComment(commentIdNotExist));
        assertEquals(1, issue.getCommentList().size());
    }


    private void assertIssueFields(Issue issue, Long expectedId, Project expectedProject, Set<User> expectedAssignees, String expectedTitle, String expectedDescription, IssuePriority expectedPriority, IssueStatus expectedStatus, IssueType expectedType, Sprint expectedSprint, boolean archived) {
        assertEquals(expectedId, issue.getId());
        assertEquals(expectedProject, issue.getProject());
        assertEquals(expectedAssignees, issue.getAssignees());
        assertEquals(expectedTitle, issue.getTitle());
        assertEquals(expectedDescription, issue.getDescription());
        assertEquals(expectedPriority, issue.getPriority());
        assertEquals(expectedStatus, issue.getStatus());
        assertEquals(expectedType, issue.getType());
        if (expectedSprint == null) {
            assertThat(issue.getCurrentSprint()).isEmpty();
        } else {
            assertEquals(expectedSprint, issue.getCurrentSprint().get());
        }
        assertEquals(issue.isArchived(), archived);
    }


}
