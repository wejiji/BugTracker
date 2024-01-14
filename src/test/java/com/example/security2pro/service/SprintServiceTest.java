package com.example.security2pro.service;

import com.example.security2pro.databuilders.IssueTestDataBuilder;
import com.example.security2pro.databuilders.ProjectTestDataBuilder;
import com.example.security2pro.databuilders.SprintTestDataBuilder;
import com.example.security2pro.domain.enums.IssueStatus;
import com.example.security2pro.domain.model.*;
import com.example.security2pro.dto.sprint.SprintCreateDto;
import com.example.security2pro.dto.sprint.SprintUpdateDto;
import com.example.security2pro.dto.sprinthistory.SprintIssueHistoryDto;
import com.example.security2pro.repository.IssueRepositoryFake;
import com.example.security2pro.repository.ProjectRepositoryFake;
import com.example.security2pro.repository.SprintIssueHistoryRepositoryFake;
import com.example.security2pro.repository.SprintRepositoryFake;
import com.example.security2pro.repository.repository_interfaces.IssueRepository;
import com.example.security2pro.repository.repository_interfaces.ProjectRepository;
import com.example.security2pro.repository.repository_interfaces.SprintIssueHistoryRepository;
import com.example.security2pro.repository.repository_interfaces.SprintRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


public class SprintServiceTest {

    IssueRepository issueRepository = new IssueRepositoryFake();

    ProjectRepository projectRepository = new ProjectRepositoryFake();

    SprintIssueHistoryRepository sprintIssueHistoryRepository =new SprintIssueHistoryRepositoryFake();

    SprintRepository sprintRepository = new SprintRepositoryFake();

    SprintService sprintService = new SprintService(sprintRepository,issueRepository,projectRepository,sprintIssueHistoryRepository);


    @Test
    public void testSprintIssueHistoryDto(){
        Sprint sprint = new SprintTestDataBuilder().withId(9L).withArchived(true).build();
        Issue issue = new IssueTestDataBuilder().withId(10L).withTitle("issueName").withDescription("issueDescription").withStatus(IssueStatus.DONE).build();
        SprintIssueHistory sprintIssueHistory = SprintIssueHistory.createSprintIssueHistory(1L,sprint,issue);

        //Execution
        SprintIssueHistoryDto sprintIssueHistoryDto= new SprintIssueHistoryDto(sprintIssueHistory);

        //Assertions
        assertEquals(1L,sprintIssueHistoryDto.getId());

        assertEquals(9L,sprintIssueHistoryDto.getSprintId());

        assertEquals(10L,sprintIssueHistoryDto.getIssueId());
        assertEquals("issueName",sprintIssueHistoryDto.getIssueName());
        assertEquals("issueDescription",sprintIssueHistoryDto.getIssueDescription());
        assertEquals(IssueStatus.DONE,sprintIssueHistoryDto.getIssueStatus());
        assertTrue(sprintIssueHistoryDto.isComplete());
    }

    @Test
    public void testSprintCreateDto(){

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        SprintCreateDto sprintCreateDto = new SprintCreateDto(1L,"sprintName","sprintDescription",startDate,endDate);

        assertEquals("sprintName",sprintCreateDto.getName());
        assertEquals("sprintDescription",sprintCreateDto.getDescription());
        assertEquals(startDate,sprintCreateDto.getStartDate());
        assertEquals(endDate,sprintCreateDto.getEndDate());
    }

    @Test
    public void testSprintUpdateDtoFromParams(){
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        SprintUpdateDto sprintUpdateDto = new SprintUpdateDto(1L,"sprintName","sprintDescription",startDate,endDate);
        assertEquals(1L,sprintUpdateDto.getId());
        assertEquals("sprintName",sprintUpdateDto.getName());
        assertEquals("sprintDescription",sprintUpdateDto.getDescription());
        assertEquals(startDate,sprintUpdateDto.getStartDate());
        assertEquals(endDate, sprintUpdateDto.getEndDate());
    }


    @Test
    public void testSprintUpdateDtoFromSprint(){
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        Sprint sprint = new SprintTestDataBuilder()
                .withId(1L)
                .withName("sprintName")
                .withDescription("sprintDescription")
                .withStartDate(startDate)
                .withEndDate(endDate)
                .build();


        SprintUpdateDto sprintUpdateDto = new SprintUpdateDto(sprint);

        assertEquals(1L,sprintUpdateDto.getId());
        assertEquals("sprintName",sprintUpdateDto.getName());
        assertEquals("sprintDescription",sprintUpdateDto.getDescription());
        assertEquals(startDate,sprintUpdateDto.getStartDate());
        assertEquals(endDate, sprintUpdateDto.getEndDate());
    }




    @Test
    public void createSprintFromDto(){
        Project project = Project.createProject(1L,"projectName","projectDescription");
        project = projectRepository.save(project);

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        SprintCreateDto sprintCreateDto = new SprintCreateDto(1L,"sprintName","sprintDescription",startDate,endDate);

        //Execution
        SprintUpdateDto sprintUpdateDto = sprintService.createSprintFromDto(1L,sprintCreateDto);

        //Assertions
        //check if sprint is correctly saved
        Sprint sprintFound= sprintRepository.getReferenceById(sprintUpdateDto.getId());
        assertEquals(sprintFound.getId(), sprintUpdateDto.getId());
        assertEquals(sprintFound.getDescription(), sprintUpdateDto.getDescription());
        assertEquals(sprintFound.getStartDate(), sprintUpdateDto.getStartDate());
        assertEquals(sprintFound.getEndDate(), sprintUpdateDto.getEndDate());
    }


