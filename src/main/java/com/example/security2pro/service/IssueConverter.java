package com.example.security2pro.service;

import com.example.security2pro.domain.enums.ActivityType;
import com.example.security2pro.domain.enums.IssuePriority;
import com.example.security2pro.domain.enums.IssueStatus;
import com.example.security2pro.domain.enums.IssueType;
import com.example.security2pro.domain.model.*;
import com.example.security2pro.dto.issue.ActivityDto;
import com.example.security2pro.dto.issue.IssueRelationDto;
import com.example.security2pro.dto.issue.IssueUpdateDto;
import com.example.security2pro.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;

@Service
@RequiredArgsConstructor
public class IssueConverter {

    private final ProjectRepository projectRepository;

    private final ProjectMemberRepository projectMemberRepository;

    private final SprintRepository sprintRepository;

    private final IssueRepository issueRepository;

    private final ActivityRepository activityRepository;

    private final IssueRelationRepository issueRelationRepository;



    public Issue convertToIssueModelToUpdate(Long projectId, IssueUpdateDto issueUpdateDto) {
        Issue issue =issueRepository.getReferenceById(issueUpdateDto.getIssueId());

        if(!issue.getProject().getId().equals(projectId)){throw new IllegalArgumentException("issue does not exist within the project with id " +projectId);}

        return convertToIssueModel(projectId,new HashSet<>(List.of(issueUpdateDto))).stream().findAny().get();
    }



    public Set<Issue> convertToIssueModel(Long projectId, Set<IssueUpdateDto> issueUpdateDtos){
        Project project = projectRepository.getReferenceById(projectId);

        HashSet<Long> sprintIds= issueUpdateDtos.stream().map(IssueUpdateDto::getCurrentSprintId).collect(toCollection(HashSet::new));
        Set<Sprint> foundSprints= sprintRepository.findActiveSprintsByIdAndProjectId(sprintIds,projectId);
        if(foundSprints.size()!=sprintIds.size()){
            throw new IllegalArgumentException("some sprints do not exist within the project with id"+ projectId +" or some sprints are not active anymore");
        }
        Map<Long,List<Sprint>> sprintMap= foundSprints.stream().collect(groupingBy(Sprint::getId));

        return issueUpdateDtos.stream()
                .map(issueUpdateDto -> convertFields(project,sprintMap.get(issueUpdateDto.getCurrentSprintId()).get(0), issueUpdateDto))
                .collect(toCollection(HashSet::new));
    }


    public Issue convertFields(Project project, Sprint sprint, IssueUpdateDto issueUpdateDto){
        Set<String> passedAssigneesUsernames= issueUpdateDto.getAssignees();
        Set<User> foundAssigneeUsers =projectMemberRepository
                .findAllByIdAndProjectIdWithUser(passedAssigneesUsernames,project.getId())
                .stream().map(ProjectMember::getUser).collect(Collectors.toCollection(HashSet::new));

        if(passedAssigneesUsernames.size()!=foundAssigneeUsers.size()){
            throw new IllegalArgumentException("some passed assignees do not exist for this issue");
        }

        String title = issueUpdateDto.getTitle();
        String description = issueUpdateDto.getDescription();
        LocalDateTime completeDate = issueUpdateDto.getCompleteDate();
        IssuePriority priority = issueUpdateDto.getPriority();
        IssueStatus status = issueUpdateDto.getStatus();
        IssueType type = issueUpdateDto.getType();

        if(status.equals(IssueStatus.DONE) && !completeDate.isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException("complete date cannot be set to future for the issues with 'DONE' status");
        }

        Issue newIssue = new Issue(null,project,foundAssigneeUsers, title, description, completeDate, priority, null, type, sprint);
        newIssue.changeStatus(issueUpdateDto.getStatus());
        return newIssue;
    }


    public Set<Activity> convertToActivityModel(Issue issue, Set<ActivityDto> activityDtos){
        Set<Long> passedActivityIds =activityDtos.stream().map(ActivityDto::getId).filter(Objects::nonNull).collect(Collectors.toCollection(HashSet::new));
        List<Activity> foundActivities = activityRepository.findAllById(passedActivityIds);
        if(passedActivityIds.size() != foundActivities.size()){
            throw new IllegalArgumentException("some passed activities do not exist for this issue");
        }
        // not used for now?..
        return activityDtos.stream()
                .filter(activityDto -> !activityDto.getType().equals(ActivityType.ISSUE_HISTORY))
                .map(activityDto -> new Activity(activityDto.getId(),issue,activityDto.getType(),activityDto.getDescription()))
                .collect(Collectors.toSet());
    }


    public Set<IssueRelation> convertToIssueRelationModel(Issue issue, Set<IssueRelationDto> issueRelationDtos) {
        //converts IssueRelationDto to IssueRelations
        Set<Long> passedIssueRelationIds=issueRelationDtos.stream().map(IssueRelationDto::getId).filter(Objects::nonNull).collect(Collectors.toCollection(HashSet::new));
        Set<IssueRelation> foundIssueRelations = issueRelationRepository.findAllByIdAndAffectedIssueId(passedIssueRelationIds,issue.getId());
        Set<Long> passedCauseIssueIds=issueRelationDtos.stream().map(IssueRelationDto::getCauseIssueId).collect(Collectors.toCollection(HashSet::new));
        List<Issue> foundCauseIssues = issueRepository.findAllById(passedCauseIssueIds);

        if(passedCauseIssueIds.size()!=foundCauseIssues.size()){
            throw new IllegalArgumentException("some cause issues not found within the project");
        }

        Map<Long,List<IssueRelation>> issueRelationsMap = foundIssueRelations.stream().collect(groupingBy(IssueRelation::getId));// check if all the ids are included here
        Map<Long,List<Issue>> causeIssuesMap = foundCauseIssues.stream().collect(groupingBy(Issue::getId));//check if all the cause issue ids are included here.

        Supplier<IllegalArgumentException> causeIssueException = ()-> new IllegalArgumentException("invalid issue relation. " +
                "cause issue cannot be the same as the affected issue. " +
                "cause Issue with 'DONE' state cannot be newly added as a cause issue");

        return issueRelationDtos.stream()
                .map(issueRelationDto -> {
                    Long issueRelationId= issueRelationDto.getId();
                    Issue causeIssue = causeIssuesMap.get(issueRelationDto.getCauseIssueId()).get(0);

                    if(issueRelationId!=null){
                        if(!issueRelationsMap.containsKey(issueRelationId)){
                            throw new IllegalArgumentException("issue relationship not found for this issue");}
                        return IssueRelation.getUpdatedIssueRelation(issueRelationId,issue,causeIssue,issueRelationDto.getRelationDescription())
                                .orElseThrow(causeIssueException);
                    } else {
                        return IssueRelation.createIssueRelation(issue,causeIssue,issueRelationDto.getRelationDescription())
                                .orElseThrow(causeIssueException);}
                })
                .collect(Collectors.toSet());
    }

}
