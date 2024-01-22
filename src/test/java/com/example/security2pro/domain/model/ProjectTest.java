package com.example.security2pro.domain.model;

import com.example.security2pro.databuilders.ProjectTestDataBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {

    /*
     * when ProjectTestData is instantiated, each field is initialized with default value when no argument is passed for the field,
     *
     * each test of this class will test at most one method of Project class
     * every test of this class is a small test
     */


    public void createProject_createsAndReturnsProject(){
        //tests success case

        //Execution
        Project project = Project.createProject(1L,"projectName","this is just for testing");

        //Assertions
        assertEquals(1L,project.getId());
        assertEquals("projectName",project.getName());
        assertEquals("this is just for testing",project.getDescription());
        assertFalse(project.isArchived());
    }

    public void createProject_createsAndReturnsProjectWithEmptyDescription_whenPassedNullDescription(){
        /* success case test
        *
        * tests if 'description' field is set to empty String
        * when null is passed
        *
        * also tests if auto generated id is assigned
        * when null is passed as 'id' field of Project
        */

        //Execution
        Project project = Project.createProject(null,"projectName",null);

        //Assertions
        assertNull(project.getId());
        assertEquals("projectName",project.getName());
        assertEquals("",project.getDescription());
        assertFalse(project.isArchived());
    }

    @Test
    void updateProject_updatesNameAndDescription(){
        //updates 'name' and 'description' fields

        //Setup
        Project project = new ProjectTestDataBuilder()
                .withId(1L)
                .withName("Original Name")
                .withDescription("Original Description")
                .withArchived(false)
                .build();

        // Execution
        project.updateProject("updatedName" , "updatedDescription");

        // Assertions
        assertEquals(1L, project.getId());
        assertEquals("updatedName", project.getName());
        assertEquals("updatedDescription", project.getDescription());
        assertFalse(project.isArchived());
    }

    @Test
    void updateProject_updatesProjectWithEmptyDescription_givenNullDescription(){
        // tests if description is set to empty String
        // when null is passed for 'description' field

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
        assertEquals("",project.getDescription());
        assertFalse(project.isArchived());
    }

    @Test
    void endProject_archivesProject() {
        // tests if 'archived' field is set to true,
        // while other fields will not be affected

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
        assertEquals(1L,project.getId());
        assertEquals("projectName",project.getName());
        assertEquals("projectDescription",project.getDescription());
    }






}
