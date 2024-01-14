package com.example.security2pro.service;

import com.example.security2pro.databuilders.IssueTestDataBuilder;
import com.example.security2pro.databuilders.ProjectTestDataBuilder;
import com.example.security2pro.databuilders.SprintTestDataBuilder;
import com.example.security2pro.domain.enums.IssueStatus;
import com.example.security2pro.domain.model.*;
import com.example.security2pro.repository.IssueRepositoryFake;
import com.example.security2pro.repository.ProjectRepositoryFake;
import com.example.security2pro.repository.SprintIssueHistoryRepositoryFake;
import com.example.security2pro.repository.SprintRepositoryFake;
import com.example.security2pro.repository.repository_interfaces.IssueRepository;
import com.example.security2pro.repository.repository_interfaces.ProjectRepository;
import com.example.security2pro.repository.repository_interfaces.SprintIssueHistoryRepository;
import com.example.security2pro.repository.repository_interfaces.SprintRepository;
import com.example.security2pro.service.usecases.HistoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


public class HistoryServiceTest_endProject {

    private final SprintRepository sprintRepository = new SprintRepositoryFake();
    private final IssueRepository issueRepository = new IssueRepositoryFake();
    private final ProjectRepository projectRepository = new ProjectRepositoryFake();
    private final SprintIssueHistoryRepository sprintIssueHistoryRepository = new SprintIssueHistoryRepositoryFake();

    private final Clock clock = Clock.fixed(ZonedDateTime.of(2023,1,1,1,10,10,1,ZoneId.systemDefault()).toInstant(),ZoneId.systemDefault());

    private HistoryService historyService = new HistoryService(projectRepository,sprintRepository,issueRepository,sprintIssueHistoryRepository, clock);



    //endProject

    //no sprint & no issue

    //sprints & no issue

    //sprints & issues & (forceEndIssue = true || forceEndIssue = false)

    //no sprint & issues &  (forceEndIssue = true || forceEndIssue = false)




    @Test
    public void endProject_NoSprintNoIssue(){
        Project project= new ProjectTestDataBuilder()
                .withId(100L)
                .build();

        projectRepository.save(project);
        assertFalse(project.isArchived());

        //Execution
        historyService.endProject(project.getId(),true);

        //Assertion
        Project expectedProject = new ProjectTestDataBuilder()
                .withId(project.getId())
                .withArchived(true)
                .build();

        Project projectFound = projectRepository.getReferenceById(project.getId());
        assertThat(project).usingRecursiveComparison().isEqualTo(expectedProject);
        assertThat(projectFound).usingRecursiveComparison().isEqualTo(expectedProject);
    }

    @Test
    public void endProject_NoIssue(){

        Project project= new ProjectTestDataBuilder()
                .withId(100L)
                .build();

        LocalDateTime startDate = LocalDateTime.now(clock).minusDays(1);
        Sprint sprint1 = new SprintTestDataBuilder()
                .withId(1L)
                .withProject(project)
                .withStartDate(startDate)
                .withArchived(false)
                .build();

        Sprint sprint2 = new SprintTestDataBuilder()
                .withId(2L)
                .withProject(project)
                .withStartDate(startDate)
                .withArchived(false)
                .build();
        projectRepository.save(project);
        sprintRepository.save(sprint1);
        sprintRepository.save(sprint2);

        //Execution
        historyService.endProject(project.getId(),true);

        //Assertions
        Sprint expectedSprint1 = new SprintTestDataBuilder()
                .withId(sprint1.getId())
                .withStartDate(sprint1.getStartDate())
                .withProject(project)
                .withArchived(true)
                .withEndDate(LocalDateTime.now(clock))
                .build();

        Sprint expectedSprint2 = new SprintTestDataBuilder()
                .withId(sprint2.getId())
                .withStartDate(sprint2.getStartDate())
                .withProject(project)
                .withArchived(true)
                .withEndDate(LocalDateTime.now(clock))
                .build();

        Project expectedProject = new ProjectTestDataBuilder()
                .withId(project.getId())
                .withArchived(true)
                .build();

        Project projectFound = projectRepository.getReferenceById(project.getId());
        assertThat(project).usingRecursiveComparison().isEqualTo(expectedProject);
        assertThat(projectFound).usingRecursiveComparison().isEqualTo(expectedProject);

        Set<Sprint> sprintsFound = sprintRepository.findAllByProjectId(project.getId());
        assertThat(Set.of(sprint1,sprint2)).usingRecursiveComparison().isEqualTo(Set.of(expectedSprint1,expectedSprint2));
        assertThat(sprintsFound).usingRecursiveComparison().isEqualTo(Set.of(expectedSprint1,expectedSprint2));
    }



