package com.example.security2pro.repository;

import com.example.security2pro.domain.model.Project;
import com.example.security2pro.repository.jpa_repository.ProjectJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase( connection = EmbeddedDatabaseConnection.H2)
public class ProjectRepositoryTest {

    @Autowired
    ProjectJpaRepository projectRepository;

    @Test
    public void ProjectRepository_Save_ReturnsSavedProject(){

        String name = "test project1";
        String description = "project1 ..  this is just for testing.. ";
        Project project = new Project(name,description);

        Project savedProject = projectRepository.save(project);

        assertThat(savedProject).isNotNull();
        assertThat(savedProject.getId()).isGreaterThan(0);
        assertThat(savedProject.getName()).isEqualTo(name);
        assertThat(savedProject.getDescription()).isEqualTo(description);
    }


    @Test
    public void ProjectRepository_GetReferenceById_ReturnsProjectWithGivenId(){

        String name = "test project1";
        String description = "project1 ..  this is just for testing.. ";
        Project project = new Project(name,description);

        projectRepository.save(project);

        Project foundProject = projectRepository.getReferenceById(project.getId());
        assertThat(foundProject).isNotNull();
        assertThat(foundProject.getName()).isEqualTo(name);
        assertThat(foundProject.getDescription()).isEqualTo(description);

    }

    @Test
    public void ProjectRepository_GetReferenceById_ThrowsExceptionWhenProjectWithGivenIdIsNotFound(){

        assertThrows(InvalidDataAccessApiUsageException.class,
                ()->projectRepository.getReferenceById(null));

        assertThrows(EntityNotFoundException.class,
                ()->{Project project=projectRepository.getReferenceById(22L);
                    project.getName();}
        );
    }


    // findAllMemberByProjectIdWithUser, DeleteProject will be included in integration test





}
