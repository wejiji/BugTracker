package com.example.security2pro.service;

import com.example.security2pro.databuilders.ProjectMemberTestDataBuilder;
import com.example.security2pro.databuilders.ProjectTestDataBuilder;
import com.example.security2pro.databuilders.UserTestDataBuilder;
import com.example.security2pro.domain.enums.ProjectMemberRole;
import com.example.security2pro.domain.model.Project;
import com.example.security2pro.domain.model.ProjectMember;
import com.example.security2pro.domain.model.User;
import com.example.security2pro.dto.projectmember.ProjectMemberCreateDto;
import com.example.security2pro.dto.projectmember.ProjectMemberReturnDto;
import com.example.security2pro.repository.ProjectMemberRepositoryFake;
import com.example.security2pro.repository.ProjectRepositoryFake;
import com.example.security2pro.repository.UserRepositoryFake;
import com.example.security2pro.repository.repository_interfaces.ProjectMemberRepository;
import com.example.security2pro.repository.repository_interfaces.ProjectRepository;
import com.example.security2pro.repository.repository_interfaces.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProjectMemberServiceTest {

    private final ProjectMemberRepository projectMemberRepository = new ProjectMemberRepositoryFake();
    private final UserRepository userRepository = new UserRepositoryFake();
    private final ProjectRepository projectRepository = new ProjectRepositoryFake();


    private final ProjectMemberService projectMemberService = new ProjectMemberService(projectMemberRepository,userRepository,projectRepository);

    @Test
    public void testProjectMemberCreateDtoFromProjectMember(){
        ProjectMemberCreateDto projectMemberCreateDto
                = new ProjectMemberCreateDto(1L,"testUsername", Set.of(ProjectMemberRole.ROLE_PROJECT_LEAD));

        assertEquals(1L,projectMemberCreateDto.getProjectId().get());
        assertEquals("testUsername",projectMemberCreateDto.getUsername());
        assertEquals(Set.of(ProjectMemberRole.ROLE_PROJECT_LEAD),projectMemberCreateDto.getAuthorities());

    }

    @Test
    public void testProjectMemberCreateDtoFromParams(){
        Project project = new ProjectTestDataBuilder().withId(1L).build();
        User user = new UserTestDataBuilder().withUsername("testUsername").build();
        ProjectMember projectMember = ProjectMember.createProjectMember(null,project,user,Set.of(ProjectMemberRole.ROLE_PROJECT_LEAD));

        ProjectMemberCreateDto projectMemberCreateDto = new ProjectMemberCreateDto(projectMember);


        assertEquals(1L,projectMemberCreateDto.getProjectId().get());
        assertEquals("testUsername",projectMemberCreateDto.getUsername());
        assertEquals(Set.of(ProjectMemberRole.ROLE_PROJECT_LEAD),projectMemberCreateDto.getAuthorities());
    }


    @Test
    public void testProjectMemberReturnDtoFromProjectMember(){
        Project project = new ProjectTestDataBuilder().withId(1L).build();
        User user = new UserTestDataBuilder().withUsername("testUsername").build();

        ProjectMember projectMember = ProjectMember.createProjectMember(10L,project,user,Set.of(ProjectMemberRole.ROLE_PROJECT_LEAD));

        ProjectMemberReturnDto projectMemberReturnDto = new ProjectMemberReturnDto(projectMember);

        assertEquals(10L,projectMemberReturnDto.getId());
        assertEquals(user.getUsername(),projectMemberReturnDto.getUsername());
        assertEquals(user.getEmail(),projectMemberReturnDto.getEmail());
        assertEquals(Set.of(ProjectMemberRole.ROLE_PROJECT_LEAD),projectMemberReturnDto.getAuthorities());
    }

    @Test
    public void testProjectMemberReturnDtoFromParams(){
        ProjectMemberReturnDto projectMemberReturnDto = new ProjectMemberReturnDto(10L,"testUsername","test@gmail.com",Set.of(ProjectMemberRole.ROLE_PROJECT_LEAD));

        assertEquals(10L,projectMemberReturnDto.getId());
        assertEquals("testUsername",projectMemberReturnDto.getUsername());
        assertEquals("test@gmail.com",projectMemberReturnDto.getEmail());
        assertEquals(Set.of(ProjectMemberRole.ROLE_PROJECT_LEAD),projectMemberReturnDto.getAuthorities());
    }



    @Test
    public void createProjectMember_throwsExceptionWhenUserNotExist(){
        Project project = new ProjectTestDataBuilder().withId(1L).build();

        projectRepository.save(project);

        ProjectMemberCreateDto projectMemberCreateDto = new ProjectMemberCreateDto(project.getId(), "testUsername", Set.of(ProjectMemberRole.ROLE_PROJECT_LEAD));

        //Execution & Assertions
        assertThrows(IllegalArgumentException.class, () -> projectMemberService.createProjectMember(projectMemberCreateDto));
    }

    @Test
    public void createProjectMember_throwsExceptionWhenProjectMemberWithTheSameUserExist() {
        User user = new UserTestDataBuilder().withUsername("testUsername").build();
        Project project = new ProjectTestDataBuilder().withId(1L).build();
        ProjectMember projectMember = ProjectMember.createProjectMember(null,project,user,Set.of(ProjectMemberRole.ROLE_PROJECT_MEMBER));

        projectRepository.save(project);
        userRepository.save(user);
        projectMemberRepository.save(projectMember);

        ProjectMemberCreateDto projectMemberCreateDto = new ProjectMemberCreateDto(project.getId(), "testUsername", Set.of(ProjectMemberRole.ROLE_PROJECT_LEAD));

        //Execution & Assertions
        assertThrows(IllegalArgumentException.class, () -> projectMemberService.createProjectMember(projectMemberCreateDto));

    }

    //not necessary
//    @Test
//    public void createProjectMember_throwsExceptionWhenAuthorityInvalid(){
//        User user = new UserTestDataBuilder().withUsername("testUsername").build();
//        Project project = new ProjectTestDataBuilder().withId(1L).build();
//        projectRepository.save(project);
//        userRepository.save(user);
//        ProjectMemberCreateDto projectMemberCreateDto = new ProjectMemberCreateDto(project.getId(), "testUsername", Set.of(UserRole.ROLE_TEAM_LEAD));
//
//        //Execution & Assertions
//        assertThrows(IllegalArgumentException.class, () -> projectMemberService.createProjectMember(projectMemberCreateDto));
//    }

    @Test
    public void createProjetMember_success(){
        User user = new UserTestDataBuilder().withUsername("testUsername").build();
        Project project = new ProjectTestDataBuilder().withId(1L).build();

        projectRepository.save(project);
        userRepository.save(user);

        assertThat(projectMemberRepository.findAllByProjectId(project.getId())).isEmpty();
        ProjectMemberCreateDto projectMemberCreateDto = new ProjectMemberCreateDto(project.getId(), "testUsername", Set.of(ProjectMemberRole.ROLE_PROJECT_LEAD));

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
    public void updateRole_throwsExceptionWhenRoleEmpty(){
        ProjectMember projectMember = new ProjectMemberTestDataBuilder().withId(10L).build();
        projectMemberRepository.save(projectMember);

        assertThrows(IllegalArgumentException.class, ()->  projectMemberService.updateRole(10L,Collections.emptySet()));
    }


    @Test
    public void updateRole_success(){
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
    public void deleteById_throwsExceptionWhenIdNotExist(){
        assertThrows(IllegalArgumentException.class, ()-> projectMemberService.deleteById(10L));
    }

    @Test
    public void deleteById_success(){
        ProjectMember projectMember = new ProjectMemberTestDataBuilder().withId(10L).build();
        projectMemberRepository.save(projectMember);

        assertThat(projectMemberRepository.findById(10L)).isPresent();
        //Execution
        projectMemberService.deleteById(10L);
        //Assertions
        assertThat(projectMemberRepository.findById(10L)).isEmpty();
    }



}
