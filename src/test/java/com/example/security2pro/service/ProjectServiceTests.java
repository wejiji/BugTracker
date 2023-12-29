package com.example.security2pro.service;

import com.example.security2pro.databuilders.*;
import com.example.security2pro.domain.enums.Role;
import com.example.security2pro.domain.model.*;
import com.example.security2pro.dto.project.ProjectCreateDto;
import com.example.security2pro.dto.project.ProjectDto;
import com.example.security2pro.dto.project.ProjectSimpleUpdateDto;
import com.example.security2pro.repository.IssueRepository;
import com.example.security2pro.repository.ProjectMemberRepository;
import com.example.security2pro.repository.ProjectRepository;
import com.example.security2pro.repository.SprintRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Set;
import static com.example.security2pro.domain.enums.Role.ROLE_PROJECT_LEAD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTests {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private SprintRepository sprintRepository;

    @Mock
    private IssueRepository issueRepository;

    @InjectMocks
    private ProjectService projectService;


    @Test
    public void startProject(){
        //Test data
        ProjectCreateDto projectCreateDto = mock(ProjectCreateDto.class);
        User user = mock(User.class);
        Set<Role> authorities = Set.of(ROLE_PROJECT_LEAD);

        //Setup
        Project project = new ProjectTestDataBuilder().withId(22L).build();
        ProjectMember projectMember = new ProjectMemberTestDataBuilder().withProject(project).withUser(user).build();

        try(MockedStatic<Project> projectStatic = Mockito.mockStatic(Project.class);
            MockedStatic<ProjectMember> projectMemberStatic = Mockito.mockStatic(ProjectMember.class);
        ){
            projectStatic.when(()->Project.createProject(projectCreateDto)).thenReturn(project);
            projectMemberStatic.when(()->ProjectMember.createProjectMember(project,user,authorities)).thenReturn(projectMember);
            when(projectMemberRepository.save(Mockito.any(ProjectMember.class))).thenReturn(projectMember);
            when(projectRepository.save(Mockito.any(Project.class))).thenReturn(project);


            // Execution
            ProjectSimpleUpdateDto projectSimpleUpdateDto = projectService.startProject(projectCreateDto,user);

            // Verification
            InOrder inOrder = inOrder(Project.class,ProjectMember.class,projectMemberRepository,projectRepository);

            inOrder.verify(projectStatic,()->Project.createProject(projectCreateDto),times(1));// the static method was called with the right argument
            inOrder.verify(projectMemberStatic,()->ProjectMember.createProjectMember(project,user,authorities),times(1));// the static method was called with the right argument
            inOrder.verify(projectMemberRepository).save(projectMember);//verify if projectMemberRepository save was called with the right argument (test will fail if the wrong argument was passed)
            inOrder.verify(projectRepository).save(project);//verify if projectRepository save was called with the right argument
            inOrder.verifyNoMoreInteractions();

            //Assertions
            assertEquals(projectSimpleUpdateDto.getId(),project.getId());
            assertEquals(projectSimpleUpdateDto.getName(),project.getName());
            assertEquals(projectSimpleUpdateDto.getDescription(),project.getDescription());
        }
    }


    @Test
    public void getProjectDetails(){

        Long projectId = 23L;
        Project project = new ProjectTestDataBuilder().withId(projectId).build();
        User user = mock(User.class);
        ProjectMember projectMember = new ProjectMemberTestDataBuilder().withProject(project).withUser(user).build();
        Set<ProjectMember> projectMembers = Set.of(projectMember);
        Sprint sprint = new SprintTestDataBuilder().build();
        Sprint sprint2 = new SprintTestDataBuilder().build();
        Sprint sprint3 = new SprintTestDataBuilder().build();
        Set<Sprint> sprints = Set.of(sprint,sprint2,sprint3);
        Issue issue = new IssueTestDataBuilder().build();
        Issue issue2 =  new IssueTestDataBuilder().build();
        Issue issue3 =  new IssueTestDataBuilder().build();
        Set<Issue> issues = Set.of(issue,issue2,issue3);

        // Mocking repository interactions
        when(projectRepository.getReferenceById(project.getId())).thenReturn(project);
        when(projectMemberRepository.findAllMemberByProjectIdWithUser(project.getId())).thenReturn(projectMembers);
        when(sprintRepository.findByProjectIdAndArchivedFalse(project.getId())).thenReturn(sprints);
        when(issueRepository.findByProjectIdAndArchivedFalse(project.getId())).thenReturn(issues);

        // Invocation of the service method
        ProjectDto projectDto = projectService.getProjectDetails(projectId);

        // Verification
        verify(projectRepository).getReferenceById(eq(projectId));
        verify(projectMemberRepository).findAllMemberByProjectIdWithUser(eq(projectId));
        verify(sprintRepository).findByProjectIdAndArchivedFalse(eq(projectId));
        verify(issueRepository).findByProjectIdAndArchivedFalse(eq(projectId));

//        // Assertion on DTO content
//        assertEquals(project.getName(),projectDto.getProjectName());
//        // Assert project members usernames
//        Set<String> expectedUsernames = projectMembers.stream().map(member -> member.getUser().getUsername()).collect(Collectors.toSet());
//        assertEquals(expectedUsernames, projectDto.getProjectMembers());
//        // Assert sprints
//        Set<SprintUpdateDto> expectedSprints= sprints.stream().map(SprintUpdateDto::new).collect(Collectors.toSet());
//        assertEquals(expectedSprints, projectDto.getSprints());
//        // Assert issues
//        Set<IssueSimpleDto>  expectedIssues= issues.stream().map(IssueSimpleDto::new).collect(Collectors.toSet());
//        assertEquals(expectedIssues, projectDto.getIssues());

        ProjectDto expectedDto = TestDataHelper.createProjectDtoWithTestData(project, projectMembers, sprints, issues);
        assertEquals(expectedDto, projectDto);

    }




    @Test
    public void updateProject_verifyCalls(){
        // mock is used to verify method calls

        // Test data
        Long projectId = 4L;
        String updatedName = "Updated Project Name";
        String updatedDescription = "Updated Project Description";
        ProjectSimpleUpdateDto projectUpdateDto = new ProjectSimpleUpdateDto(updatedName, updatedDescription);

        Project updatedProject = new ProjectTestDataBuilder().withId(projectId).withName(updatedName).withDescription(updatedDescription).build();

        // Mocking repository interactions
        Project project = mock(Project.class);
        when(projectRepository.getReferenceById(projectId)).thenReturn(project);

        // Invocation of the service method
        ProjectSimpleUpdateDto updatedProjectDto = projectService.updateProject(projectId, projectUpdateDto);

        // Verification
        InOrder inOrder = inOrder(projectRepository,project);
        inOrder.verify(projectRepository).getReferenceById(eq(projectId));
        inOrder.verify(project,times(1)).updateProject(eq(updatedName), eq(updatedDescription));
        inOrder.verify(project,times(1)).getId();
        verify(project,times(1)).getName();
        verify(project,times(1)).getDescription();
        verifyNoMoreInteractions(projectRepository,project);

    }



    @Test
    public void updateProject_verifyResult(){
        // when project entity class is mocked, it becomes not simple to update its fields.
        // therefore this method tests uses regular project class to verify if the result of 'updateProject' function is correct

        // Test data
        Long projectId = 4L;
        String updatedName = "Updated Project Name";
        String updatedDescription = "Updated Project Description";
        ProjectSimpleUpdateDto projectUpdateDto = new ProjectSimpleUpdateDto(updatedName, updatedDescription);

        // Mocking repository interactions
        Project project = new ProjectTestDataBuilder().build();
        when(projectRepository.getReferenceById(projectId)).thenReturn(project);

        // Invocation of the service method
        ProjectSimpleUpdateDto updatedProjectDto = projectService.updateProject(projectId, projectUpdateDto);

        // Assertion on the returned DTO
        assertEquals(updatedName, updatedProjectDto.getName());
        assertEquals(updatedDescription, updatedProjectDto.getDescription());
    }






//    @Test
//    public void ProjectService_StartProject_ReturnsProjectSimpleUpdateDto(){
//
//        String name = "test project1";
//        String description = "project1 ..  this is just for testing.. ";
//        Set<Role> authorities = Set.of(ROLE_PROJECT_LEAD);
//
//
//        ProjectCreateDto projectCreateDto
//                = new ProjectCreateDto(name,description);
//
//
//        Project createdProject = Project.createProject(projectCreateDto);
//        ProjectMember createdProjectMember = ProjectMember.createProjectMember(createdProject, user, authorities);
//        when(projectMemberRepository.save(Mockito.any(ProjectMember.class))).thenReturn(createdProjectMember);
//        when(projectRepository.save(Mockito.any(Project.class))).thenReturn(createdProject);
//
//        ProjectMember savedProjectMember =projectService.createFirstProjectMember(createdProject,user);
//        ProjectSimpleUpdateDto savedProject = projectService.startProject(projectCreateDto,user);
//
//        Assertions.assertThat(savedProjectMember).isNotNull();
//        Assertions.assertThat(savedProjectMember.getUser()).isEqualTo(user);
//
////        assertTrue(new ReflectionEquals(project).matches(savedProjectMember.getProject()));
//
//        Assertions.assertThat(savedProjectMember.getProject()).isEqualTo(createdProject);
//        Assertions.assertThat(savedProjectMember.getProject().getName()).isEqualTo(savedProject.getName());
//        Assertions.assertThat(savedProjectMember.getProject().getDescription()).isEqualTo(savedProject.getDescription());
//        Assertions.assertThat(savedProjectMember.getAuthorities()).isEqualTo(authorities);
//
//
//        Assertions.assertThat(savedProject).isNotNull();
//        Assertions.assertThat(savedProject.getName()).isEqualTo(name);
//        Assertions.assertThat(savedProject.getDescription()).isEqualTo(description);
//
//    }
//
//    @Test
//    public void ProjectService_CreateFirstProjectMember_RetrunsProjectMember(){
//        String name = "test project1";
//        String description = "project1 ..  this is just for testing.. ";
//        Project project = new Project(name,description);
//
//        when(projectMemberRepository.save(Mockito.any(ProjectMember.class))).thenReturn();
//
//
//    }




}
