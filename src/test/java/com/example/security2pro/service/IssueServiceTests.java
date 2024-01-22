package com.example.security2pro.service;

import com.example.security2pro.databuilders.*;
import com.example.security2pro.domain.enums.IssuePriority;
import com.example.security2pro.domain.enums.IssueStatus;
import com.example.security2pro.domain.enums.IssueType;
import com.example.security2pro.domain.model.*;
import com.example.security2pro.domain.model.issue.Issue;
import com.example.security2pro.domain.model.issue.IssueRelation;
import com.example.security2pro.dto.issue.IssueCreateDto;
import com.example.security2pro.dto.issue.IssueSimpleDto;
import com.example.security2pro.dto.issue.IssueUpdateDto;
import com.example.security2pro.dto.issue.onetomany.IssueRelationDto;
import com.example.security2pro.repository.*;
import com.example.security2pro.repository.repository_interfaces.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


public class IssueServiceTests {


    private IssueRepository issueRepository = new IssueRepositoryFake();
    private SprintRepository sprintRepository=new SprintRepositoryFake();
    private UserRepository userRepository = new UserRepositoryFake();
    private ProjectRepository projectRepository= new ProjectRepositoryFake();
    private ProjectMemberRepository projectMemberRepository = new ProjectMemberRepositoryFake();

    private IssueService issueService = new IssueService(issueRepository,sprintRepository,userRepository,projectRepository,projectMemberRepository);



    @Test
    public void testIssueSimpleDtoFromParamsNullSprint(){
        IssueSimpleDto issueSimpleDto = new IssueSimpleDto(1L,"issueTitle", IssuePriority.HIGHEST, IssueStatus.DONE,null);

        assertEquals(1L,issueSimpleDto.getId());
        assertEquals("issueTitle",issueSimpleDto.getTitle());
        assertEquals(IssuePriority.HIGHEST, issueSimpleDto.getPriority());
        assertEquals(IssueStatus.DONE, issueSimpleDto.getStatus());
        assertNull(issueSimpleDto.getCurrentSprintId());
    }

    @Test
    public void testIssueSimpleDtoFromParamsWithSprintId(){
        IssueSimpleDto issueSimpleDto = new IssueSimpleDto(1L,"issueTitle", IssuePriority.HIGHEST, IssueStatus.DONE,2L);

        assertEquals(1L,issueSimpleDto.getId());
        assertEquals("issueTitle",issueSimpleDto.getTitle());
        assertEquals(IssuePriority.HIGHEST, issueSimpleDto.getPriority());
        assertEquals(IssueStatus.DONE, issueSimpleDto.getStatus());
        assertEquals(2L,issueSimpleDto.getCurrentSprintId());
    }

    @Test
    public void testIssueSimpleDtoFromIssueWithSprintId(){
        Sprint sprint = new SprintTestDataBuilder().withId(2L).build();
        Issue issue= new IssueTestDataBuilder()
                .withId(1L)
                .withTitle("issueTitle")
                .withPriority(IssuePriority.HIGHEST)
                .withStatus(IssueStatus.DONE)
                .withSprint(sprint)
                .build();

        IssueSimpleDto issueSimpleDto = new IssueSimpleDto(issue);

        assertEquals(1L,issueSimpleDto.getId());
        assertEquals("issueTitle",issueSimpleDto.getTitle());
        assertEquals(IssuePriority.HIGHEST, issueSimpleDto.getPriority());
        assertEquals(IssueStatus.DONE, issueSimpleDto.getStatus());
        assertEquals(2L,issueSimpleDto.getCurrentSprintId());
    }


    @Test
    public void testIssueUpdateDto(){

        Sprint sprint = new SprintTestDataBuilder().withId(10L).build();
        Project project = new ProjectTestDataBuilder().withId(20L).build();
        User user = new UserTestDataBuilder().withId(30L).withUsername("username").build();
        User user2 = new UserTestDataBuilder().withId(31L).withUsername("username-2").build();

        Issue issue= new IssueTestDataBuilder()
                .withId(1L)
                .withAssignees(Set.of(user,user2))
                .withTitle("issueTitle")
                .withDescription("issueDescription")
                .withPriority(IssuePriority.HIGHEST)
                .withStatus(IssueStatus.DONE)
                .withType(IssueType.IMPROVEMENT)
                .withSprint(sprint)
                .build();

        IssueUpdateDto issueUpdateDto = new IssueUpdateDto(issue);

        assertEquals(1L,issueUpdateDto.getIssueId());
        assertEquals("issueTitle",issueUpdateDto.getTitle());
        assertEquals("issueDescription",issueUpdateDto.getDescription());
        assertEquals(Set.of("username","username-2"),issueUpdateDto.getAssignees());
        assertEquals(IssuePriority.HIGHEST,issueUpdateDto.getPriority());
        assertEquals(IssueStatus.DONE,issueUpdateDto.getStatus());
        assertEquals(IssueType.IMPROVEMENT,issueUpdateDto.getType());
        assertEquals(sprint.getId(),issueUpdateDto.getCurrentSprintId());

    }


