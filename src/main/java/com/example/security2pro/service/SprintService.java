package com.example.security2pro.service;

import com.example.security2pro.domain.enums.IssueStatus;
import com.example.security2pro.domain.model.*;
import com.example.security2pro.dto.*;
import com.example.security2pro.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SprintService {

    private final ProjectMemberRepository projectMemberRepository;

    private final SprintRepository sprintRepository;

    private final IssueRepository issueRepository;

    private final ProjectRepository projectRepository;

    private final SprintIssueHistoryRepository sprintIssueHistoryRepository;


    private final ProjectService projectService;

    public ActiveSprintDto createSprint(Long projectId, ActiveSprintDto activeSprintDto){
//        if(activeSprintDto.getEndDate().isAfter(activeSprintDto.getStartDate().plusMinutes(30))){
//            throw new IllegalArgumentException("the duration of a sprint should be at least 30 minutes");}
//        if(activeSprintDto.getEndDate().isBefore(LocalDateTime.now().plusMinutes(10))){
//            throw new IllegalArgumentException("the end date of a sprint cannot be in the past");
//        }

        Sprint sprint = sprintRepository.save(convertSprintDtoToModel(projectId, activeSprintDto));
        // sprint does not have issues field so it does not matter whether it is saved first or not
        Set<Issue> createdIssues = setCurrentSprint(projectId,sprint, activeSprintDto);

        //sprintScheduledTaskExecutor.scheduleSprintEnd(sprint);
        return new ActiveSprintDto(sprint,createdIssues);
    }

    public ActiveSprintDto updateSprint(Long projectId, ActiveSprintDto activeSprintDto){
        Sprint sprint = sprintRepository.getReferenceById(activeSprintDto.getId());
        System.out.println("sprint name is.. ========" + sprint.getName());

        if(sprint.isArchived()
        ||sprint.getEndDate().isBefore(LocalDateTime.now().plusMinutes(5))){
            throw new IllegalArgumentException("a sprint can only be updated at least 5 minutes prior to its end date");}
        if(!sprint.getProject().getId().equals(projectId)){
            throw new IllegalArgumentException("sprint with id" +sprint.getId() +" does not belong to the project with id"+ projectId);}
        if(!sprint.getEndDate().equals(activeSprintDto.getEndDate())
                && activeSprintDto.getEndDate().isBefore(LocalDateTime.now().plusMinutes(5))){
            throw new IllegalArgumentException("the end date of a sprint cannot be in the past");
        }
        if(!activeSprintDto.getStartDate().isBefore(activeSprintDto.getEndDate().plusMinutes(30))){
            throw new IllegalArgumentException("start date has to be at least 30 minutes prior to end date");
        }

        Sprint newSprint = convertSprintDtoToModel(projectId, activeSprintDto);
        newSprint.assignId(sprint.getId());

        sprint = sprintRepository.save(newSprint); // sprint does not have issues field so it does not matter whether it is saved first or not

        Set<Issue> updatedIssues = setCurrentSprint (projectId, sprint, activeSprintDto);

        //LocalDateTime beforeUpdate = sprint.getEndDate();
        //       Boolean endDateChanged = true;
//        if(beforeUpdate.isEqual(sprint.getEndDate())){endDateChanged = false;}
//        if(endDateChanged) {
//            sprintScheduledTaskExecutor.cancelscheduledSprint(sprint);
//            sprintScheduledTaskExecutor.scheduleSprintEnd(sprint);
//        }
        return new ActiveSprintDto(sprint,updatedIssues);
    }


    public void endSprint(Long projectId,Long sprintId, ActiveSprintDto activeSprintDto){
        Sprint sprint = sprintRepository.getReferenceById(sprintId);
        log.info("ending sprint: "+sprint.getName());
        if(!sprint.getProject().getId().equals(projectId)){throw new IllegalArgumentException("sprint with id" +sprint.getId() +" does not belong to the project with id"+ projectId);}

        sprint.completeSprint();//below code does not need updated info of the sprint. so save is not necessary

       Map<Long,List<IssueSimpleDto>> passedIssueDtosMap = activeSprintDto.getIssues().stream().collect(groupingBy(IssueSimpleDto::getId));
        Set<Issue> foundPassedIssues= issueRepository.findIssuesByCurrentSprintId(sprintId);

        if(passedIssueDtosMap.size() !=foundPassedIssues.size()){
            throw new IllegalArgumentException("some issues missing or issues that don't belong to the sprint were passed");
        }
        handleEndingSprintIssues(sprint, foundPassedIssues, passedIssueDtosMap);
    }

    public void handleEndingSprintIssues(Sprint sprint,Set<Issue> foundPassedIssues, Map<Long,List<IssueSimpleDto>> passedIssueDtosMap){
        // map will be empty when the sprint is ending automatically
        foundPassedIssues.stream().peek(issue-> {
                    if(issue.getStatus().equals(IssueStatus.DONE)){
                        issue.forceCompleteIssue();
                    } else if(passedIssueDtosMap.containsKey(issue.getId())){
                        if(passedIssueDtosMap.get(issue.getId()).get(0).getStatus().equals(IssueStatus.DONE)){
                            issue.forceCompleteIssue();
                        }
                    } else{ //issue will not be archived
                        issue.assignCurrentSprint(null);}})
                .collect(Collectors.toCollection(ArrayList::new));

        issueRepository.saveAll(foundPassedIssues);
        //necessary. If issue's assignee are given as unmodifiable set(when Set.of() is used), save won't work.
        //initialize the collection and do not change it..!! --- How can this be achieved???...
        sprintIssueHistoryRepository.saveAll(foundPassedIssues.stream().map(issue -> new SprintIssueHistory(sprint,issue)).collect(Collectors.toCollection(ArrayList::new)));
    }

    public void deleteSprint(Long projectId,Long sprintId){
        Project project = projectRepository.getReferenceById(projectId);
        Sprint sprint = sprintRepository.getReferenceById(sprintId);
        if(!project.getId().equals(sprint.getProject().getId())){
            throw new IllegalArgumentException("sprint with id "+ sprintId+" does not belong to the project with id"+ projectId);
        }
        Set<Issue> issues= issueRepository.findIssuesByCurrentSprintId(sprint.getId());
        issues= issues.stream().peek(issue -> issue.assignCurrentSprint(null)).collect(Collectors.toCollection(HashSet::new));
        sprintRepository.deleteById(sprint.getId());
    }

    public Sprint getReferenceById(Long sprintId){
        return sprintRepository.getReferenceById(sprintId);
        //throws exception when sprint does not exist
    }


    public ActiveSprintDto getSprintById(Long sprintId){
        Sprint sprint = sprintRepository.getReferenceById(sprintId);
        Set<Issue> issues = issueRepository.findIssuesByCurrentSprintId(sprint.getId());
        return new ActiveSprintDto(sprint,issues);
    }

    public ActiveSprintDto getActiveSprintAndIssuesToEnd(Long projectId, Long sprintId){
        Set<Issue> issues=issueRepository.findIssuesByCurrentSprintId(sprintId);
        Sprint sprint = sprintRepository.getReferenceById(sprintId);
        log.info("ending sprint :"+ sprint.getName());
        if(!sprint.getProject().getId().equals(projectId)){throw new IllegalArgumentException("sprint with id" +sprint.getId() +" does not belong to the project with id"+ projectId);}
        //error will be thrown if the sprint already ended
        return new ActiveSprintDto(sprint,issues);
    }

    public ArchivedSprintDto getArchivedSprint(Sprint sprint){
        List<SprintIssueHistory> histories=sprintIssueHistoryRepository.findByArchivedSprint(sprint.getId());
        return new ArchivedSprintDto(sprint,histories);
    }


    public ProjectDto getProjectToCreateSprint(Long projectId){
        return projectService.getProjectDetails(projectId);
    }

    public SprintUpdateDto getProjectToUpdateSprint(Long projectId, Long sprintId){
        Sprint sprintFound = sprintRepository.getReferenceById(sprintId);
        log.info("getting active sprint" + sprintFound.getName());
        ProjectDto projectDto =projectService.getProjectDetails(projectId);

        Map<Long, List<ActiveSprintDto>> sprintMap = projectDto.getSprints().stream().collect(groupingBy(ActiveSprintDto::getId));
        ActiveSprintDto sprintToBeUpdated= sprintMap.get(sprintFound.getId()).get(0);
        projectDto.getSprints().remove(sprintToBeUpdated);

        return new SprintUpdateDto(sprintToBeUpdated, projectDto);
    }



    public Sprint convertSprintDtoToModel(Long projectId, ActiveSprintDto activeSprintDto){
        Project project = projectRepository.getReferenceById(projectId);
        log.info("creating a sprint within the project:" + project.getName());

        String sprintName = activeSprintDto.getName();
        String description = activeSprintDto.getDescription();
        LocalDateTime startDate = activeSprintDto.getStartDate();
        LocalDateTime endDate = activeSprintDto.getEndDate();
        return new Sprint(project,sprintName,description,startDate,endDate);
        // constructor is used to save entity because there are no setter methods.
    }

    public Set<Issue> setCurrentSprint(Long projectId, Sprint sprint, ActiveSprintDto activeSprintDto){

        Set<Long> passedIssueIds= activeSprintDto.getIssues().stream().map(IssueSimpleDto::getId).collect(toCollection(HashSet::new));
        Set<Issue> foundIssuesWithPassedIds= issueRepository.findAllByIdAndProjectId(passedIssueIds, projectId);
        if(passedIssueIds.size()!= foundIssuesWithPassedIds.size()){//check if the issue belong to the project and not archived.
            throw new IllegalArgumentException("some issue do not belong to the project.only issues within the project can be moved to the project's sprints. ");
        }

        Map<Long, List<Issue>> foundIssuesMap= foundIssuesWithPassedIds.stream().collect(groupingBy(Issue::getId));
        Set<Issue> previousSprintIssues = new HashSet<>(issueRepository.findIssuesByCurrentSprintId(sprint.getId()));
        // previousSprintIssues will have 0 size when creation.
        Sprint finalSprint = sprint;
        Set<Issue> updatedIssues = activeSprintDto.getIssues().stream()
                .map(issueSimpleDto -> {
                    Issue foundIssue = null;
                    foundIssue = foundIssuesMap.get(issueSimpleDto.getId()).get(0); //if project has the active issue-
                    if(previousSprintIssues.contains(foundIssue)){ // check if it already belongs to the sprint
                        previousSprintIssues.remove(foundIssue); //- if yes, remove from previous group (current sprint field of what's left in 'previousSprintIssues' at the end will be set to null)
                    } else { //if it was newly moved to this sprint,
                        foundIssue.assignCurrentSprint(finalSprint); //set current sprint. there is no need to save - it was just queried. dirty checking will automatically persist it on the next flush
                    }
                    return foundIssue;
                }).collect(Collectors.toCollection(HashSet::new));
        // no need to save the above issues - because the issues were queried. -> dirty checking will automatically update issues.
        previousSprintIssues.stream().peek(issue -> issue.assignCurrentSprint(null));
        //the issues that used to belong to the sprints but not anymore - set current sprint to null
        //do nothing when creating a sprint
        return updatedIssues;
    }

    public void endProject(Long projectId){
        Project project = projectRepository.getReferenceById(projectId);
        project.endProject();

        Set<Sprint> sprintsToTerminate= sprintRepository.findActiveSprintsByProjectId(projectId);
        Map<Long,List<Sprint>> sprintsMap= sprintsToTerminate.stream().collect(groupingBy(Sprint::getId));
        Set<Long> sprintIds = sprintsToTerminate.stream().map(Sprint::getId).collect(Collectors.toCollection(HashSet::new));
        Map<Long,List<Issue>> issuesWithSprintMap = issueRepository.findAllIssuesByCurrentSprintIds(sprintIds).stream().collect(groupingBy(issue->issue.getCurrentSprint().getId()));

        issuesWithSprintMap.entrySet().stream().map(entry ->{ Sprint sprint = sprintsMap.get(entry.getKey()).get(0);
            sprint.completeSprint();
            handleEndingSprintIssues(sprint,new HashSet<>(entry.getValue()),Collections.emptyMap());
            return entry;
        });

        Set<Issue> issuesWithoutSprints= issueRepository.findActiveIssuesWithoutSprintByProjectId(projectId);
        issuesWithoutSprints.stream().peek(Issue::endIssueWithProject);
    }


//    public Optional<Sprint> findById(Long sprintId){
//        return sprintRepository.findById(sprintId);
//        //returns empty optional when sprint does not exist
//    }
//
//    public Optional<Sprint> findActiveSprintById(Long sprintId){
//        return sprintRepository.findActiveSprintById(sprintId);
//    }
//
//    public Set<IssueDto> findExistingIssuesToUpdateSprint(Long projectId){
//        Project project = projectRepository.getReferenceById(projectId);
//        log.info("creating sprint for project: "+ projectId +"- "+ project.getName());
//        Set<Issue> projectIssueList=issueRepository.findActiveExistingIssuesByProjectId(projectId);
//
//        return projectIssueList.stream().map(issue -> new IssueDto(issue,Collections.emptySet(),Collections.emptySet())).collect(toSet());
//        // this returns issues without issue relation details and histories
//    }




}
