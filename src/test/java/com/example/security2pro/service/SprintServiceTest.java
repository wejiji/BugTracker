package com.example.security2pro.service;

import com.example.security2pro.databuilders.IssueTestDataBuilder;
import com.example.security2pro.databuilders.ProjectTestDataBuilder;
import com.example.security2pro.databuilders.SprintTestDataBuilder;
import com.example.security2pro.domain.model.*;
import com.example.security2pro.dto.sprint.SprintCreateDto;
import com.example.security2pro.dto.sprint.SprintUpdateDto;
import com.example.security2pro.dto.sprinthistory.SprintIssueHistoryDto;
import com.example.security2pro.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SprintServiceTest {
    @Mock
    IssueRepository issueRepository;

    @Mock
    ProjectRepository projectRepository;

    @Mock
    SprintIssueHistoryRepository sprintIssueHistoryRepository;
    @Mock
    SprintRepository sprintRepository;

    @InjectMocks
    SprintService sprintService;

    @Test
    public void createSprintFromDto(){

        Long projectId = 2L;
        Long sprintId = 3L;
        Sprint sprint = new SprintTestDataBuilder().withId(null).build();
        Sprint sprintWithId = new SprintTestDataBuilder().withId(sprintId).build();

        SprintCreateDto sprintCreateDto = new SprintCreateDto(projectId, sprint.getName(),sprint.getDescription(),sprint.getStartDate(),sprint.getEndDate());
        Project project = new ProjectTestDataBuilder().withId(projectId).build();


        try(MockedStatic<Sprint> sprintMockedStatic = Mockito.mockStatic(Sprint.class)){

            sprintMockedStatic.when(()->Sprint.createSprint(
                            project
                            ,sprintCreateDto.getName()
                            ,sprintCreateDto.getDescription()
                            ,sprintCreateDto.getStartDate()
                            ,sprintCreateDto.getEndDate()))
                    .thenReturn(Optional.of(sprint));
            when(sprintRepository.save(Mockito.any(Sprint.class))).thenReturn(sprintWithId);
            when(projectRepository.getReferenceById(projectId)).thenReturn(project);

            //Execution
            SprintUpdateDto sprintUpdateDto = sprintService.createSprintFromDto(projectId,sprintCreateDto);

            //Assertions
            InOrder inOrder = inOrder(projectRepository,Sprint.class,sprintRepository);
            inOrder.verify(projectRepository,times(1)).getReferenceById(projectId);
            inOrder.verify(sprintMockedStatic,()->Sprint.createSprint(project, sprintCreateDto.getName(), sprintCreateDto.getDescription(), sprintCreateDto.getStartDate(), sprintCreateDto.getEndDate()),times(1));
            inOrder.verify(sprintRepository).save(sprint);

            //Assertions
            assertEquals(sprintUpdateDto.getId(),sprintWithId.getId());
            assertEquals(sprintUpdateDto.getName(),sprintWithId.getName());
            assertEquals(sprintUpdateDto.getDescription(),sprintWithId.getDescription());
            assertEquals(sprintUpdateDto.getStartDate(),sprintWithId.getStartDate());
            assertEquals(sprintUpdateDto.getEndDate(),sprintWithId.getEndDate());
        }
    }


    @Test
    public void updateSprintFromDto_VerifyResult(){

        Long sprintId = 31L;
        Sprint sprint = new SprintTestDataBuilder().withId(sprintId).build();

        when(sprintRepository.getReferenceById(sprintId)).thenReturn(sprint);
        SprintUpdateDto sprintUpdateDtoInput = new SprintUpdateDto(sprint.getId(),sprint.getName(),sprint.getDescription(),sprint.getStartDate(),sprint.getEndDate());

        //Execution
        SprintUpdateDto sprintUpdateDtoResult = sprintService.updateSprintFromDto(sprintId,sprintUpdateDtoInput);

        //Assertion
        assertEquals(sprintUpdateDtoResult, sprintUpdateDtoInput);
    }

    @Test
    public void updateSprintFromDto_VerifyCalls(){

        Long sprintId = 31L;
        Sprint sprint = mock(Sprint.class);

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        SprintUpdateDto sprintUpdateDtoInput = new SprintUpdateDto(sprintId,"sprintName","sprintDescription", start, end);

        when(sprintRepository.getReferenceById(sprintId)).thenReturn(sprint);
        when(sprint.updateFields(sprintUpdateDtoInput.getName(), sprintUpdateDtoInput.getDescription(), sprintUpdateDtoInput.getStartDate(),sprintUpdateDtoInput.getEndDate()))
                .thenReturn(Optional.of(sprint));

        //Execution
        SprintUpdateDto sprintUpdateDtoResult = sprintService.updateSprintFromDto(sprintId,sprintUpdateDtoInput);

        //verification
        InOrder inOrder = inOrder(sprintRepository,sprint);
        inOrder.verify(sprintRepository,times(1)).getReferenceById(sprintId);
        inOrder.verify(sprint,times(1)).updateFields(sprintUpdateDtoInput.getName(), sprintUpdateDtoInput.getDescription(), sprintUpdateDtoInput.getStartDate(),sprintUpdateDtoInput.getEndDate());
        verify(sprint).getId();
        verify(sprint).getName();
        verify(sprint).getDescription();
        verify(sprint).getStartDate();
        verify(sprint).getEndDate();
        verifyNoMoreInteractions(sprintRepository,sprint);
    }


    @Test
    public void endSprint_VerifyResult(){
        Sprint sprint = new SprintTestDataBuilder().build();

        Long sprintId = sprint.getId();

        when(sprintRepository.getReferenceById(sprintId)).thenReturn(sprint);

        //Execution
        sprintService.endSprint(sprintId);

        //Assertions
        assertTrue(sprint.isArchived());
        assertTrue(sprint.getEndDate().isBefore(LocalDateTime.now()));
    }

    @Test
    public void endSprint_VerifyCalls(){
        Sprint sprint = mock(Sprint.class);

        Long sprintId = 5L;

        when(sprintRepository.getReferenceById(sprintId)).thenReturn(sprint);

        //Execution
        sprintService.endSprint(sprintId);

        //Assertions
        verify(sprintRepository,times(1)).getReferenceById(sprintId);
        verify(sprint,times(1)).completeSprint();
    }

    @Test
    public void deleteSprint_verifyResult(){
        Sprint sprint = new SprintTestDataBuilder().build();
        Long sprintId = sprint.getId();
        Issue issue1 = new IssueTestDataBuilder().withSprint(sprint).build();
        Issue issue2 = new IssueTestDataBuilder().withSprint(sprint).build();
        Set<Issue> issues = Set.of(issue1,issue2);


        when(sprintRepository.getReferenceById(sprintId)).thenReturn(sprint);
        when(issueRepository.findByCurrentSprintId(sprint.getId())).thenReturn(issues);

        //Execution
        sprintService.deleteSprint(sprintId);

        //Assertion
        assertThat(issue1.getCurrentSprint()).isEmpty();
        assertThat(issue2.getCurrentSprint()).isEmpty();
    }

    @Test
    public void deleteSprint_verifyCalls(){
        Sprint sprint = new SprintTestDataBuilder().build();
        Long sprintId = sprint.getId();
        Issue issue1 = mock(Issue.class);
        Issue issue2 = mock(Issue.class);
        Set<Issue> issues = Set.of(issue1,issue2);


        when(sprintRepository.getReferenceById(sprintId)).thenReturn(sprint);
        when(issueRepository.findByCurrentSprintId(sprint.getId())).thenReturn(issues);

        //Execution
        sprintService.deleteSprint(sprintId);

        //verification
        InOrder inOrder= inOrder(sprintRepository,issueRepository,issue1,issue2);
        inOrder.verify(sprintRepository).getReferenceById(sprintId);
        inOrder.verify(issueRepository,times(1)).findByCurrentSprintId(sprint.getId());

        verify(issue1).assignCurrentSprint(null); //stream does not guarantee order. so not able to use in Order here.????
        verify(issue2).assignCurrentSprint(null);
        inOrder.verify(sprintRepository,times(1)).deleteById(sprint.getId());
    }

    @Test
    public void getSprintById(){
        Sprint sprint = new SprintTestDataBuilder().build();

        when(sprintRepository.getReferenceById(sprint.getId())).thenReturn(sprint);

        //Execution
        SprintUpdateDto sprintUpdateDto = sprintService.getSprintById(sprint.getId());

        verify(sprintRepository).getReferenceById(sprint.getId());

        //Assertions
        assertEquals(sprint.getId(),sprintUpdateDto.getId());
        assertEquals(sprint.getName(),sprintUpdateDto.getName());
        assertEquals(sprint.getDescription(),sprintUpdateDto.getDescription());
        assertEquals(sprint.getStartDate(),sprintUpdateDto.getStartDate());
        assertEquals(sprint.getEndDate(),sprintUpdateDto.getEndDate());
    }

    @Test
    public void getSprintIssueHistory(){
        Sprint sprint= new SprintTestDataBuilder().withArchivedTrue().build();

        Issue issue = new IssueTestDataBuilder().build();
        Issue issue2 = new IssueTestDataBuilder().withId(49L).build();

        SprintIssueHistory sprintIssueHistory1 = new SprintIssueHistory(sprint, issue );
        SprintIssueHistory sprintIssueHistory2 = new SprintIssueHistory(sprint, issue2 );
        Set<SprintIssueHistory> sprintIssueHistories = Set.of(sprintIssueHistory1, sprintIssueHistory2);

        Set<SprintIssueHistoryDto> expectedSprintIssueHistoryDtos= sprintIssueHistories.stream().map(SprintIssueHistoryDto::new).collect(Collectors.toSet());
        System.out.println(sprintIssueHistories.size() +"is teh size");
        System.out.println(expectedSprintIssueHistoryDtos.size() +"is teh size");

        when(sprintRepository.findByIdAndArchivedTrue(sprint.getId())).thenReturn(Optional.of(sprint));

        when(sprintIssueHistoryRepository.findAllByArchivedSprintId(sprint.getId())).thenReturn(sprintIssueHistories);

        //Execution
        Set<SprintIssueHistoryDto> sprintIssueHistoryDtos= sprintService.getSprintIssueHistory(sprint.getId());

        //Assertions
        assertEquals(sprintIssueHistoryDtos.size(), sprintIssueHistories.size());
        assertEquals(sprintIssueHistoryDtos, expectedSprintIssueHistoryDtos);
    }








}