    @Test
    public void testIssueUpdateDtoFromParams(){

        Sprint sprint = new SprintTestDataBuilder().withId(10L).build();
        User user = new UserTestDataBuilder().withId(30L).withUsername("username").build();
        User user2 = new UserTestDataBuilder().withId(31L).withUsername("username-2").build();

        IssueUpdateDto issueUpdateDto = new IssueUpdateDto(
                1L
                ,"issueTitle"
                ,"issueDescription"
                ,Set.of("username","username-2")
                ,IssuePriority.HIGHEST
                ,IssueStatus.DONE
                ,IssueType.IMPROVEMENT
                ,10L);

        assertEquals(1L,issueUpdateDto.getIssueId());
        assertEquals("issueTitle",issueUpdateDto.getTitle());
        assertEquals("issueDescription",issueUpdateDto.getDescription());
        assertEquals(Set.of("username","username-2"),issueUpdateDto.getAssignees());
        assertEquals(IssuePriority.HIGHEST,issueUpdateDto.getPriority());
        assertEquals(IssueStatus.DONE,issueUpdateDto.getStatus());
        assertEquals(IssueType.IMPROVEMENT,issueUpdateDto.getType());
        assertEquals(10L,issueUpdateDto.getCurrentSprintId());

    }


    @Test
    public void testIssueCreateDtoFromParams(){
        Sprint sprint = new SprintTestDataBuilder().withId(10L).build();
        User user = new UserTestDataBuilder().withId(30L).withUsername("username").build();
        User user2 = new UserTestDataBuilder().withId(31L).withUsername("username-2").build();

        IssueCreateDto issueCreateDto = new IssueCreateDto(
                22L
                ,"issueTitle"
                ,"issueDescription"
                ,Set.of("username","username-2")
                ,IssuePriority.HIGHEST
                ,IssueStatus.DONE
                ,IssueType.BUG
                ,10L
                );

        assertThat(issueCreateDto.getProjectId()).isPresent();
        assertEquals(22L,issueCreateDto.getProjectId().get());
        assertEquals("issueTitle",issueCreateDto.getTitle());
        assertEquals("issueDescription",issueCreateDto.getDescription());
        assertEquals(Set.of("username","username-2"),issueCreateDto.getAssignees());
        assertEquals(IssuePriority.HIGHEST,issueCreateDto.getPriority());
        assertEquals(IssueStatus.DONE,issueCreateDto.getStatus());
        assertEquals(IssueType.BUG,issueCreateDto.getType());
        assertEquals(10L,issueCreateDto.getCurrentSprintId());
    }

    @Test
    public void testIssueCreateDtoFromParams_NullAssignees(){
        Sprint sprint = new SprintTestDataBuilder().withId(10L).build();
        User user = new UserTestDataBuilder().withId(30L).withUsername("username").build();
        User user2 = new UserTestDataBuilder().withId(31L).withUsername("username-2").build();

        IssueCreateDto issueCreateDto = new IssueCreateDto(
                22L
                ,"issueTitle"
                ,"issueDescription"
                ,null
                ,IssuePriority.HIGHEST
                ,IssueStatus.DONE
                ,IssueType.BUG
                ,10L
        );

        assertThat(issueCreateDto.getProjectId()).isPresent();
        assertEquals(22L,issueCreateDto.getProjectId().get());
        assertEquals("issueTitle",issueCreateDto.getTitle());
        assertEquals("issueDescription",issueCreateDto.getDescription());
        assertEquals(Collections.emptySet(),issueCreateDto.getAssignees());
        assertEquals(IssuePriority.HIGHEST,issueCreateDto.getPriority());
        assertEquals(IssueStatus.DONE,issueCreateDto.getStatus());
        assertEquals(IssueType.BUG,issueCreateDto.getType());
        assertEquals(10L,issueCreateDto.getCurrentSprintId());
    }




    @Test
    public void getUserIssues_throwsException(){

        assertThat(userRepository.findAll()).isEmpty();

        assertThrows(IllegalArgumentException.class,()->issueService.getUserIssues("userNotExist"));
    }

    @Test
    public void getUserIssues(){
        //Setup
        User user = new UserTestDataBuilder().withUsername("username").build();
        userRepository.save(user);
        Issue issue = new IssueTestDataBuilder().withId(10L).withAssignees(Set.of(user)).build();
        Issue issue2 = new IssueTestDataBuilder().withId(20L).withAssignees(Set.of(user)).build();
        Issue issue3 = new IssueTestDataBuilder().withId(30L).withAssignees(Set.of(user)).build();

        Set<Issue> issueSet = new HashSet<>(List.of(issue,issue2,issue3));
        Set<IssueSimpleDto> expectedDtoSet = issueSet.stream().map(IssueSimpleDto::new).collect(Collectors.toCollection(HashSet::new));
        issueRepository.saveAll(issueSet);
        //Execution
        Set<IssueSimpleDto> issueSimpleDtoSet = issueService.getUserIssues("username");
        //Assertions
        assertThat(issueSimpleDtoSet).usingRecursiveComparison().isEqualTo(expectedDtoSet);
    }

    @Test
    public void getActiveIssuesBySprintId_throwsExceptionGivenArchivedSprint(){
        Sprint sprint = new SprintTestDataBuilder().withId(2L).withArchived(true).build();
        sprintRepository.save(sprint);

        assertThrows(IllegalArgumentException.class,()->issueService.getActiveIssuesBySprintId(sprint.getId()));
    }

