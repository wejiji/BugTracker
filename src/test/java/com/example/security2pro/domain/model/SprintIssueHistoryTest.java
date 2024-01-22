package com.example.security2pro.domain.model;

import com.example.security2pro.databuilders.IssueTestDataBuilder;
import com.example.security2pro.databuilders.SprintTestDataBuilder;
import com.example.security2pro.domain.model.issue.Issue;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SprintIssueHistoryTest {

    /*
     * each test of this class will test at most one method of RefreshTokenData class
     * every test of this class is a small test
     */

    @Test
    void createSprintIssueHistory_throwsException_givenNonArchivedSprint(){
        /*
        * tests if IllegalArgumentException is thrown
        * when the sprint that is not archived is passed as argument
        * */

        //Setup
        Sprint sprint = new SprintTestDataBuilder().withArchived(false).build();
        Issue issue = new IssueTestDataBuilder().build();

        //Execution & Assertion
        assertThrows(IllegalArgumentException.class,
                ()->SprintIssueHistory.createSprintIssueHistory(
                        1L,sprint,issue));
    }
    
    @Test
    void createSprintIssueHistory_throwsException_whenIssueCurrentSprintWasNotReset(){
        /*
         *
         * Tests if an IllegalArgumentException is thrown when creating a SprintIssueHistory object
         * with an Issue argument containing a 'currentSprint' field set to the archived sprint,
         * which is passed along with the Issue argument to the 'createSprintIssueHistory' method.
         *
         */

        //Setup
        Sprint sprint = new SprintTestDataBuilder().withArchived(true).build();
        Issue issue = new IssueTestDataBuilder().withSprint(sprint).build();

        //Execution & Assertion
        assertThrows(IllegalArgumentException.class,
                ()->SprintIssueHistory.createSprintIssueHistory(
                        1L,sprint,issue));
    }

    @Test
    void createSprintIssueHistory_createsAndReturnsSprintIssueHistory(){
        /*
        * this tests a success case
        * where Issue argument's 'currentSprint' field is null
         */

        Sprint sprint = new SprintTestDataBuilder().withArchived(true).build();
        Issue issue = new IssueTestDataBuilder().withSprint(null).build();

        //Execution
        SprintIssueHistory sprintIssueHistory = SprintIssueHistory.createSprintIssueHistory(1L,sprint,issue);

        //Assertions
        assertThat(sprintIssueHistory.getIssue()).usingRecursiveComparison().isEqualTo(issue);
        assertThat(sprintIssueHistory.getArchivedSprint()).usingRecursiveComparison().isEqualTo(sprint);
        assertEquals(1L,sprintIssueHistory.getId());
    }

    @Test
    void createSprintIssueHistory_createsAndReturnsSprintIssueHistory2(){
        /*
        * this tests a success case
        * where Issue argument's 'currentSprint' field has another sprint that is active
        */

        //Setup
        Sprint sprint = new SprintTestDataBuilder().withId(2L).withArchived(true).build();
        Sprint sprint2 = new SprintTestDataBuilder().withId(34L).build();
        Issue issue = new IssueTestDataBuilder().withSprint(sprint2).build();

        //Execution
        SprintIssueHistory sprintIssueHistory = SprintIssueHistory.createSprintIssueHistory(1L,sprint,issue);

        //Assertions
        assertThat(sprintIssueHistory.getIssue()).usingRecursiveComparison().isEqualTo(issue);
        assertThat(sprintIssueHistory.getArchivedSprint()).usingRecursiveComparison().isEqualTo(sprint);
        assertEquals(1L,sprintIssueHistory.getId());
    }


    
}