    private static Stream<Arguments> endProjectArgs_sprintIssuesAndNoIssuesWithOutSprint(){
        return Stream.of(

                //forceEndIssue = true
                // sprint count does not matter here
                Arguments.of(2 // initial total sprint count in the project
                        , true // issue status change after execution of the method under test
                        , 3 // expected history count after execution of the method under test
                        , IssueStatus.DONE, IssueStatus.DONE, IssueStatus.DONE  // each issue initial status
                        , IssueStatus.DONE, IssueStatus.DONE, IssueStatus.DONE // expected issue status after execution of the method under test
                        , true, true, true)// each issue history's 'complete' field. this field will be true if issue status is done when being archived

                ,Arguments.of(
                        2
                        , true
                        , 3
                        , IssueStatus.IN_PROGRESS, IssueStatus.DONE, IssueStatus.TODO
                        , IssueStatus.DONE, IssueStatus.DONE, IssueStatus.DONE
                        , true, true, true)

                ,Arguments.of(
                        2
                        , true
                        , 3
                        , IssueStatus.IN_PROGRESS, IssueStatus.TODO, IssueStatus.IN_REVIEW
                        , IssueStatus.DONE, IssueStatus.DONE, IssueStatus.DONE
                        , true, true, true)


                //forceEndIssue = false

                ,Arguments.of(2 // initial total sprint count in the project
                        , false // issue status change after execution of the method under test
                        , 3 // expected history count after execution of the method under test
                        , IssueStatus.DONE, IssueStatus.DONE, IssueStatus.DONE  // each issue initial status
                        , IssueStatus.DONE, IssueStatus.DONE, IssueStatus.DONE // expected issue status after execution of the method under test
                        , true, true, true)// each issue history's 'complete' field. this field will be true if issue status is done when being archived

                ,Arguments.of(
                        2
                        , false
                        , 3
                        , IssueStatus.IN_PROGRESS, IssueStatus.DONE, IssueStatus.TODO
                        , IssueStatus.IN_PROGRESS, IssueStatus.DONE, IssueStatus.TODO
                        , false, true, false)

                ,Arguments.of(
                        2
                        , false
                        , 3
                        , IssueStatus.IN_PROGRESS, IssueStatus.TODO, IssueStatus.IN_REVIEW
                        , IssueStatus.IN_PROGRESS, IssueStatus.TODO, IssueStatus.IN_REVIEW
                        , false, false, false)
        );
    }