    @Test
    public void getActiveIssuesBySprintId(){
        Sprint sprint = new SprintTestDataBuilder().withId(2L).build();
        assertFalse(sprint.isArchived());
        sprintRepository.save(sprint);

        Issue issue = new IssueTestDataBuilder().withId(10L).withSprint(sprint).build();
        Issue issue2 = new IssueTestDataBuilder().withId(20L).withSprint(sprint).build();
        Issue issue3 = new IssueTestDataBuilder().withId(30L).withSprint(sprint).build();

        Set<Issue> issueSet = new HashSet<>(List.of(issue,issue2,issue3));
        Set<IssueSimpleDto> expectedDtoSet = issueSet.stream().map(IssueSimpleDto::new).collect(Collectors.toCollection(HashSet::new));
        issueRepository.saveAll(issueSet);

        //Execution
        Set<IssueSimpleDto> issueSimpleDtoSet = issueService.getActiveIssuesBySprintId(sprint.getId());
        //Assertions
        assertThat(issueSimpleDtoSet).usingRecursiveComparison().isEqualTo(expectedDtoSet);
    }

    @Test
    public void getIssueSimple_throwsException(){

        assertThat(issueRepository.findAll()).isEmpty();

        assertThrows(IllegalArgumentException.class, ()->issueService.getIssueSimple(1L));
    }

    @Test
    public void getIssueSimple(){

        Sprint sprint = new SprintTestDataBuilder()
                .withId(2L)
                .build();

        User user = new UserTestDataBuilder().withId(30L).withUsername("username").build();
        User user2 = new UserTestDataBuilder().withId(31L).withUsername("username-2").build();

        Issue issue = new IssueTestDataBuilder()
                .withId(10L)
                .withTitle("issueTitle")
                .withDescription("issueDescription")
                .withAssignees(Set.of(user,user2))
                .withPriority(IssuePriority.HIGHEST)
                .withStatus(IssueStatus.DONE)
                .withType(IssueType.BUG)
                .withSprint(sprint)
                .build();

        issueRepository.save(issue);
        IssueUpdateDto expectedDto = new IssueUpdateDto(issue);

        //Execution
        IssueUpdateDto issueUpdateDto = issueService.getIssueSimple(issue.getId());

        //Assertions
        assertThat(issueUpdateDto).usingRecursiveComparison().isEqualTo(expectedDto);
    }


    @Test
    public void createIssueFromSimpleDto_throwsExceptionGivenProjectNotExist(){
        //check project & sprint & user
        Long projectIdNotExist= 1L;
        assertThrows(EntityNotFoundException.class,()->projectRepository.getReferenceById(projectIdNotExist));

        IssueCreateDto issueCreateDto = new IssueCreateDto(projectIdNotExist,"issueTitle","issueDescription",null,IssuePriority.HIGHEST,IssueStatus.DONE,IssueType.BUG,null);

        //Execution & Assertions
        assertThrows(EntityNotFoundException.class,()->issueService.createIssueFromSimpleDto(issueCreateDto));

    }

    @Test
    public void createIssueFromSimpleDto_throwsExceptionGivenSprintNotExist(){
        //check project & sprint & user

        Project project = new ProjectTestDataBuilder()
                .withId(10L)
                .build();
        projectRepository.save(project);

        Long sprintIdNotExist= 22L;
        assertThrows(EntityNotFoundException.class,()->sprintRepository.getReferenceById(sprintIdNotExist));

        IssueCreateDto issueCreateDto = new IssueCreateDto(
                project.getId()
                ,"issueTitle"
                ,"issueDescription"
                ,null
                ,IssuePriority.HIGHEST
                ,IssueStatus.DONE
                ,IssueType.BUG
                ,sprintIdNotExist);

        //Execution & Assertions
        assertThrows(IllegalArgumentException.class,()->issueService.createIssueFromSimpleDto(issueCreateDto));
    }


    @Test
    public void createIssueFromSimpleDto_throwsExceptionGivenAssigneesNotExist(){
        //check project & sprint & user
        Project project = new ProjectTestDataBuilder()
                .withId(10L)
                .build();
        projectRepository.save(project);

        Sprint sprint = new SprintTestDataBuilder()
                .withId(2L)
                .withProject(project)
                .build();
        sprintRepository.save(sprint);

        User user = new UserTestDataBuilder().withId(30L).withUsername("username").build();
        User user2 = new UserTestDataBuilder().withId(31L).withUsername("username-2").build();

//        assertThat(userRepository.findUserByUsername("username")).isEmpty();
//        assertThat(userRepository.findUserByUsername("username-2")).isEmpty();
        assertThat(projectMemberRepository.findAllByUsernameAndProjectIdWithUser(Set.of("username","username-2"),project.getId()));

        IssueCreateDto issueCreateDto = new IssueCreateDto(
                project.getId()
                ,"issueTitle"
                ,"issueDescription"
                ,Set.of("username","username-2")
                ,IssuePriority.HIGHEST
                ,IssueStatus.DONE
                ,IssueType.BUG
                ,sprint.getId());

        //Execution & Assertions
        assertThrows( IllegalArgumentException.class, ()->issueService.createIssueFromSimpleDto(issueCreateDto));

    }

