package com.example.security2pro.domain.model;

import com.example.security2pro.databuilders.ProjectTestDataBuilder;
import com.example.security2pro.databuilders.SprintTestDataBuilder;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class SprintTest {

    private Clock clock = Clock.fixed(ZonedDateTime.of(2023,1,1,1,10,10,1, ZoneId.systemDefault()).toInstant(),ZoneId.systemDefault());

    private Project project = new ProjectTestDataBuilder()
            .withId(1L)
            .withName("projectName")
            .withDescription("projectDescription")
            .build();


    @Test
    public void createSprint_returnsSprint(){
        LocalDateTime endDate = LocalDateTime.now(clock);
        LocalDateTime startDate = LocalDateTime.now(clock).minusDays(1);
        //valid dates
        //Execution
        Sprint sprintWithValidDates = Sprint.createSprint(1L,project, "originalName", "originalDescription"
                , startDate, endDate);

        //Assertion
        assertThat(sprintWithValidDates).satisfies(sprint -> {assertSprintFields(sprint, 1L, project, "originalName", "originalDescription"
                , startDate, endDate,false);});
    }

    @Test
    public void createSprint_throwsExceptionGivenInvalidDates(){
        LocalDateTime endDate = LocalDateTime.now(clock);
        LocalDateTime startDate = LocalDateTime.now(clock).plusDays(1);
        //invalid dates
        assertThrows(IllegalArgumentException.class ,()-> Sprint.createSprint(1L,project, "originalName", "originalDescription",
                startDate, endDate));
    }


    @Test
    public void createDefaultSprint(){
        Project project = new ProjectTestDataBuilder().build();
        LocalDateTime startDate = LocalDateTime.now(clock);
        //Execution
        Sprint sprint = Sprint.createDefaultSprint(project,startDate);

        assertSprintFields(sprint, null, project, sprint.getName(), sprint.getDescription(), startDate, startDate.plusDays(14),false);
    }


    @Test
    public void updateFields(){
        String updatedName = "updated name";
        String updatedDescription = "updated description";
        LocalDateTime updatedStartDate = LocalDateTime.now(clock);
        LocalDateTime updatedEndDate= LocalDateTime.now(clock).plusDays(1);

        Sprint sprint = new SprintTestDataBuilder()
                .withId(1L)
                .withProject(project)
                .withName("originalName")
                .withDescription("originalDescription")
                .withStartDate(LocalDateTime.now(clock))
                .withEndDate(LocalDateTime.now(clock).plusDays(1))
                .build();

        //Execution
        sprint= sprint.updateFields(updatedName, updatedDescription, updatedStartDate,updatedEndDate);

        //Assertions
        assertSprintFields(sprint, 1L, project, updatedName, updatedDescription, updatedStartDate, updatedEndDate,false);
    }



    @Test
    public void updateFieldsReturnsNullWhenStartDateAfterEndDate(){
        String updatedName = "updated name";
        String updatedDescription = "updated description";
        LocalDateTime updatedStartDate = LocalDateTime.now(clock).plusDays(1);
        LocalDateTime updatedEndDate= LocalDateTime.now(clock);

        Sprint sprint = new SprintTestDataBuilder()
                .withId(1L)
                .withProject(project)
                .withName("originalName")
                .withDescription("originalDescription")
                .withStartDate(LocalDateTime.now(clock))
                .withEndDate(LocalDateTime.now(clock).plusDays(1))
                .build();

        assertThrows(IllegalArgumentException.class,()-> sprint.updateFields("originalName", "originalDescription"
                , updatedStartDate, updatedEndDate));
    }



    @Test
    public void completeSprintWithPastEndDate(){

        LocalDateTime startDate = LocalDateTime.now(clock).minusDays(1);
        LocalDateTime originalEndDate = LocalDateTime.now(clock).minusDays(1);
        LocalDateTime updatedEndDate = LocalDateTime.now(clock);

        Sprint sprintWithPastEndDate = new SprintTestDataBuilder()
                .withId(1L)
                .withProject(project)
                .withName("originalName")
                .withDescription("originalDescription")
                .withStartDate(startDate)
                .withEndDate(originalEndDate)
                .withArchived(false)
                .build();

        //Execution
        sprintWithPastEndDate.completeSprint(updatedEndDate);

        //Assertion
        // end date does not change if it was already complete
        // only archived field will change

        assertSprintFields(sprintWithPastEndDate, 1L, project, "originalName", "originalDescription"
                , startDate
                , originalEndDate
                ,true);
    }

    @Test
    public void completeSprintWithFutureEndDate(){

        LocalDateTime startDate = LocalDateTime.now(clock).minusDays(1);
        LocalDateTime originalEndDate = LocalDateTime.now(clock).plusDays(1);
        LocalDateTime updatedEndDate = LocalDateTime.now(clock);

        Sprint sprintWithFutureEndDate = new SprintTestDataBuilder()
                .withId(1L)
                .withProject(project)
                .withName("originalName")
                .withDescription("originalDescription")
                .withStartDate(startDate)
                .withEndDate(originalEndDate)
                .withArchived(false)
                .build();

        //Execution
        sprintWithFutureEndDate.completeSprint(updatedEndDate);

        //Assertion
        // end date will change if the sprint was not complete
        assertSprintFields(sprintWithFutureEndDate, 1L, project, "originalName", "originalDescription"
                , startDate
                , updatedEndDate
                ,true);
    }



    private void assertSprintFields(Sprint sprint, Long expectedId, Project expectedProject,
                                    String expectedName, String expectedDescription,
                                    LocalDateTime expectedStartDate, LocalDateTime expectedEndDate, boolean archived) {
        assertEquals(expectedId, sprint.getId());
        assertEquals(expectedProject, sprint.getProject());
        assertEquals(expectedName, sprint.getName());
        assertEquals(expectedDescription, sprint.getDescription());
        assertEquals(expectedStartDate, sprint.getStartDate());
        assertEquals(expectedEndDate, sprint.getEndDate());
        assertEquals(archived,sprint.isArchived());
    }


}
