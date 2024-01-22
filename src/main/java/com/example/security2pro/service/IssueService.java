package com.example.security2pro.service;

import com.example.security2pro.domain.model.*;
import com.example.security2pro.domain.model.issue.Issue;
import com.example.security2pro.domain.model.issue.IssueRelation;
import com.example.security2pro.dto.issue.*;
import com.example.security2pro.dto.issue.onetomany.*;
import com.example.security2pro.repository.repository_interfaces.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.groupingBy;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class IssueService {

    private final IssueRepository issueRepository;

    private final SprintRepository sprintRepository;

    private final UserRepository userRepository;

    private final ProjectRepository projectRepository;

    private final ProjectMemberRepository projectMemberRepository;


    public Set<IssueSimpleDto> getUserIssues(String username){
        User user = userRepository.findUserByUsername(username).orElseThrow(()->new IllegalArgumentException("user with username"+ username +" does not exist" ));
        return issueRepository.findActiveIssueByUsername(user.getUsername()).stream()
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



//==============================================

    public IssueUpdateDto getIssueSimple(Long issueId){
        Optional<Issue> foundIssue = issueRepository.findByIdWithAssignees(issueId); //relations and assignees join fetch
        if(foundIssue.isEmpty()){
            throw new IllegalArgumentException("issue with id "+ issueId +" not found" );
        }
        return new IssueUpdateDto(foundIssue.get());
    }

    public IssueUpdateDto createIssueFromSimpleDto(IssueCreateDto issueCreateDto) {

        Issue issue = convertToIssueModelToCreate(issueCreateDto); //conversion error?
        Issue newIssue= issueRepository.save(issue);

        return new IssueUpdateDto(newIssue);
    }

    public IssueUpdateDto updateIssueFromSimpleDto(IssueUpdateDto issueUpdateDto) {
        Issue issue = convertToIssueModelToUpdate(issueUpdateDto);
        Issue updatedIssue= issueRepository.save(issue);

        return new IssueUpdateDto(updatedIssue);
    }


//    public Set<IssueSimpleDto> updateIssuesInBulk(Long projectId, Set<IssueSimpleDto> issueSimpleDtos){
//        Set<Long> issueDtoIds = issueSimpleDtos.stream().map(IssueSimpleDto::getId).collect(toCollection(HashSet::new));
//        Set<Issue> foundIssues =issueRepository.findAllByIdAndProjectIdAndArchivedFalse(issueDtoIds,projectId);
//
//        if(foundIssues.size()!= issueDtoIds.size()){
//            throw new IllegalArgumentException("some issues do not exist within the project with id"+projectId);
//        }
//        Set<Issue> resultIssues= convertToSimpleIssueModelBulk(projectId, issueSimpleDtos, foundIssues);
//        return issueRepository.saveAll(resultIssues).stream().map(IssueSimpleDto::new).collect(Collectors.toSet());
//    }

    public void deleteByIdsInBulk(Set<Long> issueIds){
//        Set<Long> idsToBeDeleted = activityRepository.findByIssueIdIn(issueIds).stream().map(Activity::getId).collect(toCollection(HashSet::new));
//        activityRepository.deleteAllByIdInBatch(idsToBeDeleted);
//        Set<Long> relations= issueRelationRepository.findAllByAffectedIssueIds(issueIds).stream().map(IssueRelation::getId).collect(toCollection(HashSet::new));
//        issueRelationRepository.deleteAllByIdInBatch(relations);
        issueRepository.deleteAllByIdInBatch(issueIds);
    }


//============================== issue relation
    public IssueRelationDto createOrUpdateIssueRelation(Long issueId, IssueRelationDto issueRelationDto){
        Issue issue= issueRepository.findByIdWithIssueRelationSet(issueId).get();
        Issue causeIssue = issueRepository.getReferenceById(issueRelationDto.getCauseIssueId());
        issue.addIssueRelation(IssueRelation.createIssueRelation(issue,causeIssue,issueRelationDto.getRelationDescription()));
        return issueRelationDto;
    }

    public void deleteIssueRelation(Long issueId,Long causeIssueId){
        Issue issue= issueRepository.findByIdWithIssueRelationSet(issueId).get();
        issue.deleteIssueRelation(causeIssueId);
    }

    public Set<IssueRelationDto> findAllByAffectedIssueId(Long affectedIssueId){
        Issue issue= issueRepository.findByIdWithIssueRelationSet(affectedIssueId).get();
        return issue.getIssueRelationSet().stream().map(IssueRelationDto::new).collect(Collectors.toSet());
        // return issueRelationRepository.findAllByAffectedIssueId(affectedIssueId).stream().map(IssueRelationDto::new).collect(Collectors.toSet());
    }

//==========================================


    private Issue convertToIssueModelToCreate(IssueCreateDto issueCreateDto){
        Project project = projectRepository.getReferenceById(issueCreateDto.getProjectId().get());

        Sprint sprint = null;
        if(issueCreateDto.getCurrentSprintId()!=null){
            sprint = getValidatedSprint(issueCreateDto.getCurrentSprintId(),project.getId());
        }

        Set<User> foundAssigneeUsers = getValidatedUsers(issueCreateDto.getAssignees(),project.getId());

        return Issue.createIssue(null,project,foundAssigneeUsers, issueCreateDto.getTitle(), issueCreateDto.getDescription(), issueCreateDto.getPriority(), issueCreateDto.getStatus(), issueCreateDto.getType(), sprint);
    }

    private Issue convertToIssueModelToUpdate(IssueUpdateDto issueUpdateDto) {
        Issue issue =issueRepository.getReferenceById(issueUpdateDto.getIssueId());
        Project project = issue.getProject();
        Sprint sprint = null;
        if(issueUpdateDto.getCurrentSprintId()!=null){
            sprint =getValidatedSprint(issueUpdateDto.getCurrentSprintId(),project.getId());
        }

        Set<User> foundAssigneeUsers= getValidatedUsers(issueUpdateDto.getAssignees(),project.getId());

        return issue.detailUpdate(issueUpdateDto.getTitle(), issueUpdateDto.getDescription(), issueUpdateDto.getPriority(), issueUpdateDto.getStatus(), issueUpdateDto.getType(),sprint, foundAssigneeUsers);
    }


    private Sprint getValidatedSprint(Long sprintId,Long projectId){
        return sprintRepository.findByIdAndProjectIdAndArchivedFalse(sprintId,projectId)
                .orElseThrow(()->new IllegalArgumentException(" the sprint does not exist within the project with id"+ projectId +" or not active anymore"));
    }

    private Set<User> getValidatedUsers(Set<String> passedAssigneesUsernames,Long projectId){
        if(passedAssigneesUsernames==null || passedAssigneesUsernames.isEmpty()){
            return Collections.emptySet();
        }
        Set<User> foundAssigneeUsers =projectMemberRepository
                .findAllByUsernameAndProjectIdWithUser(passedAssigneesUsernames,projectId)
                .stream().map(ProjectMember::getUser).collect(Collectors.toCollection(HashSet::new));

        log.info("passed = " +passedAssigneesUsernames);
        log.info("found = "+foundAssigneeUsers.stream().map(User::getUsername).collect(Collectors.toSet()));

        if(passedAssigneesUsernames.size()!=foundAssigneeUsers.size()){
            throw new IllegalArgumentException("some passed assignees do not exist for this issue");
        }
        return foundAssigneeUsers;
    }

//    private Set<Issue> convertToSimpleIssueModelBulk(Long projectId, Set<IssueSimpleDto> issueSimpleDtos, Set<Issue> issuesToBeUpdated){
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



}
