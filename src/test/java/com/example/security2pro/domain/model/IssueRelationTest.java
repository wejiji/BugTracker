package com.example.security2pro.domain.model;

import com.example.security2pro.databuilders.IssueTestDataBuilder;
import com.example.security2pro.domain.enums.IssueStatus;
import com.example.security2pro.repository.IssueRepositoryFake;
import com.example.security2pro.repository.repository_interfaces.IssueRepository;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class IssueRelationTest {

    private final IssueRepository issueRepository = new IssueRepositoryFake();


    @Test
    public void createIssueRelation_success(){
        Issue affected = new IssueTestDataBuilder().withId(10L).build();
        Issue cause = new IssueTestDataBuilder().withId(20L).build();

        //Execution
        IssueRelation issueRelationCreated = IssueRelation.createIssueRelation(affected,cause,"cause is the root cause of affected");

        //Assertions
        assertThat(issueRelationCreated.getAffectedIssue()).usingRecursiveComparison().isEqualTo(affected);
        assertThat(issueRelationCreated.getCauseIssue()).usingRecursiveComparison().isEqualTo(cause);
        assertEquals("cause is the root cause of affected",issueRelationCreated.getRelationDescription());

    }

    @Test
    public void createIssueRelation_throwsExceptionWhenAffectedIssueAndCauseIssueAreTheSame(){

        Issue affected = new IssueTestDataBuilder().withId(10L).build();
        Issue cause = new IssueTestDataBuilder().withId(10L).build();

        assertThrows(IllegalArgumentException.class,()->IssueRelation.createIssueRelation(affected,cause,"cause is the root cause of affected"));

    }

    @Test
    public void createIssueRelation_throwsExceptionWhenCausIssueStatusDone(){

        Issue affected = new IssueTestDataBuilder().withId(10L).build();
        Issue cause = new IssueTestDataBuilder().withId(10L).withStatus(IssueStatus.DONE).build();

        assertThrows(IllegalArgumentException.class,()->IssueRelation.createIssueRelation(affected,cause,"cause is the root cause of affected"));
    }

    @Test
    public void update(){

        Issue affected = new IssueTestDataBuilder().withId(10L).build();
        Issue cause = new IssueTestDataBuilder().withId(20L).build();

        issueRepository.save(affected);
        issueRepository.save(cause);

        IssueRelation issueRelationCreated = IssueRelation.createIssueRelation(affected,cause,"this is original description");

        //Execution
        issueRelationCreated.update("this is updated description");
        affected.addIssueRelation(issueRelationCreated);
        issueRepository.save(affected);

        Issue issueFound = issueRepository.findById(affected.getId()).get();

        IssueRelation issueRelationFound = issueFound.getIssueRelationSet().stream()
                .filter(issueRelation -> issueRelation.getAffectedIssue().getId().equals(affected.getId()))
                .findAny().get();

        assertEquals(cause.getId(),issueRelationFound.getCauseIssue().getId());
        assertEquals("this is updated description",issueRelationFound.getRelationDescription());
    }







}