    @Test
    public void createIssueFromSimpleDto_NullAssignees(){
        //check project & sprint & user
        Project project = new ProjectTestDataBuilder()
                .withId(10L)
                .build();
        projectRepository.save(project);

        Sprint sprint = new SprintTestDataBuilder()
                .withId(2L)
                .withProject(project)
                .build();
        sprintRepository.save(sprint);

        IssueCreateDto issueCreateDto = new IssueCreateDto(
                project.getId()
                ,"issueTitle"
                ,"issueDescription"
                ,null
                ,IssuePriority.HIGHEST
                ,IssueStatus.DONE
                ,IssueType.BUG
                ,sprint.getId());

        //Execution
        IssueUpdateDto issueUpdateDto = issueService.createIssueFromSimpleDto(issueCreateDto);

        //check if updateDto has the same field as create Dto
        assertEquals("issueTitle",issueUpdateDto.getTitle());
        assertEquals("issueDescription",issueUpdateDto.getDescription());
        assertEquals(Collections.emptySet(),issueUpdateDto.getAssignees());
        assertEquals(IssuePriority.HIGHEST,issueUpdateDto.getPriority());
        assertEquals(IssueStatus.DONE,issueUpdateDto.getStatus());
        assertEquals(IssueType.BUG,issueUpdateDto.getType());
        assertEquals(sprint.getId(),issueUpdateDto.getCurrentSprintId());

        // check if the saved data is correct
        Issue savedIssue= issueRepository.getReferenceById(issueUpdateDto.getIssueId());
        assertEquals(issueCreateDto.getProjectId().get(),savedIssue.getProject().getId());
        IssueUpdateDto savedIssueDto = new IssueUpdateDto(savedIssue);
        assertThat(savedIssueDto).usingRecursiveComparison().isEqualTo(issueUpdateDto);
    }


    @Test
    public void createIssueFromSimpleDto_success(){
        //check project & sprint & user
        Project project = new ProjectTestDataBuilder()
                .withId(10L)
                .build();
        projectRepository.save(project);

        Sprint sprint = new SprintTestDataBuilder()
                .withId(2L)
                .withProject(project)
                .build();
        sprintRepository.save(sprint);

        User user = new UserTestDataBuilder().withId(30L).withUsername("username").build();
        User user2 = new UserTestDataBuilder().withId(31L).withUsername("username-2").build();
        userRepository.save(user);
        userRepository.save(user2);

        ProjectMember projectMember = new ProjectMemberTestDataBuilder().withId(32L).withProject(project).withUser(user).build();
        ProjectMember projectMember2 = new ProjectMemberTestDataBuilder().withId(33L).withProject(project).withUser(user2).build();
        projectMemberRepository.save(projectMember);
        projectMemberRepository.save(projectMember2);

        IssueCreateDto issueCreateDto = new IssueCreateDto(
                project.getId()
                ,"issueTitle"
                ,"issueDescription"
                ,Set.of("username","username-2")
                ,IssuePriority.HIGHEST
                ,IssueStatus.DONE
                ,IssueType.BUG
                ,sprint.getId());

        //Execution
        IssueUpdateDto issueUpdateDto = issueService.createIssueFromSimpleDto(issueCreateDto);


        //check if updateDto has the same field as create Dto
        assertEquals("issueTitle",issueUpdateDto.getTitle());
        assertEquals("issueDescription",issueUpdateDto.getDescription());
        assertEquals(Set.of("username","username-2"),issueUpdateDto.getAssignees());
        assertEquals(IssuePriority.HIGHEST,issueUpdateDto.getPriority());
        assertEquals(IssueStatus.DONE,issueUpdateDto.getStatus());
        assertEquals(IssueType.BUG,issueUpdateDto.getType());
        assertEquals(sprint.getId(),issueUpdateDto.getCurrentSprintId());

        // check if the saved data is correct
        Issue savedIssue= issueRepository.getReferenceById(issueUpdateDto.getIssueId());
        assertEquals(issueCreateDto.getProjectId().get(),savedIssue.getProject().getId());
        IssueUpdateDto savedIssueDto = new IssueUpdateDto(savedIssue);
        assertThat(savedIssueDto).usingRecursiveComparison().isEqualTo(issueUpdateDto);
    }