    @ParameterizedTest
    @MethodSource(value ="endProjectArgs_sprintIssuesAndNoIssuesWithOutSprint")
    public void endProject_SprintIssuesAndNoIssuesWithOutSprint(
            int totalSprintCountOfProject
            , boolean forceEndIssue
            , int foundHistoryCount
            , IssueStatus issueStatus1, IssueStatus issueStatus2, IssueStatus issueStatus3
            , IssueStatus expectedIssue1Status, IssueStatus expectedIssue2Status, IssueStatus expectedIssue3Status
            , boolean issue1HistoryComplete, boolean issue2HistoryComplete, boolean issue3HistoryComplete
    ){

        //Setup
        Project project = new ProjectTestDataBuilder()
                .withId(100L)
                .build();
        // in order to use id generation feature, id must be from repository (fake repo in test)
        // or manually increment repository's generated id cursor..
        LocalDateTime startDate = LocalDateTime.now(clock).minusDays(1);


        Sprint sprint1 = new SprintTestDataBuilder()
                .withId(1L)
                .withProject(project)
                .withStartDate(startDate)
                .withArchived(false)
                .build();

        Sprint sprint2 = new SprintTestDataBuilder()
                .withId(2L)
                .withProject(project)
                .withStartDate(startDate)
                .withArchived(false)
                .build();
        projectRepository.save(project);
        sprintRepository.save(sprint1);
        sprintRepository.save(sprint2);



        // - set up issue data
        assertEquals(totalSprintCountOfProject,sprintRepository.findAllByProjectId(project.getId()).size());

        Issue issue1 = new IssueTestDataBuilder()
                .withId(10L)
                .withProject(project)
                .withSprint(sprint1)
                .withStatus(issueStatus1)
                .withArchived(false)
                .build();
        Issue issue2 = new IssueTestDataBuilder()
                .withId(20L)
                .withProject(project)
                .withSprint(sprint1)
                .withStatus(issueStatus2)
                .withArchived(false)
                .build();
        Issue issue3 = new IssueTestDataBuilder()
                .withId(30L)
                .withProject(project)
                .withSprint(sprint1)
                .withStatus(issueStatus3)
                .withArchived(false)
                .build();
        Set<Issue> issueSet = new HashSet<>(Set.of(issue1,issue2,issue3));
        issueRepository.saveAll(issueSet);



        //Execution

        historyService.endProject(project.getId(),forceEndIssue);

        //Assertions
        Project expectedProject = new ProjectTestDataBuilder()
                .withId(project.getId())
                .withArchived(true)
                .build();

        Project projectFound = projectRepository.getReferenceById(project.getId());
        assertThat(project).usingRecursiveComparison().isEqualTo(expectedProject);
        assertThat(projectFound).usingRecursiveComparison().isEqualTo(expectedProject);

        Sprint expectedSprint1 = new SprintTestDataBuilder()
                .withId(sprint1.getId())
                .withStartDate(sprint1.getStartDate())
                .withProject(project)
                .withArchived(true)
                .withEndDate(LocalDateTime.now(clock))
                .build();

        Sprint expectedSprint2 = new SprintTestDataBuilder()
                .withId(sprint2.getId())
                .withProject(project)
                .withStartDate(sprint2.getStartDate())
                .withEndDate(LocalDateTime.now(clock))
                .withArchived(true)

                .build();

        Set<Sprint> sprintsFound = sprintRepository.findAllByProjectId(project.getId());
        assertThat(Set.of(sprint1,sprint2)).usingRecursiveComparison().isEqualTo(Set.of(expectedSprint1,expectedSprint2));
        assertThat(sprintsFound).usingRecursiveComparison().isEqualTo(Set.of(expectedSprint1,expectedSprint2));


        //set up expected issues
        Issue expectedIssue1 = new IssueTestDataBuilder()
                .withId(issue1.getId())
                .withProject(project)
                .withSprint(null)
                .withStatus(expectedIssue1Status)
                .withArchived(true)
                .build();
        Issue expectedIssue2 = new IssueTestDataBuilder()
                .withId(issue2.getId())
                .withProject(project)
                .withSprint(null)
                .withStatus(expectedIssue2Status)
                .withArchived(true)
                .build();
        Issue expectedIssue3 = new IssueTestDataBuilder()
                .withId(issue3.getId())
                .withProject(project)
                .withSprint(null)
                .withStatus(expectedIssue3Status)
                .withArchived(true)
                .build();

        Set<Issue> expectedIssueSet = new HashSet<>(Set.of(expectedIssue1, expectedIssue2, expectedIssue3));

        //check if the sprint is archived & check against expected sprint


        //check if issue is archived or not depending on their 'issueStatus' field
        //-get saved issues
        Set<Issue> issuesFound = issueRepository.findAllById(Set.of(issue1.getId(),issue2.getId(),issue3.getId()));
        assertThat(expectedIssueSet).usingRecursiveComparison().isEqualTo(issuesFound);

        //check if the right histories were saved
        //-check saved history count
        Set<SprintIssueHistory> savedHistory = sprintIssueHistoryRepository.findAllByArchivedSprintId(expectedSprint1.getId());
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
        foundHistoryMap.values().stream().map(history -> assertThat(history.get(0).getArchivedSprint()).usingRecursiveComparison().isEqualTo(expectedSprint1));
    }


