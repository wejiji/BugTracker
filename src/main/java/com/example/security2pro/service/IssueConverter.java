package com.example.security2pro.service;

import com.example.security2pro.domain.enums.ActivityType;
import com.example.security2pro.domain.model.*;
import com.example.security2pro.dto.issue.*;
import com.example.security2pro.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;

@Service
@Transactional
@RequiredArgsConstructor
public class IssueConverter {

    private final ProjectRepository projectRepository;

    private final ProjectMemberRepository projectMemberRepository;

    private final SprintRepository sprintRepository;

    private final IssueRepository issueRepository;

    private final ActivityRepository activityRepository;

    private final IssueRelationRepository issueRelationRepository;


    Supplier<IllegalArgumentException> completeDateException = ()->new IllegalArgumentException("complete date cannot be set to future for the issues with 'DONE' status");

    public Issue convertToIssueModelToCreate(IssueCreateDto issueCreateDto){
        Project project = projectRepository.getReferenceById(issueCreateDto.getProjectId().get());

        Sprint sprint = null;
        if(issueCreateDto.getCurrentSprintId()!=null){
            sprint = getValidatedSprint(issueCreateDto.getCurrentSprintId(),project.getId());
        }

        Set<User> foundAssigneeUsers = getValidatedUsers(issueCreateDto.getAssignees(),project.getId());

        Optional<Issue> issueOptional=Issue.createIssue(project,foundAssigneeUsers, issueCreateDto.getTitle(), issueCreateDto.getDescription(), issueCreateDto.getCompleteDate(), issueCreateDto.getPriority(), issueCreateDto.getStatus(), issueCreateDto.getType(), sprint);
        return issueOptional.orElseThrow(completeDateException);
    }

    public Issue convertToIssueModelToUpdate(IssueUpdateDto issueUpdateDto) {
        System.out.println("converter..");
        Issue issue =issueRepository.getReferenceById(issueUpdateDto.getIssueId());
        Project project = issue.getProject();
        Sprint sprint = null;
        if(issueUpdateDto.getCurrentSprintId()!=null){
            sprint =getValidatedSprint(issueUpdateDto.getCurrentSprintId(),project.getId());
        }

        Set<User> foundAssigneeUsers= getValidatedUsers(issueUpdateDto.getAssignees(),project.getId());

        Optional<Issue> updatedIssue=issue.detailUpdate(issueUpdateDto.getTitle(), issueUpdateDto.getDescription(), issueUpdateDto.getCompleteDate(),issueUpdateDto.getPriority(), issueUpdateDto.getStatus(),issueUpdateDto.getType(),sprint, foundAssigneeUsers);
        return updatedIssue.orElseThrow(completeDateException);
    }


    private Sprint getValidatedSprint(Long sprintId,Long projectId){
        return sprintRepository.findByIdAndProjectIdAndArchivedFalse(sprintId,projectId)
                .orElseThrow(()->new IllegalArgumentException(" the sprint does not exist within the project with id"+ projectId +" or not active anymore"));
    }

    private Set<User> getValidatedUsers(Set<String> passedAssigneesUsernames,Long projectId){
        Set<User> foundAssigneeUsers =projectMemberRepository
                .findAllByUsernameAndProjectIdWithUser(passedAssigneesUsernames,projectId)
                .stream().map(ProjectMember::getUser).collect(Collectors.toCollection(HashSet::new));

        System.out.println("passed = " +passedAssigneesUsernames);
        System.out.println("found = "+foundAssigneeUsers.stream().map(User::getUsername).collect(Collectors.toSet()));

        if(passedAssigneesUsernames.size()!=foundAssigneeUsers.size()){
            throw new IllegalArgumentException("some passed assignees do not exist for this issue");
        }
        return foundAssigneeUsers;
    }


//    public Set<Issue> convertToSimpleIssueModelBulk(Long projectId, Set<IssueSimpleDto> issueSimpleDtos, Set<Issue> issuesToBeUpdated){
//        //Project project = projectRepository.getReferenceById(projectId);
//
//        HashSet<Long> sprintIds= issueSimpleDtos.stream().map(IssueSimpleDto::getCurrentSprintId).collect(toCollection(HashSet::new));
//        Set<Sprint> foundSprints= sprintRepository.findActiveSprintsByIdAndProjectId(sprintIds,projectId);
//        if(foundSprints.size()!=sprintIds.size()){
//            throw new IllegalArgumentException("some sprints do not exist within the project with id"+ projectId +" or some sprints are not active anymore");
//        }
//        Map<Long,List<Sprint>> sprintMap= foundSprints.stream().collect(groupingBy(Sprint::getId));
//
//        Map<Long, List<IssueSimpleDto>> issueSimpleDtoMap = issueSimpleDtos.stream().collect(groupingBy(issueSimpleDto -> issueSimpleDto.getId()));
//
//        for(Issue issue : issuesToBeUpdated){ // update by dirty checking- no save
//            IssueSimpleDto issueSimpleDto = issueSimpleDtoMap.get(issue.getId()).get(0);
//            Sprint sprint = sprintMap.get(issueSimpleDto.getCurrentSprintId()).get(0);
//            issue.simpleUpdate(issueSimpleDto.getTitle(), issueSimpleDto.getPriority(), issueSimpleDto.getStatus(), sprintMap.get(issueSimpleDto.getCurrentSprintId()).get(0));
//        }
//        return issuesToBeUpdated;
//    }
//    public Set<Issue> convertToIssueModel(Long projectId, Set<IssueUpdateDto> issueUpdateDtos){
//        Project project = projectRepository.getReferenceById(projectId);
//
//        HashSet<Long> sprintIds= issueUpdateDtos.stream().map(IssueUpdateDto::getCurrentSprintId).collect(toCollection(HashSet::new));
//        Set<Sprint> foundSprints= sprintRepository.findActiveSprintsByIdAndProjectId(sprintIds,projectId);
//        if(foundSprints.size()!=sprintIds.size()){
//            throw new IllegalArgumentException("some sprints do not exist within the project with id"+ projectId +" or some sprints are not active anymore");
//        }
//        Map<Long,List<Sprint>> sprintMap= foundSprints.stream().collect(groupingBy(Sprint::getId));
//
//        return issueUpdateDtos.stream()
//                .map(issueUpdateDto -> convertFields(project,sprintMap.get(issueUpdateDto.getCurrentSprintId()).get(0), issueUpdateDto))
//                .collect(toCollection(HashSet::new));
//    }



    public Set<Activity> convertToActivityModel(Issue issue, Set<ActivityDto> activityDtos){
        Set<Long> passedActivityIds =activityDtos.stream().map(ActivityDto::getId).filter(Objects::nonNull).collect(Collectors.toCollection(HashSet::new));
        List<Activity> foundActivities = activityRepository.findAllById(passedActivityIds);
        if(passedActivityIds.size() != foundActivities.size()){
            throw new IllegalArgumentException("some passed activities do not exist for this issue");
        }
        // not used for now?..
        return activityDtos.stream()
                .map(activityDto -> new Activity(activityDto.getId(),issue,activityDto.getType(),activityDto.getDescription()))
                .collect(Collectors.toSet());
    }







}