    @Test
    public void updateIssueFromSimpleDto_success(){
        Project project = new ProjectTestDataBuilder()
                .withId(10L)
                .build();
        projectRepository.save(project);

        Sprint sprint = new SprintTestDataBuilder()
                .withId(2L)
                .withProject(project)
                .build();
        sprintRepository.save(sprint);

        User user = new UserTestDataBuilder().withId(30L).withUsername("username").build();
        User user2 = new UserTestDataBuilder().withId(31L).withUsername("username-2").build();
        userRepository.save(user);
        userRepository.save(user2);

        ProjectMember projectMember = new ProjectMemberTestDataBuilder().withId(32L).withProject(project).withUser(user).build();
        ProjectMember projectMember2 = new ProjectMemberTestDataBuilder().withId(33L).withProject(project).withUser(user2).build();
        projectMemberRepository.save(projectMember);
        projectMemberRepository.save(projectMember2);

        Issue issue= new IssueTestDataBuilder()
                .withId(1L)
                .withProject(project)
                .withAssignees(Set.of(user,user2))
                .withTitle("issueTitle")
                .withDescription("issueDescription")
                .withPriority(IssuePriority.HIGHEST)
                .withStatus(IssueStatus.DONE)
                .withType(IssueType.IMPROVEMENT)
                .withSprint(sprint)
                .build();
        issueRepository.save(issue);

        IssueUpdateDto issueUpdateDto = new IssueUpdateDto(
                1L
                ,"updatedIssueTitle"
                ,"updatedIssueDescription"
                ,Set.of("username")
                ,IssuePriority.MEDIUM
                ,IssueStatus.TODO
                ,IssueType.NEW_FEATURE
                ,sprint.getId());

        //Execution
        IssueUpdateDto issueUpdateDtoReturned = issueService.updateIssueFromSimpleDto(issueUpdateDto);

        //Assertions
        assertThat(issueUpdateDtoReturned).usingRecursiveComparison().isEqualTo(issueUpdateDto);

        Issue foundIssue = issueRepository.getReferenceById(1L);
        IssueUpdateDto expectedUpdateDto = new IssueUpdateDto(foundIssue);
        assertThat(issueUpdateDto).usingRecursiveComparison().isEqualTo(expectedUpdateDto);
    }

    @Test
    public void updateIssueFromSimpleDto_SuccessNullAssignees(){
        //maybe this one does not need to be tested?...
        Project project = new ProjectTestDataBuilder()
                .withId(10L)
                .build();
        projectRepository.save(project);

        Sprint sprint = new SprintTestDataBuilder()
                .withId(2L)
                .withProject(project)
                .build();
        sprintRepository.save(sprint);

        User user = new UserTestDataBuilder().withId(30L).withUsername("username").build();
        User user2 = new UserTestDataBuilder().withId(31L).withUsername("username-2").build();
        userRepository.save(user);
        userRepository.save(user2);

        ProjectMember projectMember = new ProjectMemberTestDataBuilder().withId(32L).withProject(project).withUser(user).build();
        ProjectMember projectMember2 = new ProjectMemberTestDataBuilder().withId(33L).withProject(project).withUser(user2).build();
        projectMemberRepository.save(projectMember);
        projectMemberRepository.save(projectMember2);

        Issue issue= new IssueTestDataBuilder()
                .withId(1L)
                .withProject(project)
                .withAssignees(Set.of(user,user2))
                .withTitle("issueTitle")
                .withDescription("issueDescription")
                .withPriority(IssuePriority.HIGHEST)
                .withStatus(IssueStatus.DONE)
                .withType(IssueType.IMPROVEMENT)
                .withSprint(sprint)
                .build();
        issueRepository.save(issue);

        IssueUpdateDto issueUpdateDto = new IssueUpdateDto(
                1L
                ,"updatedIssueTitle"
                ,"updatedIssueDescription"
                ,null
                ,IssuePriority.MEDIUM
                ,IssueStatus.TODO
                ,IssueType.NEW_FEATURE
                ,sprint.getId());

        //Execution
        IssueUpdateDto issueUpdateDtoReturned = issueService.updateIssueFromSimpleDto(issueUpdateDto);

        assertEquals(1L,issueUpdateDtoReturned.getIssueId());
        assertEquals("updatedIssueTitle",issueUpdateDtoReturned.getTitle());
        assertEquals("updatedIssueDescription",issueUpdateDtoReturned.getDescription());
        assertEquals(Collections.emptySet(),issueUpdateDtoReturned.getAssignees());
        assertEquals(IssuePriority.MEDIUM,issueUpdateDtoReturned.getPriority());
        assertEquals(IssueStatus.TODO,issueUpdateDtoReturned.getStatus());
        assertEquals(IssueType.NEW_FEATURE,issueUpdateDtoReturned.getType());
        assertEquals(sprint.getId(),issueUpdateDtoReturned.getCurrentSprintId());


        Issue foundIssue = issueRepository.getReferenceById(1L);
        IssueUpdateDto expectedUpdateDto = new IssueUpdateDto(foundIssue);

        assertEquals(Collections.emptySet(), expectedUpdateDto.getAssignees());
        assertEquals(issueUpdateDto.getIssueId(),expectedUpdateDto.getIssueId());
        assertEquals(issueUpdateDto.getTitle(),expectedUpdateDto.getTitle());
        assertEquals(issueUpdateDto.getDescription(),expectedUpdateDto.getDescription());
        assertEquals(issueUpdateDto.getPriority(),expectedUpdateDto.getPriority());
        assertEquals(issueUpdateDto.getStatus(),expectedUpdateDto.getStatus());
        assertEquals(issueUpdateDto.getType(),expectedUpdateDto.getType());
        assertEquals(issueUpdateDto.getCurrentSprintId(),expectedUpdateDto.getCurrentSprintId());
    }

//Permission evaluator's responsibility?..
//    @Test
//    public void updateIssueFromSimpleDto_throwsExceptionWhenIssueNotExist(){
//        assertThat(issueRepository.findById(1L)).isEmpty();
//
//        IssueUpdateDto issueUpdateDto = new IssueUpdateDto(
//                1L
//                ,"updatedIssueTitle"
//                ,"updatedIssueDescription"
//                ,Set.of("username")
//                ,IssuePriority.MEDIUM
//                ,IssueStatus.TODO
//                ,IssueType.NEW_FEATURE
//                ,null);
//
//        //Execution & Assertions
//        assertThrows(EntityNotFoundException.class, ()->issueService.updateIssueFromSimpleDto(issueUpdateDto));
//    }
    @Test
    public void updateIssueFromSimpleDto_throwsExceptionWhenSprintNotExist (){
        Project project = new ProjectTestDataBuilder()
                .withId(10L)
                .build();
        projectRepository.save(project);

        Sprint sprintExist = new SprintTestDataBuilder()
                .withId(3L)
                .withProject(project)
                .build();
        sprintRepository.save(sprintExist);

        User user = new UserTestDataBuilder().withId(30L).withUsername("username").build();
        User user2 = new UserTestDataBuilder().withId(31L).withUsername("username-2").build();
        userRepository.save(user);
        userRepository.save(user2);

        ProjectMember projectMember = new ProjectMemberTestDataBuilder().withId(32L).withProject(project).withUser(user).build();
        ProjectMember projectMember2 = new ProjectMemberTestDataBuilder().withId(33L).withProject(project).withUser(user2).build();
        projectMemberRepository.save(projectMember);
        projectMemberRepository.save(projectMember2);

        Long sprintIdNotExist = 2L;
        assertThrows(EntityNotFoundException.class,()->sprintRepository.getReferenceById(sprintIdNotExist));

        Issue issue= new IssueTestDataBuilder()
                .withId(1L)
                .withProject(project)
                .withAssignees(Set.of(user,user2))
                .withTitle("issueTitle")
                .withDescription("issueDescription")
                .withPriority(IssuePriority.HIGHEST)
                .withStatus(IssueStatus.DONE)
                .withType(IssueType.IMPROVEMENT)
                .withSprint(sprintExist)
                .build();
        issueRepository.save(issue);

        IssueUpdateDto issueUpdateDto = new IssueUpdateDto(
                1L
                ,"updatedIssueTitle"
                ,"updatedIssueDescription"
                ,Set.of("username")
                ,IssuePriority.MEDIUM
                ,IssueStatus.TODO
                ,IssueType.NEW_FEATURE
                ,sprintIdNotExist);

        //Execution
        assertThrows(IllegalArgumentException.class, ()->issueService.updateIssueFromSimpleDto(issueUpdateDto));
    }