    private static Stream<Arguments> endProjectArgs_sprintIssuesAndIssuesWithOutSprint(){
        return Stream.of(

                //forceEndIssue = true
                // sprint count does not matter here
                Arguments.of(2 // initial total sprint count in the project
                        , true // issue status change after execution of the method under test
                        , 1 // expected history count after execution of the method under test
                        , IssueStatus.DONE, IssueStatus.DONE, IssueStatus.DONE  // each issue initial status
                        , IssueStatus.DONE, IssueStatus.DONE, IssueStatus.DONE // expected issue status after execution of the method under test
                        , true, true, true)// each issue history's 'complete' field. this field will be true if issue status is done when being archived

                ,Arguments.of(
                        2
                        , true
                        , 1
                        , IssueStatus.IN_PROGRESS, IssueStatus.DONE, IssueStatus.TODO
                        , IssueStatus.DONE, IssueStatus.DONE, IssueStatus.DONE
                        , true, true, true)

                ,Arguments.of(
                        2
                        , true
                        , 1
                        , IssueStatus.IN_PROGRESS, IssueStatus.TODO, IssueStatus.IN_REVIEW
                        , IssueStatus.DONE, IssueStatus.DONE, IssueStatus.DONE
                        , true, true, true)


                //forceEndIssue = false

                ,Arguments.of(2 // initial total sprint count in the project
                        , false // issue status change after execution of the method under test
                        , 1 // expected history count after execution of the method under test
                        , IssueStatus.DONE, IssueStatus.DONE, IssueStatus.DONE  // each issue initial status
                        , IssueStatus.DONE, IssueStatus.DONE, IssueStatus.DONE // expected issue status after execution of the method under test
                        , true, true, true)// each issue history's 'complete' field. this field will be true if issue status is done when being archived

                ,Arguments.of(
                        2
                        , false
                        , 1
                        , IssueStatus.IN_PROGRESS, IssueStatus.DONE, IssueStatus.TODO
                        , IssueStatus.IN_PROGRESS, IssueStatus.DONE, IssueStatus.TODO
                        , false, true, false)

                ,Arguments.of(
                        2
                        , false
                        , 1
                        , IssueStatus.IN_PROGRESS, IssueStatus.TODO, IssueStatus.IN_REVIEW
                        , IssueStatus.IN_PROGRESS, IssueStatus.TODO, IssueStatus.IN_REVIEW
                        , false, false, false)
        );
    }
    @ParameterizedTest
    @MethodSource(value ="endProjectArgs_sprintIssuesAndIssuesWithOutSprint")
    public void endProject_sprintIssuesAndIssuesWithOutSprint(
            int totalSprintCountOfProject
            , boolean forceEndIssue
            , int foundHistoryCount
            , IssueStatus issueStatus1, IssueStatus issueStatus2, IssueStatus issueStatus3
            , IssueStatus expectedIssue1Status, IssueStatus expectedIssue2Status, IssueStatus expectedIssue3Status
            , boolean issue1HistoryComplete, boolean issue2HistoryComplete, boolean issue3HistoryComplete
    ){

        //Setup
        Project project = new ProjectTestDataBuilder()
                .withId(100L)
                .build();
        // in order to use id generation feature, id must be from repository (fake repo in test)
        // or manually increment repository's generated id cursor..
        LocalDateTime startDate = LocalDateTime.now(clock).minusDays(1);


        Sprint sprint1 = new SprintTestDataBuilder()
                .withId(1L)
                .withProject(project)
                .withStartDate(startDate)
                .withArchived(false)
                .build();

        Sprint sprint2 = new SprintTestDataBuilder()
                .withId(2L)
                .withProject(project)
                .withStartDate(startDate)
                .withArchived(false)
                .build();
        projectRepository.save(project);
        sprintRepository.save(sprint1);
        sprintRepository.save(sprint2);


        // - set up issue data
        assertEquals(totalSprintCountOfProject,sprintRepository.findAllByProjectId(project.getId()).size());

        Issue issue1 = new IssueTestDataBuilder()
                .withId(10L)
                .withProject(project)
                .withSprint(sprint1)
                .withStatus(issueStatus1)
                .withArchived(false)
                .build();
        Issue issue2 = new IssueTestDataBuilder()
                .withId(20L)
                .withProject(project)
                .withSprint(null)
                .withStatus(issueStatus2)
                .withArchived(false)
                .build();
        Issue issue3 = new IssueTestDataBuilder()
                .withId(30L)
                .withProject(project)
                .withSprint(null)
                .withStatus(issueStatus3)
                .withArchived(false)
                .build();
        Set<Issue> issueSet = new HashSet<>(Set.of(issue1,issue2,issue3));
        issueRepository.saveAll(issueSet);



        //Execution
        historyService.endProject(project.getId(),forceEndIssue);

        //Assertions
        Project expectedProject = new ProjectTestDataBuilder()
                .withId(project.getId())
                .withArchived(true)
                .build();

        Project projectFound = projectRepository.getReferenceById(project.getId());
        assertThat(project).usingRecursiveComparison().isEqualTo(expectedProject);
        assertThat(projectFound).usingRecursiveComparison().isEqualTo(expectedProject);

        Sprint expectedSprint1 = new SprintTestDataBuilder()
                .withId(sprint1.getId())
                .withStartDate(sprint1.getStartDate())
                .withProject(project)
                .withArchived(true)
                .withEndDate(LocalDateTime.now(clock))
                .build();

        Sprint expectedSprint2 = new SprintTestDataBuilder()
                .withId(sprint2.getId())
                .withStartDate(sprint2.getStartDate())
                .withProject(project)
                .withArchived(true)
                .withEndDate(LocalDateTime.now(clock))
                .build();

        Set<Sprint> sprintsFound = sprintRepository.findAllByProjectId(project.getId());
        assertThat(Set.of(sprint1,sprint2)).usingRecursiveComparison().isEqualTo(Set.of(expectedSprint1,expectedSprint2));
        assertThat(sprintsFound).usingRecursiveComparison().isEqualTo(Set.of(expectedSprint1,expectedSprint2));


        //set up expected issues
        Issue expectedIssue1 = new IssueTestDataBuilder()
                .withId(issue1.getId())
                .withProject(project)
                .withSprint(null)
                .withStatus(expectedIssue1Status)
                .withArchived(true)
                .build();
        Issue expectedIssue2 = new IssueTestDataBuilder()
                .withId(issue2.getId())
                .withProject(project)
                .withSprint(null)
                .withStatus(expectedIssue2Status)
                .withArchived(true)
                .build();
        Issue expectedIssue3 = new IssueTestDataBuilder()
                .withId(issue3.getId())
                .withProject(project)
                .withSprint(null)
                .withStatus(expectedIssue3Status)
                .withArchived(true)
                .build();

        Set<Issue> expectedIssueSet = new HashSet<>(Set.of(expectedIssue1, expectedIssue2, expectedIssue3));

        //check if the sprint is archived & check against expected sprint


        //check if issue is archived or not depending on their 'issueStatus' field
        //-get saved issues
        Set<Issue> issuesFound = issueRepository.findAllById(Set.of(issue1.getId(),issue2.getId(),issue3.getId()));
        assertThat(expectedIssueSet).usingRecursiveComparison().isEqualTo(issuesFound);

        //check if the right histories were saved
        //-check saved history count
        Set<SprintIssueHistory> savedHistory = sprintIssueHistoryRepository.findAllByArchivedSprintId(expectedSprint1.getId());
        Map<Long, List<SprintIssueHistory>> foundHistoryMap = savedHistory.stream().collect(groupingBy(history -> history.getIssue().getId()));
        assertEquals(foundHistoryCount ,foundHistoryMap.size());
        assertThat(savedHistory.stream().map(SprintIssueHistory::getIssue).collect(Collectors.toSet()))
                .usingRecursiveComparison()
                .isEqualTo(Set.of(issue1)); //hard coding...

        //-get each history to check 'complete' field of histories
        SprintIssueHistory issue1History = foundHistoryMap.get(issue1.getId()).get(0);

        assertEquals(issue1HistoryComplete,issue1History.isComplete());


        //-check 'archivedSprint' field of histories
        foundHistoryMap.values().stream().map(history -> assertThat(history.get(0).getArchivedSprint()).usingRecursiveComparison().isEqualTo(expectedSprint1));
    }


