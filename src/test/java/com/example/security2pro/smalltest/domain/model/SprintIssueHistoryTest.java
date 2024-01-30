package com.example.security2pro.smalltest.domain.model;

import com.example.security2pro.databuilders.IssueTestDataBuilder;
import com.example.security2pro.databuilders.SprintTestDataBuilder;
import com.example.security2pro.domain.model.Sprint;
import com.example.security2pro.domain.model.SprintIssueHistory;
import com.example.security2pro.domain.model.issue.Issue;
import com.example.security2pro.exception.directmessageconcretes.InvalidIssueArgumentException;
import com.example.security2pro.exception.directmessageconcretes.InvalidSprintArgumentException;
import org.junit.jupiter.api.Test;


import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SprintIssueHistoryTest {

    @Test
    void createSprintIssueHistory_throwsException_givenNonArchivedSprint() {
        /*
         * Tests if an exception is thrown
         * when the non-archived sprint is passed as an argument.
         * */

        //Setup
        Sprint sprint = new SprintTestDataBuilder().withArchived(false).build();
        Issue issue = new IssueTestDataBuilder().build();

        //Execution & Assertion
        assertThrows(InvalidSprintArgumentException.class,
                () -> SprintIssueHistory.createSprintIssueHistory(
                        1L, sprint, issue));
    }


    @Test
    void createSprintIssueHistory_throwsException_givenIssueWithOldSprint() {
        /*
         * Tests if an exception is thrown
         * when attempting to create a SprintIssueHistory object
         * with an Issue argument having a non-reset 'currentSprint' field,
         * still having the just-archived sprint
         * that is passed along with the Issue argument to 'createSprintIssueHistory' method
         */

        //Setup
        Sprint sprint = new SprintTestDataBuilder().withArchived(false).build();
        Issue issue = new IssueTestDataBuilder().withSprint(sprint).build();
        sprint.completeSprint(LocalDateTime.now());// argument does not matter in this test
        assertTrue(sprint.isArchived());

        //Execution & Assertion
        assertThrows(InvalidIssueArgumentException.class,
                () -> SprintIssueHistory.createSprintIssueHistory(
                        1L, sprint, issue));
    }

    @Test
    void createSprintIssueHistory_createsAndReturnsSprintIssueHistory_givenFieldValuesWithIssueWithNullCurrentSprint() {
        /*
         * This tests a success case
         * where Issue argument's 'currentSprint' field is null.
         */

        Sprint sprint = new SprintTestDataBuilder().withArchived(true).build();
        Issue issue = new IssueTestDataBuilder().withSprint(null).build();

        //Execution
        SprintIssueHistory sprintIssueHistory
                = SprintIssueHistory.createSprintIssueHistory(1L, sprint, issue);

        //Assertions
        assertThat(sprintIssueHistory.getIssue()).usingRecursiveComparison().isEqualTo(issue);
        assertThat(sprintIssueHistory.getArchivedSprint()).usingRecursiveComparison().isEqualTo(sprint);
        assertEquals(1L, sprintIssueHistory.getId());
    }

    @Test
    void createSprintIssueHistory_createsAndReturnsSprintIssueHistory_givenFieldValuesWithIssueWithNonArchivedCurrentSprint() {
        /*
         * This tests a success case
         * where 'Issue' argument's 'currentSprint' field has a non-archived sprint.
         */

        //Setup
        Sprint sprint = new SprintTestDataBuilder().withId(2L).withArchived(true).build();
        Sprint sprint2 = new SprintTestDataBuilder().withId(34L).build();
        Issue issue = new IssueTestDataBuilder().withSprint(sprint2).build();

        //Execution
        SprintIssueHistory sprintIssueHistory
                = SprintIssueHistory.createSprintIssueHistory(1L, sprint, issue);

        //Assertions
        assertThat(sprintIssueHistory.getIssue()).usingRecursiveComparison().isEqualTo(issue);
        assertThat(sprintIssueHistory.getArchivedSprint()).usingRecursiveComparison().isEqualTo(sprint);
        assertEquals(1L, sprintIssueHistory.getId());
    }


}
