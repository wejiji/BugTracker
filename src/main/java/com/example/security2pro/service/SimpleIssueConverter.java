package com.example.security2pro.service;

import com.example.security2pro.domain.model.*;
import com.example.security2pro.dto.issue.*;
import com.example.security2pro.repository.jpa_repository.IssueJpaRepository;
import com.example.security2pro.repository.jpa_repository.ProjectMemberJpaRepository;
import com.example.security2pro.repository.jpa_repository.ProjectJpaRepository;
import com.example.security2pro.repository.jpa_repository.SprintJpaRepository;
import com.example.security2pro.repository.repository_interfaces.IssueRepository;
import com.example.security2pro.repository.repository_interfaces.ProjectMemberRepository;
import com.example.security2pro.repository.repository_interfaces.ProjectRepository;
import com.example.security2pro.repository.repository_interfaces.SprintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;


@Service
@Transactional
@RequiredArgsConstructor
public class SimpleIssueConverter {
    private final ProjectRepository projectRepository;

    private final ProjectMemberRepository projectMemberRepository;

    private final SprintRepository sprintRepository;

    private final IssueRepository issueRepository;

    public Issue convertToIssueModelToCreate(IssueCreateDto issueCreateDto){
        Project project = projectRepository.getReferenceById(issueCreateDto.getProjectId().get());

        Sprint sprint = null;
        if(issueCreateDto.getCurrentSprintId()!=null){
            sprint = getValidatedSprint(issueCreateDto.getCurrentSprintId(),project.getId());
        }

        Set<User> foundAssigneeUsers = getValidatedUsers(issueCreateDto.getAssignees(),project.getId());

        return Issue.createIssue(project,foundAssigneeUsers, issueCreateDto.getTitle(), issueCreateDto.getDescription(), issueCreateDto.getPriority(), issueCreateDto.getStatus(), issueCreateDto.getType(), sprint);
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

        return issue.detailUpdate(issueUpdateDto.getTitle(), issueUpdateDto.getDescription(), issueUpdateDto.getPriority(), issueUpdateDto.getStatus(), issueUpdateDto.getType(),sprint, foundAssigneeUsers);
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

    public Set<Issue> convertToSimpleIssueModelBulk(Long projectId, Set<IssueSimpleDto> issueSimpleDtos, Set<Issue> issuesToBeUpdated){
        //Project project = projectRepository.getReferenceById(projectId);

        HashSet<Long> sprintIds= issueSimpleDtos.stream().map(IssueSimpleDto::getCurrentSprintId).collect(toCollection(HashSet::new));
        Set<Sprint> foundSprints= sprintRepository.findActiveSprintsByIdAndProjectId(sprintIds,projectId);
        if(foundSprints.size()!=sprintIds.size()){
            throw new IllegalArgumentException("some sprints do not exist within the project with id"+ projectId +" or some sprints are not active anymore");
        }
        Map<Long,List<Sprint>> sprintMap= foundSprints.stream().collect(groupingBy(Sprint::getId));

        Map<Long, List<IssueSimpleDto>> issueSimpleDtoMap = issueSimpleDtos.stream().collect(groupingBy(issueSimpleDto -> issueSimpleDto.getId()));

        for(Issue issue : issuesToBeUpdated){ // update by dirty checking- no save
            IssueSimpleDto issueSimpleDto = issueSimpleDtoMap.get(issue.getId()).get(0);
            Sprint sprint = sprintMap.get(issueSimpleDto.getCurrentSprintId()).get(0);
            issue.simpleUpdate(issueSimpleDto.getTitle(), issueSimpleDto.getPriority(), issueSimpleDto.getStatus(), sprintMap.get(issueSimpleDto.getCurrentSprintId()).get(0));
        }
        return issuesToBeUpdated;
    }



}
