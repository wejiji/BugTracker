package com.example.security2pro.service;

import com.example.security2pro.databuilders.IssueTestDataBuilder;
import com.example.security2pro.databuilders.ProjectTestDataBuilder;
import com.example.security2pro.databuilders.SprintTestDataBuilder;
import com.example.security2pro.domain.enums.IssueStatus;
import com.example.security2pro.domain.model.*;
import com.example.security2pro.domain.model.issue.Issue;
import com.example.security2pro.repository.IssueRepositoryFake;
import com.example.security2pro.repository.ProjectRepositoryFake;
import com.example.security2pro.repository.SprintIssueHistoryRepositoryFake;
import com.example.security2pro.repository.SprintRepositoryFake;
import com.example.security2pro.repository.repository_interfaces.IssueRepository;
import com.example.security2pro.repository.repository_interfaces.ProjectRepository;
import com.example.security2pro.repository.repository_interfaces.SprintIssueHistoryRepository;
import com.example.security2pro.repository.repository_interfaces.SprintRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class HistoryServiceTest_endSprintAndSprintIssues {

    private final SprintRepository sprintRepository = new SprintRepositoryFake();
    private final IssueRepository issueRepository = new IssueRepositoryFake();
    private final ProjectRepository projectRepository = new ProjectRepositoryFake();
    private final SprintIssueHistoryRepository sprintIssueHistoryRepository = new SprintIssueHistoryRepositoryFake();

    private final Clock clock = Clock.fixed(ZonedDateTime.of(2023,1,1,1,10,10,1, ZoneId.systemDefault()).toInstant(),ZoneId.systemDefault());

    private HistoryService historyService = new HistoryService(projectRepository,sprintRepository,issueRepository,sprintIssueHistoryRepository,clock);


    private static Stream<Arguments> generateArgsCombinations_forceEndIssueTrue(){

        return Stream.of(

        //forceEndIssue = true
                // sprint count does not matter here
                Arguments.of(1 // initial total sprint count in the project
                        , true // issue status change after execution of the method under test
                        , 3 // expected history count after execution of the method under test
                        , 0 // expected active sprint count after execution of the method under test
                        , IssueStatus.DONE, IssueStatus.DONE, IssueStatus.DONE  // each issue initial status
                        , "noCurrentSprint", "noCurrentSprint", "noCurrentSprint" // expected sprint for each issue after execution of the method under test
                        , IssueStatus.DONE, IssueStatus.DONE, IssueStatus.DONE // expected issue status after execution of the method under test
                        , true,true,true// each issue's isArchived() after execution of the method under test
                        , true,true,true)// each issue history's 'complete' field. this field will be true if issue status is done when being archived

                ,Arguments.of(
                        1
                        , true
                        , 3
                        , 0
                        , IssueStatus.IN_PROGRESS, IssueStatus.DONE, IssueStatus.TODO
                        , "noCurrentSprint", "noCurrentSprint", "noCurrentSprint"
                        , IssueStatus.DONE, IssueStatus.DONE, IssueStatus.DONE
                        , true,true,true
                        , true,true,true)

                ,Arguments.of(
                        1
                        , true
                        , 3
                        , 0
                        , IssueStatus.IN_PROGRESS, IssueStatus.TODO, IssueStatus.IN_REVIEW
                        , "noCurrentSprint", "noCurrentSprint", "noCurrentSprint"
                        , IssueStatus.DONE, IssueStatus.DONE, IssueStatus.DONE
                        , true,true,true
                        , true,true,true)
        );
    }


    @ParameterizedTest
    @MethodSource(value ="generateArgsCombinations_forceEndIssueTrue")
    public void endSprintAndSprintIssues_WhenSprintsHaveIssues_forceEndIssueTrue(
            int totalSprintCountOfProject
            , boolean forceEndIssue
            , int foundHistoryCount
            , int expectedActiveSprintCount
            , IssueStatus issueStatus1, IssueStatus issueStatus2, IssueStatus issueStatus3
            , String expectedSprintForIssue1Key, String expectedSprintForIssue2Key, String expectedSprintForIssue3Key
            , IssueStatus expectedIssue1Status, IssueStatus expectedIssue2Status, IssueStatus expectedIssue3Status
            , boolean expectedIssue1Archived, boolean expectedIssue2Archived, boolean expectedIssue3Archived
            , boolean issue1HistoryComplete, boolean issue2HistoryComplete, boolean issue3HistoryComplete
    ){

        //Setup
        Project project = new ProjectTestDataBuilder()
                .withId(100L)
                .build();
        // in order to use id generation feature, id must be from repository (fake repo in test)
        // or manually increment repository's generated id cursor..

        Sprint initialSprint = new SprintTestDataBuilder()
                .withId(null)
                .withStartDate(LocalDateTime.now(clock).minusDays(1))
                .withProject(project)
                .build();

        initialSprint = sprintRepository.save(initialSprint);


        // - set up issue data
        assertEquals(totalSprintCountOfProject,sprintRepository.findAllByProjectId(project.getId()).size());
        assertEquals(0, sprintIssueHistoryRepository.findAllByArchivedSprintId(initialSprint.getId()).size());
        Issue issue1 = new IssueTestDataBuilder()
                .withId(10L)
                .withSprint(initialSprint)
                .withStatus(issueStatus1)
                .build();
        assertFalse(issue1.isArchived());
        Issue issue2 = new IssueTestDataBuilder()
                .withId(20L)
                .withSprint(initialSprint)
                .withStatus(issueStatus2)
                .build();
        assertFalse(issue2.isArchived());
        Issue issue3 = new IssueTestDataBuilder()
                .withId(30L)
                .withSprint(initialSprint)
                .withStatus(issueStatus3)
                .build();
        assertFalse(issue3.isArchived());
        Set<Issue> issueSet = new HashSet<>(Set.of(issue1,issue2,issue3));
        issueRepository.saveAll(issueSet);



        //Execution
        historyService.endSprintAndSprintIssues(initialSprint.getId(),forceEndIssue);

        //Assertions
        Sprint expectedSprint = new SprintTestDataBuilder()
                .withId(initialSprint.getId())
                .withStartDate(initialSprint.getStartDate())
                .withProject(project)
                .withArchived(true)
                .withEndDate(LocalDateTime.now(clock))
                .build();

        List<Sprint> activeSprints = new ArrayList<>(sprintRepository.findByProjectIdAndArchivedFalse(project.getId()));
        assertEquals(expectedActiveSprintCount,activeSprints.size());


        Map<String,Sprint> sprintMap = new HashMap<>();
        sprintMap.put("noCurrentSprint",null);
        sprintMap.put("initialSprint",initialSprint);


        //set up expected issues
        Issue expectedIssue1 = new IssueTestDataBuilder()
                .withId(issue1.getId())
                .withSprint(sprintMap.get(expectedSprintForIssue1Key))
                .withStatus(expectedIssue1Status)
                .withArchived(expectedIssue1Archived)
                .build();
        Issue expectedIssue2 = new IssueTestDataBuilder()
                .withId(issue2.getId())
                .withSprint(sprintMap.get(expectedSprintForIssue2Key))
                .withStatus(expectedIssue2Status)
                .withArchived(expectedIssue2Archived)
                .build();
        Issue expectedIssue3 = new IssueTestDataBuilder()
                .withId(issue3.getId())
                .withSprint(sprintMap.get(expectedSprintForIssue3Key))
                .withStatus(expectedIssue3Status)
                .withArchived(expectedIssue3Archived)
                .build();

        Set<Issue> expectedIssueSet = new HashSet<>(Set.of(expectedIssue1, expectedIssue2, expectedIssue3));

        //check if the sprint is archived & check against expected sprint
        assertTrue(initialSprint.isArchived());
        assertThat(initialSprint).usingRecursiveComparison().isEqualTo(expectedSprint);
        Sprint sprintFound = sprintRepository.findById(initialSprint.getId()).get();
        assertThat(sprintFound).usingRecursiveComparison().isEqualTo(expectedSprint);

        //check if issue is archived or not depending on their 'issueStatus' field
        //-get saved issues
        Set<Issue> issuesFound = issueRepository.findAllById(Set.of(issue1.getId(),issue2.getId(),issue3.getId()));

        assertThat(expectedIssueSet).usingRecursiveComparison().isEqualTo(issuesFound);

        //check if the right histories were saved
        //-check saved history count
        Set<SprintIssueHistory> savedHistory = sprintIssueHistoryRepository.findAllByArchivedSprintId(initialSprint.getId());
        Map<Long, List<SprintIssueHistory>> foundHistoryMap = savedHistory.stream().collect(groupingBy(history -> history.getIssue().getId()));
        assertEquals(foundHistoryCount ,foundHistoryMap.size());
        assertThat(savedHistory.stream().map(SprintIssueHistory::getIssue).collect(Collectors.toSet()))
                .usingRecursiveComparison()
                .isEqualTo(expectedIssueSet);

        //-get each history to check 'complete' field of histories
        SprintIssueHistory issue1History = foundHistoryMap.get(issue1.getId()).get(0);
        SprintIssueHistory issue2History = foundHistoryMap.get(issue2.getId()).get(0);
        SprintIssueHistory issue3History = foundHistoryMap.get(issue3.getId()).get(0);
        assertEquals(issue1HistoryComplete,issue1History.isComplete());
        assertEquals(issue2HistoryComplete,issue2History.isComplete());
        assertEquals(issue3HistoryComplete,issue3History.isComplete());

        //-check 'archivedSprint' field of histories
        foundHistoryMap.values().stream().map(history -> assertThat(history.get(0).getArchivedSprint()).usingRecursiveComparison().isEqualTo(expectedSprint));
    }





    private static Stream<Arguments> generateArgsCombinations_nextSprintExists(){
        //forceEndIssue = false & next sprint exists
        return Stream.of(

                Arguments.of(
                        2 // initial total sprint count in the project
                        , false // issue status change after execution of the method under test
                        , 3 // expected history count after execution of the method under test
                        , 1 // expected active sprint count after execution of the method under test
                        , IssueStatus.DONE, IssueStatus.DONE, IssueStatus.DONE // each issue initial status
                        , "noCurrentSprint","noCurrentSprint","noCurrentSprint" // expected sprint for each issue after execution of the method under test
                        , IssueStatus.DONE, IssueStatus.DONE, IssueStatus.DONE  // expected issue status after execution of the method under test
                        , true,true,true// each issue's isArchived() after execution of the method under test
                        , true,true,true)// each issue history's 'complete' field. this field will be true if issue status is done when being archived

                ,Arguments.of(
                        2
                        , false
                        , 3
                        , 1
                        , IssueStatus.IN_PROGRESS, IssueStatus.DONE, IssueStatus.TODO
                        , "nextActiveSprint","noCurrentSprint","nextActiveSprint"
                        , IssueStatus.IN_PROGRESS, IssueStatus.DONE, IssueStatus.TODO
                        , false,true,false
                        , false,true,false)

                ,Arguments.of(
                        2
                        , false
                        , 3
                        , 1
                        , IssueStatus.IN_PROGRESS, IssueStatus.TODO, IssueStatus.IN_REVIEW
                        , "nextActiveSprint","nextActiveSprint","nextActiveSprint"
                        , IssueStatus.IN_PROGRESS, IssueStatus.TODO, IssueStatus.IN_REVIEW
                        , false,false,false
                        , false,false,false)
        );
    }
    @ParameterizedTest
    @MethodSource(value ="generateArgsCombinations_nextSprintExists")
    public void endSprintAndSprintIssues_WhenSprintsHaveIssues_nextSprintExists(
            int totalSprintCountOfProject
            , boolean forceEndIssue
            , int foundHistoryCount
            , int expectedActiveSprintCount
            , IssueStatus issueStatus1, IssueStatus issueStatus2, IssueStatus issueStatus3
            , String expectedSprintForIssue1Key, String expectedSprintForIssue2Key, String expectedSprintForIssue3Key
            , IssueStatus expectedIssue1Status, IssueStatus expectedIssue2Status, IssueStatus expectedIssue3Status
            , boolean expectedIssue1Archived, boolean expectedIssue2Archived, boolean expectedIssue3Archived
            , boolean issue1HistoryComplete, boolean issue2HistoryComplete, boolean issue3HistoryComplete
    ){

        //Setup
        Project project = new ProjectTestDataBuilder()
                .withId(100L)
                .build();
        // in order to use id generation feature, id must be from repository (fake repo in test)
        // or manually increment repository's generated id cursor..

        Sprint initialSprint = new SprintTestDataBuilder()
                .withId(null)
                .withStartDate(LocalDateTime.now(clock).minusDays(1))
                .withProject(project)
                .build();
        Sprint nextActiveSprint = new SprintTestDataBuilder()
                .withId(null)
                .withStartDate(LocalDateTime.now(clock).minusDays(1))
                .withProject(project)
                .build();
        initialSprint = sprintRepository.save(initialSprint);
        nextActiveSprint = sprintRepository.save(nextActiveSprint);


        // - set up issue data
        assertEquals(totalSprintCountOfProject,sprintRepository.findAllByProjectId(project.getId()).size());
        assertEquals(0, sprintIssueHistoryRepository.findAllByArchivedSprintId(initialSprint.getId()).size());
        Issue issue1 = new IssueTestDataBuilder()
                .withId(10L)
                .withSprint(initialSprint)
                .withStatus(issueStatus1)
                .build();
        assertFalse(issue1.isArchived());
        Issue issue2 = new IssueTestDataBuilder()
                .withId(20L)
                .withSprint(initialSprint)
                .withStatus(issueStatus2)
                .build();
        assertFalse(issue2.isArchived());
        Issue issue3 = new IssueTestDataBuilder()
                .withId(30L)
                .withSprint(initialSprint)
                .withStatus(issueStatus3)
                .build();
        assertFalse(issue3.isArchived());
        Set<Issue> issueSet = new HashSet<>(Set.of(issue1,issue2,issue3));
        issueRepository.saveAll(issueSet);



        //Execution
        historyService.endSprintAndSprintIssues(initialSprint.getId(),forceEndIssue);

        //Assertions
        Sprint expectedSprint = new SprintTestDataBuilder()
                .withId(initialSprint.getId())
                .withStartDate(initialSprint.getStartDate())
                .withProject(project)
                .withArchived(true)
                .withEndDate(LocalDateTime.now(clock))
                .build();

        List<Sprint> activeSprints = new ArrayList<>(sprintRepository.findByProjectIdAndArchivedFalse(project.getId()));
        assertEquals(expectedActiveSprintCount,activeSprints.size());

        Map<String,Sprint> sprintMap = new HashMap<>();
        sprintMap.put("noCurrentSprint",null);
        sprintMap.put("initialSprint",initialSprint);
        sprintMap.put("nextActiveSprint",nextActiveSprint);


        //set up expected issues
        Issue expectedIssue1 = new IssueTestDataBuilder()
                .withId(issue1.getId())
                .withSprint(sprintMap.get(expectedSprintForIssue1Key))
                .withStatus(expectedIssue1Status)
                .withArchived(expectedIssue1Archived)
                .build();
        Issue expectedIssue2 = new IssueTestDataBuilder()
                .withId(issue2.getId())
                .withSprint(sprintMap.get(expectedSprintForIssue2Key))
                .withStatus(expectedIssue2Status)
                .withArchived(expectedIssue2Archived)
                .build();
        Issue expectedIssue3 = new IssueTestDataBuilder()
                .withId(issue3.getId())
                .withSprint(sprintMap.get(expectedSprintForIssue3Key))
                .withStatus(expectedIssue3Status)
                .withArchived(expectedIssue3Archived)
                .build();

        Set<Issue> expectedIssueSet = new HashSet<>(Set.of(expectedIssue1, expectedIssue2, expectedIssue3));

        //check if the sprint is archived & check against expected sprint
        assertTrue(initialSprint.isArchived());
        assertThat(initialSprint).usingRecursiveComparison().isEqualTo(expectedSprint);
        Sprint sprintFound = sprintRepository.findById(initialSprint.getId()).get();
        assertThat(sprintFound).usingRecursiveComparison().isEqualTo(expectedSprint);

        //check if issue is archived or not depending on their 'issueStatus' field
        //-get saved issues
        Set<Issue> issuesFound = issueRepository.findAllById(Set.of(issue1.getId(),issue2.getId(),issue3.getId()));

        assertThat(expectedIssueSet).usingRecursiveComparison().isEqualTo(issuesFound);

        //check if the right histories were saved
        //-check saved history count
        Set<SprintIssueHistory> savedHistory = sprintIssueHistoryRepository.findAllByArchivedSprintId(initialSprint.getId());
        Map<Long, List<SprintIssueHistory>> foundHistoryMap = savedHistory.stream().collect(groupingBy(history -> history.getIssue().getId()));
        assertEquals(foundHistoryCount ,foundHistoryMap.size());
        assertThat(savedHistory.stream().map(SprintIssueHistory::getIssue).collect(Collectors.toSet()))
                .usingRecursiveComparison()
                .isEqualTo(expectedIssueSet);

        //-get each history to check 'complete' field of histories
        SprintIssueHistory issue1History = foundHistoryMap.get(issue1.getId()).get(0);
        SprintIssueHistory issue2History = foundHistoryMap.get(issue2.getId()).get(0);
        SprintIssueHistory issue3History = foundHistoryMap.get(issue3.getId()).get(0);
        assertEquals(issue1HistoryComplete,issue1History.isComplete());
        assertEquals(issue2HistoryComplete,issue2History.isComplete());
        assertEquals(issue3HistoryComplete,issue3History.isComplete());

        //-check 'archivedSprint' field of histories
        foundHistoryMap.values().stream().map(history -> assertThat(history.get(0).getArchivedSprint()).usingRecursiveComparison().isEqualTo(expectedSprint));
    }



    private static Stream<Arguments> generateArgsCombinations_newNextSprintGenerated(){
        //forceEndIssue = false & next sprint does not exist

        return Stream.of(
                Arguments.of(
                        1 // initial total sprint count in the project
                        , false // issue status change after execution of the method under test
                        , 3 // expected history count after execution of the method under test
                        , 0 // expected active sprint count after execution of the method under test
                        , IssueStatus.DONE, IssueStatus.DONE, IssueStatus.DONE // each issue initial status
                        , "noCurrentSprint","noCurrentSprint","noCurrentSprint" // expected sprint for each issue after execution of the method under test
                        , IssueStatus.DONE, IssueStatus.DONE, IssueStatus.DONE // expected issue status after execution of the method under test
                        , true,true,true // each issue's isArchived() after execution of the method under test
                        , true,true,true) // each issue history's 'complete' field. this field will be true if issue status is done when being archived

                ,Arguments.of(
                        1
                        , false
                        , 3
                        , 1
                        , IssueStatus.IN_PROGRESS, IssueStatus.DONE, IssueStatus.TODO
                        , "newlyGeneratedSprint","noCurrentSprint","newlyGeneratedSprint"
                        , IssueStatus.IN_PROGRESS, IssueStatus.DONE, IssueStatus.TODO
                        , false,true,false
                        , false,true,false)

                ,Arguments.of(
                        1
                        , false
                        , 3
                        , 1
                        , IssueStatus.IN_PROGRESS, IssueStatus.TODO, IssueStatus.IN_REVIEW
                        , "newlyGeneratedSprint", "newlyGeneratedSprint", "newlyGeneratedSprint"
                        , IssueStatus.IN_PROGRESS, IssueStatus.TODO, IssueStatus.IN_REVIEW
                        , false,false,false
                        , false,false,false)
        );
    }
    @ParameterizedTest
    @MethodSource(value ="generateArgsCombinations_newNextSprintGenerated")
    public void endSprintAndSprintIssues_WhenSprintsHaveIssues_newNextSprintGenerated(
            int totalSprintCountOfProject
            , boolean forceEndIssue
            , int foundHistoryCount
            , int expectedActiveSprintCount
            , IssueStatus issueStatus1, IssueStatus issueStatus2, IssueStatus issueStatus3
            , String expectedSprintForIssue1Key, String expectedSprintForIssue2Key, String expectedSprintForIssue3Key
            , IssueStatus expectedIssue1Status, IssueStatus expectedIssue2Status, IssueStatus expectedIssue3Status
            , boolean expectedIssue1Archived, boolean expectedIssue2Archived, boolean expectedIssue3Archived
            , boolean issue1HistoryComplete, boolean issue2HistoryComplete, boolean issue3HistoryComplete
    ){

        //Setup
        Project project = new ProjectTestDataBuilder()
                .withId(100L)
                .build();
        // in order to use id generation feature, id must be from repository (fake repo in test)
        // or manually increment repository's generated id cursor..
        Sprint initialSprint = new SprintTestDataBuilder()
                .withId(null)
                .withStartDate(LocalDateTime.now(clock).minusDays(1))
                .withProject(project)
                .build();

        initialSprint = sprintRepository.save(initialSprint);


        // - set up issue data
        assertEquals(totalSprintCountOfProject,sprintRepository.findAllByProjectId(project.getId()).size());
        assertEquals(0, sprintIssueHistoryRepository.findAllByArchivedSprintId(initialSprint.getId()).size());
        Issue issue1 = new IssueTestDataBuilder()
                .withId(10L)
                .withSprint(initialSprint)
                .withStatus(issueStatus1)
                .build();
        assertFalse(issue1.isArchived());
        Issue issue2 = new IssueTestDataBuilder()
                .withId(20L)
                .withSprint(initialSprint)
                .withStatus(issueStatus2)
                .build();
        assertFalse(issue2.isArchived());
        Issue issue3 = new IssueTestDataBuilder()
                .withId(30L)
                .withSprint(initialSprint)
                .withStatus(issueStatus3)
                .build();
        assertFalse(issue3.isArchived());
        Set<Issue> issueSet = new HashSet<>(Set.of(issue1,issue2,issue3));
        issueRepository.saveAll(issueSet);



        //Execution
        historyService.endSprintAndSprintIssues(initialSprint.getId(),forceEndIssue);

        //Assertions
        Sprint expectedSprint = new SprintTestDataBuilder()
                .withId(initialSprint.getId())
                .withStartDate(initialSprint.getStartDate())
                .withProject(project)
                .withArchived(true)
                .withEndDate(LocalDateTime.now(clock))
                .build();

        List<Sprint> activeSprints = new ArrayList<>(sprintRepository.findByProjectIdAndArchivedFalse(project.getId()));
        assertEquals(expectedActiveSprintCount,activeSprints.size());
        Sprint newlyGeneratedSprint = activeSprints.stream().findAny()
                .orElse(new SprintTestDataBuilder().withName("dummy-not-used!").build());
        //when there is no active sprint, this value should not be used

        Map<String,Sprint> sprintMap = new HashMap<>();
        sprintMap.put("noCurrentSprint",null);
        sprintMap.put("newlyGeneratedSprint",newlyGeneratedSprint);

        //set up expected issues
        Issue expectedIssue1 = new IssueTestDataBuilder()
                .withId(issue1.getId())
                .withSprint(sprintMap.get(expectedSprintForIssue1Key))
                .withStatus(expectedIssue1Status)
                .withArchived(expectedIssue1Archived)
                .build();
        Issue expectedIssue2 = new IssueTestDataBuilder()
                .withId(issue2.getId())
                .withSprint(sprintMap.get(expectedSprintForIssue2Key))
                .withStatus(expectedIssue2Status)
                .withArchived(expectedIssue2Archived)
                .build();
        Issue expectedIssue3 = new IssueTestDataBuilder()
                .withId(issue3.getId())
                .withSprint(sprintMap.get(expectedSprintForIssue3Key))
                .withStatus(expectedIssue3Status)
                .withArchived(expectedIssue3Archived)
                .build();

        Set<Issue> expectedIssueSet = new HashSet<>(Set.of(expectedIssue1, expectedIssue2, expectedIssue3));

        //check if the sprint is archived & check against expected sprint
        assertTrue(initialSprint.isArchived());
        assertThat(initialSprint).usingRecursiveComparison().isEqualTo(expectedSprint);
        Sprint sprintFound = sprintRepository.findById(initialSprint.getId()).get();
        assertThat(sprintFound).usingRecursiveComparison().isEqualTo(expectedSprint);

        //check if issue is archived or not depending on their 'issueStatus' field
        //-get saved issues
        Set<Issue> issuesFound = issueRepository.findAllById(Set.of(issue1.getId(),issue2.getId(),issue3.getId()));

        assertThat(expectedIssueSet).usingRecursiveComparison().isEqualTo(issuesFound);

        //check if the right histories were saved
        //-check saved history count
        Set<SprintIssueHistory> savedHistory = sprintIssueHistoryRepository.findAllByArchivedSprintId(initialSprint.getId());
        Map<Long, List<SprintIssueHistory>> foundHistoryMap = savedHistory.stream().collect(groupingBy(history -> history.getIssue().getId()));
        assertEquals(foundHistoryCount ,foundHistoryMap.size());
        assertThat(savedHistory.stream().map(SprintIssueHistory::getIssue).collect(Collectors.toSet()))
                .usingRecursiveComparison()
                .isEqualTo(expectedIssueSet);

        //-get each history to check 'complete' field of histories
        SprintIssueHistory issue1History = foundHistoryMap.get(issue1.getId()).get(0);
        SprintIssueHistory issue2History = foundHistoryMap.get(issue2.getId()).get(0);
        SprintIssueHistory issue3History = foundHistoryMap.get(issue3.getId()).get(0);
        assertEquals(issue1HistoryComplete,issue1History.isComplete());
        assertEquals(issue2HistoryComplete,issue2History.isComplete());
        assertEquals(issue3HistoryComplete,issue3History.isComplete());

        //-check 'archivedSprint' field of histories
        foundHistoryMap.values().stream().map(history -> assertThat(history.get(0).getArchivedSprint()).usingRecursiveComparison().isEqualTo(expectedSprint));
    }



    @Test
    public void endSprintAndSprintIssues_WhenNoIssueExist(){

        Project project = new ProjectTestDataBuilder()
                .withId(100L)
                .build();
        // in order to use id generation feature, id must be from repository (fake repo in test)
        // or manually increment repository's generated id cursor..
        LocalDateTime startDate = LocalDateTime.now(clock).minusDays(1);
        Sprint initialSprint = new SprintTestDataBuilder()
                .withId(null)
                .withStartDate(startDate)
                .withProject(project)
                .build();
        initialSprint = sprintRepository.save(initialSprint);

        // - set up issue data
        assertEquals(1,sprintRepository.findAllByProjectId(project.getId()).size());
        assertEquals(0, sprintIssueHistoryRepository.findAllByArchivedSprintId(initialSprint.getId()).size());


        //Execution
        historyService.endSprintAndSprintIssues(initialSprint.getId(),false);

        //Assertions
        Sprint expectedSprint = new SprintTestDataBuilder()
                .withId(initialSprint.getId())
                .withStartDate(initialSprint.getStartDate())
                .withProject(project)
                .withArchived(true)
                .withEndDate(LocalDateTime.now(clock))
                .build();


        assertThat(initialSprint).usingRecursiveComparison().isEqualTo(expectedSprint);
        Sprint sprintFound = sprintRepository.findById(initialSprint.getId()).get();
        assertThat(sprintFound).usingRecursiveComparison().isEqualTo(expectedSprint);

        assertEquals(0, sprintIssueHistoryRepository.findAllByArchivedSprintId(initialSprint.getId()).size());

    }





}