    private static Stream<Arguments> endProjectArgs_onlyIssuesWithOutSprint(){
        return Stream.of(

                //forceEndIssue = true
                // sprint count does not matter here
                Arguments.of(
                         true // issue status change after execution of the method under test
                        , IssueStatus.DONE, IssueStatus.DONE, IssueStatus.DONE  // each issue initial status
                        , IssueStatus.DONE, IssueStatus.DONE, IssueStatus.DONE // expected issue status after execution of the method under test
                        )

                ,Arguments.of(
                         true
                        , IssueStatus.IN_PROGRESS, IssueStatus.DONE, IssueStatus.TODO
                        , IssueStatus.DONE, IssueStatus.DONE, IssueStatus.DONE
                        )

                ,Arguments.of(
                         true
                        , IssueStatus.IN_PROGRESS, IssueStatus.TODO, IssueStatus.IN_REVIEW
                        , IssueStatus.DONE, IssueStatus.DONE, IssueStatus.DONE
                        )


                //forceEndIssue = false

                ,Arguments.of(
                        false // issue status change after execution of the method under test
                        , IssueStatus.DONE, IssueStatus.DONE, IssueStatus.DONE  // each issue initial status
                        , IssueStatus.DONE, IssueStatus.DONE, IssueStatus.DONE // expected issue status after execution of the method under test
                        )

                ,Arguments.of(
                         false
                        , IssueStatus.IN_PROGRESS, IssueStatus.DONE, IssueStatus.TODO
                        , IssueStatus.IN_PROGRESS, IssueStatus.DONE, IssueStatus.TODO
                        )

                ,Arguments.of(
                         false
                        , IssueStatus.IN_PROGRESS, IssueStatus.TODO, IssueStatus.IN_REVIEW
                        , IssueStatus.IN_PROGRESS, IssueStatus.TODO, IssueStatus.IN_REVIEW)
        );
    }
    @ParameterizedTest
    @MethodSource(value ="endProjectArgs_onlyIssuesWithOutSprint")
    public void endProject_onlyIssuesWithOutSprint(
             boolean forceEndIssue
            , IssueStatus issueStatus1, IssueStatus issueStatus2, IssueStatus issueStatus3
            , IssueStatus expectedIssue1Status, IssueStatus expectedIssue2Status, IssueStatus expectedIssue3Status
    ){

        //Setup
        Project project = new ProjectTestDataBuilder()
                .withId(100L)
                .build();

        projectRepository.save(project);

        // - set up issue data

        Issue issue1 = new IssueTestDataBuilder()
                .withId(10L)
                .withProject(project)
                .withSprint(null)
                .withStatus(issueStatus1)
                .withArchived(false)
                .build();
        Issue issue2 = new IssueTestDataBuilder()
                .withId(20L)
                .withProject(project)
                .withSprint(null)
                .withStatus(issueStatus2)
                .withArchived(false)
                .build();
        Issue issue3 = new IssueTestDataBuilder()
                .withId(30L)
                .withProject(project)
                .withSprint(null)
                .withStatus(issueStatus3)
                .withArchived(false)
                .build();
        Set<Issue> issueSet = new HashSet<>(Set.of(issue1,issue2,issue3));
        issueRepository.saveAll(issueSet);

        //Execution

        historyService.endProject(project.getId(),forceEndIssue);

        //Assertions
        Project expectedProject = new ProjectTestDataBuilder()
                .withId(project.getId())
                .withArchived(true)
                .build();

        Project projectFound = projectRepository.getReferenceById(project.getId());
        assertThat(project).usingRecursiveComparison().isEqualTo(expectedProject);
        assertThat(projectFound).usingRecursiveComparison().isEqualTo(expectedProject);



        //set up expected issues
        Issue expectedIssue1 = new IssueTestDataBuilder()
                .withId(issue1.getId())
                .withProject(project)
                .withSprint(null)
                .withStatus(expectedIssue1Status)
                .withArchived(true)
                .build();
        Issue expectedIssue2 = new IssueTestDataBuilder()
                .withId(issue2.getId())
                .withProject(project)
                .withSprint(null)
                .withStatus(expectedIssue2Status)
                .withArchived(true)
                .build();
        Issue expectedIssue3 = new IssueTestDataBuilder()
                .withId(issue3.getId())
                .withProject(project)
                .withSprint(null)
                .withStatus(expectedIssue3Status)
                .withArchived(true)
                .build();

        Set<Issue> expectedIssueSet = new HashSet<>(Set.of(expectedIssue1, expectedIssue2, expectedIssue3));

        //check if issue is archived or not depending on their 'issueStatus' field
        //-get saved issues
        assertThat(issueSet).usingRecursiveComparison().isEqualTo(expectedIssueSet);
        Set<Issue> issuesFound = issueRepository.findAllById(Set.of(issue1.getId(),issue2.getId(),issue3.getId()));
        assertThat(issuesFound).usingRecursiveComparison().isEqualTo(expectedIssueSet);

    }







}
