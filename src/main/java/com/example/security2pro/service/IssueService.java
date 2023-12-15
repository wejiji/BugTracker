package com.example.security2pro.service;
import com.example.security2pro.domain.enums.ActivityType;
import com.example.security2pro.domain.enums.IssueStatus;
import com.example.security2pro.domain.model.*;
import com.example.security2pro.dto.issue.*;
import com.example.security2pro.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class IssueService {

    private final IssueRepository issueRepository;

    private final ActivityRepository activityRepository;

    private final IssueRelationRepository issueRelationRepository;

    private final ProjectRepository projectRepository;

    private final SprintRepository sprintRepository;

    private final UserRepository userRepository;

    private final SprintIssueHistoryRepository sprintIssueHistoryRepository;

    private final IssueConverter issueConverter;

    private final IssueHistoryService issueHistoryService;


    public void deleteByIdsInBulk(Set<Long> issueIds){
        // Can I change this to "delete from activity where ~~"?? (without getting ids first)
        // which will be faster ?? or will it be the same??
        Set<Long> idsToBeDeleted = activityRepository.findByIssueIds(issueIds).stream().map(Activity::getId).collect(toCollection(HashSet::new));
        activityRepository.deleteAllByIdInBatch(idsToBeDeleted);
        Set<Long> relations= issueRelationRepository.findAllByIssueIds(issueIds).stream().map(IssueRelation::getId).collect(toCollection(HashSet::new));
        issueRelationRepository.deleteAllByIdInBatch(relations);
        issueRepository.deleteAllByIdInBatch(issueIds);
    }


    public Set<IssueUpdateDto> getUserIssues(String username){
        User user = userRepository.findUserByUsername(username).orElseThrow(()->new IllegalArgumentException("user with username"+ username +" does not exist" ));
        return issueRepository.findActiveIssueByAssignee(user.getUsername()).stream()
                .map(issue -> new IssueUpdateDto(issue,Collections.emptySet(),Collections.emptySet(),Collections.emptyList()))
                .collect(Collectors.toSet());
    }

    public Set<Issue> getActiveIssues(Long projectId){
        return issueRepository.findActiveExistingIssuesByProjectId(projectId);
    }

    public Set<Issue> getActiveIssuesBySprintId(Long projectId,Long sprintId){
        Sprint sprint = sprintRepository.findByIdAndProjectIdAndArchivedFalse(projectId,sprintId)
                .orElseThrow(()-> new IllegalArgumentException("sprint does not exist within the project with id" + projectId));

        return issueRepository.findIssuesByCurrentSprintId(sprintId);
    }

    public Set<SprintIssueHistory> getSprintIssueHistory(Long sprintId, Long projectId){
        return sprintIssueHistoryRepository.findByArchivedSprintAndProjectId(sprintId, projectId);
    }

    public IssueUpdateDto getIssueWithDetails(Long issueId) {
        Optional<Issue> foundIssue = issueRepository.findIssueWithAssignees(issueId); //relations and assignees join fetch
        if(foundIssue.isEmpty()){
            throw new IllegalArgumentException("issue with id "+ issueId +" not found" );
        }
        Issue issue = foundIssue.get();
        Set<IssueRelation> issueRelations= issueRelationRepository.findByAffectedIssue(issue.getId());
        Set<Activity> activities = activityRepository.findByIssueId(issue.getId());
        List<IssueHistoryDto> issueHistoryDtos = issueHistoryService.getIssueHistories(issueId);
        return new IssueUpdateDto(issue,activities,issueRelations,issueHistoryDtos);
    }

    public IssueUpdateDto createIssueDetailFromDto(Long projectId, IssueCreateDto issueCreateDto) {
        IssueUpdateDto issueUpdateDto = new IssueUpdateDto(issueCreateDto);
        Issue issue = issueConverter.convertToIssueModel(projectId,new HashSet<>(List.of(issueUpdateDto))).stream().findAny().get(); //conversion error?
        Issue newIssue= issueRepository.save(issue);

        Set<IssueRelation> issueRelationList = issueConverter.convertToIssueRelationModel(issue, issueUpdateDto.getIssueRelationDtoList());
        issueRelationList = new HashSet<>(issueRelationRepository.saveAll(issueRelationList));

        List<IssueHistoryDto> issueHistoryDtos = issueHistoryService.getIssueHistories(newIssue.getId());
        return new IssueUpdateDto(newIssue,Collections.emptySet(),issueRelationList,issueHistoryDtos);
    }

    public IssueUpdateDto updateIssueDetailFromDto(Long projectId, IssueUpdateDto issueUpdateDto) {
        Issue issue = issueConverter.convertToIssueModelToUpdate(projectId, issueUpdateDto);
        Issue updatedIssue= issueRepository.save(issue);

        deleteInvalidIssueRelations(issueUpdateDto);
        Set<IssueRelation> issueRelationList = issueConverter.convertToIssueRelationModel(issue,issueUpdateDto.getIssueRelationDtoList());
        issueRelationList = new HashSet<>(issueRelationRepository.saveAll(issueRelationList));

        deleteInvalidActivities(issueUpdateDto);
        Set<Activity> activityList = issueConverter.convertToActivityModel(updatedIssue, issueUpdateDto.getActivityDtoList());
        activityList= new HashSet<>(activityRepository.saveAll(activityList)); // activities that are not histories
        Set<Activity> issueHistoryActivityList=activityRepository.findIssueHistoryByIssueId(updatedIssue.getId());
        activityList.addAll(issueHistoryActivityList);

        List<IssueHistoryDto> issueHistoryDtos = issueHistoryService.getIssueHistories(updatedIssue.getId());
        return new IssueUpdateDto(updatedIssue,activityList,issueRelationList,issueHistoryDtos);
    }

    public List<Issue> updateIssuesInBulk(Long projectId, Set<IssueUpdateDto> issueUpdateDtos){
        Set<Long> issueDtoIds = issueUpdateDtos.stream().map(IssueUpdateDto::getIssueId).collect(toCollection(HashSet::new));
        Set<Issue> foundIssues =issueRepository.findAllByIdAndProjectId(issueDtoIds,projectId);

        if(foundIssues.size()!= issueDtoIds.size()){
            throw new IllegalArgumentException("some issues do not exist within the project with id"+projectId);
        }
        Set<Issue> resultIssues= issueConverter.convertToIssueModel(projectId, issueUpdateDtos);
        return issueRepository.saveAll(resultIssues);
    }

    public void handleEndingSprintIssues(Long projectId, Long sprintId, boolean forceEndIssues) {
        Sprint sprint = sprintRepository.findByIdAndProjectIdAndArchivedFalse(sprintId,projectId)
                .orElseThrow(()-> new IllegalArgumentException("active sprint does not exist within the project with id" +projectId));

        Set<Issue> foundPassedIssues = issueRepository.findIssuesByCurrentSprintId(sprint.getId());

        if(forceEndIssues){
            foundPassedIssues.stream().peek(Issue::forceCompleteIssue);

        } else {

            Map<Boolean, List<Issue>> issueMap = foundPassedIssues.stream().collect(groupingBy(issue -> issue.getStatus().equals(IssueStatus.DONE)));
            issueMap.get(true).stream().peek(Issue::forceCompleteIssue);

            List<Issue> incompletes = issueMap.get(false);
            if (!incompletes.isEmpty()) {
                Sprint nextSprint;
                Optional<Sprint> nextSprintOptional = sprintRepository.getNext();
                if (!nextSprintOptional.isEmpty()) {
                    nextSprint = null;
                    nextSprintOptional.get().getId();
                } else {
                    nextSprint = sprintRepository.save(new Sprint(sprint.getProject(), "untitled", "", LocalDateTime.now(), LocalDateTime.now().plusDays(14)));
                }
                incompletes.stream().peek(issue -> issue.assignCurrentSprint(nextSprint));
            }
        }

        issueRepository.saveAll(foundPassedIssues);
        //necessary. If issue's assignee are given as unmodifiable set(when Set.of() is used), save won't work.
        //initialize the collection and should not change it? --- How can this be achieved???...
        sprintIssueHistoryRepository.saveAll(foundPassedIssues.stream().map(issue -> new SprintIssueHistory(sprint, issue)).collect(Collectors.toCollection(ArrayList::new)));
    }


    public void endProject(Long projectId,boolean forceEndIssues){
        Project project = projectRepository.getReferenceById(projectId);
        project.endProject();

        Set<Sprint> sprintsToTerminate= sprintRepository.findActiveSprintsByProjectId(projectId);
        Map<Long,List<Sprint>> sprintsMap= sprintsToTerminate.stream().collect(groupingBy(Sprint::getId));
        Set<Long> sprintIds = sprintsToTerminate.stream().map(Sprint::getId).collect(Collectors.toCollection(HashSet::new));
        Map<Long,List<Issue>> issuesWithSprintMap = issueRepository.findAllIssuesByCurrentSprintIds(sprintIds).stream().collect(groupingBy(issue->issue.getCurrentSprint().getId()));

        issuesWithSprintMap.entrySet().stream().map(entry ->{ Sprint sprint = sprintsMap.get(entry.getKey()).get(0);
            sprint.completeSprint();
            //sprint.getId() duplicate lines... how should I change this??
            handleEndingSprintIssues(projectId,sprint.getId(),forceEndIssues);
            return entry;
        });

        Set<Issue> issuesWithoutSprints= issueRepository.findActiveIssuesWithoutSprintByProjectId(projectId);
        issuesWithoutSprints.stream().peek(Issue::endIssueWithProject);
    }

    private void deleteInvalidIssueRelations(IssueUpdateDto issueUpdateDto) {
        Set<Long> previousIds = issueRelationRepository.findByAffectedIssue(issueUpdateDto.getIssueId()).stream().map(IssueRelation::getId).collect(Collectors.toCollection(HashSet::new));
        Set<Long> currentRelationIds = issueUpdateDto.getIssueRelationDtoList().stream().map(IssueRelationDto::getId).collect(Collectors.toCollection(HashSet::new));
        previousIds.removeAll(currentRelationIds);
        issueRelationRepository.deleteAllByIdInBatch(previousIds);
    }

    private void deleteInvalidActivities(IssueUpdateDto issueUpdateDto) {
        Set<Long> previousActivityIds = activityRepository.findByIssueId(issueUpdateDto.getIssueId()).stream().filter(activityDto->!activityDto.getType().equals(ActivityType.ISSUE_HISTORY)).map(Activity::getId).collect(toCollection(HashSet::new));
        Set<Long> currentActivityIds = issueUpdateDto.getActivityDtoList().stream().filter(activityDto->!activityDto.getType().equals(ActivityType.ISSUE_HISTORY)).map(ActivityDto::getId).collect(toCollection(HashSet::new));
        previousActivityIds.removeAll(currentActivityIds);
        activityRepository.deleteAllByIdInBatch(previousActivityIds);

    }




}
