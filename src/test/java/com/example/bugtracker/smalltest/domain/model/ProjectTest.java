package com.example.bugtracker.smalltest.domain.model;

import com.example.bugtracker.databuilders.ProjectTestDataBuilder;
import com.example.bugtracker.domain.model.Project;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {

    // When ProjectTestData is instantiated, each field is initialized with default value when no argument is passed for the field.

    @Test
    void createProject_createsAndReturnsProject_givenFieldValues() {

        //Execution
        Project project = Project.createProject(1L, "projectName", "this is just for testing");

        //Assertions
        assertEquals(1L, project.getId());
        assertEquals("projectName", project.getName());
        assertEquals("this is just for testing", project.getDescription());
        assertFalse(project.isArchived());
    }

    @Test
    void createProject_createsAndReturnsProjectWithEmptyDescription_givenFieldValuesWithNullDescription() {
        /*
         * Tests if 'description' field is set to empty String when null is passed.
         * Also tests if auto generated id is assigned
         * when null is passed as 'id' field of Project.
         */

        //Execution
        Project project = Project.createProject(null, "projectName", null);

        //Assertions
        assertNull(project.getId());
        assertEquals("projectName", project.getName());
        assertEquals("", project.getDescription());
        assertFalse(project.isArchived());
    }

    @Test
    void updateProject_updatesNameAndDescription_givenUpdatedValues() {

        //Setup
        Project project = new ProjectTestDataBuilder()
                .withId(1L)
                .withName("Original Name")
                .withDescription("Original Description")
                .withArchived(false)
                .build();

        // Execution
        project.updateProject("updatedName", "updatedDescription");

        // Assertions
        assertEquals(1L, project.getId());
        assertEquals("updatedName", project.getName());
        assertEquals("updatedDescription", project.getDescription());
        assertFalse(project.isArchived());
    }

    @Test
    void updateProject_updatesProjectWithEmptyDescription_givenNullDescription() {

        //Setup
        Project project = new ProjectTestDataBuilder()
                .withId(1L)
                .withName("Original Name")
                .withDescription("Original Description")
                .withArchived(false)
                .build();

        // Execution
        project.updateProject("updatedName", null);

        // Assertions
        assertEquals(1L, project.getId());
        assertEquals("updatedName", project.getName());
        assertEquals("", project.getDescription());
        assertFalse(project.isArchived());
    }

    @Test
    void endProject_archivesProject() {
        // Tests if the 'archived' field is set to true.

        // Test data
        Project project = new ProjectTestDataBuilder()
                .withId(1L)
                .withName("projectName")
                .withDescription("projectDescription")
                .withArchived(false)
                .build();

        // Execution
        project.endProject();

        // Assertions
        assertTrue(project.isArchived());
        assertEquals(1L, project.getId());
        assertEquals("projectName", project.getName());
        assertEquals("projectDescription", project.getDescription());
    }


}
