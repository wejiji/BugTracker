package com.example.security2pro.service;

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


    private final SimpleIssueConverter simpleIssueConverter;



    public Set<IssueSimpleDto> getUserIssues(String username){
        User user = userRepository.findUserByUsername(username).orElseThrow(()->new IllegalArgumentException("user with username"+ username +" does not exist" ));
        return issueRepository.findActiveIssueByAssignee(user.getUsername()).stream()
                .map(IssueSimpleDto::new)
                .collect(Collectors.toSet());
    }

    public Set<IssueSimpleDto> getActiveIssues(Long projectId){
        return issueRepository.findByProjectIdAndArchivedFalse(projectId).stream().map(IssueSimpleDto::new).collect(Collectors.toSet());
    }

    public Set<IssueSimpleDto> getActiveIssuesBySprintId(Long sprintId){
        Sprint sprint = sprintRepository.findByIdAndArchivedFalse(sprintId)
                .orElseThrow(()-> new IllegalArgumentException("the sprint is not active"));

        return issueRepository.findByCurrentSprintId(sprintId).stream().map(IssueSimpleDto::new).collect(Collectors.toSet());
    }


//    public IssueUpdateResponseDto getIssueWithDetails(Long issueId) {
//        Optional<Issue> foundIssue = issueRepository.findByIdWithAssignees(issueId); //relations and assignees join fetch
//        if(foundIssue.isEmpty()){
//            throw new IllegalArgumentException("issue with id "+ issueId +" not found" );
//        }
//
//        Issue issue = foundIssue.get();
//        Set<IssueRelation> issueRelations= issueRelationRepository.findAllByAffectedIssueId(issue.getId());
//
//        Set<Activity> activities = activityRepository.findAllByIssueId(issue.getId());
//        List<IssueHistoryDto> issueHistoryDtos = issueHistoryService.getIssueHistories(issueId);
//        return new IssueUpdateResponseDto(issue,activities,issueRelations,issueHistoryDtos);
//    }



//==============================================

    public IssueUpdateDto getIssueSimple(Long issueId){
        Optional<Issue> foundIssue = issueRepository.findByIdWithAssignees(issueId); //relations and assignees join fetch
        if(foundIssue.isEmpty()){
            throw new IllegalArgumentException("issue with id "+ issueId +" not found" );
        }
        return new IssueUpdateDto(foundIssue.get());
    }

    public IssueUpdateDto createIssueFromSimpleDto(IssueCreateDto issueCreateDto) {

        Issue issue = simpleIssueConverter.convertToIssueModelToCreate(issueCreateDto); //conversion error?
        Issue newIssue= issueRepository.save(issue);

        return new IssueUpdateDto(newIssue);
    }

    public IssueUpdateDto updateIssueFromSimpleDto(IssueUpdateDto issueUpdateDto) {
        System.out.println("service.. ");
        Issue issue = simpleIssueConverter.convertToIssueModelToUpdate(issueUpdateDto);
        Issue updatedIssue= issueRepository.save(issue);

        return new IssueUpdateDto(updatedIssue);
    }


    public Set<IssueSimpleDto> updateIssuesInBulk(Long projectId, Set<IssueSimpleDto> issueSimpleDtos){
        Set<Long> issueDtoIds = issueSimpleDtos.stream().map(IssueSimpleDto::getId).collect(toCollection(HashSet::new));
        Set<Issue> foundIssues =issueRepository.findAllByIdAndProjectIdAndArchivedFalse(issueDtoIds,projectId);


        if(foundIssues.size()!= issueDtoIds.size()){
            throw new IllegalArgumentException("some issues do not exist within the project with id"+projectId);
        }
        Set<Issue> resultIssues= simpleIssueConverter.convertToSimpleIssueModelBulk(projectId, issueSimpleDtos, foundIssues);
        return issueRepository.saveAll(resultIssues).stream().map(IssueSimpleDto::new).collect(Collectors.toSet());
    }

    public void deleteByIdsInBulk(Set<Long> issueIds){
        // Can I change this to "delete from activity where ~~"?? (without getting ids first)
        // which will be faster ?? or will it be the same??
        Set<Long> idsToBeDeleted = activityRepository.findByIssueIdIn(issueIds).stream().map(Activity::getId).collect(toCollection(HashSet::new));
        activityRepository.deleteAllByIdInBatch(idsToBeDeleted);
        Set<Long> relations= issueRelationRepository.findAllByIssueIds(issueIds).stream().map(IssueRelation::getId).collect(toCollection(HashSet::new));
        issueRelationRepository.deleteAllByIdInBatch(relations);
        issueRepository.deleteAllByIdInBatch(issueIds);
    }


    public void handleEndingSprintIssues(Long sprintId, boolean forceEndIssues) {
        Sprint sprint = sprintRepository.getReferenceById(sprintId);

        Set<Issue> foundPassedIssues = issueRepository.findByCurrentSprintId(sprint.getId());

        if(forceEndIssues){
            foundPassedIssues.stream().peek(Issue::forceCompleteIssue);
        } else {
            Map<Boolean, List<Issue>> issueMap = foundPassedIssues.stream().collect(Collectors.partitioningBy(issue -> issue.getStatus().equals(IssueStatus.DONE)));
            issueMap.get(Boolean.TRUE).stream().peek(Issue::forceCompleteIssue);
            List<Issue> incompletes = issueMap.get(Boolean.FALSE);
            if (!incompletes.isEmpty()) {
                Optional<Sprint> nextSprintOptional = sprintRepository.getNext();
                Sprint nextSprint = nextSprintOptional
                        .orElseGet(() -> sprintRepository.save(
                                Sprint.createSprint(sprint.getProject(), "untitled", "", LocalDateTime.now(), LocalDateTime.now().plusDays(14))
                                        .orElseThrow(()->new IllegalArgumentException("start date cannot be after end date"))));
                incompletes.forEach(incomplete -> System.out.println(incomplete.getId() +"with id and title: "+ incomplete.getTitle()));
                incompletes.forEach(issue -> issue.assignCurrentSprint(nextSprint));
            }
        }
        sprintIssueHistoryRepository.saveAll(foundPassedIssues.stream().map(issue -> new SprintIssueHistory(sprint, issue)).collect(Collectors.toCollection(ArrayList::new)));
    }


    public void endProject(Long projectId,boolean forceEndIssues){
        Project project = projectRepository.getReferenceById(projectId);
        project.endProject();

        Set<Sprint> sprintsToTerminate= sprintRepository.findByProjectIdAndArchivedFalse(projectId);
        Map<Long,List<Sprint>> sprintsMap= sprintsToTerminate.stream().collect(groupingBy(Sprint::getId));
        Set<Long> sprintIds = sprintsToTerminate.stream().map(Sprint::getId).collect(Collectors.toCollection(HashSet::new));
        Map<Long,List<Issue>> issuesWithSprintMap = issueRepository.findByCurrentSprintIdIn(sprintIds).stream().collect(groupingBy(issue->issue.getCurrentSprint().get().getId()));

        issuesWithSprintMap.entrySet().stream().map(entry ->{ Sprint sprint = sprintsMap.get(entry.getKey()).get(0);
            sprint.completeSprint();
            //sprint.getId() duplicate lines... how should I change this??
            handleEndingSprintIssues(sprint.getId(),forceEndIssues);
            return entry;
        });

        Set<Issue> issuesWithoutSprints= issueRepository.findByProjectIdAndArchivedFalseAndCurrentSprintIsNull(projectId);
        issuesWithoutSprints.stream().peek(Issue::endIssueWithProject);
    }





}
