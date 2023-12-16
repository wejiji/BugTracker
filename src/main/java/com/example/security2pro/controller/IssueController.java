package com.example.security2pro.controller;

import com.example.security2pro.domain.model.*;

import com.example.security2pro.dto.issue.IssueCreateDto;
import com.example.security2pro.dto.issue.IssueSimpleDto;
import com.example.security2pro.dto.issue.IssueUpdateDto;
import com.example.security2pro.dto.sprint.ActiveSprintUpdateDto;
import com.example.security2pro.dto.sprinthistory.SprintIssueHistoryDto;
import com.example.security2pro.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;


    @PostMapping("projects/{projectId}/issues/create")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasPermission(#projectId,'project','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public IssueUpdateDto createIssue(@PathVariable Long projectId, @Validated @RequestBody IssueCreateDto issueCreateDto
            , BindingResult bindingResult) throws BindException{
        // the below is for validation logic. If type mismatch or Other deserialization exception happens,
        // It will throw an error and this controller is not called......
        //Go to rest controller advice to take care of this.
        //also it looks like 403 is caused by this error which triggers authentication entrypoint !...
        if(bindingResult.hasErrors()){
            throw new BindException(bindingResult);
        }
        return issueService.createIssueDetailFromDto(projectId, issueCreateDto);
    }

    @GetMapping("projects/{projectId}/issues/{issueId}")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasPermission(#projectId,'project','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public IssueUpdateDto getIssueDetails(@PathVariable Long projectId, @PathVariable Long issueId){

        return issueService.getIssueWithDetails(issueId);
    }


    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasPermission(#projectId,'project','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    @GetMapping("projects/{projectId}/issues/active-issues") //needs to send existing active issues List (status != done and not archived)
    public Set<IssueSimpleDto> activeIssues(@PathVariable Long projectId){

        return issueService.getActiveIssues(projectId);
        //This page will not show the relationship between sprints and issues -!!!
    }

    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasPermission(#projectId,'project','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    @GetMapping("projects/{projectId}/archived-sprints/{sprintId}/issues")
    public Set<SprintIssueHistoryDto> sprintIssueHistory(@PathVariable Long projectId, @PathVariable Long sprintId){
        // use sprintIssueHistoryRepository  --
        return issueService.getSprintIssueHistory(sprintId,projectId);
    }
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasPermission(#projectId,'project','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    @GetMapping("projects/{projectId}/sprints/{sprintId}/issues/active-issues") //needs to send existing active issues List (status != done and not archived)
    public Set<IssueSimpleDto> activeSprintIssues(@PathVariable Long projectId, @PathVariable Long sprintId){

        return issueService.getActiveIssuesBySprintId(projectId,sprintId);
        //This page will not show the relationship between sprints and issues -!!!
    }

    @GetMapping("users/{userId}/active-issues")
    public Set<IssueSimpleDto> userIssues(@PathVariable String username){
        return issueService.getUserIssues(username);
    }

    @PostMapping("projects/{projectId}/issues/{issueId}/update")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasPermission(#projectId,'project','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public IssueUpdateDto updateIssue(@PathVariable Long projectId, @Validated @RequestBody IssueUpdateDto issueUpdateDto,
                                      BindingResult bindingResult) throws BindException {

        if(bindingResult.hasErrors()){throw new BindException(bindingResult);}
        return issueService.updateIssueDetailFromDto(projectId, issueUpdateDto);
    }

    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    @PostMapping("projects/{projectId}/issues/update-in-bulk")
    public Set<IssueSimpleDto> updateIssues(@PathVariable Long projectId, @Validated @RequestBody Set<IssueSimpleDto> issueSimpleDtos){
        return issueService.updateIssuesInBulk(projectId, issueSimpleDtos);
    }


    @PostMapping("projects/{projectId}/issues/{issueId}/delete")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasPermission(#projectId,'project','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public void deleteIssue(@PathVariable Long projectId, @PathVariable Long issueId){

        issueService.deleteByIdsInBulk(new HashSet<>(List.of(issueId)));
    }

    @PostMapping("/projects/{projectId}/sprints/{sprintId}/end-issues")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public void handleEndingSprintIssues(
            @RequestParam boolean forceEndIssues, @PathVariable Long projectId, @PathVariable Long sprintId) throws BindException {

//        if(bindingResult.hasErrors()){throw new BindException(bindingResult);}

        issueService.handleEndingSprintIssues(projectId,sprintId, forceEndIssues);
    }







}