    @Test
    public void updateIssueFromSimpleDto_throwsExceptionWhenAssigneeNotExist(){
        Project project = new ProjectTestDataBuilder()
                .withId(10L)
                .build();
        projectRepository.save(project);

        Sprint sprint = new SprintTestDataBuilder()
                .withId(2L)
                .withProject(project)
                .build();
        sprintRepository.save(sprint);

        User user = new UserTestDataBuilder().withId(30L).withUsername("username").build();
        User user2 = new UserTestDataBuilder().withId(31L).withUsername("username-2").build();
        userRepository.save(user);
        userRepository.save(user2);

        ProjectMember projectMember = new ProjectMemberTestDataBuilder().withId(32L).withProject(project).withUser(user).build();
        ProjectMember projectMember2 = new ProjectMemberTestDataBuilder().withId(33L).withProject(project).withUser(user2).build();
        projectMemberRepository.save(projectMember);
        projectMemberRepository.save(projectMember2);

        String usernameNotExist= "usernameNotExist";
        assertThat(userRepository.findUserByUsername("usernameNotExist")).isEmpty();

        Issue issue= new IssueTestDataBuilder()
                .withId(1L)
                .withProject(project)
                .withAssignees(Set.of(user,user2))
                .withTitle("issueTitle")
                .withDescription("issueDescription")
                .withPriority(IssuePriority.HIGHEST)
                .withStatus(IssueStatus.DONE)
                .withType(IssueType.IMPROVEMENT)
                .withSprint(sprint)
                .build();
        issueRepository.save(issue);

        IssueUpdateDto issueUpdateDto = new IssueUpdateDto(
                1L
                ,"updatedIssueTitle"
                ,"updatedIssueDescription"
                ,Set.of("usernameNotExist")
                ,IssuePriority.MEDIUM
                ,IssueStatus.TODO
                ,IssueType.NEW_FEATURE
                ,sprint.getId());

        //Execution
        assertThrows(IllegalArgumentException.class, ()->issueService.updateIssueFromSimpleDto(issueUpdateDto));
    }


