package com.example.security2pro.service;

import com.example.security2pro.databuilders.*;
import com.example.security2pro.domain.enums.IssuePriority;
import com.example.security2pro.domain.enums.IssueStatus;
import com.example.security2pro.domain.enums.IssueType;
import com.example.security2pro.domain.enums.ProjectMemberRole;
import com.example.security2pro.domain.enums.UserRole;
import com.example.security2pro.domain.model.*;
import com.example.security2pro.domain.model.issue.Issue;
import com.example.security2pro.dto.issue.IssueSimpleDto;
import com.example.security2pro.dto.project.ProjectCreateDto;
import com.example.security2pro.dto.project.ProjectDto;
import com.example.security2pro.dto.project.ProjectSimpleUpdateDto;
import com.example.security2pro.dto.sprint.SprintUpdateDto;
import com.example.security2pro.repository.IssueRepositoryFake;
import com.example.security2pro.repository.ProjectMemberRepositoryFake;
import com.example.security2pro.repository.ProjectRepositoryFake;
import com.example.security2pro.repository.SprintRepositoryFake;
import com.example.security2pro.repository.repository_interfaces.IssueRepository;
import com.example.security2pro.repository.repository_interfaces.ProjectMemberRepository;
import com.example.security2pro.repository.repository_interfaces.ProjectRepository;
import com.example.security2pro.repository.repository_interfaces.SprintRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


