package com.example.security2pro.smalltest.domain.model;

import com.example.security2pro.databuilders.IssueTestDataBuilder;
import com.example.security2pro.domain.enums.IssueStatus;
import com.example.security2pro.domain.model.issue.Issue;
import com.example.security2pro.domain.model.issue.IssueRelation;
import com.example.security2pro.exception.directmessageconcretes.InvalidIssueRelationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class IssueRelationTest {

    @Test
    void createIssueRelation_createsAndReturnsIssueRelation_givenValidIssueRelation() {
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
    void createIssueRelation_throwsException_givenTheSameAffectedIssueAndCauseIssue() {
        /*
         * this test checks if an exception is thrown
         * when 'affectedIssue' and 'causeIssue' has the same id
         */

        //Setup- the same id for both Issue instances
        Long sameId = 10L;
        Issue affected = new IssueTestDataBuilder().withId(sameId).build();
        Issue cause = new IssueTestDataBuilder().withId(sameId).build();

        //Assertions
        assertThrows(
                InvalidIssueRelationException.class
                , () -> IssueRelation.createIssueRelation(
                        affected, cause, "relation description"));
    }

    @Test
    void createIssueRelation_throwsException_givenCauseIssueWithDoneStatus() {
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
        assertThrows(InvalidIssueRelationException.class,
                () -> IssueRelation.createIssueRelation(
                        affected, cause, "relation description"));
    }

    @Test
    void update_updatesDescriptionAndReturnsIssueRelation_givenUpdatedDescription() {
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
    }


}