    @Test
    public void updateSprintFromDto(){
        Project project = new ProjectTestDataBuilder().build();

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        Sprint sprint = new SprintTestDataBuilder()
                .withId(1L)
                .withName("originalName")
                .withDescription("originalDescription")
                .withProject(project)
                .withStartDate(startDate)
                .withEndDate(endDate)
                .build();
        sprintRepository.save(sprint);

        LocalDateTime updatedStartDate = LocalDateTime.now().plusDays(1);
        LocalDateTime updatedEndDate = LocalDateTime.now().plusDays(3);
        SprintUpdateDto sprintUpdateDtoInput = new SprintUpdateDto(
                1L
                ,"updatedName"
                ,"updatedDescription"
                ,updatedStartDate
                ,updatedEndDate);

        //Execution
        SprintUpdateDto sprintUpdateDtoResult = sprintService.updateSprintFromDto(1L,sprintUpdateDtoInput);

        //Assertions
        assertThat(sprintUpdateDtoResult).usingRecursiveComparison().isEqualTo(sprintUpdateDtoInput);
        Sprint sprintFound= sprintRepository.getReferenceById(1L);
        assertEquals(sprintFound.getName(),"updatedName");
        assertEquals(sprintFound.getDescription(),"updatedDescription");
        assertEquals(sprintFound.getStartDate(),updatedStartDate);
        assertEquals(sprintFound.getEndDate(),updatedEndDate);
    }



//    @Test
//    public void endSprint(){
//        Sprint sprint = new SprintTestDataBuilder().build();
//        sprint = sprintRepository.save(sprint);
//        Long sprintId = sprint.getId();
//
//        //Execution
//        sprintService.endSprint(sprintId);
//
//        //Assertions
//        assertTrue(sprint.isArchived());
//        assertTrue(sprint.getEndDate().isBefore(LocalDateTime.now()));
//    }


    @Test
    public void deleteSprint(){
        //Setup
        Sprint sprint = new SprintTestDataBuilder().withId(3L).build();
        sprint = sprintRepository.save(sprint);

        Issue issue1 = new IssueTestDataBuilder().withId(6L).withSprint(sprint).build();
        Issue issue2 = new IssueTestDataBuilder().withId(7L).withSprint(sprint).build();
        issue1 = issueRepository.save(issue1);
        issue2 = issueRepository.save(issue2);

        Set<Issue> issuesBeforeDelete = issueRepository.findByCurrentSprintId(3L);
        assertThat(issuesBeforeDelete).usingRecursiveComparison().isEqualTo(Set.of(issue1,issue2));

        //Execution
        sprintService.deleteSprint(3L);

        //Assertion
        assertThrows(EntityNotFoundException.class,()->sprintRepository.getReferenceById(3L));
        Set<Issue> issuesAfterDelete= issueRepository.findByCurrentSprintId(3L);
        assertThat(issuesAfterDelete).isEmpty();
        assertThat(issueRepository.findById(6L).get().getCurrentSprint()).isEmpty();
        assertThat(issueRepository.findById(6L).get().getCurrentSprint()).isEmpty();
    }


    @Test
    public void getSprintById(){

        Sprint sprint = new SprintTestDataBuilder().withId(3L).build();
        sprint = sprintRepository.save(sprint);

        //Execution
        SprintUpdateDto sprintUpdateDto = sprintService.getSprintById(3L);

        //Assertions
        assertEquals(3L,sprintUpdateDto.getId());
        assertEquals(sprint.getName(),sprintUpdateDto.getName());
        assertEquals(sprint.getDescription(),sprintUpdateDto.getDescription());
        assertEquals(sprint.getStartDate(),sprintUpdateDto.getStartDate());
        assertEquals(sprint.getEndDate(),sprintUpdateDto.getEndDate());
    }

    @Test
    public void getSprintIssueHistory(){
        Sprint sprint= new SprintTestDataBuilder().withId(8L).withArchived(true).build();
        sprintRepository.save(sprint);

        Issue issue = new IssueTestDataBuilder().withId(1L).withSprint(null).build();
        Issue issue2 = new IssueTestDataBuilder().withId(2L).withSprint(null).build();

        SprintIssueHistory sprintIssueHistory1 = SprintIssueHistory.createSprintIssueHistory(3L, sprint, issue);
        SprintIssueHistory sprintIssueHistory2 = SprintIssueHistory.createSprintIssueHistory(4L, sprint, issue2);
        List<SprintIssueHistory> sprintIssueHistories = new ArrayList<>(List.of(sprintIssueHistory1, sprintIssueHistory2));
        sprintIssueHistoryRepository.saveAll(sprintIssueHistories);
        Set<SprintIssueHistoryDto> expectedSprintIssueHistoryDtoSet= sprintIssueHistories.stream().map(SprintIssueHistoryDto::new).collect(Collectors.toSet());

        //Execution
        Set<SprintIssueHistoryDto> sprintIssueHistoryDtoSet= sprintService.getSprintIssueHistory(8L);

        //Assertions
        assertThat(expectedSprintIssueHistoryDtoSet).usingRecursiveComparison().isEqualTo(sprintIssueHistoryDtoSet);

    }








}