    @Test
    public void deleteByIdsInBulk(){
        // exception has to be thrown when passed ids that do not exist?????
        Issue issue1 = new IssueTestDataBuilder().withId(10L).build();
        Issue issue2 = new IssueTestDataBuilder().withId(20L).build();
        Issue issue3 = new IssueTestDataBuilder().withId(30L).build();
        Issue issue4 = new IssueTestDataBuilder().withId(40L).build();

        Set<Issue> issueSet = new HashSet<>(List.of(issue1,issue2,issue3,issue4));
        issueRepository.saveAll(issueSet);
        assertThat(issueRepository.findAllById(Set.of(issue1.getId(),issue2.getId(),issue3.getId(),issue4.getId())))
                .usingRecursiveComparison().isEqualTo(issueSet);


        Set<Long> idsToBeDeleted = issueSet.stream().map(Issue::getId).collect(Collectors.toSet());
        //Execution
        issueRepository.deleteAllByIdInBatch(idsToBeDeleted);

        //Assertions
        assertThat(issueRepository.findAllById(Set.of(issue1.getId(),issue2.getId(),issue3.getId(),issue4.getId())))
                .isEmpty();

    }

    @Test
    public void testIssueRelationDto_FromParams(){
        Issue causeIssue = new IssueTestDataBuilder().withId(20L).build();


        IssueRelationDto issueRelationDto
                = new IssueRelationDto(causeIssue.getId(),"causeIssue is root cause of the affected issue");

        assertEquals(causeIssue.getId(),issueRelationDto.getCauseIssueId());
        assertEquals("causeIssue is root cause of the affected issue",issueRelationDto.getRelationDescription());

    }


    @Test
    public void testIssueRelationDto_FromIssueRelation(){
        Issue affectedIssue = new IssueTestDataBuilder().withId(10L).build();
        Issue causeIssue = new IssueTestDataBuilder().withId(20L).build();

        IssueRelation issueRelation =
                IssueRelation.createIssueRelation(affectedIssue,causeIssue,"causeIssue is root cause of the affected issue");

        IssueRelationDto issueRelationDto = new IssueRelationDto(issueRelation);

        assertEquals(causeIssue.getId(),issueRelationDto.getCauseIssueId());
        assertEquals("causeIssue is root cause of the affected issue",issueRelationDto.getRelationDescription());
    }


    @Test
    public void createOrUpdateIssueRelation_creationSuccess(){
        Issue issue1 = new IssueTestDataBuilder().withId(10L).build();
        Issue issue2 = new IssueTestDataBuilder().withId(20L).build();

        Set<Issue> issueSet = new HashSet<>(List.of(issue1,issue2));
        issueRepository.saveAll(issueSet);
        Issue issueFoundInitial =issueRepository.findById(issue1.getId()).get();
        assertThat(issueFoundInitial.getIssueRelationSet()).isEmpty();

        IssueRelationDto issueRelationDto = new IssueRelationDto(issue2.getId(),"issue 2 is causing A for issue1");

        //Execution
        IssueRelationDto issueRelationDtoReturned = issueService.createOrUpdateIssueRelation(issue1.getId(),issueRelationDto);

        //Assertions
        assertThat(issueRelationDtoReturned).usingRecursiveComparison().isEqualTo(issueRelationDto);

        Issue issueFound =issueRepository.findById(issue1.getId()).get();
        assertEquals(1,issueFound.getIssueRelationSet().size());
        IssueRelation issueRelationSaved = issueFound.getIssueRelationSet().stream().findFirst().get();

        assertEquals(issue1.getId(),issueRelationSaved.getAffectedIssue().getId());
        assertEquals(issue2.getId(),issueRelationSaved.getCauseIssue().getId());
        assertEquals("issue 2 is causing A for issue1",issueRelationSaved.getRelationDescription());

    }


    @Test
    public void createOrUpdateIssueRelation_updateSuccess(){
        Issue affected = new IssueTestDataBuilder().withId(10L).build();
        Issue cause = new IssueTestDataBuilder().withId(20L).build();
        IssueRelation issueRelation = IssueRelation.createIssueRelation(affected,cause,"cause is the root cause of affected");
        affected.addIssueRelation(issueRelation);

        Set<Issue> issueSet = new HashSet<>(List.of(affected,cause));
        issueRepository.saveAll(issueSet);
        Issue issueFoundInitial =issueRepository.findById(affected.getId()).get();
        assertEquals(1,issueFoundInitial.getIssueRelationSet().size());
        assertThat(issueFoundInitial.getIssueRelationSet().stream().findAny().get()).usingRecursiveComparison().isEqualTo(issueRelation);

        IssueRelationDto issueRelationDto = new IssueRelationDto(cause.getId(),"cause is blocked by the same issue as affected");
        //Execution
        IssueRelationDto issueRelationDtoReturned = issueService.createOrUpdateIssueRelation(affected.getId(),issueRelationDto);

        //Assertions
        assertThat(issueRelationDtoReturned).usingRecursiveComparison().isEqualTo(issueRelationDto);

        Issue issueFound =issueRepository.findById(affected.getId()).get();
        assertEquals(1,issueFound.getIssueRelationSet().size());

        IssueRelation issueRelationSaved = issueFound.getIssueRelationSet().stream().findFirst().get();
        assertEquals(affected.getId(),issueRelationSaved.getAffectedIssue().getId());
        assertEquals(cause.getId(),issueRelationSaved.getCauseIssue().getId());
        assertEquals("cause is blocked by the same issue as affected",issueRelationSaved.getRelationDescription());
    }

//PermissionEvaluator's responsibility
//    @Test
//    public void createOrUpdateIssueRelation_throwsExceptionWhenAffectedIssueNotExist(){
//
//        Long idNotExist= 10L;
//        assertThat(issueRepository.findById(idNotExist)).isEmpty();
//
//        assertThat(issueRepository.findById(20L)).isEmpty();
//        IssueRelationDto issueRelationDto = new IssueRelationDto(20L,"issue 2 is causing A for issue1");
//
//        //Execution && Assertions
//        assertThrows(NoSuchElementException.class,()->issueService.createOrUpdateIssueRelation(idNotExist,issueRelationDto));
//    }


