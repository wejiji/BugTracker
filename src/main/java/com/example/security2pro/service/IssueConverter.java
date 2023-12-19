package com.example.security2pro.service;

import com.example.security2pro.domain.enums.ActivityType;
import com.example.security2pro.domain.model.*;
import com.example.security2pro.dto.issue.*;
import com.example.security2pro.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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


    Supplier<IllegalArgumentException> completeDateException = ()->new IllegalArgumentException("complete date cannot be set to future for the issues with 'DONE' status");

    public Issue convertToIssueModelToCreate(IssueCreateDto issueCreateDto){
        Project project = projectRepository.getReferenceById(issueCreateDto.getProjectId().get());

        Sprint sprint = getValidatedSprint(issueCreateDto.getCurrentSprintId(),project.getId());

        Set<User> foundAssigneeUsers = getValidatedUsers(issueCreateDto.getAssignees(),project.getId());

        Optional<Issue> issueOptional=Issue.createIssue(project,foundAssigneeUsers, issueCreateDto.getTitle(), issueCreateDto.getDescription(), issueCreateDto.getCompleteDate(), issueCreateDto.getPriority(), issueCreateDto.getStatus(), issueCreateDto.getType(), sprint);
        return issueOptional.orElseThrow(completeDateException);
    }

    public Issue convertToIssueModelToUpdate(IssueUpdateDto issueUpdateDto) {
        System.out.println("converter..");
        Issue issue =issueRepository.getReferenceById(issueUpdateDto.getIssueId());
        Project project = issue.getProject();
        Sprint sprint =getValidatedSprint(issueUpdateDto.getCurrentSprintId(),project.getId());

        Set<User> foundAssigneeUsers= getValidatedUsers(issueUpdateDto.getAssignees(),project.getId());

        Optional<Issue> updateSuccess=issue.detailUpdate(issueUpdateDto.getTitle(), issueUpdateDto.getDescription(), issueUpdateDto.getCompleteDate(),issueUpdateDto.getPriority(), issueUpdateDto.getStatus(),issueUpdateDto.getType(),sprint, foundAssigneeUsers);
        return updateSuccess.orElseThrow(completeDateException);
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

//    public Issue convertFields(Project project, Sprint sprint, IssueUpdateDto issueUpdateDto){
//        Set<String> passedAssigneesUsernames= issueUpdateDto.getAssignees();
//        Set<User> foundAssigneeUsers =projectMemberRepository
//                .findAllByIdAndProjectIdWithUser(passedAssigneesUsernames,project.getId())
//                .stream().map(ProjectMember::getUser).collect(Collectors.toCollection(HashSet::new));
//
//        if(passedAssigneesUsernames.size()!=foundAssigneeUsers.size()){
//            throw new IllegalArgumentException("some passed assignees do not exist for this issue");
//        }
//
//        String title = issueUpdateDto.getTitle();
//        String description = issueUpdateDto.getDescription();
//        LocalDateTime completeDate = issueUpdateDto.getCompleteDate();
//        IssuePriority priority = issueUpdateDto.getPriority();
//        IssueStatus status = issueUpdateDto.getStatus();
//        IssueType type = issueUpdateDto.getType();
//
//        if(status.equals(IssueStatus.DONE) && !completeDate.isBefore(LocalDateTime.now())){
//            throw new IllegalArgumentException("complete date cannot be set to future for the issues with 'DONE' status");
//        } // ????
//
//        // need to change here
//        Issue newIssue = new Issue(null,project,foundAssigneeUsers, title, description, completeDate, priority, null, type, sprint);
//        newIssue.changeStatus(issueUpdateDto.getStatus());
//        return newIssue;
//    }
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


    public Set<IssueRelation> convertToIssueRelationModel(Issue issue, Set<IssueRelationDto> issueRelationDtos) {
        Set<Long> passedIssueRelationIds=issueRelationDtos.stream().map(IssueRelationDto::getId).filter(Objects::nonNull).collect(Collectors.toCollection(HashSet::new));
        Set<IssueRelation> foundIssueRelations = issueRelationRepository.findAllByIdAndAffectedIssueId(passedIssueRelationIds,issue.getId());

        Map<Long,List<Issue>> causeIssuesMap = getValidatedCauseIssuesMap(issueRelationDtos.stream().map(IssueRelationDto::getCauseIssueId).collect(Collectors.toCollection(HashSet::new)));
        Map<Long,List<IssueRelation>> issueRelationsMap = foundIssueRelations.stream().collect(groupingBy(IssueRelation::getId));// check if all the ids are included here

        return issueRelationDtos.stream()
                .map(issueRelationDto -> {
                    Long issueRelationId= issueRelationDto.getId();
                    Issue causeIssue = causeIssuesMap.get(issueRelationDto.getCauseIssueId()).get(0);

                    if(!issueRelationsMap.containsKey(issueRelationId)){throw new IllegalArgumentException("issue relationship not found for this issue");}
                    return IssueRelation.getUpdatedIssueRelation(issueRelationId,issue,causeIssue,issueRelationDto.getRelationDescription())
                                .orElseThrow(causeIssueException);
                }).collect(Collectors.toSet());
    }

    public Set<IssueRelation> convertToIssueRelationModelToCreate(Issue issue, Set<IssueRelationCreateDto> issueRelationCreateDtos) {
        Map<Long,List<Issue>> causeIssuesMap = getValidatedCauseIssuesMap(issueRelationCreateDtos.stream().map(IssueRelationCreateDto::getCauseIssueId).collect(Collectors.toCollection(HashSet::new)));

        return issueRelationCreateDtos.stream()
                .map(issueRelationDto -> {
                    Issue causeIssue = causeIssuesMap.get(issueRelationDto.getCauseIssueId()).get(0);
                    return IssueRelation.createIssueRelation(issue,causeIssue,issueRelationDto.getRelationDescription())
                                .orElseThrow(causeIssueException);
                }).collect(Collectors.toSet());
    }

    private final Supplier<IllegalArgumentException> causeIssueException = ()-> new IllegalArgumentException("invalid issue relation. " +
            "cause issue cannot be the same as the affected issue. " +
            "cause Issue with 'DONE' state cannot be newly added as a cause issue. ");

    private Map<Long,List<Issue>> getValidatedCauseIssuesMap(Set<Long> issueRelationDtoIds){
        List<Issue> foundCauseIssues = issueRepository.findAllById(issueRelationDtoIds);
        if(issueRelationDtoIds.size()!=foundCauseIssues.size()){
            throw new IllegalArgumentException("some cause issues not found within the project");
        }
        return foundCauseIssues.stream().collect(groupingBy(Issue::getId));
    }

}
