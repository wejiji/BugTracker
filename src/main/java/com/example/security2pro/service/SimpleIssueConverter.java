package com.example.security2pro.service;

import com.example.security2pro.domain.model.*;
import com.example.security2pro.dto.issue.*;
import com.example.security2pro.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class SimpleIssueConverter {
    private final ProjectRepository projectRepository;

    private final ProjectMemberRepository projectMemberRepository;

    private final SprintRepository sprintRepository;

    private final IssueRepository issueRepository;

    Supplier<IllegalArgumentException> completeDateException = ()->new IllegalArgumentException("complete date cannot be set to future for the issues with 'DONE' status");

    public Issue convertToIssueModelToCreate(SimpleIssueCreateDto simpleIssueCreateDto){
        Project project = projectRepository.getReferenceById(simpleIssueCreateDto.getProjectId().get());

        Sprint sprint = null;
        if(simpleIssueCreateDto.getCurrentSprintId()!=null){
            sprint = getValidatedSprint(simpleIssueCreateDto.getCurrentSprintId(),project.getId());
        }

        Set<User> foundAssigneeUsers = getValidatedUsers(simpleIssueCreateDto.getAssignees(),project.getId());

        Optional<Issue> issueOptional=Issue.createIssue(project,foundAssigneeUsers, simpleIssueCreateDto.getTitle(), simpleIssueCreateDto.getDescription(), simpleIssueCreateDto.getCompleteDate(), simpleIssueCreateDto.getPriority(), simpleIssueCreateDto.getStatus(), simpleIssueCreateDto.getType(), sprint);
        return issueOptional.orElseThrow(completeDateException);
    }

    public Issue convertToIssueModelToUpdate(SimpleIssueUpdateDto simpleIssueUpdateDto) {
        System.out.println("converter..");
        Issue issue =issueRepository.getReferenceById(simpleIssueUpdateDto.getIssueId());
        Project project = issue.getProject();
        Sprint sprint = null;
        if(simpleIssueUpdateDto.getCurrentSprintId()!=null){
            sprint =getValidatedSprint(simpleIssueUpdateDto.getCurrentSprintId(),project.getId());
        }

        Set<User> foundAssigneeUsers= getValidatedUsers(simpleIssueUpdateDto.getAssignees(),project.getId());

        Optional<Issue> updatedIssue=issue.detailUpdate(simpleIssueUpdateDto.getTitle(), simpleIssueUpdateDto.getDescription(), simpleIssueUpdateDto.getCompleteDate(),simpleIssueUpdateDto.getPriority(), simpleIssueUpdateDto.getStatus(),simpleIssueUpdateDto.getType(),sprint, foundAssigneeUsers);
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




}