    @Test
    public void createOrUpdateIssueRelation_throwsExceptionWhenCauseIssueNotExist(){
        Issue issue1 = new IssueTestDataBuilder().withId(10L).build();

        issueRepository.save(issue1);
        Issue issueFoundInitial =issueRepository.findById(issue1.getId()).get();
        assertThat(issueFoundInitial.getIssueRelationSet()).isEmpty();

        assertThat(issueRepository.findById(20L)).isEmpty();
        IssueRelationDto issueRelationDto = new IssueRelationDto(20L,"issue 2 is causing A for issue1");

        //Execution && Assertions
        assertThrows(EntityNotFoundException.class,()->issueService.createOrUpdateIssueRelation(issue1.getId(),issueRelationDto));
    }


    @Test
    public void deleteIssueRelation(){
        Issue affected = new IssueTestDataBuilder().withId(10L).build();
        Issue cause = new IssueTestDataBuilder().withId(20L).build();
        IssueRelation issueRelation = IssueRelation.createIssueRelation(affected,cause,"cause is the root cause of affected");
        affected.addIssueRelation(issueRelation);

        Set<Issue> issueSet = new HashSet<>(List.of(affected,cause));
        issueRepository.saveAll(issueSet);
        Issue issueFound =issueRepository.findById(affected.getId()).get();
        assertEquals(1,issueFound.getIssueRelationSet().size());
        assertThat(issueFound.getIssueRelationSet().stream().findAny().get()).usingRecursiveComparison().isEqualTo(issueRelation);

        //Execution
        issueService.deleteIssueRelation(affected.getId(),cause.getId());
        //Assertions
        assertThat(issueRepository.findById(affected.getId()).get().getIssueRelationSet()).isEmpty();
    }

//PermissionEvaluator's responsibility
//    @Test
//    public void deleteIssueRelation_throwsExceptionWhenAffectedIssueNotExist(){
//
//        assertThat(issueRepository.findById(1L)).isEmpty();
//        Issue cause = new IssueTestDataBuilder().withId(20L).build();
//        issueRepository.save(cause);
//        //Execution && Assertions
//        assertThrows(NoSuchElementException.class,()->issueService.deleteIssueRelation(1L,cause.getId()));
//
//    }



    @Test
    public void deleteIssueRelation_throwsExceptionWhenCauseIssueNotExist(){
        //THIS may be in another test.. (IssueRelation Entity test class)
        assertThat(issueRepository.findById(1L)).isEmpty();
        Issue affected = new IssueTestDataBuilder().withId(20L).build();
        issueRepository.save(affected);
        //Execution && Assertions
        assertThrows(IllegalArgumentException.class,()->issueService.deleteIssueRelation(affected.getId(),1L));
    }


    @Test
    public void findAllByAffectedIssueId(){

        Issue affected = new IssueTestDataBuilder().withId(10L).build();
        Issue cause1 = new IssueTestDataBuilder().withId(20L).build();
        Issue cause2 = new IssueTestDataBuilder().withId(30L).build();
        Issue cause3 = new IssueTestDataBuilder().withId(40L).build();


        Set<Issue> issueSet = new HashSet<>(List.of(affected,cause1,cause2,cause3));
        issueRepository.saveAll(issueSet);
        assertThat(issueRepository.findAllById(Set.of(affected.getId(),cause1.getId(),cause2.getId(),cause3.getId())))
                .usingRecursiveComparison().isEqualTo(issueSet);

        IssueRelation issueRelation1 = IssueRelation.createIssueRelation(affected,cause1,"1 relationship description");
        IssueRelation issueRelation2 = IssueRelation.createIssueRelation(affected,cause2,"2 relationship description");
        IssueRelation issueRelation3 = IssueRelation.createIssueRelation(affected,cause3,"3 relationship description");
        affected.addIssueRelation(issueRelation1);
        affected.addIssueRelation(issueRelation2);
        affected.addIssueRelation(issueRelation3);

        issueRepository.save(affected);

        //Assertions
        Set<IssueRelationDto> issueRelationDtoSet = issueService.findAllByAffectedIssueId(affected.getId());

        IssueRelationDto issueRelationDto1 = new IssueRelationDto(issueRelation1);
        IssueRelationDto issueRelationDto2 = new IssueRelationDto(issueRelation2);
        IssueRelationDto issueRelationDto3 = new IssueRelationDto(issueRelation3);
        Set<IssueRelationDto> expectedIssueRelationDtoSet = Set.of(issueRelationDto1,issueRelationDto2,issueRelationDto3);
        assertThat(issueRelationDtoSet).usingRecursiveComparison().isEqualTo(expectedIssueRelationDtoSet);
    }


}
