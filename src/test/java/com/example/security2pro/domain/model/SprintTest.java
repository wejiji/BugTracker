package com.example.security2pro.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class SprintTest {

    private Project project;
    private Long sprintId;
    private String originalName;
    private String originalDescription;

    private LocalDateTime now;
    private LocalDateTime earlierThanNow;
    private LocalDateTime laterThanNow;


    @BeforeEach
    void setUp() {
        project = mock(Project.class);
        sprintId = 8L;
        originalName = "original name";
        originalDescription = "original description";

        now = LocalDateTime.now();
        earlierThanNow = LocalDateTime.now().minusDays(1);
        laterThanNow= LocalDateTime.now().plusDays(1);
    }


    @Test
    public void completeSprint(){
        Sprint sprintWithPastEndDate = new Sprint(sprintId,project,originalName,originalDescription,earlierThanNow,earlierThanNow);
        Sprint sprintWithFutureEndDate = new Sprint(sprintId,project,originalName,originalDescription,earlierThanNow,laterThanNow);
        assertFalse(sprintWithPastEndDate.isArchived());
        assertFalse(sprintWithFutureEndDate.isArchived());

        //Execution
        sprintWithPastEndDate.completeSprint();
        sprintWithFutureEndDate.completeSprint();

        //Assertion
        assertTrue(sprintWithPastEndDate.isArchived());
        assertTrue(sprintWithFutureEndDate.isArchived());

        assertEquals(sprintWithPastEndDate.getEndDate(),earlierThanNow);
        assertTrue(sprintWithPastEndDate.getEndDate().isBefore(LocalDateTime.now()));
    }

    @Test
    public void updateFields(){
        Sprint sprint = new Sprint(sprintId,project,originalName,originalDescription,earlierThanNow,now);

        String updatedName = "updated name";
        String updatedDescription = "updated description";
        LocalDateTime updatedStartDate =LocalDateTime.now().plusDays(5);
        LocalDateTime updatedEndDate = LocalDateTime.now().plusDays(10);

        //Execution
        Optional<Sprint> sprintOptional = sprint.updateFields(updatedName,updatedDescription,updatedStartDate,updatedEndDate);

        //Assertions
        assertThat(sprintOptional).isPresent();
        assertSprintFields(sprint, sprintId, project, updatedName, updatedDescription, updatedStartDate, updatedEndDate);
        assertFalse(sprint.isArchived());
    }



    @Test
    public void updateFieldsReturnsNullWhenStartDateAfterEndDate(){
        String updatedName = "updated name";
        String updatedDescription = "updated description";

        Sprint sprintWithInvalidStartDate = new Sprint(sprintId,project,originalName,originalDescription,laterThanNow,now);
        Sprint sprintWithValidStartDate = new Sprint(sprintId,project,originalName,originalDescription,earlierThanNow,now);

        //Execution
        Optional<Sprint> sprintOptionalInvalid = sprintWithInvalidStartDate.updateFields(updatedName,updatedDescription,laterThanNow,now);
        Optional<Sprint> sprintOptionalValid = sprintWithValidStartDate.updateFields(updatedName,updatedDescription,earlierThanNow,now);

        //Assertion
        assertThat(sprintOptionalInvalid).isEmpty();
        assertThat(sprintOptionalValid).isPresent();
        assertEquals(sprintOptionalValid.get(),sprintWithValidStartDate);//how does this work??? equals method was not overrided.

        assertSprintFields(sprintWithInvalidStartDate, sprintId, project, originalName, originalDescription, laterThanNow, now);
        assertSprintFields(sprintWithValidStartDate, sprintId, project, updatedName, updatedDescription, earlierThanNow, now);
    }



    @Test
    public void createSprint(){

        //Execution
        Optional<Sprint> sprintOptionalValid = Sprint.createSprint(project, originalName, originalDescription,
                earlierThanNow, now);
        Optional<Sprint> sprintOptionalInvalid = Sprint.createSprint(project, originalName, originalDescription,
                laterThanNow,  now);

        //Assertion
        assertThat(sprintOptionalValid).isPresent();
        assertThat(sprintOptionalValid.get()).satisfies(sprint -> {assertSprintFields(sprint, null, project, originalName, originalDescription, earlierThanNow, now);});
        assertThat(sprintOptionalInvalid).isEmpty();
    }

    private void assertSprintFields(Sprint sprint, Long expectedId, Project expectedProject,
                                    String expectedName, String expectedDescription,
                                    LocalDateTime expectedStartDate, LocalDateTime expectedEndDate) {
        assertEquals(expectedId, sprint.getId());
        assertEquals(expectedProject, sprint.getProject());
        assertEquals(expectedName, sprint.getName());
        assertEquals(expectedDescription, sprint.getDescription());
        assertEquals(expectedStartDate, sprint.getStartDate());
        assertEquals(expectedEndDate, sprint.getEndDate());
    }


}
