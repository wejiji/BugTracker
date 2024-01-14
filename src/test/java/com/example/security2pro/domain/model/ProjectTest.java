package com.example.security2pro.domain.model;

import com.example.security2pro.databuilders.ProjectTestDataBuilder;
import com.example.security2pro.dto.project.ProjectCreateDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {


    public void createProject_retunsProject(){

        Project project = Project.createProject(1L,"projectName","this is just for testing");

        assertEquals(1L,project.getId());
        assertEquals("projectName",project.getName());
        assertEquals("this is just for testing",project.getDescription());
        assertFalse(project.isArchived());
    }

    public void createProject_savesEmptyDescription(){
        Project project = Project.createProject(null,"projectName",null);

        assertNull(project.getId());
        assertEquals(project.getName(),"projectName");
        assertEquals(project.getDescription(),"");
        assertFalse(project.isArchived());
    }

//    private static Object[] getProjectCreationParams(){
//
//        String descriptionNull = null;
//        String emptyDescription ="";
//
//        ProjectCreateDto projectCreateDto = new ProjectCreateDto(name,description);
//        ProjectCreateDto projectCreateDto2 = new ProjectCreateDto(name,descriptionNull);
//
//        return new Object[]{
//                new Object[]{projectCreateDto,description},
//                new Object[]{projectCreateDto2,emptyDescription}
//        };
//    }
//    @ParameterizedTest
//    @MethodSource("getProjectCreationParams")
//    void createProject_extractProjectFromProjectCreateDto (ProjectCreateDto projectCreateDto,String resultDescription) {
//
//        Project project = Project.createProject(projectCreateDto);
//        assertEquals(projectCreateDto.getName(),project.getName());
//        assertEquals(resultDescription,project.getDescription());
//    }

    @Test
    void updateProject(){

        Project project = new ProjectTestDataBuilder()
                .withId(1L)
                .withName("Original Name")
                .withDescription("Original Description")
                .build();
        assertThat(project.isArchived()).isFalse();

        // Execution
        project.updateProject("updatedName" , "updatedDescription");

        // Assertions
        assertEquals(project.getId(),1L);
        assertEquals(project.getName(),"updatedName");
        assertEquals(project.getDescription(),"updatedDescription");
        assertThat(project.isArchived()).isFalse();
    }

    @Test
    void updateProject_updateNullDescription(){

        Project project = new ProjectTestDataBuilder()
                .withId(1L)
                .withName("Original Name")
                .withDescription("Original Description")
                .build();
        assertThat(project.isArchived()).isFalse();

        // Execution
        project.updateProject("updatedName", null);

        // Assertions
        assertEquals(project.getId(),1L);
        assertEquals(project.getName(),"updatedName");
        assertEquals(project.getDescription(),"");
        assertThat(project.isArchived()).isFalse();
    }



    @Test
    public void testEndProject() {

        // Test data
        Project project = new ProjectTestDataBuilder()
                .withId(1L)
                .withName("projectName")
                .withDescription("projectDescription")
                .build();

        // Ensure the project is not archived initially
        assertThat(project.isArchived()).isFalse();

        // Invoke the endProject method
        project.endProject();

        // Assertions
        assertThat(project.isArchived()).isTrue();
        assertEquals(project.getId(),1L);
        assertEquals(project.getName(),"projectName");
        assertEquals(project.getDescription(),"projectDescription");
    }






}
