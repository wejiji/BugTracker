package com.example.bugtracker.smalltest.service;

import com.example.bugtracker.databuilders.ProjectMemberTestDataBuilder;
import com.example.bugtracker.databuilders.ProjectTestDataBuilder;
import com.example.bugtracker.databuilders.UserTestDataBuilder;
import com.example.bugtracker.domain.enums.ProjectMemberRole;
import com.example.bugtracker.domain.model.Project;
import com.example.bugtracker.domain.model.ProjectMember;
import com.example.bugtracker.domain.model.User;
import com.example.bugtracker.dto.projectmember.ProjectMemberCreateDto;
import com.example.bugtracker.dto.projectmember.ProjectMemberReturnDto;
import com.example.bugtracker.exception.directmessageconcretes.InvalidUserArgumentException;
import com.example.bugtracker.exception.directmessageconcretes.NotExistException;
import com.example.bugtracker.exception.directmessageconcretes.ProjectMemberInvalidRoleArgumentException;
import com.example.bugtracker.fake.repository.ProjectMemberRepositoryFake;
import com.example.bugtracker.fake.repository.ProjectRepositoryFake;
import com.example.bugtracker.fake.repository.UserRepositoryFake;
import com.example.bugtracker.repository.repository_interfaces.ProjectMemberRepository;
import com.example.bugtracker.repository.repository_interfaces.ProjectRepository;
import com.example.bugtracker.repository.repository_interfaces.UserRepository;
import com.example.bugtracker.service.ProjectMemberService;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProjectMemberServiceTest {

    private final ProjectMemberRepository projectMemberRepository = new ProjectMemberRepositoryFake();
    private final UserRepository userRepository = new UserRepositoryFake();
    private final ProjectRepository projectRepository = new ProjectRepositoryFake();
    private final ProjectMemberService projectMemberService
            = new ProjectMemberService(projectMemberRepository,userRepository,projectRepository);

    @Test
    void projectMemberCreateDto_createsAndReturnsProjectMemberCreateDto_givenFieldValues(){
        //Execution
        ProjectMemberCreateDto projectMemberCreateDto
                = new ProjectMemberCreateDto(1L,"testUsername", Set.of(ProjectMemberRole.ROLE_PROJECT_LEAD));
        //Assertions
        assertEquals(1L,projectMemberCreateDto.getProjectId().get());
        assertEquals("testUsername",projectMemberCreateDto.getUsername());
        assertEquals(Set.of(ProjectMemberRole.ROLE_PROJECT_LEAD),projectMemberCreateDto.getAuthorities());
    }

    @Test
    void projectMemberCreateDto_createsAndReturnsProjectMemberCreateDto_givenProjectMemberObject(){
        //Setup
        Project project = new ProjectTestDataBuilder().withId(1L).build();
        User user = new UserTestDataBuilder().withUsername("testUsername").build();
        ProjectMember projectMember
                = ProjectMember.createProjectMember(
                        null,project,user,Set.of(ProjectMemberRole.ROLE_PROJECT_LEAD));
        //Execution
        ProjectMemberCreateDto projectMemberCreateDto = new ProjectMemberCreateDto(projectMember);
        //Assertions
        assertEquals(1L,projectMemberCreateDto.getProjectId().get());
        assertEquals("testUsername",projectMemberCreateDto.getUsername());
        assertEquals(Set.of(ProjectMemberRole.ROLE_PROJECT_LEAD),projectMemberCreateDto.getAuthorities());
    }


    @Test
    void projectMemberReturnDto_createsAndReturnsProjectMemberReturnDto_givenProjectMemberObject(){
        //Setup
        Project project = new ProjectTestDataBuilder().withId(1L).build();
        User user = new UserTestDataBuilder().withUsername("testUsername").build();
        ProjectMember projectMember
                = ProjectMember.createProjectMember(
                        10L,project,user,Set.of(ProjectMemberRole.ROLE_PROJECT_LEAD));

        //Execution
        ProjectMemberReturnDto projectMemberReturnDto = new ProjectMemberReturnDto(projectMember);

        //Assertions
        assertEquals(10L,projectMemberReturnDto.getId());
        assertEquals(user.getUsername(),projectMemberReturnDto.getUsername());
        assertEquals(user.getEmail(),projectMemberReturnDto.getEmail());
        assertEquals(Set.of(ProjectMemberRole.ROLE_PROJECT_LEAD),projectMemberReturnDto.getAuthorities());
    }

    @Test
    void projectMemberReturnDto_createsAndReturnsProjectMemberReturnDto_givenFieldValues(){
        //Execution
        ProjectMemberReturnDto projectMemberReturnDto
                = new ProjectMemberReturnDto(10L
                ,"testUsername"
                ,"test@gmail.com"
                ,Set.of(ProjectMemberRole.ROLE_PROJECT_LEAD));

        //Assertions
        assertEquals(10L,projectMemberReturnDto.getId());
        assertEquals("testUsername",projectMemberReturnDto.getUsername());
        assertEquals("test@gmail.com",projectMemberReturnDto.getEmail());
        assertEquals(Set.of(ProjectMemberRole.ROLE_PROJECT_LEAD),projectMemberReturnDto.getAuthorities());
    }

    @Test
    void createProjectMember_throwsException_givenNonExistentUser(){
        //Setup
        Project project = new ProjectTestDataBuilder().withId(1L).build();

        projectRepository.save(project);

        ProjectMemberCreateDto projectMemberCreateDto
                = new ProjectMemberCreateDto(
                        project.getId(), "testUsername", Set.of(ProjectMemberRole.ROLE_PROJECT_LEAD));

        //Execution & Assertions
        assertThrows(NotExistException.class, () -> projectMemberService.createProjectMember(projectMemberCreateDto));
    }

    @Test
    void createProjectMember_throwsException_givenProjectMemberWithDuplicateUser() {
        //Setup
        User user = new UserTestDataBuilder().withUsername("testUsername").build();
        Project project = new ProjectTestDataBuilder().withId(1L).build();
        ProjectMember projectMember
                = ProjectMember.createProjectMember(
                        null,project,user,Set.of(ProjectMemberRole.ROLE_PROJECT_MEMBER));

        projectRepository.save(project);
        userRepository.save(user);
        projectMemberRepository.save(projectMember);

        ProjectMemberCreateDto projectMemberCreateDto
                = new ProjectMemberCreateDto(
                        project.getId(), "testUsername", Set.of(ProjectMemberRole.ROLE_PROJECT_LEAD));

        //Execution & Assertions
        assertThrows(InvalidUserArgumentException.class,
                () -> projectMemberService.createProjectMember(projectMemberCreateDto));

    }

    @Test
    void createProjectMember_createsAndReturnsProjectMember_givenProjectMemberCreateDto(){
        //Setup
        User user = new UserTestDataBuilder().withUsername("testUsername").build();
        Project project = new ProjectTestDataBuilder().withId(1L).build();

        projectRepository.save(project);
        userRepository.save(user);

        assertThat(projectMemberRepository.findAllByProjectId(project.getId())).isEmpty();
        ProjectMemberCreateDto projectMemberCreateDto
                = new ProjectMemberCreateDto(
                        project.getId(), "testUsername", Set.of(ProjectMemberRole.ROLE_PROJECT_LEAD));

        //Execution
        ProjectMemberReturnDto projectMemberReturnDto =projectMemberService.createProjectMember(projectMemberCreateDto);

        //Assertions
        Set<ProjectMember> projectMemberSet = projectMemberRepository.findAllByProjectId(project.getId());
        assertEquals(1,projectMemberSet.size());
        ProjectMember projectMemberFound = projectMemberSet.stream().findAny().get();
        assertEquals(projectMemberFound.getId(), projectMemberReturnDto.getId());
        assertEquals(projectMemberFound.getAuthorities(), projectMemberReturnDto.getAuthorities());
        assertEquals(projectMemberFound.getUser().getEmail(), projectMemberReturnDto.getEmail());
    }

    @Test
    void updateRole_throwsException_givenEmptyRoleSet(){
        //Setup
        ProjectMember projectMember = new ProjectMemberTestDataBuilder().withId(10L).build();
        projectMemberRepository.save(projectMember);
        //Execution & Assertions
        assertThrows(ProjectMemberInvalidRoleArgumentException.class,
                ()->  projectMemberService.updateRole(10L,Collections.emptySet()));
    }

    @Test
    void updateRole_updatesProjectMemberRoles_givenUpdatedRoleSet(){
        //Setup
        ProjectMember projectMember = new ProjectMemberTestDataBuilder()
                .withId(10L)
                .withAuthorities(Set.of(ProjectMemberRole.ROLE_PROJECT_MEMBER))
                .build();
        projectMemberRepository.save(projectMember);

        //Execution
        projectMemberService.updateRole(projectMember.getId() , Set.of(ProjectMemberRole.ROLE_PROJECT_LEAD));
        //Assertions
        ProjectMember projectMemberUpdated=projectMemberRepository.getReferenceById(10L);
        assertEquals(Set.of(ProjectMemberRole.ROLE_PROJECT_LEAD),projectMemberUpdated.getAuthorities());
    }

    @Test
    void deleteById_throwsException_givenNonExistentId(){
        assertThrows(NoSuchElementException.class,
                ()-> projectMemberService.deleteById(10L));
    }

    @Test
    void deleteById_deletesProjectMember_givenProjectMemberId(){
        //Setup
        ProjectMember projectMember = new ProjectMemberTestDataBuilder().withId(10L).build();
        projectMemberRepository.save(projectMember);

        assertThat(projectMemberRepository.findById(10L)).isPresent();
        //Execution
        projectMemberService.deleteById(10L);
        //Assertions
        assertThat(projectMemberRepository.findById(10L)).isEmpty();
    }



}
