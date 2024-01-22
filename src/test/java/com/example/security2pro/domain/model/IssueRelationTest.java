package com.example.security2pro.domain.model;

import com.example.security2pro.databuilders.IssueTestDataBuilder;
import com.example.security2pro.domain.enums.IssueStatus;
import com.example.security2pro.domain.model.issue.Issue;
import com.example.security2pro.domain.model.issue.IssueRelation;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class IssueRelationTest {

    /*
     * each test of this class will test at most one method of IssueRelation class.
     * every test of this class is a small test
     */

    @Test
    void createIssueRelation_createsAndReturnsIssueRelation() {
        /* success case test when arguments are valid
        *
        * there are two other tests for invalid arguments cases in this test class
        * , which are:
        * createIssueRelation_throwsException_whenAffectedIssueAndCauseIssueAreTheSame()
        * createIssueRelation_throwsException_whenCauseIssueStatusIsDone()
         */


        //Setup
        Issue affected = new IssueTestDataBuilder().withId(10L).build();
        Issue cause = new IssueTestDataBuilder().withId(20L).build();

        //Execution
        IssueRelation issueRelationCreated
                = IssueRelation.createIssueRelation
                (affected, cause, "relation description");

        //Assertions
        assertThat(issueRelationCreated.getAffectedIssue())
                .usingRecursiveComparison()
                .isEqualTo(affected);

        assertThat(issueRelationCreated.getCauseIssue())
                .usingRecursiveComparison()
                .isEqualTo(cause);

        assertEquals("relation description"
                , issueRelationCreated.getRelationDescription());
    }

    @Test
    void createIssueRelation_throwsException_whenAffectedIssueAndCauseIssueAreTheSame() {
        /*
         * in 'createIssueRelation' method, issues are considered the same if their id fields are the same
         * this test checks if IllegalArgumentException is thrown
         *  when 'affectedIssue' and 'causeIssue' has the same id
         */

        //Setup- the same id for both Issue instances
        Long sameId = 10L;
        Issue affected = new IssueTestDataBuilder().withId(sameId).build();
        Issue cause = new IssueTestDataBuilder().withId(sameId).build();

        //Assertions
        assertThrows(
                IllegalArgumentException.class
                , () -> IssueRelation.createIssueRelation(
                        affected, cause, "relation description"));
    }

    @Test
    void createIssueRelation_throwsException_whenCauseIssueStatusIsDone() {
        /*
         * 'createIssueRelation' method will validate if the status of 'causeIssue' is not 'DONE'
         * it is not allowed to add an issue with 'DONE' status as 'causeIssue'
         * However, change to any status of 'causeIssue' of an existing relationship is allowed
         */

        //Setup - cause issue with status 'DONE'
        Issue affected = new IssueTestDataBuilder().withId(10L).build();
        Issue cause = new IssueTestDataBuilder().withId(20L)
                .withStatus(IssueStatus.DONE)
                .build();

        //Execution & Assertions
        assertThrows(IllegalArgumentException.class,
                () -> IssueRelation.createIssueRelation(
                        affected, cause, "relation description"));
    }

    @Test
    void update_updatesDescriptionAndReturnsIssueRelation() {
        // tests if 'update' method only updates 'description' field of IssueRelation

        //Setup
        Issue affected = new IssueTestDataBuilder().withId(10L).build();
        Issue cause = new IssueTestDataBuilder().withId(20L).build();
        IssueRelation issueRelationCreated
                = IssueRelation.createIssueRelation(
                affected, cause, "original description");

        //Execution
        issueRelationCreated.update("updated description");

        //Assertions
        IssueRelation expectedIssueRelation = IssueRelation.createIssueRelation(
                affected, cause, "updated description");

        assertThat(issueRelationCreated)
                .usingRecursiveComparison()
                .isEqualTo(expectedIssueRelation);
        //check other fields stays the same as well
    }


}
