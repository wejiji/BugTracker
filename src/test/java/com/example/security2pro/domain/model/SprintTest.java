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

class SprintTest {

    /*
     *
     * Clock can be set to any fixed instant in this test, whether in the future or the past.
     *
     * 'Sprint' instances are sometimes constructed by using 'SprintTestData' builder in this test class.
     * When 'SprintTestData' is instantiated,
     * each field is initialized with default value when no argument is passed for the field.
     *
     * Each test of this class will test at most one method of 'Sprint' class
     * Every test of this class is a small test
     *
     * */

    private Clock clock
            = Clock.fixed(
            ZonedDateTime.of(
                    2023,
                    1,
                    1,
                    1,
                    10,
                    10,
                    1,
                    ZoneId.systemDefault()).toInstant()
            , ZoneId.systemDefault());

    private Project project = new ProjectTestDataBuilder()
            .withId(1L)
            .withName("projectName")
            .withDescription("projectDescription")
            .build();

    @Test
    void createSprint_createsAndReturnsSprint() {
        /*
         * Success case: the 'createSprint' method creates and returns a new 'Sprint' object
         * when the 'startDate' argument date is earlier than 'endDate' argument date.
         */

        //Setup (valid dates)
        LocalDateTime endDate = LocalDateTime.now(clock);
        LocalDateTime startDate = LocalDateTime.now(clock).minusDays(1);

        //Execution
        Sprint sprintWithValidDates = Sprint.createSprint(
                1L
                , project
                , "originalName"
                , "originalDescription"
                , startDate
                , endDate);


        //Assertions
        assertThat(sprintWithValidDates).satisfies(sprint -> {
            assertSprintFields(sprint,
                    1L,
                    project,
                    "originalName",
                    "originalDescription",
                    startDate,
                    endDate,
                    false);
        });
    }

    @Test
    void createSprint_throwsException_givenInvalidDates() {
        /*
         * Verifies that the 'createSprint' method throws an IllegalArgumentException
         * when the 'endDate' argument date is earlier than the 'startDate' argument date.
         */

        //Setup (invalid dates)
        LocalDateTime endDate = LocalDateTime.now(clock);
        LocalDateTime startDate = LocalDateTime.now(clock).plusDays(1);

        //Execution& Assertions
        assertThrows(IllegalArgumentException.class,
                () -> Sprint.createSprint(
                        1L,
                        project,
                        "originalName",
                        "originalDescription",
                        startDate,
                        endDate));
    }


    @Test
    void createDefaultSprint_createsAndReturnsSprintWithDefaultValues() {
        /*
         * Verifies that the 'createDefaultSprint' method correctly
         * creates and returns a 'Sprint' object initialized with default values.
         */

        //Setup
        Project project = new ProjectTestDataBuilder().build();
        LocalDateTime startDate = LocalDateTime.now(clock);

        //Execution
        Sprint sprint = Sprint.createDefaultSprint(project, startDate);

        //Assertions
        assertSprintFields(sprint,
                null,
                project,
                sprint.getName(),
                sprint.getDescription(),
                startDate,
                startDate.plusDays(14),
                false);
    }


    @Test
    void update_updatesAnsReturnsUpdatedSprint() {
        /*
         * Verifies that the 'update' method correctly
         * updates and returns the updated 'Sprint' object.
         */

        //Setup
        String updatedName = "updated name";
        String updatedDescription = "updated description";
        LocalDateTime updatedStartDate = LocalDateTime.now(clock);
        LocalDateTime updatedEndDate = LocalDateTime.now(clock).plusDays(1);

        Sprint sprint = new SprintTestDataBuilder()
                .withId(1L)
                .withProject(project)
                .withName("originalName")
                .withDescription("originalDescription")
                .withStartDate(LocalDateTime.now(clock))
                .withEndDate(LocalDateTime.now(clock).plusDays(1))
                .build();

        //Execution
        sprint = sprint.update(updatedName, updatedDescription, updatedStartDate, updatedEndDate);

        //Assertions
        assertSprintFields(sprint, 1L, project, updatedName, updatedDescription, updatedStartDate, updatedEndDate, false);
    }


    @Test
    void update_throwsException_WhenStartDateAfterEndDate() {
        /*
         * Verifies that the 'update' method throws an IllegalArgumentException
         * when the 'endDate' argument date is earlier than the 'startDate' argument date.
         */

        //Setup
        Sprint sprint = new SprintTestDataBuilder()
                .withId(1L)
                .withProject(project)
                .withName("originalName")
                .withDescription("originalDescription")
                .withStartDate(LocalDateTime.now(clock))
                .withEndDate(LocalDateTime.now(clock).plusDays(1))
                .build();

        //Execution & Assertions
        LocalDateTime updatedStartDate = LocalDateTime.now(clock).plusDays(1);
        LocalDateTime updatedEndDate = LocalDateTime.now(clock);
        assertThrows(IllegalArgumentException.class,
                () -> sprint.update(
                        "updated name"
                        , "updated description"
                        , updatedStartDate
                        , updatedEndDate));
    }


    @Test
    void completeSprint_whenEndDateInThePast() {
        /*
         * Verifies that the 'completeSprint' method sets 'archived' field to true.
         * Also verifies that, when the original 'endDate' field date is in the past,
         * the method does not reset the 'endDate'
         */

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
        assertSprintFields(sprintWithPastEndDate, 1L, project, "originalName", "originalDescription"
                , startDate
                , originalEndDate
                , true);
    }

    @Test
    void completeSprint_whenEndDateInTheFuture() {
        /*
         * Verifies that the 'completeSprint' method sets 'archived' field to true.
         * Also ensures that, when the original 'endDate' field date is in the future,
         * the method resets the 'endDate' to the given point in time, usually the current instant.
         */

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
        assertSprintFields(sprintWithFutureEndDate, 1L, project, "originalName", "originalDescription"
                , startDate
                , updatedEndDate
                , true);
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
        assertEquals(archived, sprint.isArchived());
    }


}