import static com.example.security2pro.domain.enums.ProjectMemberRole.ROLE_PROJECT_LEAD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectServiceTests {

    private final ProjectRepository projectRepository= new ProjectRepositoryFake();
    private final ProjectMemberRepository projectMemberRepository= new ProjectMemberRepositoryFake();
    private final SprintRepository sprintRepository= new SprintRepositoryFake();
    private final IssueRepository issueRepository= new IssueRepositoryFake();
    private final ProjectService projectService= new ProjectService(projectRepository,projectMemberRepository,sprintRepository,issueRepository);



    @Test
    public void testProjectCreateDtoFromParams(){
        //Execution
        ProjectCreateDto projectCreateDto = new ProjectCreateDto("projectName","projectDescription");
        //Assertions
        assertEquals("projectName", projectCreateDto.getName());
        assertEquals("projectDescription", projectCreateDto.getDescription());
    }

//    @Test
//    public void testProjectCreateDtoFromProject(){ //maybe not necessary....
//        Project project = Project.createProject(null,"projectName","projectDescription");
//        //Execution
//        ProjectCreateDto projectCreateDto = new ProjectCreateDto(project);
//        //Assertions
//        assertEquals("projectName", projectCreateDto.getName());
//        assertEquals("projectDescription", projectCreateDto.getDescription());
//    }
    @Test
    public void testProjectSimpleUpdateDtoFromParams(){
        //Execution
        ProjectSimpleUpdateDto projectSimpleUpdateDto = new ProjectSimpleUpdateDto(1L,"updatedProjectName","updatedProjectDescription");
        //Assertions
        assertEquals(1L,projectSimpleUpdateDto.getId());
        assertEquals("updatedProjectName",projectSimpleUpdateDto.getName());
        assertEquals("updatedProjectDescription",projectSimpleUpdateDto.getDescription());
    }

    @Test
    public void testProjectUpdateDtoFromProject(){ //maybe not necessary....
        Project project = Project.createProject(null,"projectName","projectDescription");
        //Execution
        ProjectSimpleUpdateDto projectSimpleUpdateDto = new ProjectSimpleUpdateDto(project);
        //Assertions
        assertEquals("projectName", projectSimpleUpdateDto.getName());
        assertEquals("projectDescription", projectSimpleUpdateDto.getDescription());
    }
    @Test
    public void testProjectDto(){
        //Setup
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        Project project = Project.createProject(1L,"projectName","projectDescription");
        Sprint sprint1 = Sprint.createSprint(4L,project,"sprintName-1","sprintDescription-1",startDate,endDate);
        Sprint sprint2 = Sprint.createSprint(5L,project,"sprintName-2","sprintDescription-2",startDate,endDate);
        User teamMemberUser1 = User.createUser(7L,"usernameMember-1","passwordMember-1","firstNameMember-1","lastNameMember-1","teamMember-1@gmail.com",Set.of(UserRole.ROLE_TEAM_MEMBER),true);
        User teamMemberUser2 = User.createUser(8L,"usernameMember-2","passwordMember-2","firstNameMember-2","lastNameMember-2","teamMember-2@gmail.com",Set.of(UserRole.ROLE_TEAM_MEMBER),true);
        User teamLeadUser = User.createUser(10L,"usernameLead","passwordLead","firstNameLead","lastNameLead","lead@gmail.com",Set.of(UserRole.ROLE_TEAM_LEAD),true);
        ProjectMember projectMember1 = ProjectMember.createProjectMember(11L, project, teamLeadUser, Set.of(ROLE_PROJECT_LEAD));
        ProjectMember projectMember2 = ProjectMember.createProjectMember(12L, project, teamMemberUser1, Set.of(ProjectMemberRole.ROLE_PROJECT_MEMBER));
        ProjectMember projectMember3 = ProjectMember.createProjectMember(13L, project, teamMemberUser2, Set.of(ProjectMemberRole.ROLE_PROJECT_MEMBER));

        Set<User> userSet1 = Set.of(teamMemberUser1);
        Set<User> userSet2 = Set.of(teamMemberUser2,teamLeadUser);
        Issue issue1 = Issue.createIssue(15L,project,userSet1,"issueTitle-1","issueDescription-1",IssuePriority.LOWEST,IssueStatus.IN_REVIEW, IssueType.NEW_FEATURE,sprint1);
        Issue issue2 = Issue.createIssue(16L,project,null,"issueTitle-2","issueDescription-2",IssuePriority.MEDIUM,IssueStatus.TODO,IssueType.NEW_FEATURE,null);
        Issue issue3 = Issue.createIssue(17L,project,userSet2,"issueTitle-3","issueDescription-3",IssuePriority.HIGH,IssueStatus.IN_PROGRESS, IssueType.BUG,sprint2);
        Issue issue4 = Issue.createIssue(18L,project,userSet2,"issueTitle-4","issueDescription-4",IssuePriority.HIGHEST,IssueStatus.IN_REVIEW,IssueType.IMPROVEMENT,sprint1);

        Set<Sprint> sprintSet = new HashSet<>(List.of(sprint1,sprint2));
        Set<ProjectMember> projectMemberSet = new HashSet<>(List.of(projectMember1, projectMember2, projectMember3));
        Set<Issue> issueSet = new HashSet<>(List.of(issue1, issue2, issue3, issue4));

       //Execution
        ProjectDto projectDto = new ProjectDto(project,projectMemberSet,sprintSet,issueSet);

        //Assertions
        assertThat(projectDto.getProjectName()).isEqualTo(project.getName());
        assertThat(projectDto.getProjectMembers()).usingRecursiveComparison().isEqualTo(projectMemberSet.stream().map(projectMember -> projectMember.getUser().getUsername()).collect(Collectors.toSet()));
        assertThat(projectDto.getSprints()).usingRecursiveComparison().isEqualTo(sprintSet.stream().map(SprintUpdateDto::new).collect(Collectors.toSet()));
        assertThat(projectDto.getIssues()).usingRecursiveComparison().isEqualTo(issueSet.stream().map(IssueSimpleDto::new).collect(Collectors.toSet()));

    }


    @Test
    public void startProject(){
        //Test data
        User user = new UserTestDataBuilder()
                .withId(1L)
                .withUsername("testUsername")
                .withPassword("testPassword")
                .withFirstName("testFirstName")
                .withLastName("testLastName")
                .withEmail("testUser@gmail.com")
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_LEAD))
                // only lead or admin is allowed to create project
                // but service layer doesnt have authorization logic
                .withEnabled(true)
                .build();

        Project project = new ProjectTestDataBuilder()
                .withId(1L)
                .withName("Original Name")
                .withDescription("Original Description")
                .build();
        assertThat(project.isArchived()).isFalse();

        Set<ProjectMemberRole> expectedFirstMemberAuthorities = Set.of(ROLE_PROJECT_LEAD);

        //Execution
        ProjectCreateDto projectCreateDto = new ProjectCreateDto("projectName","projectDescription");

        ProjectSimpleUpdateDto projectSimpleUpdateDto = projectService.startProject(projectCreateDto,user);


        //Assertions
        //check returned projectSimpleUpdateDto
        assertEquals("projectName", projectSimpleUpdateDto.getName());
        assertEquals("projectDescription", projectSimpleUpdateDto.getDescription());
        //check project data is saved and correct
        Project projectCreated= projectRepository.getReferenceById(projectSimpleUpdateDto.getId());
        assertEquals(projectSimpleUpdateDto.getId(),projectCreated.getId());
        assertEquals("projectName",projectCreated.getName());
        assertEquals("projectDescription",projectCreated.getDescription());
        //check project member data is saved and correct
        ProjectMember projectMemberCreated = projectMemberRepository.findByUsernameAndProjectId("testUsername",projectSimpleUpdateDto.getId()).get();
        //default role
        assertEquals(projectMemberCreated.getAuthorities(), expectedFirstMemberAuthorities);

        //check saved project member's user data
        assertThat(projectMemberCreated.getUser()).usingRecursiveComparison().isEqualTo(user);

        //check project member's project data
        assertEquals(projectMemberCreated.getProject().getName(), projectSimpleUpdateDto.getName());
        assertEquals(projectMemberCreated.getProject().getDescription(), projectSimpleUpdateDto.getDescription());
    }


    @Test
    public void getProjectDetails(){
        //Setup
        Project project = projectRepository.save(new ProjectTestDataBuilder().withId(1L).withName("projectName").withDescription("projectDescription").build());

        Sprint sprint1 = sprintRepository.save(new SprintTestDataBuilder().withId(4L).withProject(project).build());
        Sprint sprint2 = sprintRepository.save(new SprintTestDataBuilder().withId(5L).withProject(project).build());
        Set<Sprint> sprintSet = Set.of(sprint1,sprint2);

        User user1 = new UserTestDataBuilder().withId(7L).withUsername("username1").build();
        User user2 = new UserTestDataBuilder().withId(8L).withUsername("username2").build();
        User user3 = new UserTestDataBuilder().withId(8L).withUsername("username2").build();

        ProjectMember projectMember = projectMemberRepository.save(new ProjectMemberTestDataBuilder().withId(10L).withProject(project).withUser(user1).build());
        ProjectMember projectMember2 = projectMemberRepository.save(new ProjectMemberTestDataBuilder().withId(11L).withProject(project).withUser(user2).build());
        ProjectMember projectMember3 = projectMemberRepository.save(new ProjectMemberTestDataBuilder().withId(12L).withProject(project).withUser(user3).build());
        Set<ProjectMember> projectMemberSet = Set.of(projectMember, projectMember2, projectMember3);

        Issue issue1 = issueRepository.save(new IssueTestDataBuilder().withId(15L).withProject(project).build());
        Issue issue2 = issueRepository.save(new IssueTestDataBuilder().withId(16L).withProject(project).build());
        Issue issue3 = issueRepository.save(new IssueTestDataBuilder().withId(17L).withProject(project).build());
        Issue issue4 = issueRepository.save(new IssueTestDataBuilder().withId(18L).withProject(project).build());
        Set<Issue> issueSet = Set.of(issue1,issue2,issue3,issue4);

        Long projectId = project.getId();  //1L or project.getId() ?...

        //Execution
        ProjectDto projectDto= projectService.getProjectDetails(projectId);

        //Assertions
        assertEquals(projectDto.getProjectName(),project.getName()); // project.getName() or "projectName"?
        assertThat(projectDto.getProjectMembers()).usingRecursiveComparison().isEqualTo(projectMemberSet.stream().map(eachMember -> eachMember.getUser().getUsername()).collect(Collectors.toSet()));
        assertThat(projectDto.getSprints()).usingRecursiveComparison().isEqualTo(sprintSet.stream().map(SprintUpdateDto::new).collect(Collectors.toSet()));
        assertThat(projectDto.getIssues()).usingRecursiveComparison().isEqualTo(issueSet.stream().map(IssueSimpleDto::new).collect(Collectors.toSet()));


    }



    @Test
    public void updateProject(){

        Project project = projectRepository.save(Project.createProject(1L,"originalProjectName","originalProjectDescription"));

        // Test data
        ProjectSimpleUpdateDto projectUpdateDto = new ProjectSimpleUpdateDto(1L,"Updated Project Name","Updated Project Description");

        // Invocation of the service method
        ProjectSimpleUpdateDto updatedProjectDto = projectService.updateProject(1L, projectUpdateDto);

        // Assertions on the returned DTO
        assertEquals(1L,updatedProjectDto.getId());
        assertEquals("Updated Project Name", updatedProjectDto.getName());
        assertEquals("Updated Project Description", updatedProjectDto.getDescription());
        //Assertions on the saved project
        Project projectFound = projectRepository.getReferenceById(1L);
        assertEquals("Updated Project Name", updatedProjectDto.getName());
        assertEquals("Updated Project Description", updatedProjectDto.getDescription());
    }





}
