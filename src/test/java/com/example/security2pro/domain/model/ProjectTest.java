package com.example.security2pro.domain.model;

import com.example.security2pro.databuilders.ProjectTestDataBuilder;
import com.example.security2pro.dto.project.ProjectCreateDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {



    private static Object[] getProjectCreationParams(){
        String name = "test project1";
        String description = "this is just for testing";
        String descriptionNull = null;
        String emptyDescription ="";

        ProjectCreateDto projectCreateDto = new ProjectCreateDto(name,description);
        ProjectCreateDto projectCreateDto2 = new ProjectCreateDto(name,descriptionNull);

        return new Object[]{
                new Object[]{projectCreateDto,description},
                new Object[]{projectCreateDto2,emptyDescription}
        };
    }


    @ParameterizedTest
    @MethodSource("getProjectCreationParams")
    void createProject_extractProjectFromProjectCreateDto (ProjectCreateDto projectCreateDto,String resultDescription) {

        Project project = Project.createProject(projectCreateDto);
        assertEquals(projectCreateDto.getName(),project.getName());
        assertEquals(resultDescription,project.getDescription());
    }


    @Test
    void updateProject(){
        // Test data
        Long projectId = 1L;
        String originalName = "Original Name";
        String originalDescription = "Original Description";
        Project project = new Project(projectId, originalName, originalDescription);

        // Invoke the updateProject method
        String updatedName = "Updated Name";
        String updatedDescription = "Updated Description";
        project.updateProject(updatedName, updatedDescription);

        // Assertions
        assertThat(project.getName()).isEqualTo(updatedName);
        assertThat(project.getDescription()).isEqualTo(updatedDescription);
    }

    @Test
    public void testEndProject() {
        // Test data
        Project project = new ProjectTestDataBuilder().build();

        // Ensure the project is not archived initially
        assertThat(project.isArchived()).isFalse();

        // Invoke the endProject method
        project.endProject();

        // Assertions
        assertThat(project.isArchived()).isTrue();
    }






}
