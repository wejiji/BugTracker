package com.example.bugtracker.service;

import com.example.bugtracker.domain.enums.IssueStatus;
import com.example.bugtracker.domain.model.issue.Issue;
import com.example.bugtracker.domain.model.Project;
import com.example.bugtracker.domain.model.Sprint;
import com.example.bugtracker.domain.model.SprintIssueHistory;
import com.example.bugtracker.repository.repository_interfaces.IssueRepository;
import com.example.bugtracker.repository.repository_interfaces.ProjectRepository;
import com.example.bugtracker.repository.repository_interfaces.SprintIssueHistoryRepository;
import com.example.bugtracker.repository.repository_interfaces.SprintRepository;
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

    /*
     * 'HistoryService' has two public methods: one for archiving projects and another for archiving sprints.
     * Since 'Project' and 'Sprint' lack JPA bidirectional relationships,
     * separate queries will be executed from their respective repositories to handle all associated entities
     * during the archiving process.
     *
     * If the code is refactored to include bidirectional relationships to 'Project' and 'Sprint',
     * the logic in this service will transition to the domain layer.
     *
     * Avoid adding dependencies to other service classes to prevent circular dependency,
     * as this class is injected into other service classes.
     *
     * While calling the 'save' method is unnecessary when the entity is already in the cache,
     * as dirty checking can automatically update modified fields, it is called nevertheless for explicitness.
     */
    private final ProjectRepository projectRepository;

    private final SprintRepository sprintRepository;

    private final IssueRepository issueRepository;

    private final SprintIssueHistoryRepository sprintIssueHistoryRepository;

    private final Clock clock;

    /**
     * Archives the 'Sprint' identified by the given 'sprintId'.
     *
     * @param sprintId       The id of the 'Sprint' to be archived.
     *                       Expected to be verified for existence within the project beforehand.
     * @param forceEndIssues A query parameter from a user:
     *                       - If true, issues will be archived along with the sprint, regardless of their 'status' field value.
     *                       - If false, issues will be archived only if their 'status' is 'DONE'
     *                       ; issues with other statuses will be transferred to the new sprint.
     */
    public void endSprintAndSprintIssues(Long sprintId, boolean forceEndIssues) {

        Sprint sprint = endSprintToArchiveSprint(sprintId);

        Set<Issue> foundPassedIssues = issueRepository.findByCurrentSprintId(sprint.getId());
        if (foundPassedIssues.isEmpty()) {
            return;
        }
        foundPassedIssues = endIssuesToArchiveSprint(foundPassedIssues, sprint, forceEndIssues);

        createAndSaveSprintIssueHistories(sprint, foundPassedIssues);
    }

    /**
     * Archives the sprint with the provided id.
     *
     * @param sprintId The id of the sprint to be archived.
     * @return The archived sprint
     */
    private Sprint endSprintToArchiveSprint(Long sprintId) {
        Sprint sprint = sprintRepository.getReferenceById(sprintId);
        sprint.completeSprint(LocalDateTime.now(clock));
        return sprintRepository.save(sprint);
    }

    /**
     * Archives issues along with the sprint to be archived
     *
     * @param foundPassedIssues The issues to be handled during the archiving process.
     * @param sprint            The sprint to be archived
     * @param forceEndIssues    - If true, issues will be archived along with the sprint, regardless of their 'status' field value.
     *                          - If false, issues will be archived only if their 'status' is 'DONE'
     *                          ; issues with other statuses will be transferred to the new sprint.
     * @return Handled issues that will be returned after archiving the sprint.
     */
    private Set<Issue> endIssuesToArchiveSprint(Set<Issue> foundPassedIssues, Sprint sprint, boolean forceEndIssues) {

        if (forceEndIssues) {
            foundPassedIssues.forEach(Issue::forceCompleteIssue);
        } else {
            Map<Boolean, List<Issue>> issueMap = foundPassedIssues.stream()
                    .collect(Collectors.partitioningBy(issue -> issue.getStatus().equals(IssueStatus.DONE)));

            List<Issue> completes = issueMap.get(Boolean.TRUE);
            completes.forEach(Issue::forceCompleteIssue);// The complete ones(IssueStatus.DONE) will be archived

            List<Issue> incompletes = issueMap.get(Boolean.FALSE);
            if (!incompletes.isEmpty()) { // The incomplete ones will be transferred (other issue statuses)
                transferToNextSprint(incompletes, sprint);
                completes.addAll(incompletes);
                // The 'equals' and 'hashCode' methods were not overridden - every issue will be considered distinct
            }
            foundPassedIssues = new HashSet<>(completes);
        }
        return issueRepository.saveAll(foundPassedIssues);
    }

    /**
     * This method is called if the 'forceCompleteIssue' argument is false and there is any issue that does not have a 'DONE' status.
     * Transfers issues that haven't been completed to the next non-archived sprint during the archiving process of a sprint.
     * If any non-archived sprint is not found, new sprint with default values will be created and saved.
     *
     * @param sprint      The sprint to be archived
     * @param incompletes The issues that didn't have a 'DONE' status at the moment of archiving the sprint
     */
    private void transferToNextSprint(List<Issue> incompletes, Sprint sprint) {
        Sprint nextSprint = sprintRepository.getNext()
                .orElseGet(
                        () -> sprintRepository.save(
                                Sprint.createDefaultSprint(
                                        sprint.getProject(), LocalDateTime.now(clock))));

        incompletes.forEach(issue -> issue.assignCurrentSprint(nextSprint));
        //transfer the incomplete ones to the next sprint
    }

    /**
     * 'SprintIssueHistories' are created to keep a record of whether an issue has been completed within the duration of the archived sprint.
     *
     * @param sprint            The sprint to be archived
     * @param foundPassedIssues The issues that used to belong to the sprint
     */
    private void createAndSaveSprintIssueHistories(Sprint sprint, Set<Issue> foundPassedIssues) {
        sprintIssueHistoryRepository.saveAll(foundPassedIssues.stream()
                .map(issue -> SprintIssueHistory.createSprintIssueHistory(null, sprint, issue))
                .collect(Collectors.toCollection(ArrayList::new)));
    }


    /**
     * Archives the 'Project' identified by the given 'projectId'.
     *
     * @param projectId      The id of the 'Project' to be archived.
     *                       Expected to be verified for existence beforehand.
     * @param forceEndIssues A query parameter from a user:
     *                       - If true, issues will be archived along with the project, regardless of their 'status' field value.
     *                       - If false, issues will still be archived, but their 'status' field will remain unchanged.
     */
    public void endProject(Long projectId, boolean forceEndIssues) {
        endProjectToArchiveProject(projectId);

        Set<Sprint> sprintsToTerminate
                = sprintRepository.findByProjectIdAndArchivedFalse(projectId);
        if (!sprintsToTerminate.isEmpty()) {
            endSprintAndIssuesToArchiveProject(sprintsToTerminate, forceEndIssues);
        }

        Set<Issue> issuesWithoutSprints
                = issueRepository.findByProjectIdAndArchivedFalseAndCurrentSprintIsNull(projectId);
        if (!issuesWithoutSprints.isEmpty()) {
            endIssuesWithoutSprintToArchiveProject(issuesWithoutSprints, forceEndIssues);
        }
    }

    /**
     * Archives the 'Project' with the provided id.
     *
     * @param projectId The id of the project to be archived.
     */
    private void endProjectToArchiveProject(Long projectId) {
        Project project = projectRepository.getReferenceById(projectId);
        project.endProject();
        projectRepository.save(project);
    }


    /**
     * Fetches 'Sprints' and 'Issues' that belong to the 'Project' from the repositories to archive them.
     *
     * @param sprintsToTerminate The sprints to be archived along with the project.
     * @param forceEndIssues     This parameter will be passed to 'endSprintAndIssuesFoundToArchiveProject'.
     *                           If true: Issue's 'status' field will change to 'DONE' and be archived.
     *                           If false: Issue's 'status' will stay the same and be archived.
     */
    private void endSprintAndIssuesToArchiveProject(Set<Sprint> sprintsToTerminate, boolean forceEndIssues) {
        LocalDateTime now = LocalDateTime.now(clock);

        Map<Long, List<Sprint>> sprintsMap = sprintsToTerminate.stream().collect(groupingBy(Sprint::getId));

        Set<Long> sprintIds = sprintsToTerminate.stream()
                .map(Sprint::getId)
                .collect(Collectors.toCollection(HashSet::new));

        Map<Long, List<Issue>> issuesWithSprintMap
                = issueRepository.findByCurrentSprintIdIn(sprintIds)
                .stream()
                .collect(groupingBy(issue -> issue.getCurrentSprint().get().getId()));

        sprintsMap.forEach((key, value) -> {
            Sprint sprint = sprintsMap.get(key).get(0);
            Set<Issue> sprintIssues;
            if (issuesWithSprintMap.containsKey(sprint.getId())) {
                sprintIssues = new HashSet<>(issuesWithSprintMap.get(sprint.getId()));
                endSprintAndIssuesFoundToArchiveProject(sprint, sprintIssues, forceEndIssues, now);
            } else {
                sprint.completeSprint(now);
            }
        });

        sprintRepository
                .saveAll(sprintsMap.values().stream()
                .map(sprintList -> sprintList.get(0))
                .collect(Collectors.toCollection(HashSet::new)));
    }


    /**
     * Archives a sprint along with the project, including its associated issues.
     * 'SprintIssueHistories' are created to keep a record of whether an issue has been completed within the duration of the archived sprint.
     *
     * @param sprint            The sprint to be archived along with the project.
     * @param foundPassedIssues Issues that belong to the sprint.
     * @param forceEndIssues    If true: issue's 'status' field will change to 'DONE' and be archived.
     *                          If false: issue's 'status' will stay the same and be archived.
     * @param now               An instant used to mark the completion time of a sprint.
     */
    private void endSprintAndIssuesFoundToArchiveProject(
            Sprint sprint
            , Set<Issue> foundPassedIssues
            , boolean forceEndIssues
            , LocalDateTime now) {

        sprint.completeSprint(now);

        if (forceEndIssues) {
            foundPassedIssues.forEach(Issue::forceCompleteIssue);
        } else {
            foundPassedIssues.forEach(Issue::endIssueWithProject);
        }
        foundPassedIssues = issueRepository.saveAll(foundPassedIssues);

        createAndSaveSprintIssueHistories(sprint, foundPassedIssues);
    }

    /**
     * Archives issues along with the project.
     *
     * @param issuesWithoutSprints issues that do not belong to any sprint
     * @param forceEndIssues       If true: issue status field will change to 'DONE' and be archived.
     *                             If false: issue status will stay the same and transferred to the next sprint.
     */
    private void endIssuesWithoutSprintToArchiveProject(Set<Issue> issuesWithoutSprints, boolean forceEndIssues) {

        if (forceEndIssues) {
            issuesWithoutSprints.forEach(Issue::forceCompleteIssue);
        } else {
            issuesWithoutSprints.forEach(Issue::endIssueWithProject);
        }
        issueRepository.saveAll(issuesWithoutSprints);
    }


}
