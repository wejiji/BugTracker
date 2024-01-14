package com.example.security2pro.domain.model;

import com.example.security2pro.databuilders.IssueTestDataBuilder;
import com.example.security2pro.databuilders.SprintTestDataBuilder;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SprintIssueHistoryTest {
    
    @Test
    public void createSprintIssueHistory_exceptionForNotArchivedSprint(){
        //sprint is not archived yet
        // -> exception thrown
        
        Sprint sprint = new SprintTestDataBuilder().build();
        Issue issue = new IssueTestDataBuilder().build();

        //Execution & Assertion
        assertThrows(IllegalArgumentException.class,()->SprintIssueHistory.createSprintIssueHistory(1L,sprint,issue));
    }
    
    @Test
    public void createSprintIssueHistory_exceptionForIssueCurrentSprintNotResetYet(){
        //issue's currentSprint field is not reset (issue's currentSprint field still has old archived sprint)
        //-> exception thrown

        Sprint sprint = new SprintTestDataBuilder().withArchived(true).build();
        Issue issue = new IssueTestDataBuilder().withSprint(sprint).build();
        //Execution & Assertion
        assertThrows(IllegalArgumentException.class,()->SprintIssueHistory.createSprintIssueHistory(1L,sprint,issue));
    }
    
    @Test
    public void createSprintIssueHistory_returnsSprintIssueHistory(){
        //issue's currentSprint field is null

        Sprint sprint = new SprintTestDataBuilder().withArchived(true).build();
        Issue issue = new IssueTestDataBuilder().withSprint(null).build();

        //Execution
        SprintIssueHistory sprintIssueHistory = SprintIssueHistory.createSprintIssueHistory(1L,sprint,issue);
        //Assertions
        assertThat(sprintIssueHistory.getIssue()).usingRecursiveComparison().isEqualTo(issue);
        assertThat(sprintIssueHistory.getArchivedSprint()).usingRecursiveComparison().isEqualTo(sprint);
        assertEquals(sprintIssueHistory.getId(),1L);
    }

    @Test
    public void createSprintIssueHistory_returnsSprintIssueHistory2(){
        //issue's currentSprint field has another active sprint

        Sprint sprint = new SprintTestDataBuilder().withId(2L).withArchived(true).build();

        Sprint sprint2 = new SprintTestDataBuilder().withId(34L).build();
        Issue issue = new IssueTestDataBuilder().withSprint(sprint2).build();

        //Execution
        SprintIssueHistory sprintIssueHistory = SprintIssueHistory.createSprintIssueHistory(1L,sprint,issue);
        //Assertions
        assertThat(sprintIssueHistory.getIssue()).usingRecursiveComparison().isEqualTo(issue);
        assertThat(sprintIssueHistory.getArchivedSprint()).usingRecursiveComparison().isEqualTo(sprint);
        assertEquals(sprintIssueHistory.getId(),1L);
    }


    
}
