package com.example.security2pro.service.usecases;

import com.example.security2pro.domain.enums.IssueStatus;
import com.example.security2pro.domain.model.Issue;
import com.example.security2pro.domain.model.Project;
import com.example.security2pro.domain.model.Sprint;
import com.example.security2pro.domain.model.SprintIssueHistory;
import com.example.security2pro.repository.repository_interfaces.IssueRepository;
import com.example.security2pro.repository.repository_interfaces.ProjectRepository;
import com.example.security2pro.repository.repository_interfaces.SprintIssueHistoryRepository;
import com.example.security2pro.repository.repository_interfaces.SprintRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@RequiredArgsConstructor
@Component
@Transactional
@Slf4j
public class HistoryService {

    private final ProjectRepository projectRepository;

    private final SprintRepository sprintRepository;

    private final IssueRepository issueRepository;

    private final SprintIssueHistoryRepository sprintIssueHistoryRepository;

    private final Clock clock;

    public void endSprintAndSprintIssues(Long sprintId, boolean forceEndIssues) { //issues will be archived or transferred to the new sprint depending on 'forceEndIssues' value
        Sprint sprint = endSprintAndSprintIssues_endSprint(sprintId);

        Set<Issue> foundPassedIssues = issueRepository.findByCurrentSprintId(sprint.getId());
        if(foundPassedIssues.isEmpty()){
            return;
        }
        foundPassedIssues = endSprintAndSprintIssues_endIssues(foundPassedIssues, sprint,forceEndIssues);

        createAndSaveSprintIssueHistories(sprint, foundPassedIssues);
    }

    private Sprint endSprintAndSprintIssues_endSprint(Long sprintId){
        Sprint sprint = sprintRepository.getReferenceById(sprintId);
        sprint.completeSprint(LocalDateTime.now(clock));
        return sprintRepository.save(sprint);
    }

    private Set<Issue> endSprintAndSprintIssues_endIssues(Set<Issue> foundPassedIssues, Sprint sprint, boolean forceEndIssues){

        if(forceEndIssues){ //if forceEndIssues is true: all the issues, including the ones that are not complete, will be archived with the sprint
            foundPassedIssues.forEach(Issue::forceCompleteIssue);
        } else { //if forceEndIssues is false: incomplete issues will be transferred to the next one, and only the complete ones will be archived with the sprint
            Map<Boolean, List<Issue>> issueMap = foundPassedIssues.stream()
                    .collect(Collectors.partitioningBy(issue -> issue.getStatus().equals(IssueStatus.DONE)));
            // partition by getStatus().equals(IssueStatus.DONE)

            List<Issue> completes = issueMap.get(Boolean.TRUE);
            completes.forEach(Issue::forceCompleteIssue);// the complete ones(IssueStatus.DONE) will be archived

            List<Issue> incompletes = issueMap.get(Boolean.FALSE);
            if (!incompletes.isEmpty()) { // the incomplete ones will be transferred (other issue statuses)
                transferToNextSprint(incompletes, sprint);
                completes.addAll(incompletes); // equals and hash were not overridden - every issue will be considered distinct
            }
            foundPassedIssues = new HashSet<>(completes);
        }
        return issueRepository.saveAll(foundPassedIssues);
    }

    private void transferToNextSprint(List<Issue> incompletes, Sprint sprint){
        Sprint nextSprint = sprintRepository.getNext(sprint.getId())// try fetching the next sprint. if there is no active sprint, create a new sprint to transfer the incomplete ones to
                .orElseGet(()->sprintRepository.save(Sprint.createDefaultSprint(sprint.getProject(), LocalDateTime.now(clock))));

        incompletes.forEach(issue -> issue.assignCurrentSprint(nextSprint)); //transfer the incomplete ones to the next sprint
    }


    private void createAndSaveSprintIssueHistories(Sprint sprint, Set<Issue> foundPassedIssues){
        sprintIssueHistoryRepository.saveAll(foundPassedIssues.stream()
                .map(issue -> SprintIssueHistory.createSprintIssueHistory(null, sprint, issue))
                .collect(Collectors.toCollection(ArrayList::new)));
    }



    public void endProject(Long projectId,boolean forceEndIssues){
        endProject_endProject(projectId);

        Set<Sprint> sprintsToTerminate= sprintRepository.findByProjectIdAndArchivedFalse(projectId);
        if(!sprintsToTerminate.isEmpty()){
            endProject_endSprintAndIssues(sprintsToTerminate,forceEndIssues);
        }

        Set<Issue> issuesWithoutSprints= issueRepository.findByProjectIdAndArchivedFalseAndCurrentSprintIsNull(projectId);
        if(!issuesWithoutSprints.isEmpty()){
            endProject_endIssuesWithoutSprint( issuesWithoutSprints, forceEndIssues);
        }
    }

    private void endProject_endProject(Long projectId){
        Project project = projectRepository.getReferenceById(projectId);
        project.endProject();
        projectRepository.save(project);
    }

    private void endProject_endSprintAndIssues(Set<Sprint> sprintsToTerminate, boolean forceEndIssues){

        LocalDateTime now = LocalDateTime.now(clock);

        Map<Long, List<Sprint>> sprintsMap= sprintsToTerminate.stream().collect(groupingBy(Sprint::getId));
        Set<Long> sprintIds = sprintsToTerminate.stream().map(Sprint::getId).collect(Collectors.toCollection(HashSet::new));
        Map<Long,List<Issue>> issuesWithSprintMap = issueRepository.findByCurrentSprintIdIn(sprintIds).stream().collect(groupingBy(issue->issue.getCurrentSprint().get().getId()));

        sprintsMap.forEach((key, value) -> {
            Sprint sprint = sprintsMap.get(key).get(0);
            Set<Issue> sprintIssues;
            if(issuesWithSprintMap.containsKey(sprint.getId())){
                sprintIssues  = new HashSet<>(issuesWithSprintMap.get(sprint.getId()));
                endProject_endSprintAndIssuesFound(sprint, sprintIssues,forceEndIssues, now);
            } else {
                sprint.completeSprint(now);
            }
        });
        sprintRepository.saveAll(sprintsMap.values().stream().map(sprintList->sprintList.get(0)).collect(Collectors.toCollection(HashSet::new)));
    }

    private void endProject_endSprintAndIssuesFound(Sprint sprint, Set<Issue> foundPassedIssues, boolean forceEndIssues, LocalDateTime now) { //issues will be archived or transfer to the new sprint depending on 'forceEndIssues' value
        sprint.completeSprint(now);

        if(forceEndIssues){ //if forceEndIssues is true: issue status field will change to DONE
            foundPassedIssues.forEach(Issue::forceCompleteIssue);
        } else { //if forceEndIssues is false: issue status will stay the same
            foundPassedIssues.forEach(Issue::endIssueWithProject);
        }
        foundPassedIssues = issueRepository.saveAll(foundPassedIssues);

        createAndSaveSprintIssueHistories(sprint,foundPassedIssues);
    }

    private void endProject_endIssuesWithoutSprint(Set<Issue> issuesWithoutSprints, boolean forceEndIssues){
        if(forceEndIssues){//if forceEndIssues is true: issue status field will change to DONE
            issuesWithoutSprints.forEach(Issue::forceCompleteIssue);
        } else {//if forceEndIssues is false: issue status will stay the same
            issuesWithoutSprints.forEach(Issue::endIssueWithProject);
        }
        issueRepository.saveAll(issuesWithoutSprints);
    }


}
